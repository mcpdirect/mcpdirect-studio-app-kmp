package ai.mcpdirect.studio.app.model.aitool

import kotlinx.serialization.Serializable

@Serializable
class AIPortVirtualTool : AIPortTool() {
    var toolId: Long = 0
    var originalMakerId: Long = 0
    fun copy(): AIPortVirtualTool{
        val tool = AIPortVirtualTool()
        tool.id = id
        tool.toolId = toolId
        tool.makerId = makerId
        tool.originalMakerId = originalMakerId
        tool.status = status
        return tool
    }
}