package ai.mcpdirect.studio.app.model.account

import kotlinx.serialization.Serializable

@Serializable
data class AIPortUser(
    var id: Long = 0,
    var name: String = "",
    var language: String = "",
    var created: Long = 0,
    var type: Int = 0,
    var account:String = ""
) {
    constructor() : this(0, "", "", 0, 0)
    companion object{
        val ANONYMOUS: Int = Int.MAX_VALUE
    }
}