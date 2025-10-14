package ai.mcpdirect.studio.app.virtualmcp

import ai.mcpdirect.backend.dao.entity.aitool.AIPortTool
import ai.mcpdirect.backend.dao.entity.aitool.AIPortToolMaker
import ai.mcpdirect.backend.dao.entity.aitool.AIPortVirtualTool
import ai.mcpdirect.studio.MCPDirectStudio
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class VirtualMakerViewModel: ViewModel() {
    var errorMessage by mutableStateOf<String?>(null)
    var showAddServerDialog by mutableStateOf(false)
    var showEditServerNameDialog by mutableStateOf(false)
    var showEditServerTagsDialog by mutableStateOf(false)

    var showEditToolsView by mutableStateOf(false)

    var serverName by mutableStateOf("")
        private set
    var serverNameErrors by mutableStateOf(false)
        private set

    var serverTag by mutableStateOf("")
        private set
    var serverTagErrors by mutableStateOf(false)
        private set

    val serverTags = mutableStateSetOf<String>()

    var showValidationError by mutableStateOf(false)
        private set

    var searchQuery by mutableStateOf("")
        private set

    var selectedVirtualMaker by mutableStateOf<AIPortToolMaker?>(null)
        private set

    val _virtualMakers = mutableStateMapOf<Long,AIPortToolMaker>()
    val virtualMakers by derivedStateOf {
        _virtualMakers.values.toList()
    }
    var selectedMaker by mutableStateOf<AIPortToolMaker?>(null)
        private set
    val _makers = mutableStateMapOf<Long,AIPortToolMaker>()
    val makers by derivedStateOf {
        _makers.values.toList()
    }

    var selectedMakerTool by mutableStateOf<AIPortTool?>(null)
    var selectedMakerToolMetadata by mutableStateOf<String?>(null)
    val selectedMakerTools = mutableStateListOf<AIPortTool>()
    val _selectedVirtualMakerTools = mutableStateMapOf<Long,AIPortVirtualTool>()
    val selectedVirtualMakerTools by derivedStateOf {
        _selectedVirtualMakerTools.values.toList()
    }

    val newVirtualMakerTools = mutableStateMapOf<Long,AIPortTool>()

    fun selectAllSelectedMakerTools(){
        selectedMakerTools.forEach { tool ->
            newVirtualMakerTools[tool.id]=tool
        }
    }
    fun deselectAllSelectedMakerTools(){
        selectedMakerTools.forEach { tool ->
            newVirtualMakerTools.remove(tool.id)
        }
    }
    fun queryToolMakers(){
        MCPDirectStudio.queryToolMakers(null,null,null,null){
            code, message, data ->
            if(code==0&&data!=null){
                data.forEach {
                    if(it.type== AIPortToolMaker.TYPE_VIRTUAL){
                        _virtualMakers[it.id]=it
                    }else{
                        _makers[it.id]=it
                    }
                }
            }
        }
    }


    fun onServerNameChange(name: String) {
        val text = name.replace(" ","_")
        serverNameErrors = text.isBlank()&&text.length>32
        if(text.length<33) {
            serverName = text
        }
    }
    fun onServerTagChange(tag: String){
//        val text = tag.trim();
        serverTagErrors = tag.isNotBlank()&&tag.length<33
        if(serverTagErrors) {
            if(tag.contains(",")){
                serverTag = ""
                tag.split(",").forEach {
                    val text = it.trim()
                    if(text.isNotBlank())
                    serverTags.add(it.trim())
                    println(serverTags.joinToString())
                }
            }else serverTag = tag

        }
    }

    fun removeServerTag(tag:String){
        serverTags.remove(tag)
    }

    fun createServer() {
        MCPDirectStudio.createToolMaker(0, serverName, serverTags.joinToString()){
                code,message,data->
            if(code==0&&data!=null){
                _virtualMakers[data.id]=data
            }
        }
    }

    fun updateServerName() {
        selectedVirtualMaker?.let {
            MCPDirectStudio.modifyToolMakerName(it.id, serverName) { code, message, data ->
                if (code == 0 && data != null) {
                    _virtualMakers[data.id] = data
                }
            }
        }
    }

    fun updateServerTags() {
        selectedVirtualMaker?.let {
            MCPDirectStudio.modifyToolMakerTags(it.id, serverTags.joinToString()) { code, message, data ->
                if (code == 0 && data != null) {
                    _virtualMakers[data.id] = data
                }
            }
        }
    }

    fun setServerStatus(id:Long, status:Int) {
        MCPDirectStudio.modifyToolMakerStatus(id, status){
                code,message,data->
            if(code==0&&data!=null){
                _virtualMakers[data.id]=data
            }
        }
    }

    fun updateSearchQuery(query: String) {
        searchQuery = query
    }

    fun selectVirtualMaker(maker: AIPortToolMaker?){
        selectedVirtualMaker = maker
        serverName=""
        serverTag=""
        serverTags.clear()
        if(maker!=null) {
            serverName = maker.name
            maker.tags.split(",").forEach {
                val text = it.trim()
                if (text.isNotBlank())
                    serverTags.add(it.trim())
                println(serverTags.joinToString())
            }
        }
    }

    fun selectMaker(maker: AIPortToolMaker?){
        selectedMaker = maker
        selectedMakerTools.clear()
    }

    fun queryTools(){
        MCPDirectStudio.queryTools(null,1,null, selectedMaker?.id,null){
            code, message, data ->
            if(code==0&&data!=null){
                selectedMakerTools.addAll(data)
            }
        }
    }
    fun queryToolMetadata(id:Long){
        MCPDirectStudio.getTool(id){
                code, message, data ->
            if(code==0&&data!=null){
                val json = Json.parseToJsonElement(data.metaData)
                selectedMakerToolMetadata = json.jsonObject["description"]?.jsonPrimitive?.content
            }
        }
    }
    fun modifyVirtualMakerTools(){
        selectedVirtualMaker?.let {
            val tools = mutableListOf<AIPortVirtualTool>()
            newVirtualMakerTools.keys.forEach {
                if(!_selectedVirtualMakerTools.containsKey(it)){
                    val tool = AIPortVirtualTool()
                    tool.toolId = it
                    tool.status = 1
                    tools.add(tool)
                }
            }
            _selectedVirtualMakerTools.forEach {
                if(!newVirtualMakerTools.containsKey(it.key)){
                    val tool = AIPortVirtualTool()
                    tool.id = it.value.id
                    tool.status = -1
                    tools.add(tool)
                }
            }
            MCPDirectStudio.modifyVirtualTools(it.id,tools){
                    code, message, data ->
                if(code==0&&data!=null){
                    _selectedVirtualMakerTools.clear()
                    data.forEach {
                        _selectedVirtualMakerTools[it.id]=it
                    }

                }
            }
        }
    }
    fun queryVirtualMakerTools(){
        selectedVirtualMaker?.let {
            MCPDirectStudio.queryVirtualTools(it.id){
                    code, message, data ->
                if(code==0&&data!=null){
                    _selectedVirtualMakerTools.clear()
                    newVirtualMakerTools.clear()
                    data.forEach {
                        _selectedVirtualMakerTools[it.toolId]=it
                        newVirtualMakerTools[it.toolId]=it
                    }

                }
            }
        }
    }
}