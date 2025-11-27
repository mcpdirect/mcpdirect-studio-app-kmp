package ai.mcpdirect.studio.app.tool

import ai.mcpdirect.mcpdirectstudioapp.getPlatform
import ai.mcpdirect.studio.app.generalViewModel
import ai.mcpdirect.studio.app.model.account.AIPortAccessKey
import ai.mcpdirect.studio.app.model.account.AIPortTeam
import ai.mcpdirect.studio.app.model.aitool.*
import ai.mcpdirect.studio.app.model.repository.StudioRepository
import ai.mcpdirect.studio.app.model.repository.TeamRepository
import ai.mcpdirect.studio.app.model.repository.ToolRepository
import ai.mcpdirect.studio.app.model.repository.UserRepository
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

//val toolPermissionViewModel = ToolPermissionViewModel()
class ToolPermissionViewModel(val accessKey: AIPortAccessKey) : ViewModel(){
//    var accessKey by mutableStateOf<AIPortAccessKey?>(null)
//    private val _accessKey = MutableStateFlow(AIPortAccessKey())
//    private val _virtualToolAgent = AIPortToolAgent("Virtual MCP",-1)
//    var toolAgent by mutableStateOf<AIPortToolAgent?>(null)
//        private set
//    var toolMaker by mutableStateOf<AIPortToolMaker?>(null)
//        private set
    private val _toolAgent = MutableStateFlow(AIPortToolAgent())
    val toolAgent: StateFlow<AIPortToolAgent> = _toolAgent
    private val _toolMaker = MutableStateFlow(AIPortToolMaker())
    val toolMaker: StateFlow<AIPortToolMaker> = _toolMaker

    private val _team = MutableStateFlow(AIPortTeam())
    val team : StateFlow<AIPortTeam> = _team
    val teams: StateFlow<List<AIPortTeam>> = TeamRepository.teams
        .map { it.values.toList() }      // 转为 List
        .stateIn(
            scope = viewModelScope,      // 或 CoroutineScope(Dispatchers.Main.immediate)
            started = SharingStarted.WhileSubscribed(5000), // 按需启动
            initialValue = emptyList()
        )
//    val toolAgents = mutableStateListOf<AIPortToolAgent>()
    val toolAgents: StateFlow<List<AIPortToolAgent>> = StudioRepository.toolAgents
        .map { it.values.toList() }      // 转为 List
        .stateIn(
            scope = viewModelScope,      // 或 CoroutineScope(Dispatchers.Main.immediate)
            started = SharingStarted.WhileSubscribed(5000), // 按需启动
            initialValue = emptyList()
        )
//    val toolMakers = mutableStateListOf<AIPortToolMaker>()
//    val tools = mutableStateListOf<AIPortTool>()

//    val tools: StateFlow<List<AIPortToolMaker>> = combine(
//        ToolRepository.toolMakers,
//        _toolAgent
//    ) { toolsMap, maker ->
//        toolsMap.values.filter { tool -> tool.agentId == maker.id }.toList()
//    }.stateIn(
//        scope = viewModelScope,
//        started = SharingStarted.WhileSubscribed(5000),
//        initialValue = emptyList()
//    )
//
    // 2. 使用 combine 同时监听两个流
    val tools: StateFlow<List<AIPortTool>> = combine(
        ToolRepository.tools,
        _toolMaker
    ) { toolsMap, maker ->
        toolsMap.values.filter { tool -> tool.makerId == maker.id }.toList()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
//    val virtualTools = mutableStateListOf<AIPortVirtualTool>()

    val virtualTools: StateFlow<List<AIPortVirtualTool>> = combine(
        ToolRepository.virtualTools,
        _toolMaker
    ) { toolsMap, maker ->
        toolsMap.values.filter { tool -> tool.makerId == maker.id }.toList()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    private val _toolPermissions = mutableStateMapOf<Long, AIPortToolPermission>()
    private val _virtualToolPermissions = mutableStateMapOf<Long, AIPortVirtualToolPermission>()
    val toolPermissions = mutableStateMapOf<Long, AIPortToolPermission>()
    val virtualToolPermissions = mutableStateMapOf<Long, AIPortVirtualToolPermission>()

    fun reset(){
//        toolAgent = null
//        toolMaker = null
//        team = null
//        toolAgents.clear()
        toolPermissions.clear()
        virtualToolPermissions.clear()
//        tools.clear()
//        virtualTools.clear()
//        _toolPermissions.clear()
//        _virtualToolPermissions.clear()
    }

    fun toolSelected(tool: AIPortVirtualTool): Boolean{
        val permission = virtualToolPermissions[tool.toolId]
        return permission!=null&&permission.status>0
    }
    fun toolSelected(tool: AIPortTool): Boolean{
        val permission = toolPermissions[tool.id]
        return permission!=null&&permission.status>0
    }
//    fun toolsSelected():Boolean{
//        toolMaker?.let {
//            if(it.type==0) {
//                if(virtualToolPermissions.isEmpty()) return false
//                for (t in virtualTools) {
//                    val p = virtualToolPermissions[t.toolId]
//                    if (p!=null&&t.status > 0) return true
//                }
//            }else {
//                if(toolPermissions.isEmpty()) return false
//                for (t in tools) {
//                    val p = toolPermissions[t.id]
//                    if (p!=null&&t.status > 0) return true
//                }
//            }
//        }
//        return false
//    }
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
            permission.accessKeyId = accessKey.id
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
            permission.status = Short.MAX_VALUE.toInt()
            permission.makerId = tool.makerId
            permission.agentId = tool.agentId
            permission.accessKeyId = accessKey.id
            toolPermissions[permission.toolId]=permission
        }
    }

    fun countToolPermissions(agent:AIPortToolAgent):Int{
        var count = 0
        if(agent.id>0) toolPermissions.values.forEach {
            permission ->
            if(permission.agentId==agent.id&&permission.status>0){
                generalViewModel.toolMaker(permission.makerId)?.let {
                    if(UserRepository.me(it.userId)){
                        count++
                    }
                }
            }
        } else virtualToolPermissions.values.forEach {
            permission ->
            if(permission.agentId==agent.id&&permission.status>0){
                generalViewModel.toolMaker(permission.makerId)?.let {
                    if(UserRepository.me(it.userId)){
                        count++
                    }
                }
            }
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
    fun countToolPermissions(team: AIPortTeam):Int{
        var count = 0
        toolPermissions.values.forEach {
            generalViewModel.toolMaker(it.makerId)?.let {
                if(it.teamId==team.id){
                    count++
                }
            }
        }
        virtualToolPermissions.values.forEach {
            generalViewModel.toolMaker(it.makerId)?.let {
                if(it.teamId==team.id){
                    count++
                }
            }
        }
        return count
    }
    fun countToolPermissions():Int{
        val toolMaker = _toolMaker.value
        return countToolPermissions(toolMaker)
    }
    fun selectToolAgent(agent:AIPortToolAgent){
        _toolAgent.value = agent
//        toolAgent?.let {
//            tools.clear()
//            viewModelScope.launch {
//                generalViewModel.refreshToolMakers(toolAgentId = it.id)
//            }
//        }
        viewModelScope.launch {
            ToolRepository.loadToolMakers(true)
        }
    }

    fun selectToolMaker(maker:AIPortToolMaker){
        _toolMaker.value = maker
        viewModelScope.launch {
            viewModelScope.launch {
                if (maker.type == 0) ToolRepository.loadVirtualTools(toolMaker = maker)
                else ToolRepository.loadTools(toolMaker = maker)
            }
        }
//        toolMaker?.let {
//            tools.clear()
//            virtualTools.clear()
//            viewModelScope.launch {
//                if(it.type==0) getPlatform().queryVirtualTools(it.id){
//                        (code, message, data) ->
//                    if(code==0&&data!=null){
//                        if(data.isNotEmpty()) {
//                            virtualTools.addAll(data)
//                        }
//                    }
//                }
//                else getPlatform().queryTools(userId = it.userId,makerId = it.id){
//                        (code, message, data) ->
//                    if(code==0&&data!=null){
//                        if(data.isNotEmpty()) {
//                            tools.addAll(data)
//                        }
//                    }
//                }
//            }
//        }
    }

    fun refresh(){
        if(accessKey.id>Int.MAX_VALUE){
            viewModelScope.launch {
                ToolRepository.loadToolPermissions(accessKey){
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
//                getPlatform().queryToolPermissions(it.id){
//                    (code, message, data) ->
//                    if(code==0&&data!=null){
//                        _toolPermissions.clear()
//                        toolPermissions.clear()
//                        data.forEach {
//                            _toolPermissions[it.toolId]=it
//                            toolPermissions[it.toolId]= it.copy()
//                        }
//                    }
//                }
            }
            viewModelScope.launch {
                ToolRepository.loadVirtualToolPermissions(accessKey){
                    code, message, data ->
                    if(code==0&&data!=null){
                        _virtualToolPermissions.clear()
                        virtualToolPermissions.clear()
                        data.forEach {
                            _virtualToolPermissions[it.originalToolId]=it
                            virtualToolPermissions[it.originalToolId] = it.copy()
                        }
                    }
                }
//                getPlatform().queryVirtualToolPermissions(it.id){
//                        (code, message, data) ->
//                    if(code==0&&data!=null){
//                        _virtualToolPermissions.clear()
//                        virtualToolPermissions.clear()
//                        data.forEach {
//                            _virtualToolPermissions[it.originalToolId]=it
//                            virtualToolPermissions[it.originalToolId] = it.copy()
//                        }
//                    }
//                }
            }
        }
        viewModelScope.launch {
//            getPlatform().queryToolAgents { (code, message, data) ->
//                if(code==0&&data!=null){
//                    toolAgents.clear()
//                    toolAgents.add(_virtualToolAgent)
//                    if(data.isNotEmpty()) {
//                        toolAgents.addAll(data)
//                        if(toolAgent==null) selectToolAgent(toolAgents[0])
//                    }
//                }
//            }
            StudioRepository.loadToolAgents()
        }
    }

    fun selectAllTools(selectedAll: Boolean){
        val toolMaker = _toolMaker.value
        toolMaker.let {
            if(it.type>0)tools.value.forEach {
                selectTool(selectedAll,it)
            }else virtualTools.value.forEach {
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
                getPlatform().grantToolPermission(
                    toolPermissions.values.toList(),
                    virtualToolPermissions.values.toList()
                ){(code, message, data) ->
//                    if(code==0&&data!=null){
//                        mcpAccessKeyViewModel.toolPermissionMakerSummary.clear()
//                        mcpAccessKeyViewModel.toolPermissionMakerSummary.addAll(data)
//                    }
                }
            }
        }
    }

    fun resetPermissions(){
        val toolMaker = _toolMaker.value
        toolMaker.let {
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

    fun selectTeam(team: AIPortTeam){
        _team.value=team
//        this.team?.let {
//            tools.clear()
            generalViewModel.refreshTeamToolMakers()
            generalViewModel.refreshTeamToolMakerTemplates()
            generalViewModel.refreshToolMakers()
//            viewModelScope.launch {
//                generalViewModel.refreshToolMakers()
//            }
//        }
    }
    fun refreshTeams() {
        viewModelScope.launch {
            TeamRepository.loadTeams(true)
        }
    }
    fun refreshTeamToolMakers(){
        viewModelScope.launch {
            TeamRepository.loadTeamToolMakers(true)
        }
    }

    fun refreshTeamToolMakerTemplates(){
        viewModelScope.launch {
            TeamRepository.loadTeamToolMakerTemplates(true)
        }
    }
}
