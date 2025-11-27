package ai.mcpdirect.studio.app.virtualmcp

import ai.mcpdirect.mcpdirectstudioapp.getPlatform
import ai.mcpdirect.studio.app.UIState
import ai.mcpdirect.studio.app.model.aitool.AIPortTool
import ai.mcpdirect.studio.app.model.aitool.AIPortVirtualTool
import ai.mcpdirect.studio.app.model.aitool.AIPortToolMaker
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

val virtualMakerViewModel = VirtualMakerViewModel()
class VirtualMakerViewModel: ViewModel() {
    var uiState by mutableStateOf<UIState>(UIState.Idle)
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

    val newVirtualMakerTools = mutableStateMapOf<Long, AIPortTool>()

    fun reset(){
        _virtualMakers.clear()
        _makers.clear()
    }

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
        uiState = UIState.Loading
        viewModelScope.launch {
            getPlatform().queryToolMakers(){
                    (code, message, data) ->
                if(code==0&&data!=null){
                    data.forEach {
                        if(it.virtual()){
                            _virtualMakers[it.id]=it
                        }else{
                            _makers[it.id]=it
                        }
                    }
                    uiState = UIState.Success
                }else uiState = UIState.Error(code)
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
        uiState = UIState.Loading
        viewModelScope.launch {
            getPlatform().createToolMaker(
                AIPortToolMaker.TYPE_VIRTUAL, serverName,
                serverTags.joinToString()){ (code,message,data)->
                if(code==0){
                    data?.let {
                        _virtualMakers[it.id]=it
                    }
                    uiState = UIState.Success
                }else uiState = UIState.Error(code)
            }
        }

    }

    fun updateServerName() {
        selectedVirtualMaker?.let {
            uiState = UIState.Loading
            viewModelScope.launch {
                getPlatform().modifyToolMaker(it.id, serverName,null,null) {
                        (code, message, data) ->
                    if (code == 0) data?.let{
                        _virtualMakers[data.id] = data
                        uiState = UIState.Success
                    }else uiState = UIState.Error(code)
                }
            }
        }
    }
    fun updateServerName(toolMaker: AIPortToolMaker,toolMakerName:String) {
        uiState = UIState.Loading
        viewModelScope.launch {
            getPlatform().modifyToolMaker(toolMaker.id, toolMakerName,null,null) {
                    (code, message, data) ->
                if (code == 0) data?.let{
                    _virtualMakers[data.id] = data
                    uiState = UIState.Success
                }else uiState = UIState.Error(code)
            }
        }
    }

    fun updateServerTags() {
        selectedVirtualMaker?.let {
            getPlatform().modifyToolMaker(it.id,null, serverTags.joinToString(),null) {
                (code, message, data) ->
                if (code == 0 && data != null) {
                    _virtualMakers[data.id] = data
                }
            }
        }
    }
    fun updateServerTags(toolMaker: AIPortToolMaker,toolMakerTags:String) {
        getPlatform().modifyToolMaker(toolMaker.id,null, toolMakerTags,null) {
                (code, message, data) ->
            if (code == 0 && data != null) {
                _virtualMakers[data.id] = data
            }
        }
    }

    fun setServerStatus(id:Long, status:Int) {
        getPlatform().modifyToolMaker(id, null,null,status){
                (code,message,data)->
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
            serverName = maker.name?:""
            maker.tags?.let{
                it.split(",").forEach {
                    val text = it.trim()
                    if (text.isNotBlank())
                        serverTags.add(it.trim())
                    println(serverTags.joinToString())
                }
            }
        }
    }

    fun selectMaker(maker: AIPortToolMaker?){
        selectedMaker = maker
        selectedMakerTools.clear()
    }

    fun queryTools(){
        getPlatform().queryTools(makerId=selectedMaker?.id){
            (code, message, data) ->
            if(code==0&&data!=null){
                selectedMakerTools.addAll(data)
            }
        }
    }
    fun queryToolMetadata(id:Long){
        getPlatform().getTool(id){
                (code, message, data) ->
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
            getPlatform().modifyVirtualTools(it.id,tools){
                    (code, message, data) ->
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
            getPlatform().queryVirtualTools(makerId=it.id){
                    (code, message, data) ->
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