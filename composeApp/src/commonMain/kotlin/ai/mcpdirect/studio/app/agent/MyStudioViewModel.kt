package ai.mcpdirect.studio.app.agent

import ai.mcpdirect.mcpdirectstudioapp.getPlatform
import ai.mcpdirect.studio.app.UIState
import ai.mcpdirect.studio.app.auth.authViewModel
import ai.mcpdirect.studio.app.generalViewModel
import ai.mcpdirect.studio.app.model.AIPortServiceResponse
import ai.mcpdirect.studio.app.model.MCPServer
import ai.mcpdirect.studio.app.model.MCPServerConfig
import ai.mcpdirect.studio.app.model.aitool.AIPortMCPServerConfig
import ai.mcpdirect.studio.app.model.aitool.AIPortTool
import ai.mcpdirect.studio.app.model.aitool.AIPortToolAgent
import ai.mcpdirect.studio.app.model.aitool.AIPortToolMaker
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

val myStudioViewModel = MyStudioViewModel()
class MyStudioViewModel: ViewModel() {
    var uiState by mutableStateOf<UIState>(UIState.Idle)
    var toolAgent by mutableStateOf(AIPortToolAgent("",-1))
        private  set
    fun toolAgent(agent:AIPortToolAgent){
        if(agent.id!=toolAgent.id){
            queryMCPServers(agent)
        }
        toolAgent = agent
    }
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

    private fun updateUIState(code:Int){
        uiState = if(code==0) UIState.Success else UIState.Error(code)
    }
    fun reset(){
        toolAgent = AIPortToolAgent("",-1)
        toolMaker = AIPortToolMaker()
        tools.clear()
    }
    fun connectMCPServer(configs:Map<String, MCPServerConfig>){
        viewModelScope.launch {
            uiState = UIState.Loading
            getPlatform().connectMCPServerToStudio(toolAgent.engineId,configs){
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

    fun modifyMCPServerConfigForStudio(toolAgent: AIPortToolAgent,
                                       mcpServer: MCPServer,
                                       config:MCPServerConfig){
        viewModelScope.launch {
            uiState = UIState.Loading
            getPlatform().modifyMCPServerForStudio(
                toolAgent.engineId, mcpServer.id, serverConfig = config
            ){
                updateUIState(it.code)
                if(it.code==0) it.data?.let {
//                    it.id = makerId(it.name)
                    _toolMakers[it.id] = it
                    if(toolMaker.id==it.id){
                        toolMaker = it
                    }
                }
            }
        }
    }
    fun modifyMCPServerNameForStudio(toolAgent: AIPortToolAgent,
                                     mcpServer: MCPServer,
                                     name:String){
        viewModelScope.launch {
            uiState = UIState.Loading
            getPlatform().modifyMCPServerForStudio(
                toolAgent.engineId, mcpServer.id, serverName = name
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

    fun queryMCPServers(toolAgent: AIPortToolAgent){
        if(toolAgent.id>0) {
            uiState = UIState.Loading
            _toolMakers.clear()
            if (toolAgent.id > 0 && toolAgent.userId == authViewModel.user.id) viewModelScope.launch {
                getPlatform().queryMCPServersFromStudio(toolAgent.engineId) {
                    updateUIState(it.code)
                    if (it.code == 0) {
                        it.data?.let {
                            it.forEach {
//                                it.id = makerId(it.name)
                                _toolMakers[it.id] = it
                                if(toolMaker.id==it.id){
                                    toolMaker = it
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    fun queryMCPTools(toolMaker: AIPortToolMaker){
        if(toolMaker.id!=0L){
            uiState = UIState.Loading
            tools.clear()
            generalViewModel.toolAgent(toolMaker.agentId) { code, message, data ->
                if (code == 0) {
                    data?.let {
                        getPlatform().queryMCPToolsFromStudio(it.engineId,toolMaker.id){
                            updateUIState(it.code)
                            if(it.code==0){
                                it.data?.let {
                                    tools.addAll(it)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    fun publishMCPTools(toolMaker: AIPortToolMaker){
        if(toolMaker.id!=0L){
            uiState = UIState.Loading
            tools.clear()
            generalViewModel.toolAgent(toolMaker.agentId){
                code,message,data ->
                data?.let {
                    getPlatform().publishMCPToolsForStudio(it.engineId,toolMaker.id){
                        updateUIState(it.code)
                        if(it.code==0){
                            it.data?.let {
                                _toolMakers.remove(toolMaker.id)
                                _toolMakers[it.id]=it
                                if(this@MyStudioViewModel.toolMaker.name==toolMaker.name){
                                    this@MyStudioViewModel.toolMaker = it
                                    queryMCPTools(it)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    fun createToolMakerByTemplate(
        templateId:Long, agentId:Long, name:String, mcpServerConfig: AIPortMCPServerConfig,
        onResponse:(code:Int, message:String?, mcpServer: AIPortToolMaker?) -> Unit) {
        uiState = UIState.Loading
        viewModelScope.launch {
            getPlatform().createToolMaker(
                AIPortToolMaker.TYPE_MCP, name,
                templateId = templateId, userId = authViewModel.user.id, agentId = agentId,
                mcpServerConfig = mcpServerConfig
                ){ (code,message,data)->
                if(code== AIPortServiceResponse.SERVICE_SUCCESSFUL){
                    data?.let {
                        _toolMakers[it.id]=it
                    }
                }
                onResponse(code,message,data)
                uiState = UIState.state(code,message)
            }
        }
    }

    fun connectToolMaker(studioId: Long, makerId: Long,agentId:Long,
                         onResponse: (code: Int, message: String?, mcpServer: MCPServer?) -> Unit){
        getPlatform().connectToolMakerToStudio(studioId,makerId,agentId){
            onResponse(it.code,it.message,it.data)
        }
    }

    fun modifyMCPServerName(toolAgent: AIPortToolAgent,
                            toolMaker: AIPortToolMaker,
                            toolMakerName:String) {
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
                        toolAgent, toolMaker,toolMakerName
                    )
                }
            }
        }
    }
    fun modifyMCPServerConfig(toolAgent: AIPortToolAgent,config:AIPortMCPServerConfig){
        viewModelScope.launch {
            uiState = UIState.Loading
            getPlatform().modifyMCPServerConfig(
                config
            ){
                updateUIState(it.code)
                if(it.code==0) it.data?.let {
                        maker ->
                    getPlatform().connectToolMakerToStudio(
                        studioId = toolAgent.engineId,
                        makerId = maker.id,
                        agentId = maker.agentId
                    ) {
                        if(it.code==0) it.data?.let {
                            _toolMakers[it.id] = it
                            if (toolMaker.id == it.id) {
                                toolMaker = it
                            }
                        }
                    }
                }
            }
        }
    }
    fun modifyMCPServerTags(toolMaker: AIPortToolMaker,toolMakerTags:String) {
        viewModelScope.launch {
            getPlatform().modifyToolMaker(toolMaker.id, null, toolMakerTags, null) { (code, message, data) ->
                if (code == 0 && data != null) {
                    _toolMakers[data.id] = data
                }
            }
        }
    }
}