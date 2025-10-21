package ai.mcpdirect.mcpdirectstudioapp

import ai.mcpdirect.studio.MCPDirectStudio
import ai.mcpdirect.studio.app.mcp.connectMCPViewModel
import ai.mcpdirect.studio.app.model.AIPortServiceResponse
import ai.mcpdirect.studio.app.model.account.AIPortUser
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor

class JVMPlatform : Platform, ViewModel() {
//    var user:User? = null
    override val name: String = "MCPdirect User Platform"
    override val type: Int = 1
    override val currentMilliseconds: Long
        get() = System.currentTimeMillis()
    override val toolAgentId:Long
        get() = MCPDirectStudio.studioToolAgentId()
    override fun pasteFromClipboard(): String? {
        val clipboard = Toolkit.getDefaultToolkit().systemClipboard
        if (clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor)) {
            return clipboard.getData(DataFlavor.stringFlavor) as String
        }
        return null
    }

    override fun hstpRequest(
        usl: String,
        parameters: Map<String, JsonElement>,
        onResponse: (resp: String) -> Unit
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                MCPDirectStudio.hstpRequest(usl, Json.encodeToString(parameters)) {
                    onResponse(it)
                }
            }
        }
    }
    override fun hstpRequest(
        usl: String,
        parameters: String,
        onResponse: (resp: String) -> Unit
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                MCPDirectStudio.hstpRequest(usl, parameters) {
                    onResponse(it)
                }
            }
        }
    }

    override fun login(
        account: String,
        password: String,
        onResponse: (resp: AIPortServiceResponse<AIPortUser?>) -> Unit
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                MCPDirectStudio.login(account,password){
                    code, message, data ->
                    var user:AIPortUser?=null
                    if(code==0) {
                        user = AIPortUser(data.id, data.name, data.language, data.created, data.type)
                        MCPDirectStudio.setMcpServerNotificationHandler(connectMCPViewModel)
                    }
                    onResponse(AIPortServiceResponse(code, message, user))
                }
            }
        }
    }

    override fun logout(onResponse: (resp: AIPortServiceResponse<Boolean?>) -> Unit) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                hstpRequest("$accountUsl/logout", mapOf()){
                    onResponse(json.decodeFromString<AIPortServiceResponse<Boolean?>>(it))
                }
            }
        }
    }
}

actual fun getPlatform(): Platform = JVMPlatform()