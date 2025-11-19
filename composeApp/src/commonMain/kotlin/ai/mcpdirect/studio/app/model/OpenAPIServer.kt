package ai.mcpdirect.studio.app.model

import ai.mcpdirect.studio.app.model.aitool.AIPortToolMaker
import kotlinx.serialization.Serializable

@Serializable
class OpenAPIServer: AIPortToolMaker() {
    var url: String? = null

    var securities: Map<String, String>? = null
    var statusMessage: String? = null
}