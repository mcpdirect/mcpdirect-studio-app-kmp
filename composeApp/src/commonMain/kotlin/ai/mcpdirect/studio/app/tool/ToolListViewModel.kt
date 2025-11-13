package ai.mcpdirect.studio.app.tool

import ai.mcpdirect.studio.app.model.aitool.AIPortTool
import ai.mcpdirect.studio.app.model.aitool.AIPortToolMaker
import ai.mcpdirect.studio.app.model.repository.ToolRepository
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ToolListViewModel: ViewModel() {
    // 1. 将 toolMaker 改为 StateFlow
    private val _toolMaker = MutableStateFlow<AIPortToolMaker?>(null)
    val toolMaker: StateFlow<AIPortToolMaker?> = _toolMaker

    // 2. 使用 combine 同时监听两个流
    val tools: StateFlow<List<AIPortTool>> = combine(
        ToolRepository.tools,
        _toolMaker
    ) { toolsMap, maker ->
        maker?.let {
            toolsMap.values.filter { tool -> tool.makerId == it.id }.toList()
        } ?: emptyList()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    var tool by mutableStateOf<AIPortTool?>(null)
        private  set

    fun toolMaker(maker: AIPortToolMaker){
        tool = null
        viewModelScope.launch {
            _toolMaker.value = maker
            ToolRepository.loadTools(userId=maker.userId,makerId = maker.id)
        }
    }
    fun tool(tool: AIPortTool){
        viewModelScope.launch {
            ToolRepository.loadTool(toolId = tool.id) {
                code, message, data ->
                if(code==0) data?.let {
                    this@ToolListViewModel.tool = data
                }
            }
        }
    }
}