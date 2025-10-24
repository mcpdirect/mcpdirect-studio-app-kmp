package ai.mcpdirect.mcpdirectstudioapp

import ai.mcpdirect.studio.app.model.AIPortServiceResponse
import ai.mcpdirect.studio.app.model.MCPServer
import ai.mcpdirect.studio.app.model.MCPServerConfig
import ai.mcpdirect.studio.app.model.account.AIPortAccessKeyCredential
import ai.mcpdirect.studio.app.model.account.AIPortTeam
import ai.mcpdirect.studio.app.model.account.AIPortTeamMember
import ai.mcpdirect.studio.app.model.account.AIPortUser
import ai.mcpdirect.studio.app.model.aitool.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.encodeToJsonElement


val adminProvider = "admin.mcpdirect.ai"
val authUsl = "authentication@$adminProvider"
val accountUsl = "account.management@$adminProvider"
val aitoolsUSL = "aitools.management@$adminProvider"
val JSON = Json{
    encodeDefaults = true
    ignoreUnknownKeys = true
}
interface Platform {

    val name: String
    val type: Int
    val currentMilliseconds:Long
    val toolAgentId:Long
    fun pasteFromClipboard():String?
    fun copyToClipboard(text:String)
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
            onResponse(JSON.decodeFromString<AIPortServiceResponse<List<AIPortToolMaker>>>(it))
        }
    }

    fun queryToolAgents(onResponse: (resp: AIPortServiceResponse<List<AIPortToolAgent>>) -> Unit) {
        hstpRequest("$aitoolsUSL/tool_agent/query", mapOf()){
            onResponse(JSON.decodeFromString<AIPortServiceResponse<List<AIPortToolAgent>>>(it))
        }
    }
    fun queryTools(userId:Long?=null,status:Int?=null,agentId:Long?=null,makerId:Long?=null,name:String?=null,
                   onResponse: (resp: AIPortServiceResponse<List<AIPortTool>>) -> Unit) {
        hstpRequest("$aitoolsUSL/tool/query", mapOf(
            "userId" to JsonPrimitive(userId),
            "name" to JsonPrimitive(name),
            "agentId" to JsonPrimitive(agentId),
            "status" to JsonPrimitive(status),
            "makerId" to JsonPrimitive(makerId)
        )){
            onResponse(JSON.decodeFromString<AIPortServiceResponse<List<AIPortTool>>>(it))
        }
    }
    fun queryVirtualTools(makerId:Long,onResponse: (resp: AIPortServiceResponse<List<AIPortVirtualTool>>) -> Unit) {
        hstpRequest("$aitoolsUSL/tool/virtual/query", mapOf(
            "makerId" to JsonPrimitive(makerId)
        )){
            onResponse(JSON.decodeFromString<AIPortServiceResponse<List<AIPortVirtualTool>>>(it))
        }
    }
    fun connectMCPServerToStudio(studioId:Long, configs:Map<String, MCPServerConfig>,
                                 onResponse: (resp: AIPortServiceResponse<List<MCPServer>>) -> Unit){
        hstpRequest("studio.console@$studioId/mcp_server/connect",
            JSON.encodeToString(mapOf("mcpServerConfigs" to configs))) {
            onResponse(JSON.decodeFromString<AIPortServiceResponse<List<MCPServer>>>(it))
        }
    }
    fun configMCPServerForStudio(studioId:Long, mcpServerId:Long, config:MCPServerConfig,
                                 onResponse: (resp: AIPortServiceResponse<MCPServer>) -> Unit){
        hstpRequest("studio.console@$studioId/mcp_server/config",
            JSON.encodeToString(mapOf(
                "mcpServerId" to JsonPrimitive(mcpServerId),
                "mcpServerConfig" to JSON.encodeToJsonElement(config)
            ))) {
            onResponse(JSON.decodeFromString<AIPortServiceResponse<MCPServer>>(it))
        }
    }
    fun queryMCPServersFromStudio(studioId:Long, onResponse: (resp: AIPortServiceResponse<List<MCPServer>>) -> Unit){
        hstpRequest("studio.console@$studioId/mcp_server/query", mapOf()) {
            onResponse(JSON.decodeFromString<AIPortServiceResponse<List<MCPServer>>>(it))
        }
    }
    fun queryMCPToolsFromStudio(studioId:Long, mcpServerId:Long,
                                onResponse: (resp: AIPortServiceResponse<List<AIPortTool>>) -> Unit){
        hstpRequest("studio.console@$studioId/mcp_server/tool/query", mapOf(
            "mcpServerId" to JsonPrimitive(mcpServerId)
        )) {
            onResponse(JSON.decodeFromString<AIPortServiceResponse<List<AIPortTool>>>(it))
        }
    }
    fun publishMCPToolsForStudio(studioId:Long, mcpServerId:Long,
                                onResponse: (resp: AIPortServiceResponse<MCPServer>) -> Unit){
        hstpRequest("studio.console@$studioId/mcp_server/tool/publish", mapOf(
            "mcpServerId" to JsonPrimitive(mcpServerId)
        )) {
            onResponse(JSON.decodeFromString<AIPortServiceResponse<MCPServer>>(it))
        }
    }

    fun queryAccessKeys(onResponse: (resp: AIPortServiceResponse<List<AIPortAccessKeyCredential>>) -> Unit) {
        hstpRequest("$accountUsl/access_key/query", mapOf()){
            onResponse(JSON.decodeFromString<AIPortServiceResponse<List<AIPortAccessKeyCredential>>>(it))
        }
    }
    fun queryToolPermissionMakerSummaries(onResponse: (resp: AIPortServiceResponse<List<AIPortToolPermissionMakerSummary>>) -> Unit) {
        hstpRequest("$aitoolsUSL/tool/permission/maker/summary/query", mapOf()){
            onResponse(JSON.decodeFromString<AIPortServiceResponse<List<AIPortToolPermissionMakerSummary>>>(it))
        }
    }
    fun generateAccessKey(mcpKeyName:String,onResponse: (resp: AIPortServiceResponse<AIPortAccessKeyCredential>) -> Unit) {
        hstpRequest("$accountUsl/access_key/create", mapOf(
            "name" to JsonPrimitive(mcpKeyName)
        )){
            onResponse(JSON.decodeFromString<AIPortServiceResponse<AIPortAccessKeyCredential>>(it))
        }
    }

    fun modifyAccessKey(mcpKeyId:Long,mcpKeyStatus:Int?=null,mcpKeyName:String?=null,onResponse: (resp: AIPortServiceResponse<AIPortAccessKeyCredential>) -> Unit) {
        hstpRequest(
            "$accountUsl/access_key/modify", mapOf(
                "id" to JsonPrimitive(mcpKeyId),
                "name" to JsonPrimitive(mcpKeyName),
                "status" to JsonPrimitive(mcpKeyStatus),
            )
        ) {
            onResponse(JSON.decodeFromString<AIPortServiceResponse<AIPortAccessKeyCredential>>(it))
        }
    }

    fun queryTeams(onResponse: (resp: AIPortServiceResponse<List<AIPortTeam>>) -> Unit) {
        hstpRequest(
            "$accountUsl/team/query", mapOf()
        ) {
            onResponse(JSON.decodeFromString<AIPortServiceResponse<List<AIPortTeam>>>(it))
        }
    }
    fun getTool(toolId:Long,onResponse: (resp: AIPortServiceResponse<AIPortTool>) -> Unit) {
        hstpRequest(
            "$aitoolsUSL/tool/get", mapOf(
                "toolId" to JsonPrimitive(toolId)
            )
        ) {
            onResponse(JSON.decodeFromString<AIPortServiceResponse<AIPortTool>>(it))
        }
    }

    fun grantToolPermission(
        permissions: List<AIPortToolPermission?>,
        virtualPermissions: List<AIPortVirtualToolPermission?>,
        onResponse: (resp: AIPortServiceResponse<List<AIPortToolPermissionMakerSummary>>) -> Unit
    ) {
        hstpRequest(
            "$aitoolsUSL/tool/get", mapOf(
                "permissions" to JSON.encodeToJsonElement(permissions),
                "virtualPermissions" to JSON.encodeToJsonElement(virtualPermissions)
            )
        ) {
            onResponse(JSON.decodeFromString<AIPortServiceResponse<List<AIPortToolPermissionMakerSummary>>>(it))
        }
    }
    fun queryToolPermissions(
        accessKeyId:Long,
        onResponse: (resp: AIPortServiceResponse<List<AIPortToolPermission>>) -> Unit
    ) {
        hstpRequest(
            "$aitoolsUSL/tool/permission/query", mapOf(
                "accessKeyId" to JsonPrimitive(accessKeyId)
            )
        ) {
            onResponse(JSON.decodeFromString<AIPortServiceResponse<List<AIPortToolPermission>>>(it))
        }
    }
    fun queryVirtualToolPermissions(
        accessKeyId:Long,
        onResponse: (resp: AIPortServiceResponse<List<AIPortVirtualToolPermission>>) -> Unit
    ) {
        hstpRequest(
            "$aitoolsUSL/tool/virtual/permission/query", mapOf(
                "accessKeyId" to JsonPrimitive(accessKeyId)
            )
        ) {
            onResponse(JSON.decodeFromString<AIPortServiceResponse<List<AIPortVirtualToolPermission>>>(it))
        }
    }

    fun createTeam(mcpTeamName: String,
                   onResponse: (resp: AIPortServiceResponse<AIPortTeam>) -> Unit
    ) {
        hstpRequest(
            "$accountUsl/team/create", mapOf(
                "name" to JsonPrimitive(mcpTeamName)
            )
        ) {
            onResponse(JSON.decodeFromString<AIPortServiceResponse<AIPortTeam>>(it))
        }
    }
    fun modifyTeam(teamId: Long, name: String?, status: Int?,
                   onResponse: (resp: AIPortServiceResponse<AIPortTeam>) -> Unit
    ) {
        hstpRequest(
            "$accountUsl/team/modify", mapOf(
                "teamId" to JsonPrimitive(teamId),
                "name" to JsonPrimitive(name),
                "status" to JsonPrimitive(status)
            )
        ) {
            onResponse(JSON.decodeFromString<AIPortServiceResponse<AIPortTeam>>(it))
        }
    }
    fun inviteTeamMember(teamId: Long, account: String?,
                         onResponse: (resp: AIPortServiceResponse<AIPortTeamMember>) -> Unit
    ) {
        hstpRequest(
            "$accountUsl/team/member/invite", mapOf(
                "teamId" to JsonPrimitive(teamId),
                "account" to JsonPrimitive(account),
            )
        ) {
            onResponse(JSON.decodeFromString<AIPortServiceResponse<AIPortTeamMember>>(it))
        }
    }
    fun queryTeamMembers(teamId: Long,
                         onResponse: (resp: AIPortServiceResponse<List<AIPortTeamMember>>) -> Unit
    ) {
        hstpRequest(
            "$accountUsl/team/member/query", mapOf(
                "teamId" to JsonPrimitive(teamId),
            )
        ) {
            onResponse(JSON.decodeFromString<AIPortServiceResponse<List<AIPortTeamMember>>>(it))
        }
    }
    fun acceptTeamMember(teamId: Long, memberId: Long,
                         onResponse: (resp: AIPortServiceResponse<AIPortTeamMember>) -> Unit
    ) {
        hstpRequest(
            "$accountUsl/team/member/invite", mapOf(
                "teamId" to JsonPrimitive(teamId),
                "memberId" to JsonPrimitive(memberId),
            )
        ) {
            onResponse(JSON.decodeFromString<AIPortServiceResponse<AIPortTeamMember>>(it))
        }
    }

    fun queryTeamToolMakers(team: AIPortTeam,
                            onResponse: (resp: AIPortServiceResponse<List<AIPortTeamToolMaker>>) -> Unit){
        hstpRequest(
            "$aitoolsUSL/tool_maker/team/query", mapOf(
                "teamId" to JsonPrimitive(team.id),
                "teamOwnerId" to  JsonPrimitive(team.ownerId),
            )
        ) {
            onResponse(JSON.decodeFromString<AIPortServiceResponse<List<AIPortTeamToolMaker>>>(it))
        }
    }
    fun modifyTeamToolMakers(team: AIPortTeam, teamToolMakers: List<AIPortTeamToolMaker>,
                             onResponse: (resp: AIPortServiceResponse<List<AIPortTeamToolMaker>>) -> Unit){
        hstpRequest(
            "$aitoolsUSL/tool_maker/team/modify", mapOf(
                "teamId" to JsonPrimitive(team.id),
                "teamOwnerId" to  JsonPrimitive(team.ownerId),
                "teamToolMakers" to JSON.encodeToJsonElement(teamToolMakers)
            )
        ) {
            onResponse(JSON.decodeFromString<AIPortServiceResponse<List<AIPortTeamToolMaker>>>(it))
        }
    }
}

expect fun getPlatform(): Platform