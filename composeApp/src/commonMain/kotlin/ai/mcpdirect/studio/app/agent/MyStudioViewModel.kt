package ai.mcpdirect.studio.app.agent

import ai.mcpdirect.mcpdirectstudioapp.getPlatform
import ai.mcpdirect.studio.app.UIState
import ai.mcpdirect.studio.app.model.AIPortServiceResponse
import ai.mcpdirect.studio.app.model.MCPServer
import ai.mcpdirect.studio.app.model.MCPServerConfig
import ai.mcpdirect.studio.app.model.OpenAPIServer
import ai.mcpdirect.studio.app.model.OpenAPIServerConfig
import ai.mcpdirect.studio.app.model.aitool.AIPortTool
import ai.mcpdirect.studio.app.model.aitool.AIPortToolAgent
import ai.mcpdirect.studio.app.model.aitool.AIPortToolMaker
import ai.mcpdirect.studio.app.model.aitool.AIPortToolMakerTemplate
import ai.mcpdirect.studio.app.model.repository.StudioRepository
import ai.mcpdirect.studio.app.model.repository.ToolRepository
import ai.mcpdirect.studio.app.model.repository.UserRepository
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MyStudioViewModel(): ViewModel() {
    var uiState by mutableStateOf<UIState>(UIState.Idle)
    val localToolAgent = StudioRepository.localToolAgent
    private val _toolAgent = MutableStateFlow(AIPortToolAgent())
    val toolAgent: StateFlow<AIPortToolAgent> = _toolAgent
    fun toolAgent(agent:AIPortToolAgent){
        if(agent.id!=_toolAgent.value.id){
            _toolAgent.value = agent
            queryToolMakersFromStudio(agent)
        }
    }
    fun toolAgent(toolAgentId:Long,onResponse:((AIPortServiceResponse<AIPortToolAgent>) -> Unit)){
        viewModelScope.launch {
            StudioRepository.toolAgent(toolAgentId){
                onResponse(it)
            }
        }
    }
    fun resetToolAgent(){
        _toolAgent.value = AIPortToolAgent()
        resetToolMaker()
    }
    val toolAgents: StateFlow<List<AIPortToolAgent>> = StudioRepository.toolAgents
        .map { it.values.toList() }      // 转为 List
        .stateIn(
            scope = viewModelScope,      // 或 CoroutineScope(Dispatchers.Main.immediate)
            started = SharingStarted.WhileSubscribed(5000), // 按需启动
            initialValue = emptyList()
        )
    fun refreshToolAgents(force:Boolean=false){
        viewModelScope.launch {
            StudioRepository.loadToolAgents(force)
        }
    }

    private val _toolMaker = MutableStateFlow(AIPortToolMaker())
    val toolMaker: StateFlow<AIPortToolMaker> = _toolMaker
    fun toolMaker(maker:AIPortToolMaker){
//        when(maker.type){
//            TYPE_MCP -> queryMCPToolsFromStudio(maker)
//            TYPE_OPENAPI -> queryOpenAPIToolsFromStudio(maker)
//        }
        _toolMaker.value = maker
        queryMCPToolsFromStudio(maker)
    }
    fun resetToolMaker(){
        _toolMaker.value = AIPortToolMaker();
        tools.clear()
    }
    val toolMakers: StateFlow<List<AIPortToolMaker>> = ToolRepository.toolMakers
        .map { it.values.toList() }      // 转为 List
        .stateIn(
            scope = viewModelScope,      // 或 CoroutineScope(Dispatchers.Main.immediate)
            started = SharingStarted.WhileSubscribed(5000), // 按需启动
            initialValue = emptyList()
        )
    fun refreshToolMakers(force:Boolean=false){
        viewModelScope.launch {
            ToolRepository.loadToolMakers(force)
        }
    }
    val mcpServers: StateFlow<List<MCPServer>> = combine(
        StudioRepository.mcpServers,
        _toolAgent
    ) { servers, agent ->
        servers.values.filter { server -> server.agentId == agent.id }.toList()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val openapiServers: StateFlow<List<OpenAPIServer>> = combine(
        StudioRepository.openapiServers,
        _toolAgent
    ) { servers, agent ->
        servers.values.filter { server -> server.agentId == agent.id }.toList()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
//    private val _openapiServer = MutableStateFlow<OpenAPIServer>(OpenAPIServer())
//    val openapiServer: StateFlow<OpenAPIServer> = _openapiServer
//    private var _toolMakers = mutableStateMapOf<Long,AIPortToolMaker>()
//    val toolMakers by derivedStateOf {
//        _toolMakers.values.toList()
//    }
//    val toolMakers = mutableStateListOf<AIPortToolMaker>()
//    var toolMaker by mutableStateOf(AIPortToolMaker())
//        private set
//    fun toolMaker(maker:AIPortToolMaker){
//        if(maker.id!=toolMaker.id){
//            queryMCPTools(maker)
//        }
//        toolMaker = maker
//    }
    val tools = mutableStateListOf<AIPortTool>()

    private fun updateUIState(code:Int){
        uiState = if(code==0) UIState.Success else UIState.Error(code)
    }

//    fun connectMCPServer(configs:Map<String, MCPServerConfig>){
//        viewModelScope.launch {
//            uiState = UIState.Loading
//            getPlatform().connectMCPServerToStudio(toolAgent.engineId,configs){
//                updateUIState(it.code)
//                if(it.code==0) it.data?.let {
//                    it.forEach {
////                        it.id = makerId(it.name)
//                        _toolMakers[it.id] = it
//                    }
//                }
//            }
//        }
//    }

    fun connectMCPServerToStudio(config:MCPServerConfig){
        viewModelScope.launch {
            uiState = UIState.Loading
            StudioRepository.connectMCPServerToStudio(_toolAgent.value,config){
                (code, message, data) ->
                updateUIState(code)
            }
        }
    }

//    fun modifyMCPServerConfigForStudio(toolAgent: AIPortToolAgent,
//                                       mcpServer: MCPServer,
//                                       config:MCPServerConfig,
//                                       onResponse: ((code: Int, message: String?, mcpServer: MCPServer?) -> Unit)?=null){
//        viewModelScope.launch {
//            uiState = UIState.Loading
//            getPlatform().modifyMCPServerForStudio(
//                toolAgent.engineId, mcpServer.id, serverConfig = config
//            ){
//                updateUIState(it.code)
//                if(it.code==0) it.data?.let {
////                    it.id = makerId(it.name)
//                    _toolMakers[it.id] = it
//                    if(toolMaker.id==it.id){
//                        toolMaker = it
//                    }
//                }else generalViewModel.showSnackbar(
//                    it.message?:"Config MCP Server ${toolMaker.name} Error",
//                    actionLabel = "Error",
//                    withDismissAction = true
//                )
//                if(onResponse!=null)onResponse(it.code,it.message,it.data)
//            }
//        }
//    }
    fun modifyMCPServerConfigForStudio(toolAgent: AIPortToolAgent,
                                       mcpServer: MCPServer,
                                       config:MCPServerConfig){
        viewModelScope.launch {
            uiState = UIState.Loading
            StudioRepository.modifyMCPServerConfigForStudio(toolAgent,mcpServer,config){
                (code, message, data) ->
                updateUIState(code)
                data?.let {
                    if(it.id==_toolMaker.value.id){
                        _toolMaker.value = it
                    }
                }
            }
        }
    }
//    fun modifyMCPServerNameForStudio(toolAgent: AIPortToolAgent,
//                                     mcpServer: MCPServer,
//                                     name:String){
//        viewModelScope.launch {
//            uiState = UIState.Loading
//            getPlatform().modifyMCPServerForStudio(
//                toolAgent.engineId, mcpServer.id, serverName = name
//            ){
//                updateUIState(it.code)
//                if(it.code==0) it.data?.let {
//                    _toolMakers.remove(mcpServer.id)
//                    _toolMakers[it.id] = it
//                    if(toolMaker.id==it.id){
//                        toolMaker = it
//                    }
//                }
//            }
//        }
//    }
    fun modifyMCPServerNameForStudio(toolAgent: AIPortToolAgent,
                                     toolMaker: AIPortToolMaker,
                                     name:String){
        viewModelScope.launch {
            uiState = UIState.Loading
            StudioRepository.modifyToolMakerNameForStudio(toolAgent,toolMaker,name){
                code, message, data ->
                updateUIState(code)
                data?.let {
                    if(it.id==_toolMaker.value.id){
                        _toolMaker.value = it
                    }
                }
            }
        }
    }
//    fun queryMCPServers(toolAgent: AIPortToolAgent){
//        if(toolAgent.id>0) {
//            uiState = UIState.Loading
//            _toolMakers.clear()
//            if (toolAgent.id > 0 && toolAgent.userId == authViewModel.user.id) viewModelScope.launch {
//                getPlatform().queryMCPServersFromStudio(toolAgent.engineId) {
//                    updateUIState(it.code)
//                    if (it.code == 0) {
//                        it.data?.let {
//                            it.forEach {
////                                it.id = makerId(it.name)
//                                _toolMakers[it.id] = it
//                                if(toolMaker.id==it.id){
//                                    toolMaker = it
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }

    fun queryToolMakersFromStudio(toolAgent: AIPortToolAgent,force: Boolean=false){
        if(toolAgent.id>0) {
            uiState = UIState.Loading
            viewModelScope.launch {
                StudioRepository.queryToolMakersFromStudio(toolAgent,force)
            }
        }
    }

//    fun modifyToolMakerStatus(toolAgent: AIPortToolAgent,maker: AIPortToolMaker,status: Int){
//        val studioId = toolAgent.engineId;
//        viewModelScope.launch {
//            uiState = UIState.Loading
//            if(toolMaker.id==maker.id){
//                tools.clear()
//            }
//            if(maker.id<Int.MAX_VALUE){
//                getPlatform().modifyMCPServerForStudio(
//                    studioId,
//                    maker.id,
//                    null,
//                    status,
//                    null
//                ){
//                    if(it.code==0) it.data?.let {
//                        _toolMakers[it.id] = it
//                        if (toolMaker.id == it.id) {
//                            toolMaker = it
//                            if(status==1)getPlatform().queryMCPToolsFromStudio(
//                                studioId,
//                                it.id
//                            ){
//                                if(it.code==0) it.data?.let {
//                                    if(toolMaker.id==maker.id){
//                                        tools.addAll(it)
//                                    }
//                                }
//                            }
//                        }
//                    } else generalViewModel.showSnackbar(
//                        it.message?:"Access MCP Server ${toolMaker.name} Error",
//                        actionLabel = "Error",
//                        withDismissAction = true
//                    )
//                }
//            }else getPlatform().modifyToolMaker(
//                maker.id,
//                null,
//                null,
//                status
//            ){
//                if(it.code==0) it.data?.let {
//                        maker ->
//                    getPlatform().connectToolMakerToStudio(
//                        studioId = studioId,
//                        makerId = maker.id,
//                        agentId = maker.agentId
//                    ) {
//                        updateUIState(it.code)
//                        if(it.code==0) it.data?.let {
//                            _toolMakers[it.id] = it
//                            if (toolMaker.id == it.id) {
//                                toolMaker = it
//                                if(status==1)getPlatform().queryMCPToolsFromStudio(
//                                    studioId,
//                                    it.id
//                                ){
//                                    if(it.code==0) it.data?.let {
//                                        if(toolMaker.id==maker.id){
//                                            tools.addAll(it)
//                                        }
//                                    }
//                                }
//                            }
//                        } else generalViewModel.showSnackbar(
//                            it.message?:"Access MCP Server ${toolMaker.name} Error",
//                            actionLabel = "Error",
//                            withDismissAction = true
//                        )
//                    }
//                } else updateUIState(it.code)
//            }
//        }
//    }

    fun modifyToolMakerStatus(toolAgent: AIPortToolAgent,maker: AIPortToolMaker,status: Int){
        viewModelScope.launch {
            StudioRepository.modifyToolMakerStatus(toolAgent,maker,status){
                (code, message, maker) ->
                maker?.let {
                    if(_toolMaker.value.id==maker.id){
                        _toolMaker.value = maker
                    }
                }
            }
        }
    }

//    fun queryMCPTools(toolMaker: AIPortToolMaker){
//        if(toolMaker.id!=0L){
//            uiState = UIState.Loading
//            tools.clear()
//            viewModelScope.launch {
//                StudioRepository.toolAgent(toolMaker.agentId) { code, message, data ->
//                    if (code == 0) {
//                        data?.let {
//                            getPlatform().queryMCPToolsFromStudio(it.engineId,toolMaker.id){
//                                updateUIState(it.code)
//                                if(it.code==0){
//                                    it.data?.let {
//                                        tools.addAll(it)
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }

    fun queryMCPToolsFromStudio(toolMaker: AIPortToolMaker){
        if(toolMaker.id!=0L){
            uiState = UIState.Loading
            tools.clear()
            viewModelScope.launch {
                StudioRepository.toolAgent(toolMaker.agentId) {
                    if(it.successful()) it.data?.let {
                        viewModelScope.launch {
                            if(toolMaker.mcp()) StudioRepository.queryMCPToolsFromStudio(
                                it,toolMaker
                            ){(code, message, data) ->
                                updateUIState(code)
                                if(code==0){
                                    data?.let {
                                        tools.addAll(it)
                                    }
                                }
                            } else if(toolMaker.openapi()) StudioRepository.queryOpenAPIToolsFromStudio(
                                it,toolMaker
                            ){(code, message, data) ->
                                updateUIState(code)
                                if(code==0){
                                    data?.let {
                                        tools.addAll(it)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

//    fun publishMCPTools(toolMaker: AIPortToolMaker){
//        if(toolMaker.id!=0L){
//            uiState = UIState.Loading
//            tools.clear()
//            viewModelScope.launch {
//                StudioRepository.toolAgent(toolMaker.agentId){
//                        code,message,data ->
//                    data?.let {
//                        getPlatform().publishMCPToolsForStudio(it.engineId,toolMaker.id){
//                            updateUIState(it.code)
//                            if(it.code==0){
//                                it.data?.let {
//                                    _toolMakers.remove(toolMaker.id)
//                                    _toolMakers[it.id]=it
//                                    if(this@MyStudioViewModel.toolMaker.name==toolMaker.name){
//                                        this@MyStudioViewModel.toolMaker = it
//                                        queryMCPTools(it)
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }

    fun publishMCPTools(toolMaker: AIPortToolMaker){
        if(toolMaker.id!=0L){
            uiState = UIState.Loading
            tools.clear()
            viewModelScope.launch {
                StudioRepository.toolAgent(toolMaker.agentId) {
                    if (it.successful()) it.data?.let {
                        viewModelScope.launch {
                            StudioRepository.publishToolsFromStudio(it,toolMaker){
                                    code, message, data ->
                                updateUIState(code)
                                if(code==0){
                                    data?.let {
                                        if(_toolMaker.value.name==it.name){
                                            _toolMaker.value = it
                                            queryMCPToolsFromStudio(it)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

            }
        }
    }

//    fun createToolMakerByTemplate(
//        templateId:Long, agentId:Long, name:String, mcpServerConfig: AIPortMCPServerConfig,
//        onResponse:(code:Int, message:String?, mcpServer: AIPortToolMaker?) -> Unit) {
//        uiState = UIState.Loading
//        viewModelScope.launch {
//            getPlatform().createToolMaker(
//                AIPortToolMaker.TYPE_MCP, name,
//                templateId = templateId, userId = authViewModel.user.id, agentId = agentId,
//                mcpServerConfig = mcpServerConfig
//                ){ (code,message,data)->
//                if(code== AIPortServiceResponse.SERVICE_SUCCESSFUL){
//                    data?.let {
//                        _toolMakers[it.id]=it
//                    }
//                }
//                onResponse(code,message,data)
//                uiState = UIState.state(code,message)
//            }
//        }
//    }

//    fun createToolMakerByTemplate(
//        toolAgentId:Long,template: AIPortToolMakerTemplate, mcpServerName:String, mcpServerConfig: AIPortMCPServerConfig,
//        onResponse:(code:Int, message:String?, toolMaker: AIPortToolMaker?) -> Unit) {
//        uiState = UIState.Loading
//        viewModelScope.launch {
//            ToolRepository.createMCPServerByTemplate(
//                toolAgentId,template,mcpServerName,mcpServerConfig
//            ){ code, message, data ->
//                onResponse(code,message,data)
//                uiState = UIState.state(code,message)
//            }
//        }
//    }

    fun createToolMakerByTemplate(
        toolAgent: AIPortToolAgent,template: AIPortToolMakerTemplate, mcpServerName:String, inputs:String,
        onResponse:(code:Int, message:String?, toolMaker: AIPortToolMaker?) -> Unit) {
        viewModelScope.launch {
            StudioRepository.connectToolMakerTemplateToStudio(
                toolAgent, UserRepository.me.value.id,template.id,mcpServerName,inputs
            )
//            ToolRepository.createMCPServerByTemplate(
//                toolAgentId,template,mcpServerName,mcpServerConfig
//            ){ code, message, data ->
//                onResponse(code,message,data)
//                uiState = UIState.state(code,message)
//            }
        }
    }

    fun connectToolMakerToStudio(toolAgent: AIPortToolAgent,toolMaker: AIPortToolMaker,
                         onResponse: (code: Int, message: String?, mcpServer: MCPServer?) -> Unit){
        viewModelScope.launch {
            StudioRepository.connectToolMakerToStudio(toolAgent,toolMaker,onResponse)
        }
//        getPlatform().connectToolMakerToStudio(studioId,makerId,agentId){
//            onResponse(it.code,it.message,it.data)
//        }
    }

    fun modifyMCPServerName(toolAgent: AIPortToolAgent,
                            toolMaker: AIPortToolMaker,
                            toolMakerName:String) {
        viewModelScope.launch {
            ToolRepository.modifyToolMakerName(toolMaker,toolMakerName){
                code, message, data ->
                if (code == 0) data?.let{
                    if(toolMaker is MCPServer) modifyMCPServerNameForStudio(
                        toolAgent, toolMaker,toolMakerName
                    )
                }
            }
//            getPlatform().modifyToolMaker(toolMaker.id, toolMakerName,null,null) {
//                    (code, message, data) ->
//                if (code == 0) data?.let{
//                    val maker = _toolMakers[data.id]
//                    maker?.let {
//                        maker.name = data.name
//                        _toolMakers[data.id] = maker
//                    }
//                    if(toolMaker is MCPServer) modifyMCPServerNameForStudio(
//                        toolAgent, toolMaker,toolMakerName
//                    )
//                }
//            }
        }
    }
    fun modifyMCPServerConfig(toolAgent: AIPortToolAgent,toolMaker: AIPortToolMaker,inputs: String){
        viewModelScope.launch {
//            uiState = UIState.Loading
            StudioRepository.modifyToolMakerTemplateConfigFromStudio(
                toolAgent,toolMaker,inputs
            ){ code, message, mcpServer ->
            }
//            ToolRepository.modifyMCPServerConfig(toolMaker,config){
//                code, message, data ->
//                updateUIState(code)
//                if(code==0)data?.let{
//                    viewModelScope.launch {
//                        StudioRepository.connectToolMakerToStudio(
//                            toolAgent,toolMaker
//                        ){ code, message, mcpServer ->
//                        }
//                    }
//                }
//            }
//            getPlatform().modifyMCPServerConfig(
//                config
//            ){
//                updateUIState(it.code)
//                if(it.code==0) it.data?.let {
//                        maker ->
//                    getPlatform().connectToolMakerToStudio(
//                        studioId = toolAgent.engineId,
//                        makerId = maker.id,
//                        agentId = maker.agentId
//                    ) {
//                        if(it.code==0) it.data?.let {
//                            _toolMakers[it.id] = it
//                            if (toolMaker.id == it.id) {
//                                toolMaker = it
//                            }
//                        }else generalViewModel.showSnackbar(
//                            it.message?:"Config MCP Server ${toolMaker.name} Error",
//                            actionLabel = "Error",
//                            withDismissAction = true
//                        )
//                    }
//                }
//            }
        }
    }
    fun modifyToolMakerTags(toolMaker: AIPortToolMaker, toolMakerTags:String) {
        viewModelScope.launch {
            ToolRepository.modifyToolMakerTags(toolMaker,toolMakerTags)
//            getPlatform().modifyToolMaker(toolMaker.id, null, toolMakerTags, null) { (code, message, data) ->
//                if (code == 0 && data != null) {
//                    val maker = _toolMakers[data.id]
//                    maker?.let {
//                        it.tags = data.tags
//                        _toolMakers[data.id] = data
//                    }
//                }
//            }
        }
    }

    fun connectOpenAPIServerToStudio(
        config: OpenAPIServerConfig,
        onResponse: ((resp: AIPortServiceResponse<OpenAPIServer>) -> Unit)?=null){
        viewModelScope.launch {
            StudioRepository.connectOpenAPIServerToStudio(_toolAgent.value,config,onResponse)
        }
    }
//    fun queryOpenAPIToolsFromStudio(toolMaker: AIPortToolMaker){
//        if(toolMaker.id!=0L){
//            uiState = UIState.Loading
//            tools.clear()
//            viewModelScope.launch {
//                StudioRepository.toolAgent(toolMaker.agentId) { code, message, data ->
//                    if(code==0) data?.let {
//                        viewModelScope.launch {
//                            StudioRepository.queryOpenAPIToolsFromStudio(
//                                it,toolMaker
//                            ){code, message, data ->
//                                updateUIState(code)
//                                if(code==0){
//                                    data?.let {
//                                        tools.addAll(it)
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//    }

    fun removeMCPServer(toolAgent: AIPortToolAgent,toolMaker: AIPortToolMaker) {
        viewModelScope.launch {
            StudioRepository.removeMCPServerFromStudio(toolAgent,toolMaker)
        }
    }
    fun removeOpenAPIServer(toolAgent: AIPortToolAgent,toolMaker: AIPortToolMaker) {
        viewModelScope.launch {
            StudioRepository.removeOpenAPIServerFromStudio(toolAgent,toolMaker)
        }
    }

    fun modifyMCPTemplate(
        toolAgent: AIPortToolAgent,template: AIPortToolMakerTemplate,
        type:Int,config:String,inputs: String) {
        viewModelScope.launch {
//            uiState = UIState.Loading
            StudioRepository.modifyToolMakerTemplateForStudio(
                toolAgent, template, null,type,config,inputs
            )
        }
    }
}