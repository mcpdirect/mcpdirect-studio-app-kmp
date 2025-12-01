package ai.mcpdirect.studio.app.model

import kotlinx.serialization.Serializable

@Serializable
class ToolMakerTemplateConfig {
    var id: Long = 0
    var inputs: String? = null
}