package ai.mcpdirect.studio.app

import ai.mcpdirect.mcpdirectstudioapp.getPlatform
import ai.mcpdirect.studio.app.auth.authViewModel
import ai.mcpdirect.studio.app.model.AIPortServiceResponse
import ai.mcpdirect.studio.app.model.account.AIPortTeam
import ai.mcpdirect.studio.app.model.account.AIPortTeamMember
import ai.mcpdirect.studio.app.model.account.AIPortUser
import ai.mcpdirect.studio.app.model.aitool.*
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

val generalViewModel = GeneralViewModel()
class GeneralViewModel() : ViewModel() {
    var loadingProcess by mutableStateOf<Float?>(1.0f)
    fun loading(process: Float?=null){
        loadingProcess = process
    }

    fun loading(code: Int){
        loadingProcess = if(code== AIPortServiceResponse.SERVICE_SUCCESSFUL) 1.0f else -1.0f
    }

    var darkMode by mutableStateOf(false)
    var lastRefreshed = 0;
    var currentScreen by mutableStateOf<Screen>(Screen.Dashboard)
        private set
    fun currentScreen(currentScreen:Screen,currentScreenTitle:String?=null,previousScreen: Screen?=null){
        this.currentScreen = currentScreen
        this.currentScreenTitle = currentScreenTitle
        this.previousScreen = previousScreen
        this.topBarActions = {}
    }
    var currentScreenTitle by mutableStateOf<String?>(null)
    var previousScreen by mutableStateOf<Screen?>(null)
        private set
    fun previousScreen(){
        if(previousScreen!=null){
            currentScreenTitle = null
            currentScreen = previousScreen!!
            previousScreen = null
        }
    }
    var topBarActions by mutableStateOf<@Composable (() -> Unit)>({})
    val snackbarHostState = SnackbarHostState()

    fun showSnackbar(message: String,actionLabel: String? = null,
                     withDismissAction: Boolean = false,) {
        viewModelScope.launch {
            snackbarHostState.showSnackbar(message,actionLabel,withDismissAction)
        }
    }

    val virtualToolAgent = AIPortToolAgent("Virtual MCP",1)
    private val _toolAgents = mutableStateMapOf<Long, AIPortToolAgent>()
    val toolAgents by derivedStateOf {
        _toolAgents.values.toList()
    }
    fun toolAgent(id:Long): AIPortToolAgent?{
        return _toolAgents[id]
    }
    fun toolAgent(id:Long,onResponse:((code:Int,message:String?,data:AIPortToolAgent?) -> Unit)){
        if(id==0L){
            onResponse(0,null,virtualToolAgent)
            return
        }
        val agent = _toolAgents[id]
        if(agent!=null) onResponse(AIPortServiceResponse.SERVICE_SUCCESSFUL,null,agent)
        else viewModelScope.launch {
            getPlatform().getToolAgent(id){
                onResponse(it.code,it.message,it.data)
                if(it.code== AIPortServiceResponse.SERVICE_SUCCESSFUL){
                    it.data?.let {
                        _toolAgents[it.id] = it
                    }
                }
            }
        }
    }
    private val _toolMakers = mutableStateMapOf<Long, AIPortToolMaker>()
    val toolMakers by derivedStateOf {
        _toolMakers.values.toList()
    }
    fun toolMakerExists(toolMakerId: Long):Boolean{
        return _toolMakers.contains(toolMakerId)
    }
    fun toolMaker(id:Long): AIPortToolMaker?{
        return _toolMakers[id]
    }
    fun toolMakers(agent: AIPortToolAgent): List<AIPortToolMaker>{
        return _toolMakers.values.filter {
            if(agent.id==0L) {
                if(!(it.type== AIPortToolMaker.TYPE_VIRTUAL&&it.userId == authViewModel.user.id)){
                    println(it.type)
                    println(it.userId)
                }
                it.type== AIPortToolMaker.TYPE_VIRTUAL&&it.userId == authViewModel.user.id
            }
            else it.agentId==agent.id
        }
    }
    fun toolMakers(team: AIPortTeam): List<AIPortToolMaker>{
        val ttmts =_teamToolMakerTemplates.values.filter { it.teamId==team.id }.map { it.toolMakerTemplateId }.toList()
        val ttms =_teamToolMakers.values.filter { it.teamId==team.id }.map { it.toolMakerId }.toList()

        return _toolMakers.values.filter { it.id in ttms||it.templateId in ttmts}
    }
    private val _teamToolMakers = mutableStateMapOf<AIPortTeamToolMaker.Companion.Key, AIPortTeamToolMaker>()
    val teamToolMakers by derivedStateOf {
        _teamToolMakers.values.toList()
    }

    fun teamToolMaker(teamId: Long,makerId:Long): AIPortTeamToolMaker?{
        return _teamToolMakers[AIPortTeamToolMaker.key(teamId,makerId)]
    }

    // team toolmaker template

    private val _teamToolMakerTemplates = mutableStateMapOf<AIPortTeamToolMakerTemplate.Companion.Key, AIPortTeamToolMakerTemplate>()
    val teamToolMakerTemplates by derivedStateOf {
        _teamToolMakerTemplates.values.toList()
    }
    fun teamToolMakerTemplates(teamId: Long):List<AIPortTeamToolMakerTemplate>{
        return _teamToolMakerTemplates.values.filter { it.teamId==teamId }.toList()
    }

    // toolmaker template
    private val _toolMakerTemplates = mutableStateMapOf<Long, AIPortToolMakerTemplate>()
    val toolMakerTemplates by derivedStateOf {
        _toolMakerTemplates.values.toList()
    }
    fun toolMakerTemplateExists(templateId: Long):Boolean{
        return _toolMakerTemplates.contains(templateId)
    }
    fun toolMakerTemplate(id:Long): AIPortToolMakerTemplate?{
        return _toolMakerTemplates[id]
    }
//    fun toolMakerTemplates(team: AIPortTeam): List<AIPortToolMakerTemplate>{
//        return _teamToolMakerTemplates.values.filter {it.teamId==team.id}
//    }
//    private val _teamToolMakerTemplates = mutableStateMapOf<Long, AIPortToolMakerTemplate>()
//    val teamToolMakerTemplates by derivedStateOf {
//        _teamToolMakerTemplates.values.toList()
//    }


    // tool
    private val _tools = mutableStateMapOf<Long, AIPortTool>()
    private val _virtualTools = mutableStateListOf<AIPortVirtualTool>()

    private val _toolPermissions = mutableStateMapOf<Long, AIPortToolPermission>()
    val toolPermissions by derivedStateOf {
        _toolPermissions.values.toList()
    }
    private val _virtualToolPermissions = mutableStateMapOf<Long, AIPortVirtualToolPermission>()
    val virtualToolPermissions by derivedStateOf {
        _virtualToolPermissions.values.toList()
    }

    private val _teams = mutableStateMapOf<Long, AIPortTeam>()
    val teams by derivedStateOf {
        _teams.values.toList()
    }
    fun teams(template: AIPortToolMakerTemplate):List<AIPortTeam>{
        val teamIds = _teamToolMakerTemplates.values.filter { it.toolMakerTemplateId==template.id }.map { it.teamId }.toList()
        return _teams.values.filter { it.id in teamIds }
    }
    fun team(team: AIPortTeam){
        _teams[team.id]=team
    }
    fun team(id:Long): AIPortTeam?{
        return _teams[id]
    }
//    fun team(memberId:Long,templateId:Long): AIPortTeam?{
//
//        val teamIds = _teamToolMakerTemplates.values.filter { it.toolMakerTemplateId==templateId }.map { it.teamId }.toList()
//
//        return _teams[id]
//    }
    private val _teamMembers = mutableStateMapOf<AIPortTeamMember.Companion.Key, AIPortTeamMember>()
    val teamMembers by derivedStateOf {
        _teamMembers.values.toList()
    }

    fun teamMember(teamId:Long,memberId:Long,onResponse:((code:Int,message:String?,data: AIPortTeamMember?) -> Unit)){
        val member = _teamMembers[AIPortTeamMember.key(teamId,memberId)]
        if(member!=null) onResponse(AIPortServiceResponse.SERVICE_SUCCESSFUL,null,member)
        else viewModelScope.launch {
            getPlatform().getTeamMember(teamId,memberId){
                if(it.code== AIPortServiceResponse.SERVICE_SUCCESSFUL){
                    it.data?.let {
                        _teamMembers[AIPortTeamMember.key(it.teamId,it.memberId)] = it
                    }
                }
                onResponse(it.code,it.message,it.data)
            }
        }
    }

    private val _users = mutableStateMapOf<Long, AIPortUser>()
    fun user(userId:Long,onResponse:((code:Int,message:String?,data: AIPortUser?) -> Unit)) {
        val member = _users[userId]
        if (member != null) onResponse(AIPortServiceResponse.SERVICE_SUCCESSFUL, null, member)
        else viewModelScope.launch {
            getPlatform().getUser(userId) {
                if (it.code == AIPortServiceResponse.SERVICE_SUCCESSFUL) {
                    it.data?.let {
                        _users[it.id] = it
                    }
                }
                onResponse(it.code, it.message, it.data)
            }
        }
    }

    fun reset(){
        _toolAgents.clear()
        _toolMakers.clear()
        _tools.clear()
        _toolPermissions.clear()
        _virtualTools.clear()
        _virtualToolPermissions.clear()
        _teamToolMakers.clear()
//        _teamToolMakerTemplates.clear()
        _teams.clear()
        _teamMembers.clear()
        currentScreen = Screen.Dashboard
        previousScreen = null
    }
    fun refreshTeams(onResponse:((code:Int,message:String?) -> Unit)? = null){
        _teams.clear()
        viewModelScope.launch {
            getPlatform().queryTeams{ (code,message,data)->
                if(code==0&&data!=null){
                    data.forEach {
                        _teams[it.id]=it
                    }
                }
                onResponse?.invoke(code, message)
            }
        }
    }

    fun refreshToolAgents(force:Boolean=false){
        getPlatform().queryToolAgents {
            if(it.successful()){
                it.data?.let {
                    it.forEach {
                        _toolAgents[it.id]=it
                    }
//                    _toolAgents[0] = virtualToolAgent
                }
            }
        }
    }
//    private var toolMakersLastQueried = 0L
    fun refreshToolMakers(force:Boolean=false,
                          type:Int?=null,name:String?=null,toolAgentId:Long?=null, teamId:Long?=null,
                          onResponse:((code:Int,message:String?) -> Unit)? = null){
        getPlatform().queryToolMakers(
            type = type,name=name,toolAgentId=toolAgentId,teamId = teamId,
            lastUpdated = -1,
        ){
            if(it.successful()){
                it.data?.let {
//                    toolMakersLastQueried = getPlatform().currentMilliseconds
                    it.forEach {
//                        if(it.teamId!=0L) _teamToolMakers[it.id]=it
                        _toolMakers[it.id]=it
                    }
                }
            }
            onResponse?.invoke(it.code, it.message)
        }
    }
    fun refreshTeamToolMakers(){
        getPlatform().queryTeamToolMakers{
            if(it.successful()) it.data?.forEach {
                _teamToolMakers[AIPortTeamToolMaker.key(it.teamId,it.toolMakerId)] = it
            }
        }
    }

    fun refreshToolMakerTemplates(){
        getPlatform().queryToolMakerTemplates(
            lastUpdated = -1,
        ){
            if(it.successful()){
                it.data?.let {
//                    toolMakersLastQueried = getPlatform().currentMilliseconds
                    it.forEach {
//                        if(it.teamId!=0L) _teamToolMakerTemplates[it.id]=it
                        _toolMakerTemplates[it.id]=it
                    }
                }
            }
        }
    }
    fun refreshTeamToolMakerTemplates(){
        getPlatform().queryTeamToolMakerTemplates(
            lastUpdated = -1,
        ){
            if(it.successful())it.data?.let {
//                    toolMakersLastQueried = getPlatform().currentMilliseconds
                it.forEach {
//                        if(it.teamId!=0L) _teamToolMakerTemplates[it.id]=it
                    _teamToolMakerTemplates[AIPortTeamToolMakerTemplate.key(it.teamId,it.toolMakerTemplateId)]=it
                }
            }
        }
    }
}