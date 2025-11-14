package ai.mcpdirect.studio.app.model.repository

import ai.mcpdirect.mcpdirectstudioapp.currentMilliseconds
import ai.mcpdirect.mcpdirectstudioapp.getPlatform
import ai.mcpdirect.studio.app.generalViewModel
import ai.mcpdirect.studio.app.model.AIPortServiceResponse
import ai.mcpdirect.studio.app.model.aitool.AIPortTool
import ai.mcpdirect.studio.app.model.aitool.AIPortToolMaker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.collections.set
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
    suspend fun loadTools(userId:Long=0, makerId: Long,force:Boolean=false) {
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
                    generalViewModel.loading(it.code)
                }
            }
        }
    }
    suspend fun loadTool(toolId:Long,lastQuery:Long=currentMilliseconds(),
                         onResponse:(code:Int,message:String?,data: AIPortTool?)->Unit) {
        val tool = _tools.value[toolId]
        loadMutex.withLock {
            if(tool==null||tool.metaData.isEmpty()){
            generalViewModel.loading()
            getPlatform().getTool(toolId) {
                if(it.code== AIPortServiceResponse.SERVICE_SUCCESSFUL)
                it.data?.let { tool ->
                    _tools.update { map ->
                        map.toMutableMap().apply {
                            put(tool.id,tool)
                        }
                    }
                }
                generalViewModel.loading(it.code)
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
                    generalViewModel.loading(it.code)
                }
            }
        }
    }
}