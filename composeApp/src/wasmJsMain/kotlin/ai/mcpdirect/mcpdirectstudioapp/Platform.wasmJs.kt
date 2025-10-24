package ai.mcpdirect.mcpdirectstudioapp
import ai.mcpdirect.studio.app.model.AIPortServiceResponse
import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Int8Array
import org.w3c.fetch.CORS
import org.w3c.fetch.FOLLOW
import org.w3c.fetch.Headers
import org.w3c.fetch.NO_CACHE
import org.w3c.fetch.OMIT
import org.w3c.fetch.RequestCache
import org.w3c.fetch.RequestCredentials
import org.w3c.fetch.RequestInit
import org.w3c.fetch.RequestMode
import org.w3c.fetch.RequestRedirect
import org.w3c.fetch.Response
import kotlin.wasm.unsafe.UnsafeWasmMemoryApi
import kotlin.wasm.unsafe.withScopedMemoryAllocator

external val AI_MCPDIRECT_HSTP_WEBPORT:String
actual external fun sha256(value:String):String
actual external fun currentMilliseconds():Long

@OptIn(ExperimentalWasmJsInterop::class)
class WasmPlatform : WebPlatform() {


    override val language: String
        get() = window.navigator.language
    override fun pasteFromClipboard(): String? {
        return null
    }

    override fun copyToClipboard(text: String) {
    }

    //    override fun login(
//        account: String,
//        password: String,
//        onResponse: (resp: AIPortServiceResponse<AIPortUser?>) -> Unit
//    ) {
//        JsonObject
//
//        val hashPassword = sha256(password)
//        val milliseconds = TimeSource.Monotonic.markNow().toString()
//        println(hashPassword)
//        println(milliseconds)
//        hstpRequest("authentication@admin.mcpdirect.ai/login",
//            mapOf<String, JsonElement>(
//                "account" to JsonPrimitive(account),
//                "passoword" to JsonPrimitive(sha256("$hashPassword$milliseconds"))
//                )){
//            println(it)
//        }
//
//        //        adminProvider = env == null?"admin.mcpdirect.ai":env;
//        //        authenticationServiceAddress="authentication@"+ adminProvider;
//
////                SimpleServiceResponseMessage<AccountDetails> httpResp = HstpHttpClient.hstpRequest(
////                hstpWebport,authenticationServiceAddress+"/login",userDevice,
////                Map.of("account",account,
////                        "secretKey",SHA256.digest(hashedPassword+milliseconds),
////                        "timestamp",milliseconds
//////                        , "userDevice",serviceEngine.getEngineId().hashCode()
////                ), new TypeReference<>(){});
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
//                    response.json().then { json ->
//                        maker.name = json?.unsafeCast<WorldTimeApiResponse>()?.datetime
//                            ?.substringAfter("T")?.substringBefore(".") ?: "🧐"
//                        null
//                    }
//                } else {
//                    println( "🤷 " + response.status)
//                }
//                null
//            }
//            .catch {
//                maker.name = "$it"
//                val resp =
//                    _root_ide_package_.ai.mcpdirect.studio.app.model.AIPortServiceResponse<List<AIPortToolMaker>>()
//                resp.data = listOf(maker)
//                onResponse(resp)
//                println( "🤷 " + it)
//                null
//            }
//
//    }
//
//    fun hstpRequest(usl:String, parameters:Map<String, JsonElement>, onResponse:(resp:String)->Unit){
//
//        println(Json.encodeToString(parameters).toJsString())
////        val requestInit = RequestInit().apply {
////            method = "GET" // Or "POST", "PUT", "DELETE", etc.
////            credentials = RequestCredentials.INCLUDE // Or SAME_ORIGIN, OMIT
////            // Add other properties as needed, e.g.:
////             headers = Json.encodeToString(h)
//////             body = JSON.stringify(myRequestBody)
////        }
//
//        window.fetch(
//            AI_MCPDIRECT_HSTP_WEBPORT,
////            "http://192.168.1.7:8088/login/v1",
//            init = RequestInit().apply {
//                method = "POST"
//                credentials = RequestCredentials.OMIT
//                cache = RequestCache.NO_CACHE
//                mode = RequestMode.CORS
//                headers = Headers().apply {
//                    append("Content-Type" , "application/json")
//                    append("hstp-usl" , usl)
//                }
//                body = Json.encodeToString(parameters).toJsString()
//                redirect = RequestRedirect.FOLLOW
//                referrerPolicy = "strict-origin-when-cross-origin".toJsString()
//            }
//        ).then { response ->
//
//            if (response.ok) {
//                onResponse(response.body.toString())
//            } else {
//                println( "🤷 " + response.status)
//            }
//            null
//        }.catch {
//            val response = AIPortServiceResponse<String?>()
//            response.code = -1;
//            response.message = it.toString()
//            onResponse(Json.encodeToString(response))
//            null
//        }
//    }
    override fun httpRequest(usl: String, parameters: Map<String, JsonElement>, onResponse: (resp: String) -> Unit) {
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
                }
                body = parameters.toJsString()
                redirect = RequestRedirect.FOLLOW
                referrerPolicy = "strict-origin-when-cross-origin".toJsString()
            }
        ).then { response ->

            if (response.ok) {

                response.text().then {
                    null
                }

            } else {
                println( "🤷 " + response.status)
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
actual fun getPlatform(): Platform = WasmPlatform()