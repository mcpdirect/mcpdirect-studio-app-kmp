package ai.mcpdirect.studio.app.model.account

import kotlinx.serialization.Serializable

@Serializable
data class AIPortAccessKey(
    var id: Long = 0,
    var effectiveDate: Long = 0,
    var expirationDate: Long = 0,
    var userId: Long = 0,
    var userRoles: Int = 0,
    var created: Long = 0,
    var status: Int = 0,
    var name: String = "",
    var usage: Int = 0
) {
    companion object {
        const val STATUS_ENABLE = 1
        const val STATUS_DISABLE = 0
    }
}