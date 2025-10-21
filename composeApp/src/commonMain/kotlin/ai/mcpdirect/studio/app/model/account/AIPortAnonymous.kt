package ai.mcpdirect.studio.app.model.account

import kotlinx.serialization.Serializable

@Serializable
open class AIPortAnonymous {
    var id: Long = 0
    var created: Long = 0
    var deviceId: String = ""

    constructor()

    constructor(id: Long, created: Long, deviceId: String) {
        this.id = id
        this.created = created
        this.deviceId = deviceId
    }
}