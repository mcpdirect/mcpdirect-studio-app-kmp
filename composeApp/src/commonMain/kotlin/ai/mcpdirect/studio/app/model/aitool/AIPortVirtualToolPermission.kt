package ai.mcpdirect.studio.app.model.aitool

import kotlinx.serialization.Serializable

@Serializable
class AIPortVirtualToolPermission : AIPortToolPermission() {
    var originalToolId: Long = 0

    override fun copy(): AIPortVirtualToolPermission {
        val p = AIPortVirtualToolPermission()
        p.userId = userId
        p.accessKeyId = accessKeyId
        p.toolId = toolId
        p.lastUpdated = lastUpdated
        p.status = status
        p.agentId = agentId
        p.makerId = makerId
        p.name = name
        p.originalToolId = originalToolId
        return p
    }
}