package ai.mcpdirect.studio.app.model

import ai.mcpdirect.mcpdirectstudioapp.JSON
import ai.mcpdirect.studio.app.model.aitool.AIPortMCPServerConfig
import kotlinx.serialization.Serializable

@Serializable
open class MCPServerConfig{
    var transport: Int = 0
    var url: String? = null
    var command: String? = null
    var args: List<String>? = null
    var env: Map<String, String>? = null
    constructor()
    constructor(config: AIPortMCPServerConfig){
        transport = config.transport
        url = config.url
        command = config.command
        config.args?.let{
            args = JSON.decodeFromString(it)
        }
        config.env?.let {
            env = JSON.decodeFromString(it)
        }
    }
}