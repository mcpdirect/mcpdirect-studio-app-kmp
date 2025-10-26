package ai.mcpdirect.mcpdirectstudioapp

import ai.mcpdirect.studio.app.model.AIPortServiceResponse
import ai.mcpdirect.studio.app.model.MCPServer
import ai.mcpdirect.studio.app.model.MCPServerConfig
import ai.mcpdirect.studio.app.model.account.*
import ai.mcpdirect.studio.app.model.aitool.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.encodeToJsonElement

expect fun sha256(value:String):String
expect fun currentMilliseconds():Long

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
//    val currentMilliseconds:Long
    val toolAgentId:Long
    val language:String
    fun pasteFromClipboard():String?
    fun copyToClipboard(text:String)
    fun getenv(key:String):String?
    fun httpRequest(usl:String, parameters:Map<String, JsonElement>, onResponse:(resp:String)->Unit)
    fun hstpRequest(usl:String, parameters:Map<String, JsonElement>, onResponse:(resp:String)->Unit)
    fun hstpRequest(usl:String, parameters:String, onResponse:(resp:String)->Unit)
    fun login(account:String,password:String,onResponse:(resp: AIPortServiceResponse<AIPortUser?>)->Unit)
    fun logout(onResponse: (resp: AIPortServiceResponse<Boolean?>) -> Unit)

    fun register(account: String, language: String?=null,
                 onResponse: (resp: AIPortServiceResponse<AIPortOtp>) -> Unit){

        httpRequest("$authUsl/register",mapOf(
            "account" to JsonPrimitive(account),
            "userInfo" to JSON.encodeToJsonElement(mapOf(
                "language" to (language?:this.language)
            ))
        )){
            onResponse(JSON.decodeFromString<AIPortServiceResponse<AIPortOtp>>(it))
        }
    }

    fun register(account: String, name: String,otpId:Long,otp: String,language: String?=null,
                 onResponse: (resp: AIPortServiceResponse<AIPortOtp>) -> Unit){
        httpRequest("$authUsl/register",mapOf(
            "account" to JsonPrimitive(account),
            "otpId" to JsonPrimitive(otpId),
            "otp" to JsonPrimitive(otp),
            "userInfo" to JSON.encodeToJsonElement(mapOf(
                "name" to name,
                "language" to (language?:this.language)
            ))
        )){
            onResponse(JSON.decodeFromString<AIPortServiceResponse<AIPortOtp>>(it))
        }
    }

    fun forgotPassword(account: String,
                       onResponse: (resp: AIPortServiceResponse<AIPortOtp>) -> Unit){
        httpRequest("$authUsl/forgot_password",mapOf(
            "account" to JsonPrimitive(account),
        )){
            onResponse(JSON.decodeFromString<AIPortServiceResponse<AIPortOtp>>(it))
        }
    }

    fun forgotPassword(account: String,otpId:Long, otp: String, password: String,
                       onResponse: (resp: AIPortServiceResponse<AIPortOtp>) -> Unit){
        httpRequest("$authUsl/forgot_password",mapOf(
            "account" to JsonPrimitive(account),
            "otpId" to JsonPrimitive(otpId),
            "otp" to JsonPrimitive(otp),
            "password" to JsonPrimitive(sha256(password))
        )){
            onResponse(JSON.decodeFromString<AIPortServiceResponse<AIPortOtp>>(it))
        }
    }
    fun changePassword(
        currentPassword: String,
        confirmPassword: String,
        onResponse: (resp: AIPortServiceResponse<Boolean>) -> Unit
    ){
        val milliseconds = currentMilliseconds()
        val hashedPassword = sha256(currentPassword)
        hstpRequest("$aitoolsUSL/password/change", mapOf(
            "secretKey" to JsonPrimitive(sha256("$hashedPassword$milliseconds")),
            "password" to JsonPrimitive(confirmPassword),
            "timestamp" to JsonPrimitive(milliseconds)
        )){
            onResponse(JSON.decodeFromString<AIPortServiceResponse<Boolean>>(it))
        }
    }

    fun queryToolMakers(type:Int?=null, name:String?=null, toolAgentId:Long?=null,
                        teamId:Long?=null, lastUpdated:Long=-1,
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
        httpRequest("studio.console@$studioId/mcp_server/connect",
            mapOf("mcpServerConfigs" to JSON.encodeToJsonElement(configs))) {
            onResponse(JSON.decodeFromString<AIPortServiceResponse<List<MCPServer>>>(it))
        }
    }
    fun configMCPServerForStudio(studioId:Long, mcpServerId:Long, config:MCPServerConfig,
                                 onResponse: (resp: AIPortServiceResponse<MCPServer>) -> Unit){
        httpRequest("studio.console@$studioId/mcp_server/config",
            mapOf(
                "mcpServerId" to JsonPrimitive(mcpServerId),
                "mcpServerConfig" to JSON.encodeToJsonElement(config)
            )) {
            onResponse(JSON.decodeFromString<AIPortServiceResponse<MCPServer>>(it))
        }
    }
    fun queryMCPServersFromStudio(studioId:Long, onResponse: (resp: AIPortServiceResponse<List<MCPServer>>) -> Unit){
        httpRequest("studio.console@$studioId/mcp_server/query", mapOf()) {
            onResponse(JSON.decodeFromString<AIPortServiceResponse<List<MCPServer>>>(it))
        }
    }
    fun queryMCPToolsFromStudio(studioId:Long, mcpServerId:Long,
                                onResponse: (resp: AIPortServiceResponse<List<AIPortTool>>) -> Unit){
        httpRequest("studio.console@$studioId/mcp_server/tool/query", mapOf(
            "mcpServerId" to JsonPrimitive(mcpServerId)
        )) {
            onResponse(JSON.decodeFromString<AIPortServiceResponse<List<AIPortTool>>>(it))
        }
    }
    fun publishMCPToolsForStudio(studioId:Long, mcpServerId:Long,
                                onResponse: (resp: AIPortServiceResponse<MCPServer>) -> Unit){
        httpRequest("studio.console@$studioId/mcp_server/tool/publish", mapOf(
            "mcpServerId" to JsonPrimitive(mcpServerId)
        )) {
            onResponse(JSON.decodeFromString<AIPortServiceResponse<MCPServer>>(it))
        }
    }

    fun queryAccessKeys(onResponse: (resp: AIPortServiceResponse<List<AIPortAccessKey>>) -> Unit) {
        hstpRequest("$accountUsl/access_key/query", mapOf()){
            onResponse(JSON.decodeFromString<AIPortServiceResponse<List<AIPortAccessKey>>>(it))
        }
    }
    fun getAccessKeyCredential(keyId:Long,onResponse: (resp: AIPortServiceResponse<AIPortAccessKeyCredential>) -> Unit) {
        hstpRequest("$accountUsl/access_key/credential/get", mapOf(
            "keyId" to JsonPrimitive(keyId)
        )){
            onResponse(JSON.decodeFromString<AIPortServiceResponse<AIPortAccessKeyCredential>>(it))
        }

    }
    fun queryToolPermissionMakerSummaries(onResponse: (resp: AIPortServiceResponse<List<AIPortToolPermissionMakerSummary>>) -> Unit) {
        hstpRequest("$aitoolsUSL/tool/permission/maker/summary/query", mapOf()){
            onResponse(JSON.decodeFromString<AIPortServiceResponse<List<AIPortToolPermissionMakerSummary>>>(it))
        }
    }
    fun generateAccessKey(mcpKeyName:String,onResponse: (resp: AIPortServiceResponse<AIPortAccessKey>) -> Unit) {
        hstpRequest("$accountUsl/access_key/create", mapOf(
            "name" to JsonPrimitive(mcpKeyName)
        )){
            onResponse(JSON.decodeFromString<AIPortServiceResponse<AIPortAccessKey>>(it))
        }
    }

    fun modifyAccessKey(mcpKeyId:Long,mcpKeyStatus:Int?=null,mcpKeyName:String?=null,
                        onResponse: (resp: AIPortServiceResponse<AIPortAccessKey>) -> Unit) {
        hstpRequest(
            "$accountUsl/access_key/modify", mapOf(
                "id" to JsonPrimitive(mcpKeyId),
                "name" to JsonPrimitive(mcpKeyName),
                "status" to JsonPrimitive(mcpKeyStatus),
            )
        ) {
            onResponse(JSON.decodeFromString<AIPortServiceResponse<AIPortAccessKey>>(it))
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
            "$aitoolsUSL/tool/permission/grant", mapOf(
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
            "$accountUsl/team/member/accept", mapOf(
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

    fun modifyVirtualTools(makerId: Long, tools: List<AIPortVirtualTool>,
                           onResponse: (resp: AIPortServiceResponse<List<AIPortVirtualTool>>) -> Unit){
        hstpRequest(
            "$aitoolsUSL/tool/virtual/modify", mapOf(
                "makerId" to JsonPrimitive(makerId),
                "tools" to JSON.encodeToJsonElement(tools)
            )
        ) {
            onResponse(JSON.decodeFromString<AIPortServiceResponse<List<AIPortVirtualTool>>>(it))
        }
    }
    fun createToolMaker(type: Int,name:String,tags:String?=null,
                           onResponse: (resp: AIPortServiceResponse<AIPortToolMaker>) -> Unit){
        hstpRequest(
            "$aitoolsUSL/tool_maker/create", mapOf(
                "name" to JsonPrimitive(name),
                "type" to JsonPrimitive(type),
                "tags" to JsonPrimitive(tags)
            )
        ) {
            onResponse(JSON.decodeFromString<AIPortServiceResponse<AIPortToolMaker>>(it))
        }
    }

    fun modifyToolMaker(makerId: Long, name: String?=null, tags: String?=null, status: Int?=null,
                        onResponse: (resp: AIPortServiceResponse<AIPortToolMaker>) -> Unit){
        hstpRequest(
            "$aitoolsUSL/tool_maker/modify", mapOf(
                "makerId" to JsonPrimitive(makerId),
                "name" to JsonPrimitive(name),
                "tags" to JsonPrimitive(tags),
                "status" to JsonPrimitive(status)
            )
        ) {
            onResponse(JSON.decodeFromString<AIPortServiceResponse<AIPortToolMaker>>(it))
        }
    }
    fun modifyToolAgent(
        agentId: Long,
        name: String?=null,
        tags: String?=null,
        status: Int?=null,
        onResponse: (resp: AIPortServiceResponse<AIPortToolAgent>) -> Unit
    ) {
        hstpRequest(
            "$aitoolsUSL/tool_agent/modify", mapOf(
                "agentId" to JsonPrimitive(agentId),
                "name" to JsonPrimitive(name),
                "tags" to JsonPrimitive(tags),
                "status" to JsonPrimitive(status)
            )
        ) {
            onResponse(JSON.decodeFromString<AIPortServiceResponse<AIPortToolAgent>>(it))
        }
    }
}

expect fun getPlatform(): Platform