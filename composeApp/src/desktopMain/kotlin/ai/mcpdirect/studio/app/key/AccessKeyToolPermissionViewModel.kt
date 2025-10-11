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
    private val _virtualToolAgent = AIPortToolAgent("Virtual MCP")
    var toolAgent by mutableStateOf<AIPortToolAgent?>(null)
        private set
    var toolMaker by mutableStateOf<AIPortToolMaker?>(null)
        private set
    val toolAgents = mutableStateListOf<AIPortToolAgent>()
    val toolMakers = mutableStateListOf<AIPortToolMaker>()
    val tools = mutableStateListOf<AIPortTool>()

    val virtualTools = mutableStateListOf<AIPortVirtualTool>()
    private val _toolPermissions = mutableStateMapOf<Long, AIPortToolPermission>()
    private val _virtualToolPermissions = mutableStateMapOf<Long, AIPortVirtualToolPermission>()

    val toolPermissions = mutableStateMapOf<Long, AIPortToolPermission>()
    val virtualToolPermissions = mutableStateMapOf<Long, AIPortVirtualToolPermission>()

    fun toolSelected(tool: AIPortVirtualTool): Boolean{
        val permission = virtualToolPermissions[tool.toolId]
        return permission!=null&&permission.status>0
    }
    fun toolSelected(tool: AIPortTool): Boolean{
        val permission = toolPermissions[tool.id]
        return permission!=null&&permission.status>0
    }
    fun toolsSelected():Boolean{
        toolMaker?.let {
            if(it.type==0) {
                if(virtualToolPermissions.isEmpty()) return false
                for (t in virtualTools) {
                    val p = virtualToolPermissions[t.toolId]
                    if (p!=null&&t.status > 0) return true
                }
            }else {
                if(toolPermissions.isEmpty()) return false
                for (t in tools) {
                    val p = toolPermissions[t.id]
                    if (p!=null&&t.status > 0) return true
                }
            }
        }
        return false
    }
    fun selectTool(selected: Boolean, tool: AIPortVirtualTool){
//        var permission = virtualToolPermissions[tool.toolId]
        var permission = virtualToolPermissions.remove(tool.toolId)
        if(permission!=null){
            if(permission.status==Short.MAX_VALUE.toInt()){
//                virtualToolPermissions.remove(tool.toolId)
            }else {
                if (selected) {
                    permission.status = 1
                } else {
                    permission.status = 0
                }
                virtualToolPermissions[permission.originalToolId]=permission
            }
        }else if(selected) {
            permission = AIPortVirtualToolPermission()
            permission.toolId = tool.id
            permission.originalToolId = tool.toolId
            permission.status = Short.MAX_VALUE.toInt()
            permission.makerId = tool.makerId
            permission.agentId = 0
            permission.accessKeyId = accessKey!!.id
            virtualToolPermissions[permission.originalToolId]=permission
        }

    }

    fun selectTool(selected: Boolean, tool: AIPortTool){
//        var permission = toolPermissions[tool.id]
        var permission =  toolPermissions.remove(tool.id)
        if(permission!=null){
            if(permission.status==Short.MAX_VALUE.toInt()){
//                toolPermissions.remove(tool.id)
            }else {
                if (selected) {
                    permission.status = 1
                } else {
                    permission.status = 0
                }
                toolPermissions[permission.toolId]=permission
            }
        } else if(selected) {
            permission = AIPortToolPermission()
            permission.toolId = tool.id
            permission.makerId = tool.makerId
            permission.agentId = tool.agentId
            permission.accessKeyId = accessKey!!.id
            toolPermissions[permission.toolId]=permission
        }
    }

    fun countToolPermissions(agent:AIPortToolAgent):Int{
        var count = 0
        if(agent.id>0) toolPermissions.values.forEach {
            if(it.agentId==agent.id&&it.status>0) count++
        } else virtualToolPermissions.values.forEach {
            if(it.agentId==agent.id&&it.status>0) count++
        }
        return count
    }
    fun countToolPermissions(maker: AIPortToolMaker):Int{
        var count = 0
        if(maker.type>0) toolPermissions.values.forEach {
            if(it.makerId==maker.id&&it.status>0) count++
        } else virtualToolPermissions.values.forEach {
            if(it.makerId==maker.id&&it.status>0) count++
        }
        return count
    }

    fun countToolPermissions():Int{
        toolMaker?.let {
            return countToolPermissions(it)
        }
        return 0;
    }
    fun selectToolAgent(agent:AIPortToolAgent?){
        toolAgent = agent
        toolAgent?.let {
            toolMakers.clear()
            tools.clear()
            viewModelScope.launch {
                MCPDirectStudio.queryToolMakers(if(it.id==0L) 0 else -1 ,null,it.id){
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
            virtualTools.clear()
            viewModelScope.launch {
                if(it.type==0) MCPDirectStudio.queryVirtualTools(it.id){
                        code, message, data ->
                    if(code==0&&data!=null){
                        if(data.isNotEmpty()) {
                            virtualTools.addAll(data)
                        }
                    }
                }
                else MCPDirectStudio.queryTools(null,null,null,it.id,null){
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
                        _toolPermissions.clear()
                        toolPermissions.clear()
                        data.forEach {
                            _toolPermissions[it.toolId]=it
                            toolPermissions[it.toolId]= it.copy()
                        }
                    }
                }
            }
            viewModelScope.launch {
                MCPDirectStudio.queryVirtualToolPermissions(it.id){
                        code, message, data ->
                    if(code==0&&data!=null){
                        _virtualToolPermissions.clear()
                        virtualToolPermissions.clear()
                        data.forEach {
                            _virtualToolPermissions[it.originalToolId]=it
                            virtualToolPermissions[it.originalToolId]=it.copy()
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
                    toolAgents.add(_virtualToolAgent)
                    if(data.isNotEmpty()) {
                        toolAgents.addAll(data)
                        selectToolAgent(toolAgents[0])
                    }
                }
            }
        }
    }

    fun selectAllTools(selectedAll: Boolean){
        toolMaker?.let {
            if(it.type>0)tools.forEach {
                selectTool(selectedAll,it)
            }else virtualTools.forEach {
                selectTool(selectedAll,it)
            }
        }
    }
    fun permissionsChanged():Boolean{
        if(virtualToolPermissions.size!=_virtualToolPermissions.size||
            toolPermissions.size!=_toolPermissions.size){
            return true;
        }
        for(v in toolPermissions.values){
            val p = _toolPermissions[v.toolId]
            if(p==null||p.status!=v.status){
                return true;
            }
        }
        for(v in virtualToolPermissions.values){
            val p = _virtualToolPermissions[v.originalToolId]
            if(p==null||p.status!=v.status){
                return true;
            }
        }
        return false
    }
    fun savePermissions(){
        if(permissionsChanged()) {
            viewModelScope.launch {
                MCPDirectStudio.grantToolPermission(
                    toolPermissions.values.toList(),
                    virtualToolPermissions.values.toList()
                )
            }
        }
    }

    fun resetPermissions(){
        toolMaker?.let {
            if(it.type==0){
                for(p in virtualToolPermissions.values){
                    if(p.makerId==it.id){
                        virtualToolPermissions.remove(p.originalToolId)
                    }
                }
                for(p in _virtualToolPermissions.values){
                    if(p.makerId==it.id) {
                        virtualToolPermissions[p.originalToolId] = p.copy()
                    }
                }
            }else{
                for(p in toolPermissions.values){
                    if(p.makerId==it.id){
                        toolPermissions.remove(p.toolId)
                    }
                }
                for(p in _toolPermissions.values){
                    if(p.makerId==it.id) {
                        toolPermissions[p.toolId] = p.copy()
                    }
                }
            }
        }
    }

    fun resetAllPermissions(){
        virtualToolPermissions.clear()
        toolPermissions.clear()
        for(p in _virtualToolPermissions.values){
            virtualToolPermissions[p.originalToolId] = p.copy()
        }
        for(p in _toolPermissions.values){
            toolPermissions[p.toolId] = p.copy()
        }
    }
}
