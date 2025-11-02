package ai.mcpdirect.studio.app.model.aitool

import kotlinx.serialization.Serializable

@Serializable
open class AIPortToolMaker {
    var id: Long = 0
    var created: Long = 0
    var status: Int = 0
    var lastUpdated: Long = 0
    var type: Int = 0
    var name: String = ""
    var tags: String? = ""
    var agentId: Long = 0
    var agentStatus: Int = 0
    var agentName: String = ""
    var userId: Long = 0
    var teamId: Long = 0

    companion object {
        const val TYPE_VIRTUAL = 0
        const val TYPE_MCP = 1000
    }
}