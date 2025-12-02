package ai.mcpdirect.studio.app.model.repository

import ai.mcpdirect.mcpdirectstudioapp.currentMilliseconds
import ai.mcpdirect.mcpdirectstudioapp.getPlatform
import ai.mcpdirect.studio.app.generalViewModel
import ai.mcpdirect.studio.app.model.AIPortServiceResponse
import ai.mcpdirect.studio.app.model.aitool.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeMark
import kotlin.time.TimeSource

object ToolRepository {
    private val loadMutex = Mutex()
    private val _duration = 5.seconds
    private var _makerLastQuery:TimeMark? = null
    private val _toolMakers = MutableStateFlow<Map<Long, AIPortToolMaker>>(emptyMap())
    val toolMakers: StateFlow<Map<Long, AIPortToolMaker>> = _toolMakers

    private var _templateLastQuery:TimeMark? = null
    private val _toolMakerTemplates = MutableStateFlow<Map<Long, AIPortToolMakerTemplate>>(emptyMap())
    val toolMakerTemplates: StateFlow<Map<Long, AIPortToolMakerTemplate>> = _toolMakerTemplates

    private val _toolLastQueries = mutableMapOf<Long, TimeMark>()
    private val _tools = MutableStateFlow<Map<Long,AIPortTool>>(emptyMap())
    val tools: StateFlow<Map<Long, AIPortTool>> = _tools

//    private val _toolPermissionLastQueries = mutableMapOf<Long, TimeMark>()
//    data class ToolPermissionKey(val accessKeyId:Long,val toolId:Long)
//    private val _toolPermissions = MutableStateFlow<Map<ToolPermissionKey, AIPortToolPermission>>(emptyMap())
//    val toolPermissions: StateFlow<Map<ToolPermissionKey, AIPortToolPermission>> = _toolPermissions
//    fun toolPermission(accessKeyId: Long,toolId: Long): AIPortToolPermission?{
//        return _toolPermissions.value[ToolPermissionKey(accessKeyId, toolId )]
//    }

    private val _virtualToolLastQueries = mutableMapOf<Long, TimeMark>()
    private val _virtualTools = MutableStateFlow<Map<Long, AIPortVirtualTool>>(emptyMap())
    val virtualTools: StateFlow<Map<Long, AIPortVirtualTool>> = _virtualTools

//    private val _virtualToolPermissionLastQueries = mutableMapOf<Long, TimeMark>()
//    private val _virtualToolPermissions = MutableStateFlow<Map<ToolPermissionKey, AIPortVirtualToolPermission>>(emptyMap())
//    val virtualToolPermissions: StateFlow<Map<ToolPermissionKey, AIPortVirtualToolPermission>> = _virtualToolPermissions

    fun reset(){
        _makerLastQuery = null
        _toolMakers.update { map ->
            map.toMutableMap().apply {
                clear()
            }
        }
        _toolLastQueries.clear()
        _tools.update { map ->
            map.toMutableMap().apply {
                clear()
            }
        }
    }
    suspend fun loadTools(userId:Long=0, toolMaker: AIPortToolMaker,force:Boolean=false) {
        val makerId = toolMaker.id
        loadMutex.withLock {
            val now = TimeSource.Monotonic.markNow()
            val lastQuery = _toolLastQueries[makerId]
            if(lastQuery==null|| (force&&lastQuery.elapsedNow()>_duration)) {
                generalViewModel.loading()
                getPlatform().queryTools(
                    userId = userId,
                    makerId = makerId,
                    lastUpdated = if(lastQuery==null) 0L else currentMilliseconds()
                ) {
                    if (it.successful()) it.data?.let { tools ->
                        _tools.update { map ->
                            map.toMutableMap().apply {
                                for (tool in tools) {
                                    put(tool.id, tool)
                                }
                            }
                        }
                        _toolLastQueries[makerId] = now
                    }
                    generalViewModel.loaded("Load Tools of #${toolMaker.name}",it.code,it.message)
                }
            }
        }
    }
    suspend fun tool(toolId:Long, onResponse:(AIPortServiceResponse<AIPortTool>)->Unit) {
        val tool = _tools.value[toolId]
        loadMutex.withLock {
            if(tool==null||tool.metaData.isEmpty()){
                generalViewModel.loading()
                getPlatform().getTool(toolId) {
                    if(it.successful()) it.data?.let { tool ->
                        _tools.update { map ->
                            map.toMutableMap().apply {
                                put(tool.id,tool)
                            }
                        }
                    }
                    generalViewModel.loaded("Load Tool",it.code,it.message)
                    onResponse(it)

                }
            }else{
                onResponse( AIPortServiceResponse<AIPortTool>().apply {
                    code = 0
                    data = tool
                })
            }
        }
    }

    suspend fun loadToolMakers(force: Boolean=false){
        loadMutex.withLock {
            val now = TimeSource.Monotonic.markNow()
            if(_makerLastQuery==null|| (force&& _makerLastQuery!!.elapsedNow()>_duration)) {
                generalViewModel.loading()
                getPlatform().queryToolMakers(
                    lastUpdated = if(_makerLastQuery==null) 0L else currentMilliseconds(),
                ) {
                    if (it.successful()) it.data?.let { makers ->
                        _toolMakers.update { map ->
                            map.toMutableMap().apply {
                                for (maker in makers) {
                                    put(maker.id, maker)
                                }
                            }
                        }
                        _makerLastQuery = now
                    }
                    generalViewModel.loaded("Load Tool Makers",it.code,it.message)
                }
            }
        }
    }
    fun toolMakers(toolMakers: List<AIPortToolMaker>) {
        _toolMakers.update { map ->
            map.toMutableMap().apply {
                for (toolMaker in toolMakers) {
                    put(toolMaker.id, toolMaker)
                }
            }
        }
    }
    fun toolMaker(toolMaker: AIPortToolMaker) {
        _toolMakers.update { map ->
            map.toMutableMap().apply {
                put(toolMaker.id, toolMaker)
            }
        }
    }
    fun toolMaker(toolMakerId: Long): AIPortToolMaker? {
        return _toolMakers.value[toolMakerId]
    }
//    suspend fun createMCPServerByTemplate(
//        toolAgentId:Long,template: AIPortToolMakerTemplate, mcpServerName:String,
////        mcpServerConfig: AIPortMCPServerConfig,
//        onResponse:(code:Int, message:String?, data: AIPortToolMaker?) -> Unit) {
//        loadMutex.withLock {
//            generalViewModel.loading()
//            getPlatform().createToolMaker(
//                AIPortToolMaker.TYPE_MCP, mcpServerName,
//                templateId = template.id, userId = UserRepository.me.value.id, agentId = toolAgentId,
//                mcpServerConfig = mcpServerConfig
//            ){
//                if(it.successful()){
//                    it.data?.let { toolMaker ->
//                        _toolMakers.update { map ->
//                            map.toMutableMap().apply {
//                                put(toolMaker.id, toolMaker)
//                            }
//                        }
//                    }
//                }
//                generalViewModel.loaded(
//                    "Create MCP Server #${mcpServerName} by Template #${template.name}",it.code,it.message
//                )
//                onResponse(it.code,it.message,it.data)
//            }
//        }
//    }

    suspend fun modifyToolMakerName(
        toolMaker: AIPortToolMaker,
        toolMakerName:String,
        onResponse:(code:Int, message:String?, toolMaker: AIPortToolMaker?) -> Unit) {
        loadMutex.withLock {
            generalViewModel.loading()
            getPlatform().modifyToolMaker(toolMaker.id, toolMakerName,null,null) {
                if (it.successful()) it.data?.let{
                    _toolMakers.update { map ->
                        map.toMutableMap().apply {
                            put(toolMaker.id, toolMaker)
                        }
                    }
                }
                generalViewModel.loaded(
                    "Modify tool maker name of #${toolMaker.name} to $toolMakerName",it.code,it.message
                )
                onResponse(it.code,it.message,it.data)
            }
        }
    }

//    suspend fun modifyMCPServerConfig(
//        toolMaker: AIPortToolMaker,
//        config:AIPortMCPServerConfig,
//        onResponse:(code:Int, message:String?, toolMaker: AIPortToolMaker?) -> Unit
//    ){
//        loadMutex.withLock {
//            generalViewModel.loading()
//            getPlatform().modifyMCPServerConfig(config){
//                if (it.successful()) it.data?.let{ toolMaker ->
//                    _toolMakers.update { map ->
//                        map.toMutableMap().apply {
//                            put(toolMaker.id, toolMaker)
//                        }
//                    }
//                }
//                generalViewModel.loaded(
//                    "Modify tool maker config of #${toolMaker.name}",it.code,it.message
//                )
//                onResponse(it.code,it.message,it.data)
//            }
//        }
//    }

    suspend fun modifyToolMakerTags(toolMaker: AIPortToolMaker, toolMakerTags:String) {
        loadMutex.withLock {
            generalViewModel.loading()
            getPlatform().modifyToolMaker(toolMaker.id, null, toolMakerTags, null) {
                if (it.successful()) it.data?.let{ toolMaker ->
                    _toolMakers.update { map ->
                        map.toMutableMap().apply {
                            put(toolMaker.id, toolMaker)
                        }
                    }
                }
                generalViewModel.loaded(
                    "Modify tool maker tags of #${toolMaker.name}",it.code,it.message
                )
            }
        }
    }

    suspend fun loadToolPermissions(
        accessKey: AIPortToolAccessKey,force: Boolean=false,
        onResponse: (AIPortServiceResponse<List<AIPortToolPermission>>) -> Unit
    ) {
        loadMutex.withLock {
            generalViewModel.loading()
            getPlatform().queryToolPermissions(accessKey.id){
                generalViewModel.loaded("Load Tool Permissions of #${accessKey.name}",it.code,it.message)
                onResponse(it)
            }
        }
    }

    suspend fun loadVirtualTools(userId:Long=0, toolMaker: AIPortToolMaker, force:Boolean=false) {
        val makerId = toolMaker.id
        loadMutex.withLock {
            val now = TimeSource.Monotonic.markNow()
            val lastQuery = _virtualToolLastQueries[makerId]
            if(lastQuery==null|| (force&&lastQuery.elapsedNow()>_duration)) {
                generalViewModel.loading()
                getPlatform().queryVirtualTools(
                    userId = userId,
                    makerId = makerId,
                    lastUpdated = if(lastQuery==null) 0L else currentMilliseconds()
                ) {
                    if (it.successful()) it.data?.let { tools ->
                        _virtualTools.update { map ->
                            map.toMutableMap().apply {
                                for (tool in tools) {
                                    put(tool.id, tool)
                                }
                            }
                        }
                        _virtualToolLastQueries[makerId] = now
                    }
                    generalViewModel.loaded("Load Virtual Tools of #${toolMaker.name}",it.code,it.message)
                }
            }
        }
    }
    suspend fun loadVirtualToolPermissions(
        accessKey: AIPortToolAccessKey,force: Boolean=false,
        onResponse: (AIPortServiceResponse<List<AIPortVirtualToolPermission>>) -> Unit
    ) {
        loadMutex.withLock {
            generalViewModel.loading()
            getPlatform().queryVirtualToolPermissions(accessKey.id){
                generalViewModel.loaded("Load Tool Permissions of #${accessKey.name}",it.code,it.message)
                onResponse(it)
            }
        }
    }

    suspend fun loadToolMakerTemplates(force: Boolean=false){
        loadMutex.withLock {
            val now = TimeSource.Monotonic.markNow()
            if(_templateLastQuery==null|| (force&& _templateLastQuery!!.elapsedNow()>_duration)) {
                generalViewModel.loading()
                getPlatform().queryToolMakerTemplates(
                    lastUpdated = if(_templateLastQuery==null) 0L else currentMilliseconds()
                ){
                    if(it.successful())it.data?.let { templates ->
                        _toolMakerTemplates.update { map ->
                            map.toMutableMap().apply {
                                templates.forEach {
                                    put(it.id,it)
                                }
                            }
                        }
                        _templateLastQuery = now
                    }
                    generalViewModel.loaded("Load Tool Maker Templates ",it.code,it.message)
                }
            }
        }
    }
    fun toolMakerTemplate(id:Long): AIPortToolMakerTemplate?{
        return _toolMakerTemplates.value[id]
    }
    fun toolMakerTemplate(template: AIPortToolMakerTemplate){
        _toolMakerTemplates.update { map ->
            map.toMutableMap().apply {
                put(template.id,template)
            }
        }
    }

//    suspend fun createToolMakerTemplate(name:String,type:Int,agentId:Long,config:String,inputs:String){
//        loadMutex.withLock {
//            generalViewModel.loading()
//            getPlatform().createToolMakerTemplate(name,type,agentId,config,inputs){
//                if(it.successful()) it.data?.let{
//                    _toolMakerTemplates.update { map ->
//                        map.toMutableMap().apply {
//                            put(it.id,it)
//                        }
//                    }
//                }
//            }
//        }
//    }
}