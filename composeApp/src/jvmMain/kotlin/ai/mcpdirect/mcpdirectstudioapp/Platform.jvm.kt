package ai.mcpdirect.mcpdirectstudioapp

import ai.mcpdirect.studio.MCPDirectStudio
import ai.mcpdirect.studio.app.generalViewModel
import ai.mcpdirect.studio.app.mcp.connectMCPViewModel
import ai.mcpdirect.studio.app.model.AIPortServiceResponse
import ai.mcpdirect.studio.app.model.MCPServer
import ai.mcpdirect.studio.app.model.account.AIPortUser
import ai.mcpdirect.studio.handler.NotificationHandler
import appnet.util.crypto.SHA256
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.StringSelection
import java.util.*


class JVMPlatform : Platform, NotificationHandler{
    override val name: String = "MCPdirect Studio"
    override val type: Int = 1
    override val toolAgentId:Long
        get() = MCPDirectStudio.studioToolAgentId()
    override val language: String
        get() = Locale.getDefault().language
    override fun pasteFromClipboard(): String? {
        val clipboard = Toolkit.getDefaultToolkit().systemClipboard
        if (clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor)) {
            return clipboard.getData(DataFlavor.stringFlavor) as String
        }
        return null
    }

    override fun copyToClipboard(text: String) {
        val clipboard = Toolkit.getDefaultToolkit().systemClipboard
        clipboard.setContents(
            StringSelection(text),
            null)
        generalViewModel.showSnackbar("Text copied to clipboard!")
    }

    override fun getenv(key: String):String? {
        return System.getenv(key)
    }

    override fun onMCPServerNotification(server: ai.mcpdirect.studio.dao.entity.MCPServer?) {
        server?.let {
            val mcpServer = MCPServer()
            mcpServer.id = server.id
            mcpServer.created = server.created
            mcpServer.status =  server.status
            mcpServer.lastUpdated =  server.lastUpdated
            mcpServer.type =  server.type
            mcpServer.name =  server.name
            mcpServer.tags =  server.tags
            mcpServer.agentId = server.agentId
            mcpServer.userId =  server.userId
            mcpServer.teamId =  server.teamId
            mcpServer.transport = server.transport
            mcpServer.url =  server.url
            mcpServer.command = server.command
            mcpServer.args =  server.args
            mcpServer.env =  server.env
            mcpServer.statusMessage = server.statusMessage()
            connectMCPViewModel.updateToolMaker(mcpServer)
        }

    }

    override fun httpRequest(
        usl: String,
        parameters: Map<String, JsonElement>,
        onResponse: (resp: String) -> Unit) {

        CoroutineScope(Dispatchers.IO).launch {
            MCPDirectStudio.httpRequest(usl, Json.encodeToString(parameters)) {
                println("httpRequest")
                println(usl)
                println(it)
                onResponse(it)
            }
        }
    }
    override fun hstpRequest(
        usl: String,
        parameters: Map<String, JsonElement>,
        onResponse: (resp: String) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            MCPDirectStudio.hstpRequest(usl, Json.encodeToString(parameters)) {
                println("hstpRequest")
                println(usl)
                println(it)
                onResponse(it)
            }
        }
    }
    override fun hstpRequest(
        usl: String,
        parameters: String,
        onResponse: (resp: String) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            MCPDirectStudio.hstpRequest(usl, parameters) {
                println("httpRequest1")
                println(usl)
                println(it)
                onResponse(it)
            }
        }
//        viewModelScope.launch {
//            withContext(Dispatchers.IO) {
//                MCPDirectStudio.hstpRequest(usl, parameters) {
//                    onResponse(it)
//                }
//            }
//        }
    }

    override fun login(
        account: String,
        password: String,
        onResponse: (resp: AIPortServiceResponse<AIPortUser?>) -> Unit
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            MCPDirectStudio.login(account,password){
                    code, message, data ->
                var user:AIPortUser?=null
                if(code==0) {
                    user = AIPortUser(data.id, data.name, data.language, data.created, data.type)
                        MCPDirectStudio.setNotificationHandler(this@JVMPlatform)
                }
                onResponse(AIPortServiceResponse(code, message, user))
            }
        }
//        viewModelScope.launch {
//            withContext(Dispatchers.IO){
//                MCPDirectStudio.login(account,password){
//                    code, message, data ->
//                    var user:AIPortUser?=null
//                    if(code==0) {
//                        user = AIPortUser(data.id, data.name, data.language, data.created, data.type)
////                        MCPDirectStudio.setMcpServerNotificationHandler(connectMCPViewModel)
//                    }
//                    onResponse(AIPortServiceResponse(code, message, user))
//                }
//            }
//        }
    }

    override fun logout(onResponse: (resp: AIPortServiceResponse<Boolean?>) -> Unit) {
        connectMCPViewModel.reset()
        CoroutineScope(Dispatchers.IO).launch {
            MCPDirectStudio.logout {
                code, message, data ->
                onResponse(AIPortServiceResponse(code,message,data))
            }
        }
    }
}

actual fun getPlatform(): Platform = JVMPlatform()
actual fun sha256(value: String): String {
    return SHA256.digest(value)
}

actual fun currentMilliseconds(): Long {
    return System.currentTimeMillis()
}