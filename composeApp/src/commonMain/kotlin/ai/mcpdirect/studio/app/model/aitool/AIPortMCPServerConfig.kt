package ai.mcpdirect.studio.app.model.aitool

import kotlinx.serialization.Serializable

@Serializable
open class AIPortMCPServerConfig {
    var id: Long = 0
    var transport: Int = 0
    var created: Long = 0
    var url: String = ""
    var command: String = ""
    var args: String = ""
    var env: String = ""
}