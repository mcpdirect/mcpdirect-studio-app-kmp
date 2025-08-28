package ai.mcpdirect.studio.app.logbook

import ai.mcpdirect.studio.handler.ToolLogHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex


class ToolsLogRepositoryImpl : ToolsLogRepository {
    private val logs = mutableListOf<ToolLogHandler.ToolLog>()
    private val lock = Mutex()
    private val _allLogs = MutableStateFlow<List<ToolLogHandler.ToolLog>>(emptyList())
    
    override fun getAllLogs(): List<ToolLogHandler.ToolLog> = _allLogs.value
    
    override fun getLogsByMaker(makerName: String): Flow<List<ToolLogHandler.ToolLog>> {
        return _allLogs.map { logs -> logs.filter { it.makerName == makerName } }
    }
    
    override fun getLogsByClient(clientName: String): Flow<List<ToolLogHandler.ToolLog>> {
        return _allLogs.map { logs -> logs.filter { it.clientName == clientName } }
    }
    
//    override fun getMakerSummaries(): Flow<List<MakerSummary>> {
//        return _allLogs.map { logs ->
//            logs.groupBy { it.makerName }
//                .map { (makerName, logs) ->
//                    MakerSummary(
//                        name = makerName,
//                        logCount = logs.size,
//                        lastLogTimestamp = logs.maxOfOrNull { it.timestamp } ?: 0
//                    )
//                }
//        }
//    }
//
//    override fun getClientSummaries(): Flow<List<ClientSummary>> {
//        return _allLogs.map { logs ->
//            logs.groupBy { it.clientName }
//                .map { (agentName, logs) ->
//                    ClientSummary(
//                        name = agentName,
//                        logCount = logs.size,
//                        lastLogTimestamp = logs.maxOfOrNull { it.timestamp } ?: 0
//                    )
//                }
//        }
//    }
    
    override fun addLog(log: ToolLogHandler.ToolLog) {
//        lock.withLock {
            logs.add(0,log)
            _allLogs.value = logs.toList() // Emit new value to the flow
//        }
    }
    
    
//    override fun searchMakers(query: String): List<MakerSummary> {
////        var result;
////        getMakerSummaries()
////            .filter { it.name.contains(query, ignoreCase = true) }.collect { list-> result=list }
////        return result;
//        return listOf()
//    }
//
//    override fun searchClients(query: String): List<ClientSummary> {
////        return getAgentSummaries()
////            .filter { it.name.contains(query, ignoreCase = true) }
//        return listOf()
//    }
//
//    override fun filterLogsByToolName(toolName: String): List<ToolLogHandler.ToolLog> {
//        return logs.filter { it.toolName.contains(toolName, ignoreCase = true) }
//    }
//
//    override fun filterLogsByClientName(agentName: String): List<ToolLogHandler.ToolLog> {
//        return logs.filter { it.clientName.contains(agentName, ignoreCase = true) }
//    }
//
//    override fun filterLogsByMakerName(makerName: String): List<ToolLogHandler.ToolLog> {
//        return logs.filter { it.makerName.contains(makerName, ignoreCase = true) }
//    }

    override fun filterLogs(query: String): List<ToolLogHandler.ToolLog> {
        if(query.isBlank()){
            return logs;
        }else if(query.startsWith("marker?")){
            return logs.filter { it.makerName.contains(query.substring(7), ignoreCase = true) }
        }else if(query.startsWith("client?")){
            return logs.filter { it.clientName.contains(query.substring(8), ignoreCase = true) }
        }else if(query.startsWith("tool?")){
            return logs.filter { it.toolName.contains(query.substring(5), ignoreCase = true) }
        }
        return listOf();
    }
}