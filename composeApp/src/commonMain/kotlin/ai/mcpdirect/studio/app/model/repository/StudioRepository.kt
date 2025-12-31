package ai.mcpdirect.studio.app.model.repository

import ai.mcpdirect.mcpdirectstudioapp.getPlatform
import ai.mcpdirect.studio.app.generalViewModel
import ai.mcpdirect.studio.app.model.*
import ai.mcpdirect.studio.app.model.aitool.AIPortTool
import ai.mcpdirect.studio.app.model.aitool.AIPortToolAgent
import ai.mcpdirect.studio.app.model.aitool.AIPortToolMaker
import ai.mcpdirect.studio.app.model.aitool.AIPortToolMakerTemplate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeMark
import kotlin.time.TimeSource

object StudioRepository {
    private val loadMutex = Mutex()
    private val _duration = 5.seconds
    private val _virtualToolAgent = AIPortToolAgent("Virtual MCP",1)
    private val _localToolAgent = MutableStateFlow(AIPortToolAgent())
    val localToolAgent: StateFlow<AIPortToolAgent> = _localToolAgent
    fun localToolAgent(agent: AIPortToolAgent){
        _localToolAgent.value = agent
        _toolAgents.update { map ->
            map.toMutableMap().apply {
                put(agent.id,agent)
            }
        }
    }
    private var _toolAgentLastQuery:TimeMark? = null
    private val _toolAgents = MutableStateFlow<Map<Long, AIPortToolAgent>>(mapOf(
        0L to _virtualToolAgent
    ))
    val toolAgents: StateFlow<Map<Long, AIPortToolAgent>> = _toolAgents
    private val _toolMakerLastQueries = mutableMapOf<Long, TimeMark>()

    private val _toolMakers = MutableStateFlow<Map<Long, AIPortToolMaker>>(emptyMap())
    val toolMakers: StateFlow<Map<Long, AIPortToolMaker>> = _toolMakers

    private val _mcpServers = MutableStateFlow<Map<Long, MCPServer>>(emptyMap())
    fun mcpServer(id:Long): MCPServer?{
        return _mcpServers.value[id];
    }
    val mcpServers: StateFlow<Map<Long, MCPServer>> = _mcpServers
    fun mcpServer(server: MCPServer){
        _mcpServers.update { map ->
            map.toMutableMap().apply {
                if(server.status== AIPortToolMaker.STATUS_ABANDONED) remove(server.id)
                else put(server.id, server)
            }
        }
        _toolMakers.update { map ->
            map.toMutableMap().apply {
                if(server.status== AIPortToolMaker.STATUS_ABANDONED) remove(server.id)
                else put(server.id, server)
            }
        }
    }

    private val _openapiServers = MutableStateFlow<Map<Long, OpenAPIServer>>(emptyMap())
    val openapiServers: StateFlow<Map<Long, OpenAPIServer>> = _openapiServers
    fun openapiServer(server: OpenAPIServer){
        _openapiServers.update { map ->
            map.toMutableMap().apply {
                if(server.status== AIPortToolMaker.STATUS_ABANDONED) remove(server.id)
                else put(server.id, server)
            }
        }
        _toolMakers.update { map ->
            map.toMutableMap().apply {
                if(server.status== AIPortToolMaker.STATUS_ABANDONED) remove(server.id)
                else put(server.id, server)
            }
        }
    }

    fun reset(){
        _localToolAgent.value = AIPortToolAgent()
        _toolAgentLastQuery = null
        _toolAgents.update { map ->
            map.toMutableMap().apply {
                clear()
            }
        }
        _toolMakerLastQueries.clear()
        _toolMakers.update { map ->
            map.toMutableMap().apply {
                clear()
            }
        }
        _mcpServers.update { map ->
            map.toMutableMap().apply {
                clear()
            }
        }
        _openapiServers.update { map ->
            map.toMutableMap().apply {
                clear()
            }
        }
    }

    suspend fun queryToolMakersFromStudio(toolAgent: AIPortToolAgent,force: Boolean=false){
        val agentId = toolAgent.id
        loadMutex.withLock {
            val now = TimeSource.Monotonic.markNow()
            val lastQuery = _toolMakerLastQueries[agentId]
            if(lastQuery==null|| (force&&lastQuery.elapsedNow()> _duration)) {
                generalViewModel.loading()
                getPlatform().queryToolMakersFromStudio(toolAgent.engineId) {
                    if (it.successful()) it.data?.let { toolMakers ->
                        _mcpServers.update { map ->
                            map.toMutableMap().apply {
                                toolMakers.mcpServers?.let{ servers ->
                                    for (server in servers) {
                                        put(server.id, server)
                                    }
                                }
                            }
                        }
                        _openapiServers.update { map ->
                            map.toMutableMap().apply {
                                toolMakers.openapiServers?.let{ servers ->
                                    for (server in servers) {
                                        put(server.id, server)
                                    }
                                }
                            }
                        }
                        _toolMakers.update { map ->
                            map.toMutableMap().apply {
                                toolMakers.mcpServers?.let{ servers ->
                                    for (server in servers) {
                                        put(server.id, server)
                                    }
                                }
                                toolMakers.openapiServers?.let{ servers ->
                                    for (server in servers) {
                                        put(server.id, server)
                                    }
                                }
                            }
                        }
                        _toolMakerLastQueries[agentId] = now
                    }
                    generalViewModel.loaded(
                        "Load MCP Servers From Studio #${toolAgent.name}",it.code,it.message
                    )
                }
            }
        }
    }
    fun toolAgent(id:Long): AIPortToolAgent?{
        return _toolAgents.value[id]
    }
    suspend fun toolAgent(id:Long,onResponse:(AIPortServiceResponse<AIPortToolAgent>) -> Unit){
        if(id==0L){
            val resp = AIPortServiceResponse<AIPortToolAgent>()
            resp.code = 0
            resp.data = _virtualToolAgent
            onResponse(resp)
            return
        }
        val agent = _toolAgents.value[id]
        if(agent!=null) {
            val resp = AIPortServiceResponse<AIPortToolAgent>()
            resp.code = 0
            resp.data = agent
            onResponse(resp)
        } else loadMutex.withLock {
            generalViewModel.loading()
            getPlatform().getToolAgent(id){
                if(it.successful()){
                    it.data?.let { toolAgent ->
                        _toolAgents.update { map ->
                            map.toMutableMap().apply {
                                put(toolAgent.id,toolAgent)
                            }
                        }
                    }
                }
                generalViewModel.loaded(
                    "Load Studio",it.code,it.message
                )
                onResponse(it)
            }
        }
    }

    suspend fun loadToolAgents(force:Boolean=false){
        loadMutex.withLock {
            val now = TimeSource.Monotonic.markNow()
            if (_toolAgentLastQuery == null || (force && _toolAgentLastQuery!!.elapsedNow() > _duration)) {
                generalViewModel.loading()
                getPlatform().queryToolAgents {
                    if (it.successful()) {
                        it.data?.let { toolAgents ->
                            _toolAgents.update { map ->
                                map.toMutableMap().apply {
                                    toolAgents.forEach {
                                        put(it.id,it)
                                    }
                                }
                            }
                        }
                        _toolAgentLastQuery = now
                    }
                    generalViewModel.loaded(
                        "Load Studios",it.code,it.message
                    )
                }
            }
        }
    }
//    suspend fun connectMCPServersToStudio(
//        toolAgent: AIPortToolAgent, configs:Map<String, MCPServerConfig>,
//        onResponse:((code:Int,message:String?,data:List<MCPServer>?) -> Unit)){
//        val studioId = toolAgent.engineId
//        loadMutex.withLock {
//            generalViewModel.loading()
//            getPlatform().connectMCPServerToStudio(studioId,configs){
//                if(it.code==0) it.data?.let { servers ->
//                    _mcpServers.update { map ->
//                        map.toMutableMap().apply {
//                            for (server in servers) {
//                                put(server.id, server)
//                            }
//                        }
//                    }
//                }
//                generalViewModel.loaded(
//                    "Connect MCP Servers to Studio #${toolAgent.name}",it.code,it.message
//                )
//                onResponse(it.code,it.message,it.data)
//            }
//        }
//    }
    suspend fun connectMCPServerToStudio(
        toolAgent: AIPortToolAgent, config:MCPServerConfig,
        onResponse:((resp:AIPortServiceResponse<MCPServer>) -> Unit)?=null){
        val studioId = toolAgent.engineId
        loadMutex.withLock {
            generalViewModel.loading()
            getPlatform().connectMCPServerToStudio(studioId,config){
                if(it.code==0) it.data?.let { server ->
                    _mcpServers.update { map ->
                        map.toMutableMap().apply {
                            put(server.id, server)
                        }
                    }
                }
                generalViewModel.loaded(
                    "Connect MCP Servers to Studio #${toolAgent.name}",it.code,it.message
                )
                onResponse?.invoke(it)
            }
        }
    }
    suspend fun removeMCPServerFromStudio(
        toolAgent: AIPortToolAgent,toolMaker: AIPortToolMaker
    ){
        loadMutex.withLock {
            generalViewModel.loading()
            getPlatform().removeToolMakerFromStudio(
                toolAgent.engineId,toolMaker.id,toolMaker.type
            ){
                if(it.successful()||it.code== AIPortServiceResponse.TOOL_MAKER_NOT_EXISTS){
                    _mcpServers.update { map ->
                        map.toMutableMap().apply {
                            remove(toolMaker.id)
                        }
                    }
                }
                generalViewModel.loaded(
                    "Remove MCP Server #${toolMaker.name} from Studio #${toolAgent.name}",it.code,it.message
                )
            }
        }
    }
    suspend fun modifyMCPServerForStudio(
        toolAgent: AIPortToolAgent,
        mcpServer: MCPServer,
        serverName: String? = null,
        serverStatus: Int? = null,
        serverConfig: MCPServerConfig? = null,
        onResponse: ((resp: AIPortServiceResponse<MCPServer>) -> Unit)? = null
    ){
        val studioId = toolAgent.engineId
        val mcpServerId = mcpServer.id
        if(serverName == null && serverStatus == null && serverConfig==null){
            onResponse?.invoke(AIPortServiceResponse())
        }
        loadMutex.withLock {
            generalViewModel.loading()
            getPlatform().modifyMCPServerForStudio(
                studioId, mcpServerId, serverName,serverStatus, serverConfig
            ){
                if(it.code==0) it.data?.let { server ->
                    _mcpServers.update { map ->
                        map.toMutableMap().apply {
                            put(server.id, server)
                        }
                    }
                    _toolMakers.update { map ->
                        map.toMutableMap().apply {
                            put(server.id, server)
                        }
                    }
                }
                generalViewModel.loaded(
                    "Modify config of MCP Server #${mcpServer.name} in Studio #${toolAgent.name}",it.code,it.message
                )
//                onResponse(it.code,it.message,it.data)
                onResponse?.let{ response->
                    response(it)
                }
            }
        }
    }
    suspend fun modifyMCPServerConfigForStudio(
        toolAgent: AIPortToolAgent, mcpServer: MCPServer,config:MCPServerConfig,
        onResponse: ((resp: AIPortServiceResponse<MCPServer>) -> Unit)? = null
    ){
        val studioId = toolAgent.engineId
        val mcpServerId = mcpServer.id
        loadMutex.withLock {
            generalViewModel.loading()
            getPlatform().modifyMCPServerForStudio(
                studioId, mcpServerId, null,null, config
            ){
                if(it.code==0) it.data?.let { server ->
                    _mcpServers.update { map ->
                        map.toMutableMap().apply {
                            put(server.id, server)
                        }
                    }
                    _toolMakers.update { map ->
                        map.toMutableMap().apply {
                            put(server.id, server)
                        }
                    }
                }
                generalViewModel.loaded(
                    "Modify config of MCP Server #${mcpServer.name} in Studio #${toolAgent.name}",it.code,it.message
                )
//                onResponse(it.code,it.message,it.data)
                onResponse?.let{ response->
                    response(it)
                }
            }
        }
    }
    private fun updateMCPServers(toolMaker: AIPortToolMaker,resp: AIPortServiceResponse<MCPServer>){
        if(resp.code==0) resp.data?.let { server ->
            _mcpServers.update { map ->
                map.toMutableMap().apply {
                    put(server.id, server)
                    if(server.id!=toolMaker.id){
                        remove(toolMaker.id)
                    }
                }
            }
        }
    }
    private fun updateOpenAPIServers(toolMaker: AIPortToolMaker,resp: AIPortServiceResponse<OpenAPIServer>){
        if(resp.code==0) resp.data?.let { server ->
            _openapiServers.update { map ->
                map.toMutableMap().apply {
                    put(server.id, server)
                    if(server.id!=toolMaker.id){
                        remove(toolMaker.id)
                    }
                }
            }
        }
    }
    suspend fun modifyToolMakerNameForStudio(
        toolAgent: AIPortToolAgent, toolMaker: AIPortToolMaker, name:String,
        onResponse: (code: Int, message: String?, maker: AIPortToolMaker?) -> Unit
    ){
        val studioId = toolAgent.engineId
        val serverId = toolMaker.id
        loadMutex.withLock {
            generalViewModel.loading()
            if(toolMaker.mcp()) getPlatform().modifyMCPServerForStudio(
                studioId, serverId, serverName = name
            ){
                updateMCPServers(toolMaker,it)
                generalViewModel.loaded(
                    "Modify name of MCP Server #${toolMaker.name} in Studio #${toolAgent.name}",it.code,it.message
                )
                onResponse(it.code,it.message,it.data)
            } else if(toolMaker.openapi()) getPlatform().modifyOpenAPIServerForStudio(
                studioId, serverId, serverName = name
            ){
                updateOpenAPIServers(toolMaker, it)
                generalViewModel.loaded(
                    "Modify name of OpenAPI Server #${toolMaker.name} in Studio #${toolAgent.name}",it.code,it.message
                )
                onResponse(it.code,it.message,it.data)
            }
        }
    }

    suspend fun modifyToolMakerStatus(
        toolAgent: AIPortToolAgent, toolMaker: AIPortToolMaker,status: Int,
        onResponse: (AIPortServiceResponse<AIPortToolMaker>) -> Unit
    ){
        val studioId = toolAgent.engineId
        val mcpServerId = toolMaker.id
        loadMutex.withLock {
            generalViewModel.loading()
            if(mcpServerId<Int.MAX_VALUE){
                if(toolMaker.mcp()) getPlatform().modifyMCPServerForStudio(
                    studioId, mcpServerId, serverStatus = status
                ){
                    if(it.code==0) it.data?.let { server ->
                        _mcpServers.update { map ->
                            map.toMutableMap().apply {
                                put(server.id, server)
                            }
                        }
                    }
                    generalViewModel.loaded(
                        "Modify status of MCP Server #${toolMaker.name} in Studio #${toolAgent.name}",it.code,it.message
                    )
                    val resp = AIPortServiceResponse<AIPortToolMaker>()
                    resp.code = it.code
                    resp.message = it.message
                    resp.data = it.data
                    onResponse(resp)
                } else if(toolMaker.openapi()) getPlatform().modifyOpenAPIServerForStudio(
                    studioId, mcpServerId, serverStatus = status
                ){
                    if(it.code==0) it.data?.let { server ->
                        _openapiServers.update { map ->
                            map.toMutableMap().apply {
                                put(server.id, server)
                            }
                        }
                    }
                    generalViewModel.loaded(
                        "Modify status of OpenAPI Server#${toolMaker.name} in Studio #${toolAgent.name}",it.code,it.message
                    )
                    val resp = AIPortServiceResponse<AIPortToolMaker>()
                    resp.code = it.code
                    resp.message = it.message
                    resp.data = it.data
                    onResponse(resp)
                }
            }else getPlatform().modifyToolMaker(
                mcpServerId,
                null,
                null,
                status
            ){
                if(it.code==0) it.data?.let {
                        maker ->
                    getPlatform().connectToolMakerToStudio(
                        studioId = studioId,
                        makerId = maker.id,
                        agentId = maker.agentId
                    ) {
                        if(it.code==0) it.data?.let { server ->
                            _mcpServers.update { map ->
                                map.toMutableMap().apply {
                                    put(server.id, server)
                                }
                            }
                        }
                        generalViewModel.loaded(
                                "Reconnect Tool Provider #${toolMaker.name} to Studio #${toolAgent.name}",it.code,it.message
                        )
                    }
                }
                generalViewModel.loaded(
                    "Modify status of Tool Provider #${toolMaker.name} in Studio #${toolAgent.name}",it.code,it.message
                )
            }
        }
    }
    suspend fun queryMCPToolsFromStudio(
        toolAgent: AIPortToolAgent,toolMaker: AIPortToolMaker,
        onResponse: (resp: AIPortServiceResponse<List<AIPortTool>>) -> Unit
    ){
        loadMutex.withLock {
            generalViewModel.loading()
            getPlatform().queryMCPToolsFromStudio(toolAgent.engineId,toolMaker.id){
                generalViewModel.loaded(
                    "Query tools of MCP Server #${toolMaker.name} in Studio #${toolAgent.name}",it.code,it.message
                )
                onResponse(it)
            }
        }
    }
    suspend fun publishToolsFromStudio(
        toolAgent: AIPortToolAgent,toolMaker: AIPortToolMaker,
        onResponse: (code: Int, message: String?, data: AIPortToolMaker?) -> Unit
    ){
        loadMutex.withLock {
            generalViewModel.loading()
            if(toolMaker.mcp()) getPlatform().publishMCPToolsFromStudio(toolAgent.engineId,toolMaker.id){
                updateMCPServers(toolMaker,it)
                generalViewModel.loaded(
                    "Publish tools of MCP Server #${toolMaker.name} from Studio #${toolAgent.name}",it.code,it.message
                )
                onResponse(it.code,it.message,it.data)
            } else getPlatform().publishOpenAPIToolsFromStudio(toolAgent.engineId,toolMaker.id){
                updateOpenAPIServers(toolMaker,it)
                generalViewModel.loaded(
                    "Publish tools of OpenAPI Server #${toolMaker.name} from Studio #${toolAgent.name}",it.code,it.message
                )
                onResponse(it.code,it.message,it.data)
            }
        }
    }

    suspend fun connectToolMakerToStudio(
        toolAgent: AIPortToolAgent,
        toolMaker: AIPortToolMaker,
        onResponse: (code: Int, message: String?, mcpServer: MCPServer?) -> Unit){
        loadMutex.withLock {
            generalViewModel.loading()
            getPlatform().connectToolMakerToStudio(
                toolAgent.engineId, toolMaker.id, toolAgent.id
            ) {
                if(it.code==0) it.data?.let { server ->
                    _mcpServers.update { map ->
                        map.toMutableMap().apply {
                            put(server.id, server)
                        }
                    }
                }
                generalViewModel.loaded(
                    "Reonnect tool maker #${toolMaker.name} to Studio #${toolAgent.name}",it.code,it.message
                )
                onResponse(it.code, it.message, it.data)
            }
        }
    }

    suspend fun connectOpenAPIServerToStudio(
        toolAgent: AIPortToolAgent, config: OpenAPIServerConfig,
        onResponse: ((code: Int, message: String?, toolMaker: AIPortToolMaker?) -> Unit)?=null){
        loadMutex.withLock {
            generalViewModel.loading()
            getPlatform().connectOpenAPIServerToStudio(toolAgent.engineId,config){
                if(it.code==0) it.data?.let { server ->
                    _openapiServers.update { map ->
                        map.toMutableMap().apply {
                            put(server.id, server)
                        }
                    }
                }
                generalViewModel.loaded(
                    "Connect OpenAPI Server #${config.name} to Studio #${toolAgent.name}",it.code,it.message
                )
                if(onResponse!=null){
                    onResponse(it.code,it.message,it.data)
                }
            }
        }
    }

    suspend fun queryOpenAPIToolsFromStudio(
        toolAgent: AIPortToolAgent,toolMaker: AIPortToolMaker,
        onResponse: (resp: AIPortServiceResponse<List<AIPortTool>>) -> Unit
    ){
        loadMutex.withLock {
            generalViewModel.loading()
            getPlatform().queryOpenAPIToolsFromStudio(toolAgent.engineId,toolMaker.id){
                generalViewModel.loaded(
                    "Query tools of OpenAPI Server #${toolMaker.name} in Studio #${toolAgent.name}",it.code,it.message
                )
                onResponse(it)
            }
        }
    }
    suspend fun removeOpenAPIServerFromStudio(
        toolAgent: AIPortToolAgent,toolMaker: AIPortToolMaker
    ){
        loadMutex.withLock {
            generalViewModel.loading()
            getPlatform().removeToolMakerFromStudio(
                toolAgent.engineId,toolMaker.id,toolMaker.type
            ){
                if(it.successful()||it.code== AIPortServiceResponse.TOOL_MAKER_NOT_EXISTS) {
                    _openapiServers.update { map ->
                        map.toMutableMap().apply {
                            remove(toolMaker.id)
                        }
                    }
                }
                generalViewModel.loaded(
                    "Remove OpenAPI Server #${toolMaker.name} from Studio #${toolAgent.name}",it.code,it.message
                )
            }
        }
    }

    suspend fun getOpenAPIServerConfigFromStudio(
        toolAgent: AIPortToolAgent,toolMaker: AIPortToolMaker,
        onResponse: (resp:AIPortServiceResponse<OpenAPIServerConfig>) -> Unit
    ){
        loadMutex.withLock {
            generalViewModel.loading()
            getPlatform().getOpenAPIServerConfigFromStudio(
                toolAgent.engineId,toolMaker.id
            ){
                generalViewModel.loaded(
                    "Get OpenAPI Server Config #${toolMaker.name} from Studio #${toolAgent.name}",it.code,it.message
                )
                onResponse(it)
            }
        }
    }

    suspend fun createToolMakerTemplateForStudio(toolAgent: AIPortToolAgent,name:String,type:Int,config:String,inputs:String){
        loadMutex.withLock {
            generalViewModel.loading()
            getPlatform().createToolMakerTemplateForStudio(toolAgent.engineId,name,type,config,inputs){
                if(it.successful()) it.data?.let{
                    ToolRepository.toolMakerTemplate(it)
                }
                generalViewModel.loaded(
                    "Create ToolMaker Template #${name} from Studio #${toolAgent.name}",it.code,it.message
                )
            }
        }
    }

    suspend fun modifyToolMakerTemplateForStudio(
        toolAgent: AIPortToolAgent,template: AIPortToolMakerTemplate,
        name: String?,type: Int,config:String,inputs:String){
        loadMutex.withLock {
            generalViewModel.loading()
            getPlatform().modifyToolMakerTemplateForStudio(
                toolAgent.engineId,template.id,name,type,config,inputs
            ){
                if(it.successful()) it.data?.let{
                    val newName = name?.trim()
                    template.type = type
                    if(!newName.isNullOrEmpty()){
                        template.name = newName
                    }
                    ToolRepository.toolMakerTemplate(template)
                }
                generalViewModel.loaded(
                    "Modify ToolMaker Template #${template.name} from Studio #${toolAgent.name}",it.code,it.message
                )
            }
        }
    }

    suspend fun connectToolMakerTemplateToStudio(toolAgent: AIPortToolAgent,userId:Long,templateId:Long,name:String,inputs:String){
        loadMutex.withLock {
            generalViewModel.loading()
            getPlatform().connectToolMakerTemplateToStudio(toolAgent.engineId,userId,templateId,name,inputs){
                if(it.successful()) it.data?.let{
                    if(it.mcp()&& it is MCPServer) _mcpServers.update { map ->
                        map.toMutableMap().apply {
                            put(it.id,it)
                        }
                    }else if(it.openapi()&& it is OpenAPIServer) _openapiServers.update { map ->
                        map.toMutableMap().apply {
                            put(it.id,it)
                        }
                    }
                }
                generalViewModel.loaded(
                    "Connect ToolMaker Template #${name} To Studio #${toolAgent.name}",it.code,it.message
                )
            }
        }
    }

    suspend fun getMakerTemplateFromStudio(
        toolAgent: AIPortToolAgent,template: AIPortToolMakerTemplate,
        onResponse: (AIPortServiceResponse<ToolMakerTemplate>) -> Unit){
        loadMutex.withLock {
            generalViewModel.loading()
            getPlatform().getMakerTemplateFromStudio(toolAgent.engineId,template.id,){
                generalViewModel.loaded(
                    "Get ToolMaker Template #${template.name} From Studio #${toolAgent.name}",it.code,it.message
                )
                onResponse(it)
            }
        }
    }
    suspend fun getToolMakerTemplateConfigFromStudio(
        toolAgent: AIPortToolAgent, toolMaker: AIPortToolMaker,
        onResponse: (resp: AIPortServiceResponse<ToolMakerTemplateConfig>) -> Unit){
        loadMutex.withLock {
            generalViewModel.loading()
            getPlatform().getMakerTemplateConfigFromStudio(toolAgent.engineId,toolMaker.id,){
                generalViewModel.loaded(
                    "Get ToolMaker Template Config #${toolMaker.name} From Studio #${toolAgent.name}",it.code,it.message
                )
                onResponse(it)
            }
        }
    }

    suspend fun modifyToolMakerTemplateConfigFromStudio(
        toolAgent: AIPortToolAgent, toolMaker: AIPortToolMaker,inputs: String,
        onResponse: (code: Int, message: String?, data: AIPortToolMaker?) -> Unit){
        loadMutex.withLock {
            generalViewModel.loading()
            getPlatform().modifyToolMakerTemplateConfigFromStudio(
                toolAgent.engineId,toolMaker.id,inputs
            ){
                generalViewModel.loaded(
                    "Modify ToolMaker Template Config #${toolMaker.name} From Studio #${toolAgent.name}",it.code,it.message
                )
                onResponse(it.code,it.message,it.data)
            }
        }
    }
    suspend fun getToolFromStudio(
        toolAgent: AIPortToolAgent, toolMaker: AIPortToolMaker, tool: AIPortTool,
        onResponse: (AIPortServiceResponse<AIPortTool>) -> Unit){
        loadMutex.withLock {
            generalViewModel.loading()
            getPlatform().getToolFromStudio(
                toolAgent.engineId,toolMaker.id,toolMaker.type, tool.name
            ){
                generalViewModel.loaded(
                    "Get Tool #${tool.name} From Studio #${toolAgent.name}",it.code,it.message
                )
                onResponse(it)
            }
        }
    }
    suspend fun checkMCPServerRTMFromStudio(
        toolAgent: AIPortToolAgent, rtm:String,
        onResponse: (AIPortServiceResponse<String>) -> Unit){
        loadMutex.withLock {
            generalViewModel.loading()
            getPlatform().checkMCPServerRTMFromStudio(
                toolAgent.engineId,rtm
            ){
                generalViewModel.loaded(
                    "Check MCP server RTM #${rtm} From Studio #${toolAgent.name}",it.code,it.message
                )
                onResponse(it)
            }
        }
    }
    suspend fun installMCPServerRTMFromStudio(
        toolAgent: AIPortToolAgent, rtm:String,
        onResponse: (AIPortServiceResponse<String>) -> Unit){
        loadMutex.withLock {
            generalViewModel.loading()
            getPlatform().installMCPServerRTMFromStudio(
                toolAgent.engineId,rtm
            ){
                generalViewModel.loaded(
                    "Install MCP server RTM #${rtm} From Studio #${toolAgent.name}",it.code,it.message
                )
                onResponse(it)
            }
        }
    }
}