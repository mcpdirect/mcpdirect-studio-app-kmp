package ai.mcpdirect.mcpdirectstudioapp

import ai.mcpdirect.studio.MCPDirectStudio
import ai.mcpdirect.studio.app.generalViewModel
//import ai.mcpdirect.studio.app.mcp.connectMCPViewModel
import ai.mcpdirect.studio.app.model.AIPortServiceResponse
import ai.mcpdirect.studio.app.model.MCPServer
import ai.mcpdirect.studio.app.model.OpenAPIServer
import ai.mcpdirect.studio.app.model.account.AIPortUser
import ai.mcpdirect.studio.app.model.aitool.AIPortToolAgent
import ai.mcpdirect.studio.app.model.repository.StudioRepository
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

    override fun onToolAgentNotification(agent: ai.mcpdirect.backend.dao.entity.aitool.AIPortToolAgent?) {
        agent?.let {
            val toolAgent = AIPortToolAgent()
            toolAgent.id = it.id
            toolAgent.name = it.name?:""
            toolAgent.status = it.status
            toolAgent.userId = it.userId
            toolAgent.engineId = it.engineId
            toolAgent.tags = it.tags?:""
            toolAgent.created = it.created
            toolAgent.device = it.device?:""
            toolAgent.deviceId = it.deviceId
            StudioRepository.localToolAgent(toolAgent)
        }
    }

    override fun onToolMakerNotification(server: ai.mcpdirect.backend.dao.entity.aitool.AIPortToolMaker?) {
        server?.let {
            when(it){
                is ai.mcpdirect.studio.dao.entity.MCPServer->{
                    val mcpServer = MCPServer()
                    mcpServer.id = it.id
                    mcpServer.status =  it.status
                    if(it.status>Int.MIN_VALUE) {
                        mcpServer.created = it.created
                        mcpServer.lastUpdated = it.lastUpdated
                        mcpServer.type = it.type
                        mcpServer.name = it.name?:""
                        mcpServer.tags = it.tags
                        mcpServer.agentId = it.agentId
                        mcpServer.userId = it.userId
//                        mcpServer.teamId = it.teamId
                        mcpServer.templateId = it.templateId
                        mcpServer.transport = it.transport
                        mcpServer.url = it.url
                        mcpServer.command = it.command
                        mcpServer.args = it.args
                        mcpServer.env = it.env
                        mcpServer.errorCode = it.errorCode
                        mcpServer.errorMessage = it.errorMessage
                    }
                    StudioRepository.mcpServer(mcpServer)
                }
                is ai.mcpdirect.studio.dao.entity.OpenAPIServer ->{
                    val openapiServer = OpenAPIServer()
                    openapiServer.id = it.id
                    openapiServer.status = it.status
                    if(it.status>Int.MIN_VALUE) {
                        openapiServer.created = it.created
                        openapiServer.lastUpdated = it.lastUpdated
                        openapiServer.type = it.type
                        openapiServer.name = it.name?:""
                        openapiServer.tags = it.tags
                        openapiServer.agentId = it.agentId
                        openapiServer.userId = it.userId
                        openapiServer.templateId = it.templateId
//                        openapiServer.teamId = it.teamId
                        openapiServer.url = it.url
                        openapiServer.securities = it.securities
                        openapiServer.errorCode = it.errorCode
                        openapiServer.errorMessage = it.errorMessage
                        StudioRepository.openapiServer(openapiServer)
                    }
                }
            }
        }
    }

//    override fun onMCPServerNotification(server: ai.mcpdirect.studio.dao.entity.MCPServer?) {
//        server?.let {
//            val mcpServer = MCPServer()
//            mcpServer.id = it.id
//            mcpServer.status =  it.status
//            if(it.status>Int.MIN_VALUE) {
//                mcpServer.created = it.created
//                mcpServer.lastUpdated = it.lastUpdated
//                mcpServer.type = it.type
//                mcpServer.name = it.name?:""
//                mcpServer.tags = it.tags
//                mcpServer.agentId = it.agentId
//                mcpServer.userId = it.userId
//                mcpServer.teamId = it.teamId
//                mcpServer.transport = it.transport
//                mcpServer.url = it.url
//                mcpServer.command = it.command
//                mcpServer.args = it.args
//                mcpServer.env = it.env
//                mcpServer.statusMessage = it.statusMessage()
//            }
//            connectMCPViewModel.updateToolMaker(mcpServer)
//        }
//    }
//
//    override fun onOpenAPIServerNotification(server: ai.mcpdirect.studio.dao.entity.OpenAPIServer?) {
//        server?.let {
//            val openapiServer = OpenAPIServer()
//            openapiServer.id = it.id
//            openapiServer.status = it.status
//            if(it.status>Int.MIN_VALUE) {
//                openapiServer.name = it.name?:""
//                openapiServer.url = it.url
//                openapiServer.securities = it.securities
//                openapiServer.statusMessage = it.statusMessage
//            }
//        }
//    }

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
            MCPDirectStudio.setNotificationHandler(this@JVMPlatform)
            MCPDirectStudio.login(account,password){
                    code, message, data ->
                var user:AIPortUser?=null
                if(code==0) {
                    user = AIPortUser(data.id, data.name, data.language, data.created, data.type,data.account)
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
//        connectMCPViewModel.reset()
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