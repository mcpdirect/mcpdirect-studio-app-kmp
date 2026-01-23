package ai.mcpdirect.studio.app.home

import ai.mcpdirect.studio.app.generalViewModel
import ai.mcpdirect.studio.app.model.account.AIPortTeam
import ai.mcpdirect.studio.app.model.aitool.AIPortTeamToolMaker
import ai.mcpdirect.studio.app.model.aitool.AIPortToolAccessKey
import ai.mcpdirect.studio.app.model.aitool.AIPortToolAgent
import ai.mcpdirect.studio.app.model.aitool.AIPortToolMaker
import ai.mcpdirect.studio.app.model.repository.AccessKeyRepository
import ai.mcpdirect.studio.app.model.repository.AppVersionRepository
import ai.mcpdirect.studio.app.model.repository.StudioRepository
import ai.mcpdirect.studio.app.model.repository.TeamRepository
import ai.mcpdirect.studio.app.model.repository.ToolRepository
import ai.mcpdirect.studio.app.model.repository.UserRepository
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel: ViewModel() {
    val localToolAgent = StudioRepository.localToolAgent
    val toolAgents: StateFlow<List<AIPortToolAgent>> = StudioRepository.toolAgents
        .map { it.values.sortedBy { it.name } }      // 转为 List
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
        .map { it.values.sortedBy { it.name } }      // 转为 List
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

    val toolMakerFilter = MutableStateFlow("")
    val toolMakers: StateFlow<List<AIPortToolMaker>> = combine(
        ToolRepository.toolMakers,
        toolMakerFilter
    ){ makers,filter ->
        makers.values.filter {
            it.type>0 &&(filter.isEmpty()||it.name.contains(filter,ignoreCase = true))
        }.sortedBy { it.name }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    val virtualToolMakerFilter = MutableStateFlow("")
    val virtualToolMakers: StateFlow<List<AIPortToolMaker>> = combine(
        ToolRepository.toolMakers,
        virtualToolMakerFilter
    ){ makers,filter ->
        makers.values.filter {
            it.type==0 &&(filter.isEmpty()||it.name.contains(filter,ignoreCase = true))
        }.sortedBy { it.name }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
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
//    val teamToolMakers: StateFlow<List<AIPortTeamToolMaker>> = TeamRepository.teamToolMakers
//        .map { it.values.toList() }
//        .stateIn(
//            scope = viewModelScope,
//            started = SharingStarted.WhileSubscribed(5000),
//            initialValue = emptyList()
//        )
    fun refreshTeamToolMakers(force:Boolean=false){
        viewModelScope.launch {
            TeamRepository.loadTeamToolMakers(force=force)
        }
    }
    fun checkAppUpdate(){
        viewModelScope.launch {
            AppVersionRepository.checkAppVersion{
                if(it.successful()&&it.data!=null){
                    generalViewModel.showSnackbar("You're using the last version.")
                }
            }
        }
    }
}