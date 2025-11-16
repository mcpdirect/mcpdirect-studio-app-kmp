package ai.mcpdirect.studio.app.template

import ai.mcpdirect.mcpdirectstudioapp.JSON
import ai.mcpdirect.mcpdirectstudioapp.getPlatform
import ai.mcpdirect.studio.app.generalViewModel
import ai.mcpdirect.studio.app.model.AIPortServiceResponse
import ai.mcpdirect.studio.app.model.MCPServerConfig
import ai.mcpdirect.studio.app.model.OpenAPIServerConfig
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

val mcpTemplateListViewModel = MCPTemplateListViewModel()
class MCPTemplateListViewModel: ViewModel() {
//    private val _toolMakerTemplates = mutableStateMapOf<Long, AIPortToolMakerTemplate>()
//
//    val toolMakerTemplates by derivedStateOf {
//        _toolMakerTemplates.values.toList()
//    }
    var toolMakerTemplate by mutableStateOf<AIPortToolMakerTemplate?>(null)
        private  set

    fun toolMakerTemplate(template: AIPortToolMakerTemplate?){
        toolMakerTemplate = template
    }
    fun createToolMakerTemplate(name:String,type:Int,agentId:Long,config:String,inputs:String){
        viewModelScope.launch {
            getPlatform().createToolMakerTemplate(name,type,agentId,config,inputs){
                if(it.code== AIPortServiceResponse.SERVICE_SUCCESSFUL){
//                    it.data?.let {
//                        _toolMakerTemplates[it.id] = it
//                    }
                    generalViewModel.refreshToolMakerTemplates()
                }
            }
        }
    }
}