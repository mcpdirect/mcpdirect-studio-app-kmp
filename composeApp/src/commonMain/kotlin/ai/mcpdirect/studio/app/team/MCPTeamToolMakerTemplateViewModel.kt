package ai.mcpdirect.studio.app.team

import ai.mcpdirect.mcpdirectstudioapp.getPlatform
import ai.mcpdirect.studio.app.UIState
import ai.mcpdirect.studio.app.model.account.AIPortTeam
import ai.mcpdirect.studio.app.model.aitool.AIPortTeamToolMakerTemplate
import ai.mcpdirect.studio.app.model.aitool.AIPortToolMakerTemplate
import ai.mcpdirect.studio.app.model.repository.TeamRepository
import ai.mcpdirect.studio.app.model.repository.ToolRepository
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

//val mcpTeamToolMakerTemplateViewModel = MCPTeamToolMakerTemplateViewModel()
class MCPTeamToolMakerTemplateViewModel: ViewModel() {
    var searchQuery by mutableStateOf("")
        private set
//    var uiState by mutableStateOf<UIState>(UIState.Idle)
//    private fun updateUIState(code:Int){
//        uiState = if(code==0) UIState.Success else UIState.Error(code)
//    }
    fun updateSearchQuery(query: String) {
        searchQuery = query
    }
    var toolMakerTemplate by mutableStateOf<AIPortToolMakerTemplate?>(null)
        private set
    fun toolMakerTemplate(template: AIPortToolMakerTemplate?){
        toolMakerTemplate = template
    }
//    private val toolMakerTemplates = mutableStateMapOf<Long, AIPortToolMakerTemplate>()
    private val _teamToolMakerTemplates = mutableStateMapOf<Long, AIPortTeamToolMakerTemplate>()
    val teamToolMakerTemplates = mutableStateMapOf<Long, AIPortTeamToolMakerTemplate>()
    fun reset(){
        _teamToolMakerTemplates.clear()
        teamToolMakerTemplates.clear()
        toolMakerTemplate = null
    }

    val toolMakerTemplates: StateFlow<List<AIPortToolMakerTemplate>> = ToolRepository.toolMakerTemplates
        .map { it.values.toList() }      // 转为 List
        .stateIn(
            scope = viewModelScope,      // 或 CoroutineScope(Dispatchers.Main.immediate)
            started = SharingStarted.WhileSubscribed(5000), // 按需启动
            initialValue = emptyList()
        )

    fun refreshTeamToolMakerTemplates(team: AIPortTeam){
        viewModelScope.launch {
            TeamRepository.loadTeamToolMakerTemplates(team){
                code, message, data ->
                if(code==0) data?.forEach {
                    _teamToolMakerTemplates[it.toolMakerTemplateId]=it
                    teamToolMakerTemplates[it.toolMakerTemplateId] = it.copy()
                }
            }
//            getPlatform().queryTeamToolMakerTemplates(team.id){ (code, message, data) ->
//                if(code==0){
//                    var loadToolMakers = false
//                    data?.forEach {
//                        _teamToolMakerTemplates[it.toolMakerTemplateId]=it
//                        teamToolMakerTemplates[it.toolMakerTemplateId] = it.copy()
////                        if(generalViewModel.toolMaker(it.toolMakerTemplateId)==null){
////                            loadToolMakers = true
////                        }
//                    }
////                    if(loadToolMakers) generalViewModel.refreshToolMakers {
////                            code, message ->
////                        updateUIState(code)
////                    }
//                }
//                updateUIState(code)
//            }
        }
    }
    fun toolMakerTemplateSelected(template: AIPortToolMakerTemplate): Boolean{
        val t = teamToolMakerTemplates[template.id]
        return t!=null&&t.status>0
    }
    fun selectToolMakerTemplate(selected: Boolean, template: AIPortToolMakerTemplate){
//        var permission = virtualToolPermissions[tool.toolId]
        var t = teamToolMakerTemplates.remove(template.id)
        if(t!=null){
            if(t.status==Short.MAX_VALUE.toInt()){
//                virtualToolPermissions.remove(tool.toolId)
            }else {
                if (selected) {
                    t.status = 1
                } else {
                    t.status = 0
                }
                teamToolMakerTemplates[t.toolMakerTemplateId]=t
            }
        }else if(selected) {
            t = AIPortTeamToolMakerTemplate()
                .templateId(template.id)
                .status(Short.MAX_VALUE.toInt())
            teamToolMakerTemplates[t.toolMakerTemplateId]=t
        }
    }
    fun teamToolMakerTemplatesChanged():Boolean{
        if(teamToolMakerTemplates.size!=_teamToolMakerTemplates.size){
            return true;
        }
        for(v in teamToolMakerTemplates.values){
            val p = _teamToolMakerTemplates[v.toolMakerTemplateId]
            if(p==null||p.status!=v.status){
                return true;
            }
        }
        return false
    }
    fun saveTeamToolMakerTemplates(team: AIPortTeam, onResponse:(code:Int, message:String?)-> Unit){
        if(teamToolMakerTemplatesChanged()) {
            viewModelScope.launch {
                TeamRepository.modifyTeamToolMakerTemplates(
                    team, teamToolMakerTemplates.values.toList()
                ){ code, message, data ->
                    onResponse(code,message)
                }
//                getPlatform().modifyTeamToolMakerTemplates(
//                    team.id, teamToolMakerTemplates.values.toList()
//                ){ (code, message, data) ->
//                    onResponse(code,message)
//                }
            }
        }
    }

    fun resetAllTeamToolMakerTemplates(){
        teamToolMakerTemplates.clear()
        for(p in _teamToolMakerTemplates.values){
            teamToolMakerTemplates[p.toolMakerTemplateId] = p.copy()
        }
    }
}