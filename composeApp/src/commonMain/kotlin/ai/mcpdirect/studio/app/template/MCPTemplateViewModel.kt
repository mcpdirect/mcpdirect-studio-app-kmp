package ai.mcpdirect.studio.app.template

import ai.mcpdirect.studio.app.model.aitool.AIPortToolMaker
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

//val mcpTemplateViewModel = MCPTemplateViewModel()
class MCPTemplateViewModel: ViewModel() {
    val toolMakers: StateFlow<List<AIPortToolMaker>> = ToolRepository.toolMakers
        .map { it.values.toList() }      // 转为 List
        .stateIn(
            scope = viewModelScope,      // 或 CoroutineScope(Dispatchers.Main.immediate)
            started = SharingStarted.WhileSubscribed(5000), // 按需启动
            initialValue = emptyList()
        )
    var toolMaker by mutableStateOf<AIPortToolMaker?>(null)
        private set

}