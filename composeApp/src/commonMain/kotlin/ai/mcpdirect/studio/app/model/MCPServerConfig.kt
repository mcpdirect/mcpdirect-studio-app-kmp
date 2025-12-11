package ai.mcpdirect.studio.app.model

import kotlinx.serialization.Serializable

@Serializable
open class MCPServerConfig: MCPConfig(){
//    var id:Long = 0
//    var name: String = ""
//    var status:Int = 0
    var transport: Int = 0
    var url: String? = null
    var command: String? = null
    var args: List<String>? = null
    var env: Map<String, String>? = null
//    constructor()
//    constructor(config: AIPortMCPServerConfig){
//        transport = config.transport
//        url = config.url
//        command = config.command
//        config.args?.let{
//            args = JSON.decodeFromString(it)
//        }
//        config.env?.let {
//            env = JSON.decodeFromString(it)
//        }
//    }
}