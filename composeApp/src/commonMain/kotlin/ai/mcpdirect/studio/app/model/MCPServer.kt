package ai.mcpdirect.studio.app.model

import ai.mcpdirect.studio.app.model.aitool.AIPortToolMaker
import kotlinx.serialization.Serializable

@Serializable
class MCPServer: AIPortToolMaker(){
    var transport:Int = 0
    var url: String? = null
    var command: String? = null
    var args: List<String>? = null
    var env: Map<String, String>? = null
    var statusMessage:String? = null
}