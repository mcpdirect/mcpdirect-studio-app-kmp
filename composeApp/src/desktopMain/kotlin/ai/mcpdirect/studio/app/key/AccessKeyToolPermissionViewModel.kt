package ai.mcpdirect.studio.app.key

import ai.mcpdirect.studio.MCPDirectStudio
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import ai.mcpdirect.backend.dao.entity.account.AIPortAccessKeyCredential

import kotlinx.coroutines.launch

import androidx.compose.material3.SnackbarHostState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ai.mcpdirect.backend.dao.entity.aitool.AIPortTool
import ai.mcpdirect.backend.dao.entity.aitool.AIPortToolPermission
import ai.mcpdirect.backend.dao.entity.aitool.AIPortToolAgent
import ai.mcpdirect.backend.dao.entity.aitool.AIPortToolMaker
import ai.mcpdirect.backend.dao.entity.aitool.AIPortVirtualTool
import ai.mcpdirect.backend.dao.entity.aitool.AIPortVirtualToolPermission
import androidx.compose.runtime.mutableStateMapOf
import kotlin.collections.forEach

class AccessKeyToolPermissionViewModel : ViewModel(){
    val snackbarHostState = SnackbarHostState()

    fun showSnackbar(message: String) {
        viewModelScope.launch {
            snackbarHostState.showSnackbar(message)
        }
    }

    var accessKey by mutableStateOf<AIPortAccessKeyCredential?>(null)
    var toolAgent by mutableStateOf<AIPortToolAgent?>(null)
        private set
    var toolMaker by mutableStateOf<AIPortToolMaker?>(null)
        private set
    val toolAgents = mutableStateListOf<AIPortToolAgent>()
    val toolMakers = mutableStateListOf<AIPortToolMaker>()
    val tools = mutableStateListOf<AIPortTool>()

    val virtualTools = mutableStateMapOf<Long, AIPortVirtualTool>()
    private val _toolPermissions = mutableStateMapOf<Long, AIPortToolPermission>()
    private val _virtualToolPermissions = mutableStateMapOf<Long, AIPortVirtualToolPermission>()

    val toolPermissions = mutableStateMapOf<Long, AIPortToolPermission>()
    val virtualToolPermissions = mutableStateMapOf<Long, AIPortVirtualToolPermission>()

    fun selectToolAgent(agent:AIPortToolAgent?){
        toolAgent = agent
        toolAgent?.let {
            toolMakers.clear()
            tools.clear()
            viewModelScope.launch {
                MCPDirectStudio.queryToolMakers(null,null,it.id){
                        code, message, data ->
                    if(code==0&&data!=null){

                        if(data.isNotEmpty()) {
                            toolMakers.addAll(data)
                            selectToolMaker(toolMakers[0])
                        }
                    }
                }
            }
        }
    }

    fun selectToolMaker(maker:AIPortToolMaker?){
        toolMaker = maker
        toolMaker?.let {
            tools.clear()
            viewModelScope.launch {
                if(it.type==0)
                    MCPDirectStudio.queryVirtualTools(it.id){
                            code, message, data ->
                        if(code==0&&data!=null){
                            if(data.isNotEmpty()) {
                                tools.addAll(data)
                            }
                        }
                    }
                    else
                MCPDirectStudio.queryTools(null,null,null,it.id,null){
                    code, message, data ->
                    if(code==0&&data!=null){
                        if(data.isNotEmpty()) {
                            tools.addAll(data)
                        }
                    }
                }
            }
        }
    }

    fun refresh(){
        accessKey?.let {
            viewModelScope.launch {
                MCPDirectStudio.queryToolPermissions(it.id){
                    code, message, data ->
                    if(code==0&&data!=null){
                        _toolPermissions.clear();
                        data.forEach {
                            _toolPermissions[it.toolId]=it
                            toolPermissions[it.toolId]=it
                        }
                    }
                }
            }
            viewModelScope.launch {
                MCPDirectStudio.queryVirtualToolPermissions(it.id){
                        code, message, data ->
                    if(code==0&&data!=null){
                        _virtualToolPermissions.clear()
                        data.forEach {
                            _virtualToolPermissions[it.originalToolId]=it
                            virtualToolPermissions[it.originalToolId]=it
                        }
                    }
                }
            }
        }
        viewModelScope.launch {
            MCPDirectStudio.queryToolAgents {
                    code, message, data ->
                if(code==0&&data!=null){
                    toolAgents.clear()
                    if(data.isNotEmpty()) {
                        toolAgents.addAll(data)
                        selectToolAgent(toolAgents[0])
                    }
                }
            }
        }
    }

    fun loadToolMakers(){

    }
}
