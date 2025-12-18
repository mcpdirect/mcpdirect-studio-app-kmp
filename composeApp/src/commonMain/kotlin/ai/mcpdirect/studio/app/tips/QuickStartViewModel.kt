package ai.mcpdirect.studio.app.tips

import ai.mcpdirect.mcpdirectstudioapp.getPlatform
import ai.mcpdirect.studio.app.model.AIPortServiceResponse
import ai.mcpdirect.studio.app.model.MCPServer
import ai.mcpdirect.studio.app.model.MCPServerConfig
import ai.mcpdirect.studio.app.model.OpenAPIServer
import ai.mcpdirect.studio.app.model.aitool.AIPortTool
import ai.mcpdirect.studio.app.model.aitool.AIPortToolAccessKey
import ai.mcpdirect.studio.app.model.aitool.AIPortToolAgent
import ai.mcpdirect.studio.app.model.aitool.AIPortToolMaker
import ai.mcpdirect.studio.app.model.aitool.AIPortToolPermission
import ai.mcpdirect.studio.app.model.repository.AccessKeyRepository
import ai.mcpdirect.studio.app.model.repository.StudioRepository
import ai.mcpdirect.studio.app.model.repository.UserRepository
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.collections.set

class QuickStartViewModel: ViewModel() {

    val accessKeys: StateFlow<List<AIPortToolAccessKey>> = AccessKeyRepository.accessKeys
        .map { it.values.toList() }      // 转为 List
        .stateIn(
            scope = viewModelScope,      // 或 CoroutineScope(Dispatchers.Main.immediate)
            started = SharingStarted.WhileSubscribed(5000), // 按需启动
            initialValue = emptyList()
        )
    var currentAccessKey by mutableStateOf<AIPortToolAccessKey?>(null)
        private set
    fun selectAccessKey(accessKey: AIPortToolAccessKey){
        currentAccessKey = accessKey
    }
    fun selectedAccessKey(accessKey: AIPortToolAccessKey): Boolean{
        return currentAccessKey?.id == accessKey.id
    }
    val toolMakers: StateFlow<List<AIPortToolMaker>> = combine(
        StudioRepository.toolMakers,
        StudioRepository.localToolAgent,
        UserRepository.me,
    ) { servers, agent, me ->
        servers.values.filter { server -> server.agentId == agent.id && server.userId == me.id }.toList()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _selectedToolMakers = mutableStateMapOf<Long,AIPortToolMaker>()
    val selectedToolMakers by derivedStateOf {
        _selectedToolMakers.values.toList()
    }
    fun selectToolMaker(selected:Boolean,maker: AIPortToolMaker){
        if(selected) {
            _selectedToolMakers[maker.id] = maker
            currentToolMaker(maker)
        }
        else _selectedToolMakers.remove(maker.id)
    }

    fun selectedToolMaker(maker: AIPortToolMaker): Boolean{
        return _selectedToolMakers.containsKey(maker.id)
    }

    private val _tools = mutableStateMapOf<Long,AIPortTool>()
    val tools by derivedStateOf {
        _tools.values.sortedBy { it.name }.toList()
    }
    fun countTools(toolMaker: AIPortToolMaker): Int{
        return _tools.values.count { it.makerId == toolMaker.id }
    }
    private val _selectedTools = mutableStateMapOf<Long,AIPortTool>()
    val selectedTools by derivedStateOf { _selectedTools.values.toList() }

    fun selectTool(selected:Boolean,tool: AIPortTool){
        if(selected) _selectedTools[tool.id] = tool
        else _selectedTools.remove(tool.id)
    }
    fun selectedTool(tool: AIPortTool):Boolean{
        return _selectedTools.containsKey(tool.id)
    }
    fun selectAllTools(selected: Boolean,toolMaker: AIPortToolMaker){
        if(selected) {
            for (entry in _tools) if (entry.value.makerId == toolMaker.id) {
                _selectedTools[entry.key] = entry.value
            }
        } else for (entry in _selectedTools) if(entry.value.makerId==toolMaker.id) {
            _selectedTools.remove(entry.key)
        }
    }
    fun countSelectedTools(toolMaker: AIPortToolMaker): Int{
        return _selectedTools.values.count { it.makerId == toolMaker.id }
    }

//    var currentToolMaker by mutableStateOf<AIPortToolMaker?>(null)
//        private set
    private var _currentToolMaker:MutableStateFlow<AIPortToolMaker?> = MutableStateFlow(AIPortToolMaker())
    val currentToolMaker : StateFlow<AIPortToolMaker?> = combine(
        StudioRepository.toolMakers,
        _currentToolMaker,
    ) { servers, toolMaker ->
        if(toolMaker!=null)servers[toolMaker.id] else null
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )
    val currentTools = mutableStateListOf<AIPortTool>()
    fun updateCurrentToolMaker(maker: AIPortToolMaker){
        if(_currentToolMaker.value?.id==maker.id&&maker.status==1&&maker.errorCode==0){
            currentTools.clear()
            viewModelScope.launch {
                when(maker){
                    is MCPServer -> StudioRepository.queryMCPToolsFromStudio(
                        StudioRepository.localToolAgent.value,maker
                    ){
                        if(maker.id==currentToolMaker.value?.id&&it.successful()) it.data?.let{ data ->
                            currentTools.addAll(data)
                            for (tool in data) {
                                _tools[tool.id] = tool
                                _selectedTools[tool.id] = tool
                            }
                        }
                    }
                    is OpenAPIServer -> StudioRepository.queryOpenAPIToolsFromStudio(
                        StudioRepository.localToolAgent.value,maker
                    ){
                        if(maker.id==currentToolMaker.value?.id&&it.successful()) it.data?.let{ data ->
                            currentTools.addAll(data)
                            for (tool in data) {
                                _tools[tool.id] = tool
                                _selectedTools[tool.id] = tool
                            }
                        }
                    }
                }
            }
        }
    }
    fun currentToolMaker(maker: AIPortToolMaker?){
        _currentToolMaker.value = maker
        if(maker!=null)updateCurrentToolMaker(maker)
    }

    fun modifyMCPServerConfig(mcpServer: MCPServer,config: MCPServerConfig) {
        viewModelScope.launch {
            StudioRepository.modifyMCPServerConfigForStudio(
                StudioRepository.localToolAgent.value,
                mcpServer,config
            ){
                if(it.successful()) it.data?.let{ data ->
                    currentToolMaker.value?.let{
                        if(it.id==data.id) currentToolMaker(data)
                    }
                }
            }
        }
    }
    fun modifyToolMakerStatus(toolAgent: AIPortToolAgent,maker: AIPortToolMaker,status: Int){
        viewModelScope.launch {
            StudioRepository.modifyToolMakerStatus(toolAgent,maker,status){
                if(it.successful()) it.data?.let{ data ->
                    currentToolMaker.value?.let{
                        if(it.id==data.id) currentToolMaker(data)
                    }
                }
            }
        }
    }
    fun installMCPServer(
        config: MCPServerConfig,
        onResponse: (resp: AIPortServiceResponse<MCPServer>) -> Unit
    ) {
        viewModelScope.launch { StudioRepository.connectMCPServerToStudio(
            StudioRepository.localToolAgent.value, config, onResponse
        ) }
    }

    fun grantToolPermissions(){
        currentAccessKey?.let { accessKey ->
            val toolPermissions = _selectedTools.values.map { tool ->
                AIPortToolPermission().apply {
                    toolId = tool.id
                    status = Short.MAX_VALUE.toInt()
                    makerId = tool.makerId
                    agentId = tool.agentId
                    accessKeyId = accessKey.id
                }
            }.toList()
            if(toolPermissions.isNotEmpty()) viewModelScope.launch {
                getPlatform().grantToolPermission(
                    toolPermissions, null
                ){(code, message, data) ->
                }
            }
        }
    }
}