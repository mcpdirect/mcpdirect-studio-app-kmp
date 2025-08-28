package ai.mcpdirect.studio.app.data.model

import ai.mcpdirect.backend.dao.entity.account.AIPortAccessKeyCredential
import java.time.LocalDateTime

data class ToolLogEntry(
    val key: AIPortAccessKeyCredential?,
    val agentName: String,
    val makerName: String,
    val toolName: String,
    val input: Map<String, Any>,
    val output: String,
    val timestamp: LocalDateTime = LocalDateTime.now()
)

data class ToolMaker(
    val name: String,
    var logTimes: Int,
    var lastLogTimestamp: LocalDateTime
)

data class Agent(
    val name: String,
    var logTimes: Int,
    var lastLogTimestamp: LocalDateTime
)
