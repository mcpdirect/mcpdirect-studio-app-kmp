package ai.mcpdirect.studio.app.template

import ai.mcpdirect.studio.app.model.AIPortServiceResponse
import ai.mcpdirect.studio.app.model.aitool.AIPortToolAgent
import ai.mcpdirect.studio.app.model.aitool.AIPortToolMaker
import ai.mcpdirect.studio.app.model.aitool.AIPortToolMakerTemplate
import ai.mcpdirect.studio.app.model.repository.StudioRepository
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

//val mcpTemplateListViewModel = MCPTemplateListViewModel()
class MCPTemplateListViewModel: ViewModel() {
    val toolMakerTemplates: StateFlow<List<AIPortToolMakerTemplate>> = ToolRepository.toolMakerTemplates
        .map { it.values.toList() }      // 转为 List
        .stateIn(
            scope = viewModelScope,      // 或 CoroutineScope(Dispatchers.Main.immediate)
            started = SharingStarted.WhileSubscribed(5000), // 按需启动
            initialValue = emptyList()
        )
    var toolMakerTemplate by mutableStateOf<AIPortToolMakerTemplate?>(null)
        private  set

    fun toolMakerTemplate(template: AIPortToolMakerTemplate?){
        toolMakerTemplate = template
    }
    fun createToolMakerTemplate(toolAgent: AIPortToolAgent,name:String,type:Int,config:String,inputs:String){
        viewModelScope.launch {
            StudioRepository.createToolMakerTemplateForStudio(
                toolAgent, name,type,config,inputs
            )
//            ToolRepository.createToolMakerTemplate(name,type,agentId,config,inputs)
//            getPlatform().createToolMakerTemplate(name,type,agentId,config,inputs){
//                if(it.code== AIPortServiceResponse.SERVICE_SUCCESSFUL){
////                    it.data?.let {
////                        _toolMakerTemplates[it.id] = it
////                    }
//                    generalViewModel.refreshToolMakerTemplates()
//                }
//            }
        }
    }
    fun toolAgent(toolAgentId:Long,onResponse:(AIPortServiceResponse<AIPortToolAgent>) -> Unit){
        viewModelScope.launch {
            StudioRepository.toolAgent(toolAgentId){
                onResponse(it)
            }
        }
    }
}