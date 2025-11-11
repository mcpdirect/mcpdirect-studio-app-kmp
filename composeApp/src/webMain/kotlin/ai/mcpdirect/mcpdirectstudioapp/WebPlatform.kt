package ai.mcpdirect.mcpdirectstudioapp

import ai.mcpdirect.studio.app.model.AIPortServiceResponse
import ai.mcpdirect.studio.app.model.account.AIPortUser

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive

@Serializable
private  class AccountDetails {
    var account: String? = null
    var accountKeySeed: String? = null
    var accessToken: String? = null
    var accessTokenType: Int = 0
    var newAccount: Boolean = false
    var userInfo: AIPortUser? = null
}
private var accountDetails : AccountDetails? = null
abstract class WebPlatform : Platform {
    fun accessToken():String{
        return if (accountDetails == null) "" else accountDetails!!.accessToken!!
    }


    override val name: String = "MCPdirect User Platform"
    override val type: Int = 0
    override val toolAgentId: Long = -1
    override fun login(
        account: String,
        password: String,
        onResponse: (resp: AIPortServiceResponse<AIPortUser?>) -> Unit
    ) {

        val hashPassword = sha256(password)
        val milliseconds = currentMilliseconds()
        hstpRequest("$authUsl/login",
            mapOf<String, JsonElement>(
                "account" to JsonPrimitive(account),
                "secretKey" to JsonPrimitive(sha256("$hashPassword$milliseconds")),
                "timestamp" to JsonPrimitive(milliseconds)
            )){
            val resp = JSON.decodeFromString<AIPortServiceResponse<AccountDetails>>(it)
            if(resp.successful()&&resp.data!=null){
                accountDetails = resp.data
            }
            onResponse(AIPortServiceResponse(resp.code,resp.message,accountDetails?.userInfo))
        }
    }

    override fun logout(onResponse: (resp: AIPortServiceResponse<Boolean?>) -> Unit) {
        hstpRequest("$accountUsl/logout", mapOf()){
            val resp = JSON.decodeFromString<AIPortServiceResponse<Boolean?>>(it)
            if(resp.successful()){
                accountDetails = null;
            }
            onResponse(resp)
        }
    }
}