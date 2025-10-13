package ai.mcpdirect.studio.app.team

import ai.mcpdirect.backend.dao.entity.account.AIPortTeam
import ai.mcpdirect.backend.dao.entity.aitool.AIPortTool
import ai.mcpdirect.backend.dao.entity.aitool.AIPortToolMaker
import ai.mcpdirect.backend.dao.entity.aitool.AIPortToolMakerTeam
import ai.mcpdirect.backend.dao.entity.aitool.AIPortVirtualTool
import ai.mcpdirect.studio.MCPDirectStudio
import ai.mcpdirect.studio.app.UIState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlin.collections.set

val mcpToolMakerTeamViewModel = MCPToolMakerTeamViewModel()
class MCPToolMakerTeamViewModel: ViewModel() {
    var searchQuery by mutableStateOf("")
        private set
    var uiState by mutableStateOf<UIState>(UIState.Idle)
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
                if(it.type==0) MCPDirectStudio.queryVirtualTools(it.id){
                        code, message, data ->
                    if(code==0&&data!=null){
                        if(data.isNotEmpty()) {
                            virtualTools.addAll(data)
                        }
                    }
                }
                else MCPDirectStudio.queryTools(null,null,null,it.id,null){
                        code, message, data ->
                    if(code==0&&data!=null){
                        if(data.isNotEmpty()) {
                            tools.addAll(data)
                        }
                    }
                }
            }
        }
    }
    private val _toolMakerTeams = mutableStateMapOf<Long, AIPortToolMakerTeam>()

    val toolMakerTeams = mutableStateMapOf<Long, AIPortToolMakerTeam>()
    val tools = mutableStateListOf<AIPortTool>()

    val virtualTools = mutableStateListOf<AIPortVirtualTool>()
    fun toolMakerSelected(toolMaker: AIPortToolMaker): Boolean{
        val t = toolMakerTeams[toolMaker.id]
        return t!=null&&t.status>0
    }
    fun selectToolMaker(selected: Boolean, toolMaker: AIPortToolMaker){
//        var permission = virtualToolPermissions[tool.toolId]
        var t = toolMakerTeams.remove(toolMaker.id)
        if(t!=null){
            if(t.status==Short.MAX_VALUE.toInt()){
//                virtualToolPermissions.remove(tool.toolId)
            }else {
                if (selected) {
                    t.status = 1
                } else {
                    t.status = 0
                }
                toolMakerTeams[t.toolMakerId]=t
            }
        }else if(selected) {
            t = AIPortToolMakerTeam()
                .toolMakerId(toolMaker.id)
                .status(Short.MAX_VALUE.toInt())
            toolMakerTeams[t.toolMakerId]=t
        }
    }
    fun toolMakerTeamsChanged():Boolean{
        if(toolMakerTeams.size!=_toolMakerTeams.size){
            return true;
        }
        for(v in toolMakerTeams.values){
            val p = _toolMakerTeams[v.toolMakerId]
            if(p==null||p.status!=v.status){
                return true;
            }
        }
        return false
    }
    fun saveToolMakerTeams(team: AIPortTeam,onResponse:(code:Int,message:String?)-> Unit){
        if(toolMakerTeamsChanged()) {
            viewModelScope.launch {
                MCPDirectStudio.modifyToolMakerTeams(
                    team, toolMakerTeams.values.toList()
                ){
                    code, message, data ->
                    onResponse(code,message)
                }
            }
        }
    }

    fun resetAllToolMakerTeam(){
        toolMakerTeams.clear()
        for(p in _toolMakerTeams.values){
            toolMakerTeams[p.toolMakerId] = p.copy()
        }
    }
}