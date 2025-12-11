package ai.mcpdirect.studio.app.tips

import ai.mcpdirect.studio.app.model.MCPConfig
import ai.mcpdirect.studio.app.model.MCPServerConfig
import ai.mcpdirect.studio.app.model.OpenAPIServerConfig
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class QuickStartViewModel: ViewModel() {
    private val _mcpConfigs = mutableStateMapOf<String, MCPConfig>()
    val mcpConfigs by derivedStateOf {
        _mcpConfigs.values.toList()
    }
    fun removeMCPConfig(mcpConfig: MCPConfig) {
        _mcpConfigs.remove(mcpConfig.name)
    }
    fun addMCPConfig(mcpConfig: MCPConfig) {
        _mcpConfigs[mcpConfig.name] = mcpConfig
    }
    fun addMCPConfigs(mcpConfigs: Map<String,MCPConfig>) {
        viewModelScope.launch {
            mcpConfigs.forEach { (name, config) ->
                config.name = name
                when (config) {
                    is MCPServerConfig ->{
                        val command = config.command?.trim()?:""
                        val url = config.url?.trim()?:""
                        if(command.isBlank()&&url.isBlank()){
                            config.status = -1
                        }
                        if(command.isNotBlank()) {
                            config.command = command
                            config.transport = 0
                            config.url = null
                        }else if(url.isNotBlank()){
                            config.url = url
                            if(url.endsWith("/sse")) config.transport = 1
                            else if(url.endsWith("/mcp")) config.transport = 2
                            else {
                                config.transport = -1
                                config.status = -2
                            }
                        }
                    }
                    is OpenAPIServerConfig ->{

                    }
                }
                addMCPConfig(config)
            }
        }
    }
}