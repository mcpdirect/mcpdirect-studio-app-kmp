package ai.mcpdirect.studio.app.model

import kotlinx.serialization.Serializable

@Serializable
open class MCPServerConfig{
    var transport: Int = 0
    var url: String? = null
    var command: String? = null
    var args: List<String>? = null
    var env: Map<String, String>? = null
}