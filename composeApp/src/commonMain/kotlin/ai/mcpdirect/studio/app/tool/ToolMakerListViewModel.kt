package ai.mcpdirect.studio.app.tool

import ai.mcpdirect.studio.app.model.account.AIPortTeam
import ai.mcpdirect.studio.app.model.aitool.AIPortTool
import ai.mcpdirect.studio.app.model.aitool.AIPortToolMaker
import ai.mcpdirect.studio.app.model.repository.TeamRepository
import ai.mcpdirect.studio.app.model.repository.ToolRepository
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ToolMakerListViewModel: ViewModel() {
    // 1. 将 toolMaker 改为 StateFlow
    private val _toolMaker = MutableStateFlow<AIPortToolMaker?>(null)
    val toolMaker: StateFlow<AIPortToolMaker?> = _toolMaker

    // 2. 使用 combine 同时监听两个流
    val toolMakers: StateFlow<List<AIPortToolMaker>> = ToolRepository.toolMakers
        .map { it.values.filter { maker -> !maker.virtual() }.toList() }      // 转为 List
        .stateIn(
            scope = viewModelScope,      // 或 CoroutineScope(Dispatchers.Main.immediate)
            started = SharingStarted.WhileSubscribed(5000), // 按需启动
            initialValue = emptyList()
        )

    fun toolMaker(maker: AIPortToolMaker){
        _toolMaker.value = maker
    }
    fun refreshToolMakers(){
        viewModelScope.launch {
            ToolRepository.loadToolMakers()
        }
    }
    fun refreshTeams(){
        viewModelScope.launch {
            TeamRepository.loadTeams()
        }
    }
    fun team(teamId:Long,onResponse:(code:Int,message:String?,data: AIPortTeam?)->Unit){
        viewModelScope.launch {
            TeamRepository.loadTeam(teamId=teamId){
                code, message, data ->
                onResponse(code,message,data)
            }
        }
    }
}