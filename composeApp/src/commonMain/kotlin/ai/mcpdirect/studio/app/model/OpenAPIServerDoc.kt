package ai.mcpdirect.studio.app.model

import kotlinx.serialization.Serializable

@Serializable
class OpenAPIServerDoc {
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
    @Serializable
    class Path {
        var method: String? = null
        var path: String? = null
    }
    var doc:String? = null
    var servers: MutableList<Server>? = null
    var securities: MutableMap<String, Security>? = null
    var paths: MutableMap<String,Path>? = null
}