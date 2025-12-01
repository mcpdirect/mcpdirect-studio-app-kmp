package ai.mcpdirect.studio.app.model

import kotlinx.serialization.Serializable

@Serializable
class ToolMakerTemplate {
    var id: Long = 0
    var type: Int = 0
    var config: String = ""
    var inputs: String = ""
}