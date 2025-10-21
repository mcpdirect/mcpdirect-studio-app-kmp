package ai.mcpdirect.studio.app.model.aitool

import kotlinx.serialization.Serializable

@Serializable
data class AIPortToolPermissionMakerSummary(
    var accessKeyId: Long = 0,
    var makerId: Long = 0,
    var count: Int = 0
)