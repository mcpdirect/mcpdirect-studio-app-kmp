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

object ToolRepository {
    private val loadMutex = Mutex()
    private val duration = 10000
    private var _makerLastUpdated = 0L
    private val _toolMakers = MutableStateFlow<Map<Long, AIPortToolMaker>>(emptyMap())
    val toolMakers: StateFlow<Map<Long, AIPortToolMaker>> = _toolMakers
//    private val _virtualToolMakers = MutableStateFlow<Map<Long, AIPortToolMaker>>(emptyMap())
//    val virtualToolMakers: StateFlow<Map<Long, AIPortToolMaker>> = _virtualToolMakers
    private val _toolsLastUpdates = mutableMapOf<Long,Long>()
    private val _tools = MutableStateFlow<Map<Long,AIPortTool>>(emptyMap())
    val tools: StateFlow<Map<Long, AIPortTool>> = _tools
    suspend fun loadTools(userId:Long=0,makerId: Long,lastQuery:Long=currentMilliseconds()) {
        loadMutex.withLock {
            var lastUpdated = _toolsLastUpdates[makerId]?:0L
            if(lastUpdated==0L|| lastQuery-lastUpdated>duration) {
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
                                    if (tool.lastUpdated > lastUpdated)
                                        lastUpdated = tool.lastUpdated
                                }
                            }
                        }
                        _toolsLastUpdates[makerId] = lastUpdated
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

    suspend fun loadToolMakers(lastQuery:Long=currentMilliseconds()){
        loadMutex.withLock {
            if(_makerLastUpdated==0L|| lastQuery-_makerLastUpdated>duration) {
                generalViewModel.loading()
                getPlatform().queryToolMakers(
                    lastUpdated = _makerLastUpdated,
                ) {
                    if (it.successful()) it.data?.let { makers ->
                        _toolMakers.update { map ->
                            map.toMutableMap().apply {
                                for (maker in makers) {
                                    put(maker.id, maker)
                                    if (maker.lastUpdated > _makerLastUpdated)
                                        _makerLastUpdated = maker.lastUpdated
                                }
                            }
                        }
                    }
                    generalViewModel.loading(it.code)
                }
            }
        }
    }
}