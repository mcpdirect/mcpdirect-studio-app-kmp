package ai.mcpdirect.mcpdirectstudioapp

import ai.mcpdirect.studio.MCPDirectStudio
import ai.mcpdirect.studio.app.mcp.connectMCPViewModel
import ai.mcpdirect.studio.app.model.AIPortServiceResponse
import ai.mcpdirect.studio.app.model.MCPServer
import ai.mcpdirect.studio.app.model.account.AIPortUser
import ai.mcpdirect.studio.handler.NotificationHandler
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import java.awt.Toolkit
import java.awt.datatransfer.DataFlavor

class JVMPlatform : Platform, NotificationHandler, ViewModel() {
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
    override fun hstpRequest(
        usl: String,
        parameters: Map<String, JsonElement>,
        onResponse: (resp: String) -> Unit
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                MCPDirectStudio.hstpRequest(usl, Json.encodeToString(parameters)) {
                    println(it)
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
        CoroutineScope(Dispatchers.IO).launch {
            MCPDirectStudio.hstpRequest(usl, parameters) {
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
        CoroutineScope(Dispatchers.IO).launch {
            MCPDirectStudio.logout {
                code, message, data ->
                onResponse(AIPortServiceResponse(code,message,data))
            }
        }

//        viewModelScope.launch {
//            withContext(Dispatchers.IO) {
//                hstpRequest("$accountUsl/logout", mapOf()){
//                    onResponse(json.decodeFromString<AIPortServiceResponse<Boolean?>>(it))
//                }
//            }
//        }
    }
}

actual fun getPlatform(): Platform = JVMPlatform()