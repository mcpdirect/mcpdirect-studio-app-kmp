package ai.mcpdirect.studio.app.model.account

import kotlinx.serialization.Serializable

@Serializable
class AIPortAnonymousCredential : AIPortAnonymous {
    var secretKey: String = ""

    constructor() : super()
    
    constructor(id: Long, created: Long, deviceId: String, secretKey: String) : super(id, created, deviceId) {
        this.secretKey = secretKey
    }
}