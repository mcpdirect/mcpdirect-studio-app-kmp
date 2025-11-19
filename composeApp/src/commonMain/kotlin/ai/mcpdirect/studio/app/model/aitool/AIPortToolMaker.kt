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
    var templateId: Long =0
    fun virtual():Boolean{
        return type == TYPE_VIRTUAL
    }
    fun notVirtual():Boolean{
        return type > TYPE_VIRTUAL
    }
    fun mcp(): Boolean{
        return type == TYPE_MCP
    }
    fun openapi():Boolean{
        return type == TYPE_OPENAPI
    }
    companion object {
        const val TYPE_VIRTUAL = 0
        const val TYPE_OPENAPI = 1
        const val TYPE_MCP = 1000
    }
}