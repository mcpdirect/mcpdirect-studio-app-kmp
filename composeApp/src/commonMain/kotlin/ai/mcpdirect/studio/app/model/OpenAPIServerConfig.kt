package ai.mcpdirect.studio.app.model

import kotlinx.serialization.Serializable

@Serializable
class OpenAPIServerConfig {
    val id: Long = 0
    val name: String = ""
    var status: Int = 1

    var url: String? = null

    var docUri: String? = null

    var doc: String? = null

    var securities: Map<String, String>? = null

}