package ai.mcpdirect.studio.app.mcpkey

import ai.mcpdirect.mcpdirectstudioapp.getPlatform
import ai.mcpdirect.studio.app.Screen
import ai.mcpdirect.studio.app.UIState
import ai.mcpdirect.studio.app.generalViewModel
import ai.mcpdirect.studio.app.model.account.AIPortAccessKey
import ai.mcpdirect.studio.app.model.account.AIPortAccessKeyCredential
import ai.mcpdirect.studio.app.model.aitool.AIPortToolPermissionMakerSummary
import ai.mcpdirect.studio.app.tool.toolPermissionViewModel
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

val mcpAccessKeyViewModel = MCPAccessKeyViewModel()
class MCPAccessKeyViewModel : ViewModel(){
    var uiState by mutableStateOf<UIState>(UIState.Idle)
    private fun updateUIState(code:Int){
        uiState = when(code) {
            0 -> UIState.Success
            else -> UIState.Error(code)
        }
    }

    var mcpKeyName by mutableStateOf("")
        private set
    var mcpKeyNameErrors by mutableStateOf<MCPKeyNameError>(MCPKeyNameError.None)
    private val _accessKeys = mutableStateMapOf<Long, AIPortAccessKey>()
    val accessKeys by derivedStateOf {
        _accessKeys.values.toList()
    }
    val toolPermissionMakerSummary = mutableStateListOf<AIPortToolPermissionMakerSummary>()
    fun reset(){
        _accessKeys.clear()
        toolPermissionMakerSummary.clear()
    }
    fun refreshMCPAccessKeys() {
        uiState = UIState.Loading
        _accessKeys.clear()
        toolPermissionMakerSummary.clear()
        viewModelScope.launch {
            getPlatform().queryAccessKeys{ (code, message, data) ->
                updateUIState(code)
                if(message!=null) generalViewModel.showSnackbar(message)
                if(code==0&&data!=null){
                    data.forEach {
                        _accessKeys[it.id]=it
                    }
                }
            }
            getPlatform().queryToolPermissionMakerSummaries { ( code, message, data) ->
                if(data!=null) {
                    toolPermissionMakerSummary.clear()
                    toolPermissionMakerSummary.addAll(data)
                }
            }
        }
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
    fun generateMCPKey() {
        mcpKeyName = mcpKeyName.trim()
        if (mcpKeyName.isEmpty()) {
            mcpKeyNameErrors = MCPKeyNameError.Invalid
            return
        }

        viewModelScope.launch {
            getPlatform().generateAccessKey(mcpKeyName){ (code, message, data) ->
                if(message!=null) generalViewModel.showSnackbar(message)
                if(code==0&&data!=null){
                    _accessKeys[data.id]=data
                    toolPermissionViewModel.accessKey = data
                    generalViewModel.currentScreen(Screen.ToolPermission,
                        previousScreen = Screen.MCPAccessKey)
                }
            }
        }
    }

    fun getMCPAccessKeyCredential(id:Long,
                                  onResponse: (resp: AIPortAccessKeyCredential?) -> Unit){
        viewModelScope.launch {
            getPlatform().getAccessKeyCredential(id){
                onResponse(it.data)
            }
        }

    }

    fun setMCPKeyStatus(key: AIPortAccessKey, status:Int) {
        viewModelScope.launch {
            getPlatform().modifyAccessKey(key.id,status){
                    (code, message, data) ->
                if(code==0&&data!=null){
                    _accessKeys.remove(key.id)
                    key.status = status
                    _accessKeys[key.id]=data
                }
            }
        }
    }
    fun setMCPKeyName(key: AIPortAccessKey) {
        viewModelScope.launch {
            try {
                getPlatform().modifyAccessKey(key.id, mcpKeyName=mcpKeyName){
                    (code, message, data) ->
                    if(code==0&&data!=null){
                        _accessKeys.remove(key.id)
                        key.name = mcpKeyName
                        _accessKeys[key.id]=data
                        mcpKeyNameErrors = MCPKeyNameError.None
                    }else if(message!=null){
                        mcpKeyNameErrors = MCPKeyNameError.Duplicate
                    }
                }
            } catch (e: Exception) {
                generalViewModel.showSnackbar("Error updating key name: ${e.message}")
            }
        }
    }
}