package ai.mcpdirect.studio.app.mcpkey

import ai.mcpdirect.mcpdirectstudioapp.getPlatform
import ai.mcpdirect.studio.app.model.account.AIPortAccessKey
import ai.mcpdirect.studio.app.model.account.AIPortAccessKeyCredential
import ai.mcpdirect.studio.app.model.aitool.AIPortToolPermission
import ai.mcpdirect.studio.app.model.aitool.AIPortToolPermissionMakerSummary
import ai.mcpdirect.studio.app.model.repository.AccessKeyRepository
import ai.mcpdirect.studio.app.model.repository.ToolRepository
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.collections.set

//val mcpAccessKeyViewModel = MCPAccessKeyViewModel()
class MCPAccessKeyViewModel : ViewModel(){
//    var uiState by mutableStateOf<UIState>(UIState.Idle)
//    private fun updateUIState(code:Int){
//        uiState = when(code) {
//            0 -> UIState.Success
//            else -> UIState.Error(code)
//        }
//    }
    var mcpKey by mutableStateOf<AIPortAccessKey?>(null)
        private set
    private val _toolPermissions = mutableStateMapOf<Long, AIPortToolPermission>()
    val toolPermissions by derivedStateOf {
        _toolPermissions.values.toList()
    }
//    val virtualToolPermissions = mutableStateMapOf<Long, AIPortVirtualToolPermission>()
    fun mcpKey(accessKey:AIPortAccessKey){
        mcpKey = accessKey
        if(accessKey.id>Int.MAX_VALUE) {
            _toolPermissions.clear()
            viewModelScope.launch {
                ToolRepository.loadToolPermissions(accessKey) {
                    if (it.successful()) it.data?.let {

                        it.forEach {
                            _toolPermissions[it.toolId] = it
                        }
                    }
                }
            }
            viewModelScope.launch {
                ToolRepository.loadVirtualToolPermissions(accessKey) {
                    if (it.successful()) it.data?.let {
                        it.forEach {
                            _toolPermissions[it.originalToolId] = it
                        }
                    }
                }
            }
        }
    }
    var mcpKeyName by mutableStateOf("")
        private set
    var mcpKeyNameErrors by mutableStateOf<MCPKeyNameError>(MCPKeyNameError.None)
//    private val _accessKeys = mutableStateMapOf<Long, AIPortAccessKey>()
//    val accessKeys by derivedStateOf {
//        _accessKeys.values.toList()
//    }
    val accessKeys: StateFlow<List<AIPortAccessKey>> = AccessKeyRepository.accessKeys
        .map { it.values.toList() }      // 转为 List
        .stateIn(
            scope = viewModelScope,      // 或 CoroutineScope(Dispatchers.Main.immediate)
            started = SharingStarted.WhileSubscribed(5000), // 按需启动
            initialValue = emptyList()
        )
    val toolPermissionMakerSummary = mutableStateListOf<AIPortToolPermissionMakerSummary>()
    fun reset(){
//        _accessKeys.clear()
        toolPermissionMakerSummary.clear()
    }
    fun refreshMCPAccessKeys() {
//        uiState = UIState.Loading
//        _accessKeys.clear()
        toolPermissionMakerSummary.clear()
        viewModelScope.launch {
            AccessKeyRepository.loadAccessKeys()
//            getPlatform().queryAccessKeys{ (code, message, data) ->
////                updateUIState(code)
//                if(message!=null) generalViewModel.showSnackbar(message)
//                if(code==0&&data!=null){
//                    data.forEach {
//                        _accessKeys[it.id]=it
//                    }
//                }
//            }
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
            AccessKeyRepository.generateAccessKey(mcpKeyName)
//            getPlatform().generateAccessKey(mcpKeyName){ (code, message, data) ->
//                if(message!=null) generalViewModel.showSnackbar(message)
//                if(code==0&&data!=null){
//                    _accessKeys[data.id]=data
//                    toolPermissionViewModel.accessKey = data
//                    generalViewModel.currentScreen(Screen.ToolPermission,
//                        previousScreen = Screen.MCPAccessKey())
//                }
//            }
        }
    }

    fun getMCPAccessKeyCredential(key: AIPortAccessKey,
                                  onResponse: (resp: AIPortAccessKeyCredential?) -> Unit){
        viewModelScope.launch {
//            getPlatform().getAccessKeyCredential(id){
//                onResponse(it.data)
//            }
            AccessKeyRepository.getAccessKeyCredential(key,onResponse)
        }

    }

    fun setMCPKeyStatus(key: AIPortAccessKey, status:Int) {
        viewModelScope.launch {
            AccessKeyRepository.modifyAccessKey(key,status)
//            getPlatform().modifyAccessKey(key.id,status){
//                    (code, message, data) ->
//                if(code==0&&data!=null){
//                    _accessKeys.remove(key.id)
//                    key.status = status
//                    _accessKeys[key.id]=data
//                }
//            }
        }
    }
    fun setMCPKeyName(key: AIPortAccessKey) {
        viewModelScope.launch {
            AccessKeyRepository.modifyAccessKey(key,name=mcpKeyName)
//            try {
//                getPlatform().modifyAccessKey(key.id, mcpKeyName=mcpKeyName){
//                    (code, message, data) ->
//                    if(code==0&&data!=null){
//                        _accessKeys.remove(key.id)
//                        key.name = mcpKeyName
//                        _accessKeys[key.id]=data
//                        mcpKeyNameErrors = MCPKeyNameError.None
//                    }else if(message!=null){
//                        mcpKeyNameErrors = MCPKeyNameError.Duplicate
//                    }
//                }
//            } catch (e: Exception) {
//                generalViewModel.showSnackbar("Error updating key name: ${e.message}")
//            }
        }
    }
}