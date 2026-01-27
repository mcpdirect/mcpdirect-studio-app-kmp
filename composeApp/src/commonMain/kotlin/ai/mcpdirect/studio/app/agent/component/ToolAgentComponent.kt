package ai.mcpdirect.studio.app.agent.component

import ai.mcpdirect.mcpdirectstudioapp.getPlatform
import ai.mcpdirect.studio.app.compose.ListButton
import ai.mcpdirect.studio.app.compose.StudioActionBar
import ai.mcpdirect.studio.app.compose.StudioBoard
import ai.mcpdirect.studio.app.compose.StudioListItem
import ai.mcpdirect.studio.app.compose.Tag
import ai.mcpdirect.studio.app.mcp.ConfigMCPServerView
import ai.mcpdirect.studio.app.mcp.openapi.ConfigOpenAPIServerView
import ai.mcpdirect.studio.app.model.AIPortServiceResponse
import ai.mcpdirect.studio.app.model.MCPServer
import ai.mcpdirect.studio.app.model.MCPServerConfig
import ai.mcpdirect.studio.app.model.OpenAPIServer
import ai.mcpdirect.studio.app.model.OpenAPIServerConfig
import ai.mcpdirect.studio.app.model.aitool.AIPortMCPServer
import ai.mcpdirect.studio.app.model.aitool.AIPortTool
import ai.mcpdirect.studio.app.model.aitool.AIPortToolAccessKey
import ai.mcpdirect.studio.app.model.aitool.AIPortToolAgent
import ai.mcpdirect.studio.app.model.aitool.AIPortToolMaker
import ai.mcpdirect.studio.app.model.aitool.AIPortToolMaker.Companion.ERROR
import ai.mcpdirect.studio.app.model.aitool.AIPortToolMaker.Companion.STATUS_OFF
import ai.mcpdirect.studio.app.model.aitool.AIPortToolMaker.Companion.STATUS_WAITING
import ai.mcpdirect.studio.app.model.aitool.AIPortToolPermission
import ai.mcpdirect.studio.app.model.repository.AccessKeyRepository
import ai.mcpdirect.studio.app.model.repository.StudioRepository
import ai.mcpdirect.studio.app.model.repository.UserRepository
import ai.mcpdirect.studio.app.tips.ConnectMCPViewAction
import ai.mcpdirect.studio.app.tips.mcpServerCatalog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import mcpdirectstudioapp.composeapp.generated.resources.Res
import mcpdirectstudioapp.composeapp.generated.resources.check
import mcpdirectstudioapp.composeapp.generated.resources.design_services
import mcpdirectstudioapp.composeapp.generated.resources.error
import mcpdirectstudioapp.composeapp.generated.resources.inbox_empty
import mcpdirectstudioapp.composeapp.generated.resources.mobiledata_off
import mcpdirectstudioapp.composeapp.generated.resources.more
import org.jetbrains.compose.resources.painterResource
import kotlin.collections.iterator
import kotlin.collections.set

class ToolAgentComponentViewModel: ViewModel() {
    private val _currentToolAgent = MutableStateFlow(AIPortToolAgent())
    val currentToolAgent: StateFlow<AIPortToolAgent> = _currentToolAgent
    fun currentToolAgent(agent:AIPortToolAgent){
        _currentToolAgent.value = agent
        viewModelScope.launch {
            StudioRepository.queryToolMakersFromStudio(agent,true)
        }
    }
    val toolAgents: StateFlow<List<AIPortToolAgent>> = StudioRepository.toolAgents
        .map { it.values.filter { it.id!=0L }.sortedBy { it.name } }      // 转为 List
        .stateIn(
            scope = viewModelScope,      // 或 CoroutineScope(Dispatchers.Main.immediate)
            started = SharingStarted.WhileSubscribed(5000), // 按需启动
            initialValue = emptyList()
        )

    val toolMakers: StateFlow<List<AIPortToolMaker>> = combine(
        StudioRepository.toolMakers,
        _currentToolAgent,
        UserRepository.me,
    ) { servers, agent, me ->
        servers.values.filter {
                server -> agent.id > 0L && server.agentId == agent.id && server.userId == me.id
        }.sortedBy { it.name }
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
                        currentToolAgent.value,maker
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
                        currentToolAgent.value,maker
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

    fun modifyMCPServerConfig(
        mcpServer: MCPServer,name:String?=null,status:Int?=null,config: MCPServerConfig?=null,
        onResponse: (resp:AIPortServiceResponse<MCPServer>)->Unit
    ) {
        viewModelScope.launch {
            StudioRepository.modifyMCPServerForStudio(
                currentToolAgent.value,
                mcpServer,name,status,config
            ){
                if(it.successful()) it.data?.let{ data ->
                    currentToolMaker.value?.let{
                        if(it.id==data.id) currentToolMaker(data)
                    }
                }
                onResponse(it)
            }
        }
    }
    fun modifyToolMakerStatus(maker: AIPortToolMaker,status: Int){
        viewModelScope.launch {
            StudioRepository.modifyToolMakerStatus(currentToolAgent.value,maker,status){
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
    ) { viewModelScope.launch { StudioRepository.connectMCPServerToStudio(
        currentToolAgent.value, config, onResponse
    ) } }

    fun installOpenAPIServer(
        config:OpenAPIServerConfig,
        onResponse: (resp: AIPortServiceResponse<OpenAPIServer>) -> Unit
    ){
        viewModelScope.launch {
            StudioRepository.connectOpenAPIServerToStudio(
                currentToolAgent.value,config,onResponse
            )
        }
    }
    fun modifyOpenAPIServerConfig(
        server: OpenAPIServer,name:String?=null,status:Int?=null,config: OpenAPIServerConfig?=null,
        onResponse: (resp:AIPortServiceResponse<OpenAPIServer>)->Unit
    ){
        viewModelScope.launch {
            StudioRepository.modifyOpenAPIServerForStudio(
                currentToolAgent.value,
                server,name,status,config
            ){
                if(it.successful()) it.data?.let{ data ->
                    currentToolMaker.value?.let{
                        if(it.id==data.id) currentToolMaker(data)
                    }
                }
                onResponse(it)
            }
        }
    }
    fun getOpenAPIServerConfig(
        maker: AIPortToolMaker,
        onResponse: (resp: AIPortServiceResponse<OpenAPIServerConfig>) -> Unit
    ){
        viewModelScope.launch {
            StudioRepository.getOpenAPIServerConfigFromStudio(
                currentToolAgent.value,maker
            ){
                onResponse(it)
            }
        }
    }

    fun removeToolMaker(toolMaker: AIPortToolMaker) {
        viewModelScope.launch {
            if(toolMaker.mcp())
                StudioRepository.removeMCPServerFromStudio(currentToolAgent.value,toolMaker)
            else if(toolMaker.openapi())
                StudioRepository.removeOpenAPIServerFromStudio(currentToolAgent.value,toolMaker)
        }
    }
    fun grantToolPermissions(accessKey: AIPortToolAccessKey,tools:List<AIPortTool>){
        val toolPermissions = tools.map { tool ->
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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolAgentSelectionMenu(
    viewModel: ToolAgentComponentViewModel,
    modifier: Modifier = Modifier,
) {
    val toolAgents by viewModel.toolAgents.collectAsState()
    val localToolAgent by StudioRepository.localToolAgent.collectAsState()
    val currentToolAgent by viewModel.currentToolAgent.collectAsState()
    LaunchedEffect(null){
        if (currentToolAgent.id < 1L && toolAgents.isNotEmpty()) {
            if(localToolAgent.id>0L) viewModel.currentToolAgent(localToolAgent)
            else viewModel.currentToolAgent(toolAgents[0])
        }
    }
    if(toolAgents.isNotEmpty()){
        var expanded by remember { mutableStateOf(false) }
        var checkedIndex: Int? by remember { mutableStateOf(null) }
        ExposedDropdownMenuBox(
            expanded = expanded, onExpandedChange = { expanded = it },
            modifier = modifier,
        ) {
//            TextField(
//                // The `menuAnchor` modifier must be passed to the text field to handle
//                // expanding/collapsing the menu on click. A read-only text field has
//                // the anchor type `PrimaryNotEditable`.
//                modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
//                state = textFieldState,
//                readOnly = true,
//                lineLimits = TextFieldLineLimits.SingleLine,
//                label = { Text("Label") },
//                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
//                colors = ExposedDropdownMenuDefaults.textFieldColors(),
//            )
            OutlinedButton(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp),
                onClick = {
                    expanded = !expanded
                }
            ) {
//                if (currentToolAgent.id < 1L && toolAgents.isNotEmpty()) {
//                    if(localToolAgent.id>0L) viewModel.currentToolAgent(localToolAgent)
//                    else viewModel.currentToolAgent(toolAgents[0])
//                }
                Icon(painterResource(Res.drawable.design_services), contentDescription = "")
                Spacer(Modifier.size(8.dp))
                BadgedBox(
                    badge = {
                        if (currentToolAgent.id == localToolAgent.id) Badge(Modifier.padding(start = 8.dp)) {
                            Text(
                                "This device",
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                ) {
                    Text(
                        currentToolAgent.name, softWrap = false,
                        overflow = TextOverflow.MiddleEllipsis
                    )
                }
//                Text(if (localToolAgent.id == currentToolAgent.id) "This Device" else currentToolAgent.name,
//                     fontWeight = FontWeight.Bold,)
                Spacer(Modifier.weight(1f))
//                TooltipIconButton(
//                    "Edit MCPdirect Studio name" ,
//                    onClick = {},
//                    Modifier.size(32.dp)
//                ){
//                    Icon(painterResource(Res.drawable.edit), contentDescription = "", Modifier.size(20.dp))
//                }
                Icon(painterResource(Res.drawable.more), contentDescription = "")
            }
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
//            containerColor = MenuDefaults.groupStandardContainerColor,
//            shape = MenuDefaults.standaloneGroupShape,
            ) {
//                val optionCount = options.size
                toolAgents.forEachIndexed { index, option ->
                    DropdownMenuItem(
//                    shapes = MenuDefaults.itemShape(index, optionCount),
                        text = {
                            BadgedBox(
                                badge = {
                                    if (option.id == localToolAgent.id) Badge(Modifier.padding(start = 8.dp)) {
                                        Text(
                                            "This device",
                                            fontWeight = FontWeight.Bold,
                                        )
                                    }
                                }
                            ) {
                                Text(
                                    option.name, softWrap = false,
                                    overflow = TextOverflow.MiddleEllipsis
                                )
                            }
//                            Text(option.name, style = MaterialTheme.typography.bodyLarge)
                        },
//                    selected = index == checkedIndex,
                        onClick = {
//                            textFieldState.setTextAndPlaceCursorAtEnd(option.name)
                            checkedIndex = index
                            expanded = false
                            viewModel.currentToolAgent(option)
                        },
                        leadingIcon = {
                            if(checkedIndex == index)
                                Icon(painterResource(Res.drawable.check), contentDescription = null)
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    )
                }
            }
        }
    }
}
@Composable
fun ToolAgentComponent(
    modifier: Modifier,
    viewModel: ToolAgentComponentViewModel,
    showCatalog: Long = -1
){
    val currentToolAgent by viewModel.currentToolAgent.collectAsState()
    val currentToolMaker by viewModel.currentToolMaker.collectAsState()
    val toolMakers by viewModel.toolMakers.collectAsState()
    var action by remember { mutableStateOf(ConnectMCPViewAction.MAIN) }
    var catalog by remember { mutableStateOf(showCatalog) }
    var currentMCPTemplate by remember {
        mutableStateOf(AIPortMCPServer(if(showCatalog<0) 0 else showCatalog))
    }
    Row(modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)){
        OutlinedCard(Modifier.weight(1f)) {
            if(catalog<0) {
                StudioActionBar (
                    "Installed MCP servers",
                ){
                    TextButton(
                        modifier = Modifier.height(32.dp),
                        contentPadding = PaddingValues(8.dp,0.dp),
                        onClick = {
                            catalog = 0
                        }) {
                        Text(
                            "MCP Catalog",
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
                ToolAgentSelectionMenu(viewModel, Modifier.padding(16.dp,8.dp))
                if (toolMakers.isEmpty()) StudioBoard(Modifier.weight(1f)) {
                    Icon(
                        painterResource(Res.drawable.inbox_empty),
                        contentDescription = null,
                        modifier = Modifier.size(80.dp).padding(bottom = 16.dp)
                    )
                    Text("No MCP server installed.")
                    Text("Install one from MCP catalog.")
                } else LazyColumn(
                    Modifier.weight(1f).padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(toolMakers) { toolMaker ->
                        ListButton(
                            selected = currentToolMaker?.id == toolMaker.id,
                            onClick =  {
                                action = ConnectMCPViewAction.MAIN
                                viewModel.currentToolMaker(toolMaker)
                            },
                            headlineContent = { Text(toolMaker.name, style = MaterialTheme.typography.bodyMedium) },
                            trailingContent = {
                                if (toolMaker.status == STATUS_OFF) Icon(
                                    painterResource(Res.drawable.mobiledata_off),
                                    contentDescription = "Disconnect",
                                    Modifier.size(48.dp).padding(12.dp),
                                    tint = MaterialTheme.colorScheme.error
                                ) else if (toolMaker.status == STATUS_WAITING) {
                                    CircularProgressIndicator(
                                        Modifier.size(48.dp).padding(8.dp),
                                    )
                                } else if (toolMaker.errorCode == ERROR) Icon(
                                    painterResource(Res.drawable.error),
                                    contentDescription = "Error",
                                    Modifier.size(48.dp).padding(12.dp),
                                    tint = MaterialTheme.colorScheme.error
                                ) else {
                                    LaunchedEffect(toolMaker) {
                                        viewModel.updateCurrentToolMaker(toolMaker)
                                    }
                                    Checkbox(
                                        checked = viewModel.selectedToolMaker(toolMaker),
                                        onCheckedChange = {
                                            viewModel.selectToolMaker(it, toolMaker)
                                        },
                                    )
                                }
                            },
                            supportingContent = {
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Badge(
                                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                    ) {
                                        when (toolMaker) {
                                            is MCPServer -> {
                                                if (toolMaker.transport == 0) {
                                                    Text("STDIO")
                                                } else if (toolMaker.transport == 1) {
                                                    Text("SSE")
                                                } else if (toolMaker.transport == 2) {
                                                    Text("HTTP")
                                                }
                                            }
                                            is OpenAPIServer -> {
                                                Text("OpenAPI")
                                            }
                                        }
                                    }
                                }
                            }
                        )
                    }
                }
            }else{
                StudioActionBar (
                    "MCP Catalog",
                ){
                    TextButton(
                        modifier = Modifier.height(32.dp),
                        contentPadding = PaddingValues(8.dp,0.dp),
                        onClick = {
                            catalog = -1
                        }) {
                        Text(
                            "Installed MCP servers",
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
//                    HorizontalDivider()
                ToolAgentSelectionMenu(viewModel, Modifier.padding(16.dp,8.dp))
                LazyColumn(Modifier.weight(1f)){
                    items(mcpServerCatalog) { mcpServer ->
                        if (mcpServer.id <100) {
                            when(mcpServer.id){
                                0L -> StudioListItem(
                                    selected = currentMCPTemplate.id==mcpServer.id,
                                    headlineContent = { Text("MCP Server") },
                                    modifier = Modifier.clickable {currentMCPTemplate = mcpServer }
                                )
                                1L -> StudioListItem(
                                    selected =
                                        currentMCPTemplate.id==mcpServer.id,
                                    headlineContent = { Text("OpenAPI") },
                                    modifier = Modifier.clickable {currentMCPTemplate = mcpServer }
                                )
                                -1L -> HorizontalDivider()
                            }
                        }else StudioListItem(
                            selected = currentMCPTemplate.id==mcpServer.id,
                            headlineContent = { Text(mcpServer.name) },
                            modifier = Modifier.clickable {currentMCPTemplate = mcpServer }
                        )
                    }
                }
            }
        }
        Card(Modifier.weight(2f)) {
            if(catalog<0&&toolMakers.isNotEmpty()) {
                currentToolMaker?.let { toolMaker ->
                    when (action) {
                        ConnectMCPViewAction.MAIN -> {
                            ToolMakerComponent(toolMaker, viewModel, Modifier.weight(2f)) {
                                action = it
                            }
                        }

                        ConnectMCPViewAction.CONFIG_MCP -> {
                            ConfigMCPServerView(
                                currentToolAgent, toolMaker as MCPServer, Modifier.weight(2f),
                                onBack = { action = ConnectMCPViewAction.MAIN }
                            ) { config, changed ->
                                viewModel.modifyMCPServerConfig(
                                    toolMaker, config.name,
                                    config = if (changed) config else null
                                ) { if (it.successful()) action = ConnectMCPViewAction.MAIN }
                            }
                        }

                        ConnectMCPViewAction.CONFIG_MCP_TEMPLATE -> {

                        }

                        ConnectMCPViewAction.CONFIG_OPENAPI -> {
                            if(toolMaker is OpenAPIServer) {
                                var openAPIServerConfig by remember { mutableStateOf<OpenAPIServerConfig?>(null) }
                                LaunchedEffect(toolMaker) {
                                    viewModel.getOpenAPIServerConfig(toolMaker) {
                                        if (it.successful()) openAPIServerConfig = it.data
                                    }
                                }
                                openAPIServerConfig?.let { config ->
                                    ConfigOpenAPIServerView(
                                        toolMaker.name, currentToolAgent, config,
                                        onBack = { action = ConnectMCPViewAction.MAIN }
                                    ) {
                                        viewModel.modifyOpenAPIServerConfig(
                                            toolMaker, null, null, it
                                        ) {
                                            if (it.successful()) action = ConnectMCPViewAction.MAIN
                                        }
                                    }
                                } ?: StudioBoard(modifier) {
                                    CircularProgressIndicator()
                                    Text("${toolMaker.name} config loading")
                                }
                            }
                        }
                    }

                }
            } else {
                fun installMCPServer(config: MCPServerConfig){
                    viewModel.installMCPServer(config){
                        if(it.successful()) it.data?.let { data ->
                            viewModel.currentToolMaker(data)
                            viewModel.selectToolMaker(true,data)
                            catalog = -1
                        }
                    }
                }
                when(currentMCPTemplate.id){
                    0L -> ConfigMCPServerView(currentToolAgent,modifier = Modifier.weight(2f)){ config,changed ->
                        installMCPServer(config)
                    }
                    1L -> ConfigOpenAPIServerView(toolAgent = currentToolAgent, modifier = Modifier.weight(2f)){ config ->
                        viewModel.installOpenAPIServer(config){
                            if(it.successful()) it.data?.let { data ->
                                viewModel.currentToolMaker(data)
                                viewModel.selectToolMaker(true,data)
                                catalog = -1
                            }
                        }
                    }
                    else -> ConfigMCPServerView(currentToolAgent,currentMCPTemplate,Modifier.weight(2f)){ config ->
                        installMCPServer(config)
                    }
                }
            }
        }
    }
}