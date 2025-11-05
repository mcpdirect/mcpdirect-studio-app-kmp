package ai.mcpdirect.studio.app.model.aitool

import kotlinx.serialization.Serializable

@Serializable
class AIPortToolMakerTemplate {
    var id: Long = 0
    var created: Long = 0
    var status: Int = 0
    var type: Int = 0
    var name: String = ""
    var agentId: Long = 0
    var lastUpdated: Long = 0
    var userId: Long = 0
    var config: String = "{}"
    var inputs: String = ""
//    var teamId:Long =0
}