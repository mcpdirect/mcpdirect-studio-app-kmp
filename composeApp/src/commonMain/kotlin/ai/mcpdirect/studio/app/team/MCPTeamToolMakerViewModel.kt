package ai.mcpdirect.studio.app.team

import ai.mcpdirect.mcpdirectstudioapp.getPlatform
import ai.mcpdirect.studio.app.UIState
import ai.mcpdirect.studio.app.generalViewModel
import ai.mcpdirect.studio.app.model.account.AIPortTeam
import ai.mcpdirect.studio.app.model.aitool.AIPortTeamToolMaker
import ai.mcpdirect.studio.app.model.aitool.AIPortTool
import ai.mcpdirect.studio.app.model.aitool.AIPortToolMaker
import ai.mcpdirect.studio.app.model.aitool.AIPortVirtualTool
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlin.collections.set

val mcpTeamToolMakerViewModel = MCPTeamToolMakerViewModel()
class MCPTeamToolMakerViewModel: ViewModel() {
    var searchQuery by mutableStateOf("")
        private set
    var uiState by mutableStateOf<UIState>(UIState.Idle)
    private fun updateUIState(code:Int){
        uiState = if(code==0) UIState.Success else UIState.Error(code)
    }
    fun updateSearchQuery(query: String) {
        searchQuery = query
    }
    var toolMaker by mutableStateOf<AIPortToolMaker?>(null)
        private set
    fun toolMaker(maker: AIPortToolMaker?){
        toolMaker = maker
        toolMaker?.let {
            tools.clear()
            virtualTools.clear()
            viewModelScope.launch {
                if(it.type==0) getPlatform().queryVirtualTools(makerId = it.id){
                        (code, message, data) ->
                    if(code==0&&data!=null){
                        if(data.isNotEmpty()) {
                            virtualTools.addAll(data)
                        }
                    }
                }
                else getPlatform().queryTools(null,null,null,it.id,null){
                        (code, message, data) ->
                    if(code==0&&data!=null){
                        if(data.isNotEmpty()) {
                            tools.addAll(data)
                        }
                    }
                }
            }
        }
    }
    private val _teamToolMakers = mutableStateMapOf<Long, AIPortTeamToolMaker>()

    val teamToolMakers = mutableStateMapOf<Long, AIPortTeamToolMaker>()
    val tools = mutableStateListOf<AIPortTool>()

    val virtualTools = mutableStateListOf<AIPortVirtualTool>()
    fun reset(){
        _teamToolMakers.clear()
        teamToolMakers.clear()
        tools.clear()
        virtualTools.clear()
        toolMaker = null
    }

    fun refreshTeamToolMakers(team: AIPortTeam){
        viewModelScope.launch {
            getPlatform().queryTeamToolMakers(team.id){ (code, message, data) ->
                if(code==0){
                    var loadToolMakers = false
                    data?.forEach {
                        _teamToolMakers[it.toolMakerId]=it
                        teamToolMakers[it.toolMakerId] = it.copy()
                        if(generalViewModel.toolMaker(it.toolMakerId)==null){
                            loadToolMakers = true
                        }
                    }
                    if(loadToolMakers) generalViewModel.refreshToolMakers {
                            code, message ->
                        updateUIState(code)
                    }
                }
                updateUIState(code)
            }
        }
    }
    fun toolMakerSelected(toolMaker: AIPortToolMaker): Boolean{
        val t = teamToolMakers[toolMaker.id]
        return t!=null&&t.status>0
    }
    fun selectToolMaker(selected: Boolean, toolMaker: AIPortToolMaker){
//        var permission = virtualToolPermissions[tool.toolId]
        var t = teamToolMakers.remove(toolMaker.id)
        if(t!=null){
            if(t.status==Short.MAX_VALUE.toInt()){
//                virtualToolPermissions.remove(tool.toolId)
            }else {
                if (selected) {
                    t.status = 1
                } else {
                    t.status = 0
                }
                teamToolMakers[t.toolMakerId]=t
            }
        }else if(selected) {
            t = AIPortTeamToolMaker()
                .toolMakerId(toolMaker.id)
                .status(Short.MAX_VALUE.toInt())
            teamToolMakers[t.toolMakerId]=t
        }
    }
    fun teamToolMakersChanged():Boolean{
        if(teamToolMakers.size!=_teamToolMakers.size){
            return true;
        }
        for(v in teamToolMakers.values){
            val p = _teamToolMakers[v.toolMakerId]
            if(p==null||p.status!=v.status){
                return true;
            }
        }
        return false
    }
    fun saveTeamToolMakers(team: AIPortTeam, onResponse:(code:Int, message:String?)-> Unit){
        if(teamToolMakersChanged()) {
            viewModelScope.launch {
                getPlatform().modifyTeamToolMakers(
                    team, teamToolMakers.values.toList()
                ){ (code, message, data) ->
                    onResponse(code,message)
                }
            }
        }
    }

    fun resetAllTeamToolMakers(){
        teamToolMakers.clear()
        for(p in _teamToolMakers.values){
            teamToolMakers[p.toolMakerId] = p.copy()
        }
    }
}