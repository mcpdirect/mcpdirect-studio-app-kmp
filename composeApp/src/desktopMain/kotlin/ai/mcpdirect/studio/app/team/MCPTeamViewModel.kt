package ai.mcpdirect.studio.app.team

import ai.mcpdirect.backend.dao.entity.account.AIPortTeam
import ai.mcpdirect.backend.dao.entity.account.AIPortTeamMember
import ai.mcpdirect.studio.MCPDirectStudio
import ai.mcpdirect.studio.app.UIState
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed class MCPTeamDialog() {
    object None : MCPTeamDialog()
    object CreateTeam : MCPTeamDialog()
    object EditTeamName : MCPTeamDialog()
}
sealed class MCPTeamNameError() {
    object None : MCPTeamNameError()
    object Invalid : MCPTeamNameError()
    object Duplicate : MCPTeamNameError()
}
val mcpTeamViewModel = MCPTeamViewModel()
class MCPTeamViewModel : ViewModel(){
    var searchQuery by mutableStateOf("")
        private set
    var uiState by mutableStateOf<UIState>(UIState.Idle)
    var mcpTeamName by mutableStateOf("")
        private set
    var mcpTeamNameError by mutableStateOf<MCPTeamNameError>(MCPTeamNameError.None)
        private  set
    val mcpTeamNameErrors by derivedStateOf {
        mcpTeamNameError != MCPTeamNameError.None
    }
    var mcpTeamTag by mutableStateOf("")
        private set
    var mcpTeamTagError by mutableStateOf<MCPTeamNameError>(MCPTeamNameError.None)
        private  set
    val mcpTeamTagErrors by derivedStateOf {
        mcpTeamTagError != MCPTeamNameError.None
    }

    val mcpTeamTags = mutableStateSetOf<String>()
    private val _mcpTeams = mutableStateMapOf<Long, AIPortTeam>()
    val mcpTeams by derivedStateOf {
        _mcpTeams.values.toList()
    }
    var mcpTeam by mutableStateOf<AIPortTeam?>(null)
        private set

    private val _mcpTeamMembers = mutableStateMapOf<Long, AIPortTeamMember>()
    val mcpTeamMembers by derivedStateOf {
        _mcpTeamMembers.values.toList()
    }

    fun updateSearchQuery(query: String) {
        searchQuery = query
    }
    fun queryMCPTeams(){
        uiState = UIState.Loading
        _mcpTeams.clear()
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                MCPDirectStudio.queryTeams(){
                    code,message,data->
                    uiState = UIState.Success
                    if(code==0&&data!=null){
                        data.forEach {
                            _mcpTeams[it.id]=it
                        }
                    }
                }
            }
        }
    }

    fun onMCPTeamNameChange(name: String) {
        mcpTeamNameError = if(name.length > 32||name.isBlank()) MCPTeamNameError.Invalid else MCPTeamNameError.None
        if(name.isBlank())
            mcpTeamName = ""
        else if (!mcpTeamNameErrors) {
            mcpTeamName = name
        }
    }

    fun onMCPTeamTagChange(tag: String){
        if(tag.isBlank()){
            mcpTeamTag = tag
        }else {
            mcpTeamTagError = if (tag.length > 32) MCPTeamNameError.Invalid else MCPTeamNameError.None
            if (!mcpTeamTagErrors) {
                if (tag.contains(",")) {
                    mcpTeamTag = ""
                    tag.split(",").forEach {
                        val text = it.trim()
                        if (text.isNotBlank())
                            mcpTeamTags.add(it.trim())
                    }
                } else mcpTeamTag = tag
            }
        }
    }

    fun removeMCPTeamTag(tag:String){
        mcpTeamTags.remove(tag)
    }
    fun setMCPTeam(team: AIPortTeam){
        mcpTeam = team
        queryMCPTeamMembers(team.id);
    }
    fun createMCPTeam(onSuccess: () -> Unit) {
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                MCPDirectStudio.createTeam(mcpTeamName){
                    code, message, data ->
                    if(code==0&&data!=null){
                        _mcpTeams[data.id]=data
                        mcpTeam = data
                    }
                }
            }
        }
    }
    fun modifyMCPTeam(name:String?,status:Int?,onSuccess: () -> Unit){
        mcpTeam?.let {
            viewModelScope.launch {
                withContext(Dispatchers.IO){
                    MCPDirectStudio.modifyTeam(it.id,name,status){
                            code, message, data ->
                        if(code==0&&data!=null){
                            _mcpTeams[data.id]=data
                        }
                    }
                }
            }
        }
    }
    fun inviteMCPTeamMember(account:String?,onSuccess: () -> Unit){
        mcpTeam?.let {
            viewModelScope.launch {
                withContext(Dispatchers.IO){
                    MCPDirectStudio.inviteTeamMember(it.id,account){
                            code, message, data ->
                        if(code==0&&data!=null){
                            _mcpTeamMembers[data.memberId]=data
                        }
                    }
                }
            }
        }
    }
    fun queryMCPTeamMembers(teamId:Long){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                MCPDirectStudio.queryTeamMembers(teamId){
                        code, message, data ->
                    if(code==0&&data!=null){
                        data.forEach {
                            _mcpTeamMembers[it.memberId]=it
                        }
                    }
                }
            }
        }
    }
}