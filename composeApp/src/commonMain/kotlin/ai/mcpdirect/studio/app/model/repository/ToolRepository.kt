package ai.mcpdirect.studio.app.model.repository

import ai.mcpdirect.mcpdirectstudioapp.currentMilliseconds
import ai.mcpdirect.mcpdirectstudioapp.getPlatform
import ai.mcpdirect.studio.app.generalViewModel
import ai.mcpdirect.studio.app.model.AIPortServiceResponse
import ai.mcpdirect.studio.app.model.AIPortServiceResponse.Companion.SERVICE_SUCCESSFUL
import ai.mcpdirect.studio.app.model.AIPortServiceResponse.Companion.SQL_DUPLICATE_KEY
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
    private var _makerLastUpdate:Long = 0L
    private val _toolMakers = MutableStateFlow<Map<Long, AIPortToolMaker>>(emptyMap())
    val toolMakers: StateFlow<Map<Long, AIPortToolMaker>> = _toolMakers
    private val _loadToolMakers = MutableStateFlow(false)
    val loadToolMakers: StateFlow<Boolean> = _loadToolMakers

    private var _templateLastQuery:TimeMark? = null
    private var _templateLastUpdate:Long = 0L
    private val _toolMakerTemplates = MutableStateFlow<Map<Long, AIPortToolMakerTemplate>>(emptyMap())
    val toolMakerTemplates: StateFlow<Map<Long, AIPortToolMakerTemplate>> = _toolMakerTemplates

    private val _toolLastQueries = mutableMapOf<Long, TimeMark>()
    private val _toolLastUpdated = mutableMapOf<Long, Long>()
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
            var lastUpdated = _toolLastUpdated[makerId]?:0L
            if(force||lastQuery==null||lastQuery.elapsedNow()>_duration) {
                generalViewModel.loading()
                getPlatform().queryTools(
                    userId = userId,
                    makerId = makerId,
                    lastUpdated = lastUpdated
                ) {
                    if (it.successful()) it.data?.let { tools ->
                        _tools.update { map ->
                            map.toMutableMap().apply {
                                for (tool in tools) {
                                    put(tool.id, tool)
                                    if(tool.lastUpdated>lastUpdated){
                                        lastUpdated = tool.lastUpdated
                                    }
                                }
                            }
                        }
                        _toolLastQueries[makerId] = now
                        _toolLastUpdated[makerId] = lastUpdated
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

    suspend fun loadToolMakers(
        force: Boolean=false,
        onResponse: ((resp: AIPortServiceResponse<List<AIPortToolMaker>>) -> Unit)?=null
    ){
        loadMutex.withLock {
            val now = TimeSource.Monotonic.markNow()
            if(force||_makerLastQuery==null||_makerLastQuery!!.elapsedNow()>_duration) {
                generalViewModel.loading()
                getPlatform().queryToolMakers(
                    lastUpdated = if(_toolMakers.value.isEmpty()) 0L else _makerLastUpdate,
                ) {
                    if (it.successful()) it.data?.let { makers ->
                        _toolMakers.update { map ->
                            map.toMutableMap().apply {
                                for (maker in makers)
                                    //TODO ignore before ToolMakerTemplate completed
                                    if(maker.templateId==0L){
                                        if(maker.status<0) remove(maker.id)
                                        else put(maker.id, maker)
                                        if(maker.lastUpdated>_makerLastUpdate){
                                            _makerLastUpdate = maker.lastUpdated
                                        }
                                    }
                            }
                        }
                        _makerLastQuery = now
                        if(!_loadToolMakers.value) _loadToolMakers.value = true
                    }
                    generalViewModel.loaded("Load Tool Makers",it.code,it.message)
                    onResponse?.invoke(it)
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
    suspend fun createVirtualToolMaker(
        name:String,tags:List<String>?,
        onResponse: (resp: AIPortServiceResponse<AIPortToolMaker>) -> Unit
    ){
        loadMutex.withLock {
            getPlatform().createToolMaker(
                AIPortToolMaker.TYPE_VIRTUAL, name,
                tags?.joinToString()){
                if(it.successful()) it.data?.let { toolMaker ->
                    toolMaker(toolMaker)
                }
                onResponse(it)
            }
        }
    }
    suspend fun modifyVirtualTools(
        toolMaker: AIPortToolMaker,tools:List<AIPortVirtualTool>,
        onResponse: (resp: AIPortServiceResponse<List<AIPortVirtualTool>>) -> Unit
    ) {
        loadMutex.withLock {
            getPlatform().modifyVirtualTools(toolMaker.id,tools){
                if(it.successful()) it.data?.let { data ->
                    _virtualTools.update { map ->
                        map.toMutableMap().apply {
                            for (tool in data) {
                                put(tool.id, tool)
                            }
                        }
                    }
                }
                onResponse(it)
            }
        }
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

    suspend fun modifyToolMaker(
        toolMaker: AIPortToolMaker,
        name:String?=null,
        status:Int?=null,
        tags:String?=null,
        onResponse:(resp: AIPortServiceResponse<AIPortToolMaker>) -> Unit) {
        loadMutex.withLock {
            generalViewModel.loading()
            getPlatform().modifyToolMaker(toolMaker.id, name,tags,status) {
                if (it.successful()) it.data?.let{ data ->
                    _toolMakers.update { map ->
                        map.toMutableMap().apply {
                            put(data.id, data)
                        }
                    }
                }
                val message = when(it.code){
                    SERVICE_SUCCESSFUL -> null
                    SQL_DUPLICATE_KEY -> "MCP server \"${name}\" already exists"
                    else -> if(it.message?.contains("duplicate key") == true) "MCP server \"${name}\" already exists" else it.message
                }

                generalViewModel.loaded(
                    "Modify MCP server \"${toolMaker.name}\"",it.code,message
                )
                onResponse(it)
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
    private fun respondLoadVirtualTools(makerId:Long):AIPortServiceResponse<List<AIPortVirtualTool>>{
        val tools = mutableListOf<AIPortVirtualTool>()
        _virtualTools.value.values.forEach {
            if(it.makerId==makerId) {
                tools.add(it)
            }
        }
        val respData = AIPortServiceResponse<List<AIPortVirtualTool>>()
        respData.code = 0;
        respData.data = tools
        return respData
    }
    suspend fun loadVirtualTools(
        toolMaker: AIPortToolMaker, force:Boolean=false,
        onResponse: ((resp: AIPortServiceResponse<List<AIPortVirtualTool>>) -> Unit)?=null
    ) {
        val makerId = toolMaker.id
        loadMutex.withLock {
            val now = TimeSource.Monotonic.markNow()
            val lastQuery = _virtualToolLastQueries[makerId]
            if(lastQuery==null|| (force&&lastQuery.elapsedNow()>_duration)) {
                generalViewModel.loading()
                getPlatform().queryVirtualTools(
//                    userId = userId,
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
                        onResponse?.let {
                            it(respondLoadVirtualTools(makerId))
                        }
                    } else if(onResponse!=null) onResponse(it)
                    generalViewModel.loaded("Load Virtual Tools of #${toolMaker.name}",it.code,it.message)

                }
            }else onResponse?.let {
                it(respondLoadVirtualTools(makerId))
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
            if(force||_templateLastQuery==null|| _templateLastQuery!!.elapsedNow()>_duration) {
                generalViewModel.loading()
                getPlatform().queryToolMakerTemplates(
                    lastUpdated = _templateLastUpdate
                ){
                    if(it.successful())it.data?.let { templates ->
                        _toolMakerTemplates.update { map ->
                            map.toMutableMap().apply {
                                templates.forEach {
                                    put(it.id,it)
                                    if(it.lastUpdated>_templateLastUpdate){
                                        _templateLastUpdate = it.lastUpdated
                                    }
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