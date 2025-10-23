package ai.mcpdirect.mcpdirectstudioapp

import ai.mcpdirect.studio.app.model.AIPortServiceResponse
import ai.mcpdirect.studio.app.model.account.AIPortUser

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive

import kotlin.js.ExperimentalWasmJsInterop
expect fun sha256(value:String):String
expect fun currentMilliseconds():Long

//val adminProvider = "admin.mcpdirect.ai"
//val authUsl = "authentication@$adminProvider"
//val accountUsl = "account.management@$adminProvider"
//val aitoolsUSL = "aitools.management@$adminProvider"
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
@OptIn(ExperimentalWasmJsInterop::class)
abstract class WebPlatform : Platform {
//    abstract fun hstpRequest(usl:String, parameters:Map<String, JsonElement>, onResponse:(resp:String)->Unit)
//    val json = Json{
//        encodeDefaults = true
//        ignoreUnknownKeys = true
//    }

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

//    override fun changePassword(
//        currentPassword: String,
//        confirmPassword: String,
//        onResponse: (resp: AIPortServiceResponse<List<AIPortToolMaker>>) -> Unit
//    ) {
//        TODO("Not yet implemented")
//    }
//
//    override fun queryToolMakers(type:Int?, name:String?, toolAgentId:Long?, teamId:Long?, lastUpdated:Long,
//                                 onResponse: (resp: AIPortServiceResponse<List<AIPortToolMaker>>) -> Unit) {
//        val parameters = mutableMapOf<String, JsonElement>()
//        type?.let {
//            parameters["type"]=JsonPrimitive(type)
//        }
//        name?.let {
//            parameters["name"]=JsonPrimitive(name)
//        }
//        toolAgentId?.let {
//            parameters["toolAgentId"]=JsonPrimitive(toolAgentId)
//        }
//        teamId?.let {
//            parameters["teamId"]=JsonPrimitive(teamId)
//        }
//        parameters["lastUpdated"]=JsonPrimitive(lastUpdated)
//        hstpRequest("$aitoolsUSL/tool_maker/query", parameters){
//            onResponse(json.decodeFromString<AIPortServiceResponse<List<AIPortToolMaker>>>(it))
//        }
//    }
//
//    override fun queryToolAgents(onResponse: (resp: AIPortServiceResponse<List<AIPortToolAgent>>) -> Unit) {
//        hstpRequest("$aitoolsUSL/tool_agent/query", mapOf()){
//            onResponse(json.decodeFromString<AIPortServiceResponse<List<AIPortToolAgent>>>(it))
//        }
//    }
}