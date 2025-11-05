package ai.mcpdirect.studio.app.tool

import ai.mcpdirect.mcpdirectstudioapp.JSON
import ai.mcpdirect.mcpdirectstudioapp.getPlatform
import ai.mcpdirect.studio.app.model.AIPortServiceResponse
import ai.mcpdirect.studio.app.model.MCPServerConfig
import ai.mcpdirect.studio.app.model.aitool.AIPortToolMaker
import ai.mcpdirect.studio.app.model.aitool.AIPortToolMakerTemplate
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
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
//    private val _toolMakerTemplates = mutableStateMapOf<Long, AIPortToolMakerTemplate>()
//
//    val toolMakerTemplates by derivedStateOf {
//        _toolMakerTemplates.values.toList()
//    }
//    var toolMakerTemplate by mutableStateOf<AIPortToolMakerTemplate?>(null)
//        private  set

    fun toolMaker(maker: AIPortToolMaker?){
        toolMaker = maker
        mcpServerConfig = null
    }
//    fun toolMakerTemplate(template: AIPortToolMakerTemplate?){
//        toolMakerTemplate = template
//    }
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
                            it.args?.let{
                                config.args = JSON.decodeFromString(it)
                            }
                            it.env?.let {
                                config.env = JSON.decodeFromString(it)
                            }
                            mcpServerConfig = config
                        }
                    }
                }
            }
        }
    }
//    fun createToolMakerTemplate(name:String,type:Int,agentId:Long,config:String,inputs:String){
//        viewModelScope.launch {
//            getPlatform().createToolMakerTemplate(name,type,agentId,config,inputs){
//                if(it.code== AIPortServiceResponse.SERVICE_SUCCESSFUL){
//                    it.data?.let {
//                        _toolMakerTemplates[it.id] = it
//                    }
//                }
//            }
//        }
//    }
//    fun queryToolMakerTemplates(){
//        viewModelScope.launch {
//            getPlatform().queryToolMakerTemplates {
//                if(it.code== AIPortServiceResponse.SERVICE_SUCCESSFUL){
//                    it.data?.let {
//                        it.forEach {
//                            _toolMakerTemplates[it.id] = it
//                        }
//                    }
//                }
//            }
//        }
//    }
}