package ai.mcpdirect.studio.app.model.account

import kotlinx.serialization.Serializable
import kotlin.math.max

@Serializable
class AIPortOtp {
    var id: Long = 0
    var expirationDate: Long = 0
    var account: String = ""
    var otp: String = ""

    companion object {
        fun createOtp(id: Long): AIPortOtp {
            val aiOtp = AIPortOtp()
            aiOtp.id = id
            val s = aiOtp.hashCode().toString()
            aiOtp.otp = s.substring(max(0, s.length - 6))
            return aiOtp
        }
    }
}