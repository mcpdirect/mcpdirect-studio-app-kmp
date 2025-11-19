package ai.mcpdirect.studio.app.model.repository

import ai.mcpdirect.mcpdirectstudioapp.currentMilliseconds
import ai.mcpdirect.mcpdirectstudioapp.getPlatform
import ai.mcpdirect.studio.app.auth.authViewModel
import ai.mcpdirect.studio.app.generalViewModel
import ai.mcpdirect.studio.app.model.aitool.AIPortMCPServerConfig
import ai.mcpdirect.studio.app.model.aitool.AIPortTool
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

object ToolRepository {
    private val loadMutex = Mutex()
    private val _duration = 5.seconds
    private var _makerLastQuery:TimeMark? = null
    private val _toolMakers = MutableStateFlow<Map<Long, AIPortToolMaker>>(emptyMap())
    val toolMakers: StateFlow<Map<Long, AIPortToolMaker>> = _toolMakers
//    private val _virtualToolMakers = MutableStateFlow<Map<Long, AIPortToolMaker>>(emptyMap())
//    val virtualToolMakers: StateFlow<Map<Long, AIPortToolMaker>> = _virtualToolMakers
    private val _toolLastQueries = mutableMapOf<Long, TimeMark>()
    private val _tools = MutableStateFlow<Map<Long,AIPortTool>>(emptyMap())
    val tools: StateFlow<Map<Long, AIPortTool>> = _tools
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
    suspend fun tool(toolId:Long, onResponse:(code:Int,message:String?,data: AIPortTool?)->Unit) {
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
                    onResponse(it.code,it.message,it.data)

                }
            }else{
                onResponse(0,null,tool)
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
    suspend fun createMCPServerByTemplate(
        toolAgentId:Long,template: AIPortToolMakerTemplate,
        mcpServerName:String, mcpServerConfig: AIPortMCPServerConfig,
        onResponse:(code:Int, message:String?, data: AIPortToolMaker?) -> Unit) {
        loadMutex.withLock {
            generalViewModel.loading()
            getPlatform().createToolMaker(
                AIPortToolMaker.TYPE_MCP, mcpServerName,
                templateId = template.id, userId = authViewModel.user.id, agentId = toolAgentId,
                mcpServerConfig = mcpServerConfig
            ){
                if(it.successful()){
                    it.data?.let { toolMaker ->
                        _toolMakers.update { map ->
                            map.toMutableMap().apply {
                                put(toolMaker.id, toolMaker)
                            }
                        }
                    }
                }
                generalViewModel.loaded(
                    "Create MCP Server #${mcpServerName} by Template #${template.name}",it.code,it.message
                )
                onResponse(it.code,it.message,it.data)
            }
        }
    }

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

    suspend fun modifyMCPServerConfig(
        toolMaker: AIPortToolMaker,
        config:AIPortMCPServerConfig,
        onResponse:(code:Int, message:String?, toolMaker: AIPortToolMaker?) -> Unit
    ){
        loadMutex.withLock {
            generalViewModel.loading()
            getPlatform().modifyMCPServerConfig(config){
                if (it.successful()) it.data?.let{ toolMaker ->
                    _toolMakers.update { map ->
                        map.toMutableMap().apply {
                            put(toolMaker.id, toolMaker)
                        }
                    }
                }
                generalViewModel.loaded(
                    "Modify tool maker config of #${toolMaker.name}",it.code,it.message
                )
                onResponse(it.code,it.message,it.data)
            }
        }
    }

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
}