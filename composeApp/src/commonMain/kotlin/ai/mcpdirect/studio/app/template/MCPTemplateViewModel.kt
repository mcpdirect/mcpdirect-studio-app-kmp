package ai.mcpdirect.studio.app.template

import ai.mcpdirect.mcpdirectstudioapp.getPlatform
import ai.mcpdirect.studio.app.model.AIPortServiceResponse
import ai.mcpdirect.studio.app.model.MCPServerConfig
import ai.mcpdirect.studio.app.model.account.AIPortOtp
import ai.mcpdirect.studio.app.model.aitool.AIPortToolMaker
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

val mcpTemplateViewModel = MCPTemplateViewModel()
class MCPTemplateViewModel: ViewModel() {
    var toolMaker by mutableStateOf<AIPortToolMaker?>(null)
        private set

}