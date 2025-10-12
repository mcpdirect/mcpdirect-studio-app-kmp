package ai.mcpdirect.studio.app.viewmodel

import ai.mcpdirect.backend.dao.entity.account.AIPortAccessKeyCredential
import ai.mcpdirect.backend.dao.entity.aitool.AIPortToolPermissionMakerSummary
import ai.mcpdirect.studio.MCPDirectStudio
import ai.mcpdirect.studio.app.generalViewModel
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
sealed class MCPKeyDialog() {
    object None : MCPKeyDialog()
    object GenerateMCPKey : MCPKeyDialog()
    object DisplayMCPKey : MCPKeyDialog()
    object EditMCPKeyName : MCPKeyDialog()
}
sealed class MCPKeyNameError() {
    object None : MCPKeyNameError()
    object Invalid : MCPKeyNameError()
    object Duplicate : MCPKeyNameError()
}
class MCPAccessKeyViewModel : ViewModel(){
    val snackbarHostState = SnackbarHostState()

    fun showSnackbar(message: String) {
        viewModelScope.launch {
            snackbarHostState.showSnackbar(message)
        }
    }
    var loadding by mutableStateOf(true)
//    var showGenerateMCPKeyDialog by mutableStateOf(false)
    var mcpKeyName by mutableStateOf("")
        private set
    var mcpKeyNameErrors by mutableStateOf<MCPKeyNameError>(MCPKeyNameError.None)
    private val _accessKeys = mutableStateMapOf<Long,AIPortAccessKeyCredential>()
    val accessKeys by derivedStateOf {
        _accessKeys.values.toList()
    }
    val toolPermissionMakerSummary = mutableStateListOf<AIPortToolPermissionMakerSummary>()
    fun refreshMCPAccessKeys() {
//        if(_accessKeys.isNotEmpty()) return
        loadding = true
        _accessKeys.clear()
        toolPermissionMakerSummary.clear()
        viewModelScope.launch {

            val result = withContext(Dispatchers.IO) {
//                Thread.sleep(1000)
                MCPDirectStudio.queryAccessKeys(){
                        code, message, data ->
                    if(message!=null) generalViewModel.showSnackbar(message)
                    if(code==0&&data!=null){
                        data.forEach {
                            _accessKeys[it.id]=it
                        }
                        loadding = false
                    }
                }
                var refreshToolMakers = false

                MCPDirectStudio.queryToolPermissionMakerSummaries {
                        code, message, data ->
                    if(data!=null) {
                        toolPermissionMakerSummary.addAll(data)
                        for (s in data){
                            if(generalViewModel.toolMaker(s.makerId)==null){
                                refreshToolMakers = true
                                break;
                            }
                        }
                    }
                }
                if(refreshToolMakers){
                    generalViewModel.loadToolMakers()
                }
            }

        }
//        var refreshToolMakers = false;
//        viewModelScope.launch {
//            toolPermissionMakerSummary.clear()
//            MCPDirectStudio.queryToolPermissionMakerSummaries {
//                code, message, data ->
//                if(data!=null) {
//                    toolPermissionMakerSummary.addAll(data)
//                    for (s in data){
//                        if(generalViewModel.toolMaker(s.makerId)==null){
//                            refreshToolMakers = true
//                            break;
//                        }
//                    }
//                }
//            }
//        }
//        if(refreshToolMakers){
//            generalViewModel.refreshToolMakers()
//        }
    }
    fun onMCPKeyNameChange(name: String) {
        if(name.isBlank())
            mcpKeyName = ""
        else {
            mcpKeyNameErrors = if(name.length > 32) MCPKeyNameError.Invalid else MCPKeyNameError.None
            if (name.length < 33) {
                mcpKeyName = name
            }
        }
    }
    fun generateMCPKey(onToolPermissionConfigClick: (key: AIPortAccessKeyCredential) -> Unit) {
        mcpKeyName = mcpKeyName.trim()
        if (mcpKeyName.isEmpty()) {
            mcpKeyNameErrors = MCPKeyNameError.Invalid
            return
        }

        viewModelScope.launch {
            MCPDirectStudio.generateAccessKey(mcpKeyName){
                    code, message, data ->
                if(message!=null) generalViewModel.showSnackbar(message)
                if(code==0&&data!=null){
//                    showGenerateMCPKeyDialog = false
                    _accessKeys[data.id]=data
                    onToolPermissionConfigClick(data)
                }
            }
        }
    }

    fun getMCPAccessKeyFromLocal(id:Long): String?{
        return MCPDirectStudio.getAccessKey(id)
    }

    fun setMCPKeyStatus(key: AIPortAccessKeyCredential, status:Int) {
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                MCPDirectStudio.modifyAccessKey(key.id,"",status){
                    code, message, data ->
                    if(code==0&&data!=null){
                        _accessKeys.remove(key.id)
                        key.status = status
                        _accessKeys[key.id]=key
                    }
                }
            }
        }
    }
    fun setMCPKeyName(key: AIPortAccessKeyCredential) {
        viewModelScope.launch {
            try {
                MCPDirectStudio.modifyAccessKey(key.id, mcpKeyName, null){
                    code, message, data ->
                    if(code==0&&data!=null){
                        _accessKeys.remove(key.id)
                        key.name = mcpKeyName
                        _accessKeys[key.id]=key
                        mcpKeyNameErrors = MCPKeyNameError.None
                    }else if(message!=null){
                        mcpKeyNameErrors = MCPKeyNameError.Duplicate
                    }
                }
            } catch (e: Exception) {
                showSnackbar("Error updating key name: ${e.message}")
            }
        }
    }
}