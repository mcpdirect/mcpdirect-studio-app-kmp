package ai.mcpdirect.studio.app.mcp

import ai.mcpdirect.mcpdirectstudioapp.getPlatform
import ai.mcpdirect.studio.MCPDirectStudio
import ai.mcpdirect.studio.app.UIState
import ai.mcpdirect.studio.app.generalViewModel
import ai.mcpdirect.studio.app.model.MCPServer
import ai.mcpdirect.studio.app.model.MCPServerConfig
import ai.mcpdirect.studio.app.model.aitool.AIPortMCPServerConfig
import ai.mcpdirect.studio.app.model.aitool.AIPortTool
import ai.mcpdirect.studio.app.model.aitool.AIPortToolMaker
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

val connectMCPViewModel = ConnectMCPViewModel()
class ConnectMCPViewModel: ViewModel() {
    var uiState by mutableStateOf<UIState>(UIState.Idle)
        private set
    private var _toolMakers = mutableStateMapOf<Long,AIPortToolMaker>()
    val toolMakers by derivedStateOf {
        _toolMakers.values.toList()
    }
    //    val toolMakers = mutableStateListOf<AIPortToolMaker>()
    var toolMaker by mutableStateOf(AIPortToolMaker())
        private set
    fun toolMaker(maker:AIPortToolMaker){
        if(maker.id!=toolMaker.id){
            queryMCPTools(maker)
        }
        toolMaker = maker
    }
    val tools = mutableStateListOf<AIPortTool>()
    fun reset(){
        toolMaker = AIPortToolMaker()
        _toolMakers.clear()
        tools.clear()
    }
    private fun updateUIState(code:Int?=null){
        code?.let {
            uiState = if (it == 0) UIState.Success else UIState.Error(code)
            generalViewModel.loadingProcess = 1.0f
        }?: {
            uiState = UIState.Loading
            generalViewModel.loadingProcess = null
        }
    }
    fun updateToolMaker(maker: AIPortToolMaker){
        viewModelScope.launch {
            if(maker.status==-1) _toolMakers.remove(maker.id)
            else if(maker.id!=0L)_toolMakers[maker.id]=maker;
        }
    }
    fun connectMCPServer( configs:Map<String, MCPServerConfig>){
        viewModelScope.launch {
//            uiState = UIState.Loading
            updateUIState()
            getPlatform().connectMCPServerToStudio(MCPDirectStudio.studioId(),configs){
                updateUIState(it.code)
                if(it.code==0) it.data?.let {
                    it.forEach {
//                        it.id = makerId(it.name)
                        _toolMakers[it.id] = it
                    }
                }
            }
        }
    }

    fun modifyMCPServerName(toolMaker: AIPortToolMaker, toolMakerName:String) {
        viewModelScope.launch {
            getPlatform().modifyToolMaker(toolMaker.id, toolMakerName,null,null) {
                    (code, message, data) ->
                if (code == 0) data?.let{
                    val maker = _toolMakers[data.id]
                    maker?.let {
                        maker.name = data.name
                        _toolMakers[data.id] = maker
                    }
                    if(toolMaker is MCPServer) modifyMCPServerNameForStudio(
                        toolMaker,toolMakerName
                    )
                }
            }
        }
    }

    fun modifyMCPServerConfigForStudio(mcpServer: MCPServer,
                                       config:MCPServerConfig,
                                       onResponse: (code: Int, message: String?, mcpServer: MCPServer?) -> Unit){
        viewModelScope.launch {
            uiState = UIState.Loading
            getPlatform().modifyMCPServerForStudio(
                MCPDirectStudio.studioId(), mcpServer.id, serverConfig = config
            ){
                updateUIState(it.code)
                if(it.code==0) it.data?.let {
//                    it.id = makerId(it.name)
                    _toolMakers[it.id] = it
                    if(toolMaker.id==it.id){
                        toolMaker = it
                    }
                }
                onResponse(it.code,it.message,it.data)
            }
        }
    }
    fun modifyMCPServerNameForStudio(mcpServer: MCPServer, name:String){
        viewModelScope.launch {
            uiState = UIState.Loading
            getPlatform().modifyMCPServerForStudio(
                MCPDirectStudio.studioId(), mcpServer.id, serverName = name
            ){
                updateUIState(it.code)
                if(it.code==0) it.data?.let {
                    _toolMakers.remove(mcpServer.id)
                    _toolMakers[it.id] = it
                    if(toolMaker.id==it.id){
                        toolMaker = it
                    }
                }
            }
        }
    }

    fun modifyToolMakerTags(toolMaker: AIPortToolMaker, toolMakerTags:String) {
        viewModelScope.launch {
            getPlatform().modifyToolMaker(toolMaker.id, null, toolMakerTags, null) { (code, message, data) ->
                if (code == 0 && data != null) {
                    val maker = _toolMakers[data.id]
                    maker?.let {
                        it.tags = data.tags
                        _toolMakers[data.id] = data
                    }
                }
            }
        }
    }
    fun modifyMCPServerConfig(config:AIPortMCPServerConfig){
        viewModelScope.launch {
            uiState = UIState.Loading
            getPlatform().modifyMCPServerConfig(
                config
            ){
                updateUIState(it.code)
                if(it.code==0) it.data?.let {
                        maker ->
                    getPlatform().connectToolMakerToStudio(
                        studioId = MCPDirectStudio.studioId(),
                        makerId = maker.id,
                        agentId = maker.agentId
                    ) {
                        if(it.code==0) it.data?.let {
                            _toolMakers[it.id] = it
                            if (toolMaker.id == it.id) {
                                toolMaker = it
                            }
                        } else generalViewModel.showSnackbar(
                            it.message?:"Config MCP Server ${toolMaker.name} Error",
                            actionLabel = "Error",
                            withDismissAction = true
                        )
                    }
                }
            }
        }
    }
    fun queryMCPTools(toolMaker: AIPortToolMaker){
        if(toolMaker.id!=0L){
//            uiState = UIState.Loading
            updateUIState()
            tools.clear()
            getPlatform().queryMCPToolsFromStudio(MCPDirectStudio.studioId(),toolMaker.id){
                updateUIState(it.code)
                if(it.code==0){
                    it.data?.let {
                        tools.addAll(it)
                    }
                }
            }
        }
    }
    fun publishMCPTools(toolMaker: AIPortToolMaker){
        if(toolMaker.id!=0L){
//            uiState = UIState.Loading
            updateUIState()
            tools.clear()
            getPlatform().publishMCPToolsForStudio(MCPDirectStudio.studioId(),toolMaker.id){
                updateUIState(it.code)
                if(it.code==0){
                    it.data?.let {
                        _toolMakers.remove(toolMaker.id)
                        _toolMakers[it.id]=it
                        if(this@ConnectMCPViewModel.toolMaker.name==toolMaker.name){
                            this@ConnectMCPViewModel.toolMaker = it
                            queryMCPTools(it)
                        }
                    }
                }
            }
        }
    }
}