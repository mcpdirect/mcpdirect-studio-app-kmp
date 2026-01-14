package ai.mcpdirect.studio.app.model.aitool

import kotlinx.serialization.Serializable

@Serializable
class AIPortToolAgent {
    var id: Long = 0
    var userId: Long = 0
    var engineId: String = ""
    var appId: Long = 0
    var created: Long = 0
    var deviceId: Long = 0
    var device: String = ""
    var name: String = ""
    var tags: String = ""
    var status: Int = 0
    constructor(){
        id = -1
        status = -1
    }
    
    constructor(id:Long,name: String,status: Int) {
        this.id = id
        this.name = name
        this.status = status
    }
}