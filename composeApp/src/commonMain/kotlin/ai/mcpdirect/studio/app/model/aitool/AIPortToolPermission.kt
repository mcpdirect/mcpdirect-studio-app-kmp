package ai.mcpdirect.studio.app.model.aitool

import kotlinx.serialization.Serializable

@Serializable
open class AIPortToolPermission {
    var userId: Long = 0
    var accessKeyId: Long = 0
    var toolId: Long = 0
    var lastUpdated: Long = 0
    var status: Int = 0
    var agentId: Long = 0
    var makerId: Long = 0
    var name: String = ""

    constructor()

    open fun copy(): AIPortToolPermission {
        val p = AIPortToolPermission()
        p.userId = userId
        p.accessKeyId = accessKeyId
        p.toolId = toolId
        p.lastUpdated = lastUpdated
        p.status = status
        p.agentId = agentId
        p.makerId = makerId
        p.name = name
        return p
    }
}