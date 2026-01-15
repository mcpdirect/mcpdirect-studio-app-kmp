package ai.mcpdirect.studio.app.team

import ai.mcpdirect.studio.app.UIState
//import ai.mcpdirect.studio.app.generalViewModel
import ai.mcpdirect.studio.app.model.account.AIPortTeam
import ai.mcpdirect.studio.app.model.account.AIPortTeamMember
import ai.mcpdirect.studio.app.model.aitool.AIPortTeamToolMakerTemplate
import ai.mcpdirect.studio.app.model.aitool.AIPortToolMaker
import ai.mcpdirect.studio.app.model.repository.TeamRepository
import ai.mcpdirect.studio.app.model.repository.ToolRepository
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

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
//val mcpTeamViewModel = MCPTeamViewModel()
class MCPTeamViewModel : ViewModel(){
    var dialog by mutableStateOf<MCPTeamDialog>(MCPTeamDialog.None)
    var searchQuery by mutableStateOf("")
        private set
//    var uiState by mutableStateOf<UIState>(UIState.Idle)
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

//    var mcpTeam by mutableStateOf<AIPortTeam?>(null)
    private val _mcpTeam = MutableStateFlow(AIPortTeam())
    val mcpTeam : StateFlow<AIPortTeam> = _mcpTeam

    val teams: StateFlow<List<AIPortTeam>> = TeamRepository.teams
        .map { it.values.toList() }      // 转为 List
        .stateIn(
            scope = viewModelScope,      // 或 CoroutineScope(Dispatchers.Main.immediate)
            started = SharingStarted.WhileSubscribed(5000), // 按需启动
            initialValue = emptyList()
        )

    private val _mcpTeamMembers = mutableStateMapOf<Long, AIPortTeamMember>()
    val mcpTeamMembers by derivedStateOf {
        _mcpTeamMembers.values.toList()
    }

    private val _ttms: StateFlow<List<Long>> = combine(
        TeamRepository.teamToolMakers,
        _mcpTeam
    ) { toolsMap, maker ->
        toolsMap.values.filter { tool -> tool.teamId == maker.id }.map{it.toolMakerId}.toList()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    private val _ttmts: StateFlow<List<Long>> = combine(
        TeamRepository.teamToolMakerTemplates,
        _mcpTeam
    ) { toolsMap, maker ->
        toolsMap.values.filter { tool -> tool.teamId == maker.id }.map{it.toolMakerTemplateId}.toList()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val toolMakersFromTeam: StateFlow<List<AIPortToolMaker>> = combine(
        ToolRepository.toolMakers,
        _ttms,
        _ttmts
    ) { toolsMap, makers,templates ->
        toolsMap.values.filter { tool -> tool.id in _ttms.value||tool.templateId in _ttmts.value }.toList()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val teamToolMakerTemplates: StateFlow<List<AIPortTeamToolMakerTemplate>> = combine(
        TeamRepository.teamToolMakerTemplates,
        _mcpTeam
    ) { toolsMap, maker ->
        toolsMap.values.filter { tool -> tool.teamId == maker.id }.toList()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

//    fun reset(){
//        mcpTeam = null
//        _mcpTeamMembers.clear()
//    }
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
    fun setMCPTeam(team: AIPortTeam){
        _mcpTeam.value = team
        if(team.id>Int.MAX_VALUE) {
            viewModelScope.launch {
                TeamRepository.loadTeamToolMakers()
                TeamRepository.loadTeamToolMakerTemplates()
            }
            queryMCPTeamMembers(team)
        }
//        if(team!=null) {
////            queryMCPTeamToolMakers(team)
////            queryMCPTeamToolMakerTemplates(team)
//            generalViewModel.refreshTeamToolMakers()
//            generalViewModel.refreshTeamToolMakerTemplates()
//            queryMCPTeamMembers(team.id)
//        }
    }
    fun createMCPTeam(onResponse: (code:Int,message:String?,data: AIPortTeam?) -> Unit) {
        viewModelScope.launch {
//            getPlatform().createTeam(mcpTeamName){
//                    (code, message, data) ->
//                if(code==0&&data!=null){
//                    generalViewModel.team(data)
//                    mcpTeam = data
//                }
//                onResponse(code,message)
//            }
            TeamRepository.createTeam(mcpTeamName){
                onResponse(it.code,it.message,it.data)
            }
        }
    }
    fun modifyMCPTeam(name:String?,status:Int?,onResponse: (code:Int,message:String?,data: AIPortTeam?) -> Unit){
        viewModelScope.launch {
//            getPlatform().modifyTeam(it.id,name,status){
//                    (code, message, data) ->
//                if(code==0&&data!=null){
//                    generalViewModel.team(data)
//                }
//                onResponse(code,message)
//            }
            TeamRepository.modifyTeam(_mcpTeam.value,name,status,onResponse)
        }
    }
    fun inviteMCPTeamMember(account:String?,onResponse: (code:Int,message:String?,data: AIPortTeamMember?) -> Unit){
        viewModelScope.launch {
//            getPlatform().inviteTeamMember(it.id,account){
//                    (code, message, data) ->
//                if(code==0&&data!=null){
//                    _mcpTeamMembers[data.memberId]=data
//                }
//                onResponse(code,message)
//            }
            TeamRepository.inviteTeamMember(_mcpTeam.value,account){
                code, message, data ->
                if(code==0&&data!=null){
                    _mcpTeamMembers[data.memberId]=data
                }
                onResponse(code,message,data)
            }
        }
    }
    fun queryMCPTeamMembers(team: AIPortTeam){
        viewModelScope.launch {
            _mcpTeamMembers.clear()
//            getPlatform().queryTeamMembers(teamId){
//                    (code, message, data) ->
//                if(code==0&&data!=null){
//                    data.forEach {
//                        _mcpTeamMembers[it.memberId]=it
//                    }
//                }
//            }
            TeamRepository.loadTeamMembers(team){
                    code, message, data ->
                if(code==0&&data!=null){
                    data.forEach {
                        _mcpTeamMembers[it.memberId]=it
                    }
                }
            }
        }
    }
//    fun queryMCPTeamToolMakers(team: AIPortTeam){
//        viewModelScope.launch {
//            _mcpTeamToolMakers.clear()
//            getPlatform().queryTeamToolMakers(team.id){
//                    (code, message, data) ->
//                if(code==0&&data!=null){
//                    data.forEach {
//                        _mcpTeamToolMakers[it.toolMakerId]=it
//                    }
//                }
//            }
//        }
//    }
//    fun queryMCPTeamToolMakerTemplates(team: AIPortTeam){
//        viewModelScope.launch {
//            _mcpTeamToolMakerTemplates.clear()
//            getPlatform().queryTeamToolMakerTemplates(team.id){
//                    (code, message, data) ->
//                if(code==0&&data!=null){
//                    data.forEach {
//                        _mcpTeamToolMakerTemplates[it.toolMakerTemplateId]=it
//                    }
//                }
//            }
//        }
//    }

    fun acceptMCPTeamMember(team: AIPortTeam, memberId:Long){
        viewModelScope.launch {
//            getPlatform().acceptTeamMember(teamId,memberId){
//                    (code, message, data) ->
//                if(code==0&&data!=null){
//                    _mcpTeamMembers[data.memberId]=data
//                }
//            }
            TeamRepository.acceptTeamMember(team,memberId){
                code, message, data ->
                if(code==0&&data!=null){
                    _mcpTeamMembers[data.memberId]=data
                }
            }
        }
    }
}