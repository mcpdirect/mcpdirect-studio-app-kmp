package ai.mcpdirect.mcpdirectstudioapp

import ai.mcpdirect.studio.app.model.AIPortServiceResponse
import ai.mcpdirect.studio.app.model.MCPServer
import ai.mcpdirect.studio.app.model.MCPServerConfig
import ai.mcpdirect.studio.app.model.account.AIPortUser
import ai.mcpdirect.studio.app.model.aitool.*
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.encodeToJsonElement


val adminProvider = "admin.mcpdirect.ai"
val authUsl = "authentication@$adminProvider"
val accountUsl = "account.management@$adminProvider"
val aitoolsUSL = "aitools.management@$adminProvider"
val json = Json{
    encodeDefaults = true
    ignoreUnknownKeys = true
}
interface Platform {

    val name: String
    val type: Int
    val currentMilliseconds:Long
    val toolAgentId:Long
    fun pasteFromClipboard():String?
    fun hstpRequest(usl:String, parameters:Map<String, JsonElement>, onResponse:(resp:String)->Unit)
    fun hstpRequest(usl:String, parameters:String, onResponse:(resp:String)->Unit)
    fun login(account:String,password:String,onResponse:(resp: AIPortServiceResponse<AIPortUser?>)->Unit)
    fun logout(onResponse: (resp: AIPortServiceResponse<Boolean?>) -> Unit)

    fun changePassword(
        currentPassword: String,
        confirmPassword: String,
        onResponse: (resp: AIPortServiceResponse<List<AIPortToolMaker>>) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    fun queryToolMakers(type:Int?, name:String?, toolAgentId:Long?, teamId:Long?, lastUpdated:Long,
                                 onResponse: (resp: AIPortServiceResponse<List<AIPortToolMaker>>) -> Unit) {
        val parameters = mutableMapOf<String, JsonElement>()
        type?.let {
            parameters["type"]=JsonPrimitive(type)
        }
        name?.let {
            parameters["name"]=JsonPrimitive(name)
        }
        toolAgentId?.let {
            parameters["toolAgentId"]=JsonPrimitive(toolAgentId)
        }
        teamId?.let {
            parameters["teamId"]=JsonPrimitive(teamId)
        }
        parameters["lastUpdated"]=JsonPrimitive(lastUpdated)
        hstpRequest("$aitoolsUSL/tool_maker/query", parameters){
            onResponse(json.decodeFromString<AIPortServiceResponse<List<AIPortToolMaker>>>(it))
        }
    }

    fun queryToolAgents(onResponse: (resp: AIPortServiceResponse<List<AIPortToolAgent>>) -> Unit) {
        hstpRequest("$aitoolsUSL/tool_agent/query", mapOf()){
            onResponse(json.decodeFromString<AIPortServiceResponse<List<AIPortToolAgent>>>(it))
        }
    }
    fun queryTools(userId:Long?,status:Int?,agentId:Long?,makerId:Long?,name:String?,
                   onResponse: (resp: AIPortServiceResponse<List<AIPortTool>>) -> Unit) {
        hstpRequest("$aitoolsUSL/tool/query", mapOf(
            "userId" to JsonPrimitive(userId),
            "name" to JsonPrimitive(name),
            "agentId" to JsonPrimitive(agentId),
            "status" to JsonPrimitive(status),
            "makerId" to JsonPrimitive(makerId)
        )){
            onResponse(json.decodeFromString<AIPortServiceResponse<List<AIPortTool>>>(it))
        }
    }
    fun queryVirtualTools(makerId:Long,onResponse: (resp: AIPortServiceResponse<List<AIPortVirtualTool>>) -> Unit) {
        hstpRequest("$aitoolsUSL/tool/virtual/query", mapOf(
            "makerId" to JsonPrimitive(makerId)
        )){
            onResponse(json.decodeFromString<AIPortServiceResponse<List<AIPortVirtualTool>>>(it))
        }
    }
    fun connectMCPServerToStudio(studioId:Long, configs:Map<String, MCPServerConfig>,
                                 onResponse: (resp: AIPortServiceResponse<List<MCPServer>>) -> Unit){
        hstpRequest("studio.console@$studioId/mcp_server/connect",
            json.encodeToString(mapOf("mcpServers" to configs))) {
            onResponse(json.decodeFromString<AIPortServiceResponse<List<MCPServer>>>(it))
        }
    }
    fun configMCPServerForStudio(studioId:Long,mcpServerName:String, config:MCPServerConfig,
                                 onResponse: (resp: AIPortServiceResponse<MCPServer>) -> Unit){
        hstpRequest("studio.console@$studioId/mcp_server/config",
            json.encodeToString(mapOf(
                "name" to JsonPrimitive(mcpServerName),
                "config" to json.encodeToJsonElement(config)
            ))) {
            onResponse(json.decodeFromString<AIPortServiceResponse<MCPServer>>(it))
        }
    }
    fun queryMCPServersFromStudio(studioId:Long, onResponse: (resp: AIPortServiceResponse<List<MCPServer>>) -> Unit){
        hstpRequest("studio.console@$studioId/mcp_server/query", mapOf()) {
            onResponse(json.decodeFromString<AIPortServiceResponse<List<MCPServer>>>(it))
        }
    }
    fun queryMCPToolsFromStudio(studioId:Long, makerName:String,
                                onResponse: (resp: AIPortServiceResponse<List<AIPortTool>>) -> Unit){
        hstpRequest("studio.console@$studioId/mcp_server/tool/query", mapOf(
            "makerName" to JsonPrimitive(makerName)
        )) {
            onResponse(json.decodeFromString<AIPortServiceResponse<List<AIPortTool>>>(it))
        }
    }
}

expect fun getPlatform(): Platform