package ai.mcpdirect.studio.app

import ai.mcpdirect.backend.dao.entity.account.AIPortAccessKeyCredential
import ai.mcpdirect.backend.dao.entity.aitool.AIPortTool
import ai.mcpdirect.backend.dao.entity.aitool.AIPortToolAgent
import ai.mcpdirect.backend.dao.entity.aitool.AIPortToolMaker
import ai.mcpdirect.backend.dao.entity.aitool.AIPortToolPermission
import ai.mcpdirect.backend.dao.entity.aitool.AIPortVirtualTool
import ai.mcpdirect.backend.dao.entity.aitool.AIPortVirtualToolPermission
import ai.mcpdirect.studio.MCPDirectStudio
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

class GeneralViewModel() : ViewModel() {
    var currentScreen by mutableStateOf<Screen>(Screen.MCPServerIntegration)
    var backToScreen by mutableStateOf<Screen?>(null)
    val snackbarHostState = SnackbarHostState()

    fun showSnackbar(message: String) {
        viewModelScope.launch {
            snackbarHostState.showSnackbar(message)
        }
    }

    private val _virtualToolAgent = AIPortToolAgent("Virtual MCP")
    private val _toolAgents = mutableStateMapOf<Long, AIPortToolAgent>()
    val toolAgents by derivedStateOf {
        _toolAgents.values.toList()
    }
    private val _toolMakers = mutableStateMapOf<Long, AIPortToolMaker>()
    val toolMakers by derivedStateOf {
        _toolMakers.values.toList()
    }
    private val _tools = mutableStateMapOf<Long, AIPortTool>()
    private val _virtualTools = mutableStateListOf<AIPortVirtualTool>()

    private val _toolPermissions = mutableStateMapOf<Long, AIPortToolPermission>()
    val toolPermissions by derivedStateOf {
        _toolPermissions.values.toList()
    }
    private val _virtualToolPermissions = mutableStateMapOf<Long, AIPortVirtualToolPermission>()
    val virtualToolPermissions by derivedStateOf {
        _virtualToolPermissions.values.toList()
    }
    fun toolMaker(id:Long): AIPortToolMaker?{
        return _toolMakers[id]
    }
    fun refreshToolAgents(){
        MCPDirectStudio.queryToolAgents {
                code, message, data ->
            if(code==0&&data!=null){
                _toolAgents.clear()
                _toolAgents[0] = _virtualToolAgent
                data.forEach {
                    _toolAgents[it.id]=it
                }
            }
        }
    }
    fun refreshToolMakers(){
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                loadToolMakers()
            }
        }
    }
    fun loadToolMakers(){
        MCPDirectStudio.queryToolMakers(null ,null,null){
                code, message, data ->
            if(code==0&&data!=null){
                _toolMakers.clear()
                data.forEach {
                    _toolMakers[it.id]=it
                }
            }
        }
    }

    fun copyToClipboard(key: AIPortAccessKeyCredential) {
        val clipboard = Toolkit.getDefaultToolkit().systemClipboard
        clipboard.setContents(
            StringSelection(
                MCPDirectStudio.createMCPConfigFromKey(key)
            ),
            null)
        showSnackbar("MCP Server Config copied to clipboard!")
    }
}