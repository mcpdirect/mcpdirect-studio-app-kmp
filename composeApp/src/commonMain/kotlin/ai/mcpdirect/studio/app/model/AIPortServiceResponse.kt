package ai.mcpdirect.studio.app.model

import kotlinx.serialization.Serializable

@Serializable
data class AIPortServiceResponse<T>(
    var code: Int = SERVICE_FAILED,

    var message: String? = null,

    var data: T? = null
){
    companion object{
        const val SERVICE_SUCCESSFUL: Int = 0
        const val SERVICE_FAILED: Int = 255
        const val SERVICE_NOT_FOUND: Int = 254
        const val SERVICE_UNAVAILABLE: Int = 253
        const val SERVICE_UNAUTHORIZED: Int = 252
        const val SERVICE_TIMEOUT: Int = 251
        const val SERVICE_ERROR: Int = 250

        //auth
        const val ACCOUNT_NOT_EXIST: Int = 1001000
        const val ACCOUNT_EXISTED: Int = 1001001
        const val PASSWORD_INCORRECT: Int = 1001002
        const val SIGN_IN_FAILED: Int = 1001003
        const val ACCOUNT_INCORRECT: Int = 1001004
        const val OTP_EXPIRED: Int = 1001005
        const val OTP_FAILED: Int = 1001006

        const val TEAM_NOT_EXIST: Int = 1002000

        const val TOOL_MAKER_NOT_EXISTS: Int = 1003000
        const val TOOL_MAKER_OCCUPIED: Int = 1003001

        const val OPENAPI_DOC_NOT_EXIST: Int = 2001000
    }
    fun successful(): Boolean {
        return code == SERVICE_SUCCESSFUL
    }
}