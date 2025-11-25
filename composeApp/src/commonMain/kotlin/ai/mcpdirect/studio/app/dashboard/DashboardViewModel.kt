package ai.mcpdirect.studio.app.dashboard

import ai.mcpdirect.studio.app.model.account.AIPortAccessKey
import ai.mcpdirect.studio.app.model.account.AIPortTeam
import ai.mcpdirect.studio.app.model.aitool.AIPortTool
import ai.mcpdirect.studio.app.model.aitool.AIPortToolAgent
import ai.mcpdirect.studio.app.model.repository.AccessKeyRepository
import ai.mcpdirect.studio.app.model.repository.StudioRepository
import ai.mcpdirect.studio.app.model.repository.TeamRepository
import ai.mcpdirect.studio.app.model.repository.ToolRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DashboardViewModel: ViewModel() {
    val localToolAgent = StudioRepository.localToolAgent
    val toolAgents: StateFlow<List<AIPortToolAgent>> = StudioRepository.toolAgents
        .map { it.values.toList() }      // 转为 List
        .stateIn(
            scope = viewModelScope,      // 或 CoroutineScope(Dispatchers.Main.immediate)
            started = SharingStarted.WhileSubscribed(5000), // 按需启动
            initialValue = emptyList()
        )
    fun refreshToolAgents(force:Boolean=false){
        viewModelScope.launch {
            StudioRepository.loadToolAgents(force)
        }
    }
    val accessKeys: StateFlow<List<AIPortAccessKey>> = AccessKeyRepository.accessKeys
        .map { it.values.toList() }      // 转为 List
        .stateIn(
            scope = viewModelScope,      // 或 CoroutineScope(Dispatchers.Main.immediate)
            started = SharingStarted.WhileSubscribed(5000), // 按需启动
            initialValue = emptyList()
        )
    fun refreshAccessKeys(force:Boolean=false){
        viewModelScope.launch {
            AccessKeyRepository.loadAccessKeys(force)
        }
    }
//    val tools: StateFlow<List<AIPortTool>> = ToolRepository.tools
//        .map { it.values.toList() }      // 转为 List
//        .stateIn(
//            scope = viewModelScope,      // 或 CoroutineScope(Dispatchers.Main.immediate)
//            started = SharingStarted.WhileSubscribed(5000), // 按需启动
//            initialValue = emptyList()
//        )
//    fun refreshTools(force:Boolean=false){
//        viewModelScope.launch {
//            ToolRepository.loadTools()
//        }
//    }
    val teams: StateFlow<List<AIPortTeam>> = TeamRepository.teams
        .map { it.values.toList() }      // 转为 List
        .stateIn(
            scope = viewModelScope,      // 或 CoroutineScope(Dispatchers.Main.immediate)
            started = SharingStarted.WhileSubscribed(5000), // 按需启动
            initialValue = emptyList()
        )
    fun refreshTeams(force:Boolean=false){
        viewModelScope.launch {
            TeamRepository.loadTeams(force)
        }
    }
}