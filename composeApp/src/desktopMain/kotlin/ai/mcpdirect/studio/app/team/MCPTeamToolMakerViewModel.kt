package ai.mcpdirect.studio.app.team

import ai.mcpdirect.backend.dao.entity.aitool.AIPortToolMaker
import ai.mcpdirect.studio.app.UIState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

val mcpTeamToolMakerViewModel = MCPTeamToolMakerViewModel()
class MCPTeamToolMakerViewModel: ViewModel() {
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
    }
}