package ai.mcpdirect.studio.app.model

import kotlinx.serialization.Serializable

@Serializable
class OpenAPIServerConfig {
    @Serializable
    class Security {
        var description: String? = null
        var key: String? = null
    }
    @Serializable
    class Server {
        var description: String? = null
        var url: String? = null
    }
    var status: Int = 1

    var url: String? = null

    var servers: List<Server>? = null

    var docUri: String? = null

    var doc: String? = null

    var securities: Map<String, Security>? = null

}