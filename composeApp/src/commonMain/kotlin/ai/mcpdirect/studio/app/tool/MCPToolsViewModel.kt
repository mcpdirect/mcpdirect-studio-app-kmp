package ai.mcpdirect.studio.app.tool

import ai.mcpdirect.mcpdirectstudioapp.JSON
import ai.mcpdirect.mcpdirectstudioapp.getPlatform
import ai.mcpdirect.studio.app.model.AIPortServiceResponse
import ai.mcpdirect.studio.app.model.MCPServerConfig
import ai.mcpdirect.studio.app.model.aitool.AIPortToolMaker
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

val mcpToolsViewModel = MCPToolsViewModel()
class MCPToolsViewModel: ViewModel() {
    var toolMaker by mutableStateOf<AIPortToolMaker?>(null)
        private  set
    var mcpServerConfig by mutableStateOf<MCPServerConfig?>(null)
        private set
    fun toolMaker(maker: AIPortToolMaker?){
        toolMaker = maker
        mcpServerConfig = null
    }
    fun getMCPServerConfig(id:Long) {
        viewModelScope.launch {
            getPlatform().getMCPServerConfig(id) {
                if (it.code == AIPortServiceResponse.SERVICE_SUCCESSFUL) {
                    it.data?.let {
                        if (it.id == id) {
                            val config = MCPServerConfig()
                            config.transport = it.transport
                            config.url = it.url
                            config.command = it.command
                            config.args = JSON.decodeFromString(it.args)
                            config.env = JSON.decodeFromString(it.env)
                            mcpServerConfig = config
                        }
                    }
                }
            }
        }
    }
}