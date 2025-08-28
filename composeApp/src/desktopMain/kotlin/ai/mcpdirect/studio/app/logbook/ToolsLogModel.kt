package ai.mcpdirect.studio.app.logbook

import ai.mcpdirect.studio.handler.ToolLogHandler
import ai.mcpdirect.backend.dao.entity.account.AIPortAccessKeyCredential
import kotlinx.coroutines.flow.Flow

// Data Models
//data class ToolLog(
//    val id: Long,
//    val credential: AIPortAccessKeyCredential?,
//    val clientName: String,
//    val makerName: String,
//    val toolName: String,
//    val input: Map<String, Any>,
//    val output: String,
//    val timestamp: Long = System.currentTimeMillis()
//)

data class MakerSummary(
    val name: String,
    val logCount: Int,
    val lastLogTimestamp: Long
)

data class ClientSummary(
    val name: String,
    val logCount: Int,
    val lastLogTimestamp: Long
)

// Repository Interface
interface ToolsLogRepository {
    fun addLog(log: ToolLogHandler.ToolLog)
        // Change return types to Flow for reactive streams
    fun getAllLogs(): List<ToolLogHandler.ToolLog>
    fun getLogsByMaker(makerName: String): Flow<List<ToolLogHandler.ToolLog>>
    fun getLogsByClient(clientName: String): Flow<List<ToolLogHandler.ToolLog>>
//    fun getMakerSummaries(): Flow<List<MakerSummary>>
//    fun getClientSummaries(): Flow<List<ClientSummary>>
//
//    fun searchMakers(query: String): List<MakerSummary>
//    fun searchClients(query: String): List<ClientSummary>
    
//    fun filterLogsByToolName(toolName: String): List<ToolLogHandler.ToolLog>
//    fun filterLogsByClientName(clientName: String): List<ToolLogHandler.ToolLog>
//    fun filterLogsByMakerName(makerName: String): List<ToolLogHandler.ToolLog>
    fun filterLogs(query: String): List<ToolLogHandler.ToolLog>
}