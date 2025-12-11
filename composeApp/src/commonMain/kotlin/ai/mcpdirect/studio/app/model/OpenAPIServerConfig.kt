package ai.mcpdirect.studio.app.model

import kotlinx.serialization.Serializable

@Serializable
class OpenAPIServerConfig: MCPConfig() {
//    var id: Long = 0
//    var name: String = ""
//    var toolPrefix:String =""
//    var status: Int = 1

    var url: String? = null

    var docUri: String? = null

    var doc: String? = null

    var securities: Map<String, String>? = null

}