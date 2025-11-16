package ai.mcpdirect.studio.app.model.repository

import ai.mcpdirect.mcpdirectstudioapp.getPlatform
import ai.mcpdirect.studio.app.generalViewModel
import ai.mcpdirect.studio.app.model.MCPServer
import ai.mcpdirect.studio.app.model.OpenAPIServer
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
    private val _toolMakerLastQueries = mutableMapOf<Long, TimeMark>()
    private val _mcpServer = MutableStateFlow<Map<Long, MCPServer>>(emptyMap())
    val mcpServer: StateFlow<Map<Long, MCPServer>> = _mcpServer

    private val _openapiServerLastQueries = mutableMapOf<Long, TimeMark>()
    private val _openapiServer = MutableStateFlow<Map<Long, OpenAPIServer>>(emptyMap())
    val openapiServer: StateFlow<Map<Long, OpenAPIServer>> = _openapiServer
    suspend fun loadToolMakers(studioId:Long,force: Boolean=false){
        loadMutex.withLock {
            val now = TimeSource.Monotonic.markNow()
            val lastQuery = _toolMakerLastQueries[studioId]
            if(lastQuery==null|| (force&&lastQuery.elapsedNow()> _duration)) {
                generalViewModel.loading()
                getPlatform().queryToolMakersFromStudio(studioId) {
                    if (it.successful()) it.data?.let { toolMakers ->
                        _mcpServer.update { map ->
                            map.toMutableMap().apply {
                                toolMakers.mcpServers?.let{ servers ->
                                    for (server in servers) {
                                        put(server.id, server)
                                    }
                                }
                            }
                        }
                        _openapiServer.update { map ->
                            map.toMutableMap().apply {
                                toolMakers.openapiServers?.let{ servers ->
                                    for (server in servers) {
                                        put(server.id, server)
                                    }
                                }
                            }
                        }
                        _toolMakerLastQueries[studioId] = now
                    }
                    generalViewModel.loading(it.code)
                }
            }
        }
    }
}