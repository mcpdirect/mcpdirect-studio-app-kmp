package ai.mcpdirect.studio.app.model

import kotlinx.serialization.Serializable

@Serializable
open class MCPConfig {
    var id: Long = 0
    var name: String = ""
    var status:Int = 1
}