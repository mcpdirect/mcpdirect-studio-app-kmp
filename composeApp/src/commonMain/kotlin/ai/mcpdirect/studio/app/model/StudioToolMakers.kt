package ai.mcpdirect.studio.app.model

import kotlinx.serialization.Serializable

@Serializable
class StudioToolMakers {
    var mcpServers:List<MCPServer>? = null
    var openapiServers:List<OpenAPIServer>? = null
}