package ai.mcpdirect.studio.app.model.aitool

import kotlinx.serialization.Serializable

@Serializable
class AIPortVirtualTool : AIPortTool() {
    var toolId: Long = 0
}