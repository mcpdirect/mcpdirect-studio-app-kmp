package ai.mcpdirect.studio.app.home

import ai.mcpdirect.studio.app.model.account.AIPortTeam
import ai.mcpdirect.studio.app.model.aitool.AIPortToolAccessKey
import ai.mcpdirect.studio.app.model.aitool.AIPortToolAgent
import ai.mcpdirect.studio.app.model.aitool.AIPortToolMaker
import ai.mcpdirect.studio.app.model.repository.AccessKeyRepository
import ai.mcpdirect.studio.app.model.repository.StudioRepository
import ai.mcpdirect.studio.app.model.repository.TeamRepository
import ai.mcpdirect.studio.app.model.repository.ToolRepository
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.time.TimeMark

class HomeViewModel: ViewModel() {
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
    val accessKeys: StateFlow<List<AIPortToolAccessKey>> = AccessKeyRepository.accessKeys
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
    val toolMakers: StateFlow<List<AIPortToolMaker>> = ToolRepository.toolMakers
        .map { it.values.filter { it.type>0 }.toList() }      // 转为 List
        .stateIn(
            scope = viewModelScope,      // 或 CoroutineScope(Dispatchers.Main.immediate)
            started = SharingStarted.WhileSubscribed(5000), // 按需启动
            initialValue = emptyList()
        )
    val virtualToolMakers: StateFlow<List<AIPortToolMaker>> = ToolRepository.toolMakers
        .map { it.values.filter { it.type==0 }.toList() }      // 转为 List
        .stateIn(
            scope = viewModelScope,      // 或 CoroutineScope(Dispatchers.Main.immediate)
            started = SharingStarted.WhileSubscribed(5000), // 按需启动
            initialValue = emptyList()
        )
    var showTips by mutableStateOf<Boolean?>(null)
    fun refreshToolMakers(force:Boolean=false){
        viewModelScope.launch {
            if(ToolRepository.loadToolMakers.value){
                showTips = false
            }
            ToolRepository.loadToolMakers(force){
                if(showTips==null) showTips = ToolRepository.toolMakers.value.isEmpty()
            }
        }
    }
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