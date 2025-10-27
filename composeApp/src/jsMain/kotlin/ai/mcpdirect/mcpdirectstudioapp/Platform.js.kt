package ai.mcpdirect.mcpdirectstudioapp

import ai.mcpdirect.studio.app.model.AIPortServiceResponse
import kotlinx.browser.window
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import org.w3c.fetch.*

external val AI_MCPDIRECT_HSTP_WEBPORT:String
actual external fun sha256(value:String):String
actual external fun currentMilliseconds():Long
external fun env(value:String):String?

//@Serializable
//private  class AccountDetails {
//    var account: String? = null
//    var accountKeySeed: String? = null
//    var accessToken: String? = null
//    var accessTokenType: Int = 0
//    var newAccount: Boolean = false
//    var userInfo: AIPortUser? = null
//}
@OptIn(ExperimentalWasmJsInterop::class)
class JsPlatform : WebPlatform() {
//    override val currentMilliseconds:Long
//        get() = currentMilliseconds()
    override val language: String
        get() = window.navigator.language
    override fun pasteFromClipboard(): String? {
        return null
    }

    override fun copyToClipboard(text: String) {

    }

    override fun getenv(key: String): String? {
        return env(key)
    }

    //    override fun sha256(value: String): String {
//        return _sha256(value)
//    }
//
//    override fun currentMilliseconds(): Long {
//        return _currentMilliseconds()
//    }
//    val json = Json{
//        encodeDefaults = true
//        ignoreUnknownKeys = true
//    }
//    private var accountDetails : AccountDetails? = null
//
//    override val name: String = "Web with Kotlin/Js"
//
//    override fun login(
//        account: String,
//        password: String,
//        onResponse: (resp: AIPortServiceResponse<AIPortUser?>) -> Unit
//    ) {
//
//        val hashPassword = sha256(password)
//        val milliseconds = currentMilliseconds()
//        println(hashPassword)
//        println(milliseconds)
//        hstpRequest("authentication@admin.mcpdirect.ai/login",
//            mapOf<String, JsonElement>(
//                "account" to JsonPrimitive(account),
//                "secretKey" to JsonPrimitive(sha256("$hashPassword$milliseconds")),
//                "timestamp" to JsonPrimitive(milliseconds)
//            )){
//            val resp = json.decodeFromString<AIPortServiceResponse<AccountDetails>>(it)
//            if(resp.successful()&&resp.data!=null){
//                accountDetails = resp.data
//            }
//            onResponse(AIPortServiceResponse(resp.code,resp.message,accountDetails?.userInfo))
//        }
//    }
//
//    override fun changePassword(
//        currentPassword: String,
//        confirmPassword: String,
//        onResponse: (resp: AIPortServiceResponse<List<AIPortToolMaker>>) -> Unit
//    ) {
//        TODO("Not yet implemented")
//    }
//
//    override fun queryMakers(onResponse: (resp: AIPortServiceResponse<List<AIPortToolMaker>>) -> Unit) {
//        val resp =Json.decodeFromString<AIPortServiceResponse<List<AIPortToolMaker>>>("{}")
//        println(resp)
//        val maker = AIPortToolMaker()
//        maker.name ="maker"
//        window.fetch("https://worldtimeapi.org/api/timezone/Europe/Amsterdam")
//            .then { response ->
//
//                if (response.ok) {
//
//                } else {
//                    println( "🤷 " + response.status)
//                }
//                null
//            }
//            .catch {
//                maker.name = "$it"
//                val resp = AIPortServiceResponse<List<AIPortToolMaker>>()
//                resp.data = listOf(maker)
//                onResponse(resp)
//                println( "🤷 " + it)
//                null
//            }
//
//    }
    override fun httpRequest(usl:String, parameters:Map<String, JsonElement>, onResponse:(resp:String)->Unit){
        hstpRequest(usl,Json.encodeToString(parameters),onResponse)
    }
    override fun hstpRequest(usl:String, parameters:Map<String, JsonElement>, onResponse:(resp:String)->Unit){
        hstpRequest(usl,Json.encodeToString(parameters),onResponse)
    }
    override fun hstpRequest(usl:String, parameters:String, onResponse:(resp:String)->Unit){

        window.fetch(
            AI_MCPDIRECT_HSTP_WEBPORT,
            init = RequestInit().apply {
                method = "POST"
                credentials = RequestCredentials.OMIT
                cache = RequestCache.NO_CACHE
                mode = RequestMode.CORS
                headers = Headers().apply {
                    append("Content-Type" , "application/json")
                    append("hstp-usl" , usl)
                    append("hstp-auth", accessToken())
                    append("mcpdirect-device","browser")
                }
                body = parameters
                redirect = RequestRedirect.FOLLOW
                referrerPolicy = "strict-origin-when-cross-origin".toJsString()
            }
        ).then { response ->

            if (response.ok) {
                response.text().then {
                    onResponse(it)
                }

            } else {
                throw Throwable(response.statusText)
            }
            null
        }.catch {
            val response = AIPortServiceResponse<String?>()
            response.code = -1;
            response.message = it.toString()
            onResponse(Json.encodeToString(response))
            null
        }
    }

}

actual fun getPlatform(): Platform = JsPlatform()