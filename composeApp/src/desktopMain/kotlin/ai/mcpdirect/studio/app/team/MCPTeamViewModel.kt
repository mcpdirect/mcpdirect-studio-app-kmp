package ai.mcpdirect.studio.app.team

import ai.mcpdirect.backend.dao.entity.account.AIPortTeam
import ai.mcpdirect.backend.dao.entity.account.AIPortTeamMember
import ai.mcpdirect.backend.dao.entity.aitool.AIPortToolMaker
import ai.mcpdirect.studio.MCPDirectStudio
import ai.mcpdirect.studio.app.UIState
import ai.mcpdirect.studio.app.generalViewModel
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
    object InviteTeamMember : MCPTeamDialog()
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

    var mcpTeam by mutableStateOf<AIPortTeam?>(null)

    private val _mcpTeamMembers = mutableStateMapOf<Long, AIPortTeamMember>()
    val mcpTeamMembers by derivedStateOf {
        _mcpTeamMembers.values.toList()
    }
    fun teamMember(id:Long): AIPortTeamMember?{
        return _mcpTeamMembers[id]
    }

    fun updateSearchQuery(query: String) {
        searchQuery = query
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
    fun setMCPTeam(team: AIPortTeam?){
        mcpTeam = team
        if(team!=null) queryMCPTeamMembers(team.id)
    }
    fun createMCPTeam(onResponse: (code:Int,message:String?) -> Unit) {
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                MCPDirectStudio.createTeam(mcpTeamName){
                    code, message, data ->
                    if(code==0&&data!=null){
                        generalViewModel.team(data)
                        mcpTeam = data
                    }
                    onResponse(code,message)
                }
            }
        }
    }
    fun modifyMCPTeam(name:String?,status:Int?,onResponse: (code:Int,message:String?) -> Unit){
        mcpTeam?.let {
            viewModelScope.launch {
                withContext(Dispatchers.IO){
                    MCPDirectStudio.modifyTeam(it.id,name,status){
                            code, message, data ->
                        if(code==0&&data!=null){
                            generalViewModel.team(data)
                        }
                        onResponse(code,message)
                    }
                }
            }
        }
    }
    fun inviteMCPTeamMember(account:String?,onResponse: (code:Int,message:String?) -> Unit){
        mcpTeam?.let {
            viewModelScope.launch {
                withContext(Dispatchers.IO){
                    MCPDirectStudio.inviteTeamMember(it.id,account){
                            code, message, data ->
                        if(code==0&&data!=null){
                            _mcpTeamMembers[data.memberId]=data
                        }
                        onResponse(code,message)
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
    fun acceptMCPTeamMember(teamId:Long,memberId:Long){
        mcpTeam?.let {
            viewModelScope.launch {
                withContext(Dispatchers.IO){
                    MCPDirectStudio.acceptTeamMember(teamId,memberId){
                            code, message, data ->
                        if(code==0&&data!=null){
                            _mcpTeamMembers[data.memberId]=data
                        }
                    }
                }
            }
        }
    }
}