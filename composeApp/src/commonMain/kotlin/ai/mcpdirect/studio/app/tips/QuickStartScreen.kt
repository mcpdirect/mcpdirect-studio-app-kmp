package ai.mcpdirect.studio.app.tips

import ai.mcpdirect.mcpdirectstudioapp.AppInfo
import ai.mcpdirect.mcpdirectstudioapp.JSON
import ai.mcpdirect.mcpdirectstudioapp.getPlatform
import ai.mcpdirect.studio.app.Screen
import ai.mcpdirect.studio.app.compose.*
import ai.mcpdirect.studio.app.generalViewModel
import ai.mcpdirect.studio.app.key.component.AIAgentGuideComponent
import ai.mcpdirect.studio.app.key.component.AIAgentListComponent
import ai.mcpdirect.studio.app.mcp.ConfigMCPServerView
import ai.mcpdirect.studio.app.mcp.openapi.ConfigOpenAPIServerView
import ai.mcpdirect.studio.app.model.MCPServer
import ai.mcpdirect.studio.app.model.MCPServerConfig
import ai.mcpdirect.studio.app.model.OpenAPIServer
import ai.mcpdirect.studio.app.model.OpenAPIServerConfig
import ai.mcpdirect.studio.app.model.aitool.AIPortMCPServer
import ai.mcpdirect.studio.app.model.aitool.AIPortTool
import ai.mcpdirect.studio.app.model.aitool.AIPortToolAccessKey
import ai.mcpdirect.studio.app.model.aitool.AIPortToolMaker
import ai.mcpdirect.studio.app.model.aitool.AIPortToolMaker.Companion.ERROR
import ai.mcpdirect.studio.app.model.aitool.AIPortToolMaker.Companion.STATUS_OFF
import ai.mcpdirect.studio.app.model.aitool.AIPortToolMaker.Companion.STATUS_WAITING
import ai.mcpdirect.studio.app.model.repository.AccessKeyRepository
import ai.mcpdirect.studio.app.model.repository.StudioRepository
import ai.mcpdirect.studio.app.model.repository.ToolRepository
import ai.mcpdirect.studio.app.model.repository.UserRepository
import ai.mcpdirect.studio.app.tool.ToolDetails
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import mcpdirectstudioapp.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource

@Composable
fun QuickStartScreen(
    paddingValues: PaddingValues
) {
    val viewModel by remember { mutableStateOf(QuickStartViewModel()) }

    var stepIndex by remember { mutableStateOf(0) }
    val steps = listOf(
        "1. Connect MCP servers to MCPdirect",
        "2. Generate MCPdirect key for access",
        "3. Integrate MCPdirect with AI Agents"
    )
    Column(Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp)) {
        Row(
            Modifier.fillMaxWidth().padding(top = 16.dp,bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            repeat(steps.size) { index ->
                Box(
                    Modifier.background(
                        if(stepIndex>=index) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                        ButtonDefaults.shape,
                    ).height(40.dp).padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ){
                    Text(steps[index], color = if(stepIndex==index) MaterialTheme.colorScheme.onTertiaryContainer else MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        when(stepIndex){
            0 -> ConnectMCPView(Modifier.weight(1f),viewModel)
            1 -> GenerateMCPdirectKeyView(Modifier.weight(1f),viewModel)
            2 -> ConfigAIAgentView(Modifier.weight(1f),viewModel)
        }
        Row(
            Modifier.fillMaxWidth().padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End,
        ){
            if(stepIndex>0){
                TextButton(
                    modifier = Modifier.padding(start = 8.dp),
                    onClick = {stepIndex--},
                ) {
                    Text("Previous")
                }
                Spacer(Modifier.weight(1f))
            }
            when(stepIndex){
                0 -> {
                    Text(
                        "Select MCP servers for MCPdirect key and go to ",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Button(
                        modifier = Modifier.padding(start = 8.dp),
                        enabled = !viewModel.selectedToolMakers.isEmpty(),
                        onClick = {stepIndex++}
                    ){
                        Text("Next")
                    }
                }
                1 -> {
                    Text(
                        "Select MCP tools that MCPdirect key can access and go to ",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Button(
                        modifier = Modifier.padding(start = 8.dp),
                        enabled = !viewModel.selectedTools.isEmpty()&&viewModel.currentAccessKey!=null,
                        onClick = {
                            stepIndex++
                            viewModel.grantToolPermissions()
                        }
                    ){
                        Text("Next")
                    }
                }
                2 -> {
                    Button(
                        modifier = Modifier.padding(start = 8.dp),
                        onClick = {
                            generalViewModel.currentScreen(Screen.Home)
                        }
                    ){
                        Text("Done")
                    }
                }
            }
        }

    }
}

enum class ConnectMCPViewAction {
    MAIN,
    CONFIG_MCP,
    CONFIG_MCP_TEMPLATE,
    CONFIG_OPENAPI,
}

@Composable
fun ConnectMCPView(
    modifier: Modifier,
    viewModel: QuickStartViewModel,
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
                ToolAgentSelectionMenu(viewModel, Modifier.padding(horizontal = 16.dp))
                if (toolMakers.isEmpty()) StudioBoard(Modifier.weight(1f)) {
                    Icon(
                        painterResource(Res.drawable.inbox_empty),
                        contentDescription = null,
                        modifier = Modifier.size(80.dp).padding(bottom = 16.dp)
                    )
                    Text("No MCP server installed.")
                    Text("Install one from MCP catalog.")
                } else LazyColumn(
                    Modifier.weight(1f).padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(toolMakers) { toolMaker ->
                        ListButton(
                            selected = currentToolMaker?.id == toolMaker.id,
                            onClick = {
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
                ToolAgentSelectionMenu(viewModel, Modifier.padding(horizontal = 16.dp))
                LazyColumn(
                    Modifier.weight(1f).padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ){
                    items(mcpServerCatalog) { mcpServer ->
                        if (mcpServer.id <100) {
                            when(mcpServer.id){
                                0L -> ListButton(
                                    selected = currentMCPTemplate.id==mcpServer.id,
                                    headlineContent = { Text("MCP Server") },
                                    onClick = {currentMCPTemplate = mcpServer }
                                )
                                1L -> ListButton(
                                    selected =
                                        currentMCPTemplate.id==mcpServer.id,
                                    headlineContent = { Text("OpenAPI") },
                                    onClick = {currentMCPTemplate = mcpServer }
                                )
                                -1L -> HorizontalDivider()
                            }
                        }else ListButton(
                            selected = currentMCPTemplate.id==mcpServer.id,
                            headlineContent = { Text(mcpServer.name) },
                            onClick = {currentMCPTemplate = mcpServer }
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
                            MCPServerMainView(toolMaker, viewModel, Modifier.weight(2f)) {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolAgentSelectionMenu(
    viewModel: QuickStartViewModel,
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
//        val textFieldState = rememberTextFieldState(if (localToolAgent.id == currentToolAgent.id) "This Device" else currentToolAgent.name)
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
                    if (option.id > 0L && UserRepository.me(option.userId)) {
                        if (currentToolAgent.id == option.id) {
                            checkedIndex = index
                        }
                        DropdownMenuItem(
                            contentPadding = PaddingValues(horizontal = 12.dp),
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
                                if (checkedIndex == index)
                                    Icon(painterResource(Res.drawable.check), contentDescription = null)
                            },
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun MCPServerMainView(
    toolMaker: AIPortToolMaker,
    viewModel: QuickStartViewModel,
    modifier: Modifier = Modifier,
    onActionChange: (action: ConnectMCPViewAction)->Unit
){
    val tools = remember(toolMaker.id, viewModel.tools) {
        derivedStateOf {
            viewModel.tools.filter { it.makerId == toolMaker.id }
        }
    }.value
    if(toolMaker.status == STATUS_WAITING){
        StudioBoard(modifier) {
            CircularProgressIndicator()
            Text("${toolMaker.name} starting")
        }
    }else Column(modifier) {
        Row(
            Modifier.padding(start = 16.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painterResource(Res.drawable.plug_connect), contentDescription = "", Modifier.size(24.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(toolMaker.name, fontWeight = FontWeight.Bold)
            Spacer(Modifier.weight(1f))
            TooltipIconButton("Configure",onClick = {
                if (toolMaker.templateId > 0) {
                    if (toolMaker.mcp()) onActionChange(ConnectMCPViewAction.CONFIG_MCP_TEMPLATE)
                } else if (toolMaker.mcp()) onActionChange(ConnectMCPViewAction.CONFIG_MCP)
                else if (toolMaker.openapi()) onActionChange(ConnectMCPViewAction.CONFIG_OPENAPI)
            }) {
                Icon(
                    painterResource(Res.drawable.setting_config), contentDescription = "",
                    Modifier.size(24.dp)
                )
            }
            TooltipIconButton("Remove", onClick = {
                viewModel.removeToolMaker(toolMaker)
            }){
                Icon(
                    painterResource(Res.drawable.delete), contentDescription = "",
                    Modifier.size(24.dp), tint = MaterialTheme.colorScheme.error
                )
            }
            Spacer(Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(width = 36.dp, height = 22.dp) // Manually adjusted size
                    .wrapContentSize(Alignment.Center)
            ) {
                var checked by remember { mutableStateOf(toolMaker.status>0) }
                Switch(
                    checked = checked,
                    onCheckedChange = {
                        viewModel.modifyToolMakerStatus(
                            toolMaker, if(it) 1 else 0
                        ){
                            if(it.successful()) it.data?.let { data ->
                                checked = data.status>0
                            }
                        }
                    },
                    modifier = Modifier
                        .scale(0.6f)
                    // Remove default touch padding if it interferes with your layout
                    // (Optional, use with caution for accessibility)
                )
            }
        }
//        HorizontalDivider()
        if(toolMaker.errorCode!=0){
            Text(toolMaker.errorMessage,Modifier.padding(horizontal = 8.dp) , color = MaterialTheme.colorScheme.error)
        } else {
            var currentTool by remember{ mutableStateOf<AIPortTool?>(null) }
            var toolDetails by remember { mutableStateOf(ToolDetails("","{}")) }
            val scrollState = rememberScrollState()
            LaunchedEffect(toolMaker){
                currentTool = null
            }
            LaunchedEffect(currentTool){
                currentTool?.let { tool ->
                    ToolRepository.tool(tool.id) {
                        if (it.successful()) it.data?.let { data->
                            val json = JSON.parseToJsonElement(data.metaData)
                            toolDetails = ToolDetails(
                                json.jsonObject["description"]?.jsonPrimitive?.content ?: "",
                                json.jsonObject["requestSchema"]?.jsonPrimitive?.content ?: "{}"
                            )
                        }
                    }
                }
            }
            currentTool?.let { tool ->
                var tabIndex by remember { mutableStateOf(0) }
                OutlinedCard(modifier = Modifier.fillMaxSize().padding(start=16.dp,end=16.dp, bottom = 16.dp)) {
                    StudioActionBar(tool.name) {
                        SecondaryTabRow(
                            tabIndex,
                            Modifier.width(300.dp),
//                            containerColor = CardDefaults.cardColors().containerColor,
//                            contentColor = CardDefaults.cardColors().contentColor,
                        ){
                            Tab(tabIndex==0, onClick = {tabIndex = 0}, text = {Text("Description")})
                            Tab(tabIndex==1, onClick = {tabIndex = 1}, text = {Text("Input Schema")})
                        }
                        IconButton(onClick = {currentTool=null}){
                            Icon(painterResource(Res.drawable.close), contentDescription = "")
                        }
                    }
                    HorizontalDivider()
                    when(tabIndex){
                        0 -> Text(toolDetails.description, Modifier.padding(16.dp))
                        1 -> JsonTreeView(toolDetails.inputSchema, Modifier.padding(16.dp))
                    }
//                    Text(toolDetails.description, Modifier.padding(16.dp))
                }
            }?: Box(modifier = Modifier.fillMaxSize()) {
                Column (
                    modifier = Modifier
                        .verticalScroll(scrollState)
                        .padding(start = 16.dp,end=16.dp, bottom = 16.dp) // Add padding to prevent content from going under the scrollbar
                )  {
                    FlowRow(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ){
                        var index = 1

                        tools.forEach { tool ->
                            if (tool.makerId == toolMaker.id && tool.status>-1) {
                                TextButton(
                                    shape = OutlinedTextFieldDefaults.shape,
                                    border = BorderStroke(1.dp, ButtonDefaults.textButtonColors().contentColor),
                                    onClick = { currentTool = tool }
                                ) {
                                    Text("${index++}. ${tool.name}")
                                }
                            }

                        }
                    }
                }
                VerticalScrollbar(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    adapter = rememberScrollbarAdapter(scrollState = scrollState)
                )
            }
        }
    }
}

@Composable
fun GenerateMCPdirectKeyView(
    modifier: Modifier = Modifier,
    viewModel: QuickStartViewModel
){
    val accessKeys by viewModel.accessKeys.collectAsState()
//    val currentAccessKey = viewModel.currentAccessKey
    var generateKey by remember { mutableStateOf(false) }
    Row(modifier.fillMaxSize()){
        LazyColumn(Modifier.weight(2f)) {
            items(viewModel.selectedToolMakers) { toolMaker ->
                val toolCount = viewModel.countTools(toolMaker)
                val selectedToolCount = viewModel.countSelectedTools(toolMaker)
                val tools = viewModel.tools.filter { it.makerId == toolMaker.id }.toList()

                OutlinedCard{
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = selectedToolCount>0,
                            onCheckedChange = {
                                viewModel.selectAllTools(it,toolMaker)
                            }
                        )
                        Text("${toolMaker.name} ($selectedToolCount/$toolCount)")
                    }
                    HorizontalDivider()
                    FlowRow(
                        Modifier.padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ){
                        tools.forEach { tool ->
                            Tag(
                                tool.name,
                                toggle = viewModel.selectedTool(tool),
                            ){
                                viewModel.selectTool(it,tool)
                            }
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
        }
        Spacer(Modifier.width(8.dp))
        OutlinedCard(Modifier.weight(1f).fillMaxHeight()) {
            if(!generateKey&&accessKeys.isNotEmpty()){

                StudioActionBar("MCPdirect Keys"){
                    TextButton(
                        modifier = Modifier.height(32.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp),
                        onClick = { generateKey = true }
                    ) {
                        Text(
                            "Generate New Key",
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
                HorizontalDivider()
                LazyColumn {
                    items(accessKeys) { accessKey ->
                        val selected = viewModel.selectedAccessKey(accessKey)
                        StudioListItem(
                            modifier = Modifier.clickable {
                                viewModel.selectAccessKey(accessKey)
                            },
                            selected = selected,
                            leadingContent = {
                                Checkbox(checked = selected, onCheckedChange = {
                                    viewModel.selectAccessKey(accessKey)
                                })
                            },
                            headlineContent = { Text(accessKey.name) },
                        )
                    }
                }
            } else {
                StudioActionBar("Generate New Key"){
                    if(accessKeys.isNotEmpty())TextButton(
                        modifier = Modifier.height(32.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp),
                        onClick = { generateKey = false }
                    ) {
                        Text(
                            "MCPdirect Keys",
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
                HorizontalDivider()
                var name by remember { mutableStateOf("") }
                var nameError by remember { mutableStateOf(true) }
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    value = name,
                    onValueChange = { text ->
                        nameError = text.isBlank() || text.length>20
                        if(text.isBlank()) name = ""
                        else name = text
                    },
                    label = { Text("MCPdirect Key Name") },
                    isError = nameError,
                    supportingText = {
                        Text("Name must not be empty and should have at most 20 characters")
                    },
                )
                Button(
                    enabled = !nameError,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    onClick = {
                        viewModel.generateMCPdirectKey(name){
                            generateKey = !it.successful()
                        }
                    },
                ){
                    Text("Generate")
                }
            }
        }
    }
}

@Composable
fun ConfigAIAgentView(
    modifier: Modifier,
    viewModel: QuickStartViewModel
){
    var aiAgent by remember { mutableStateOf<AIAgent?>(null) }
    Row(modifier) {
//        LazyColumn(Modifier.weight(1f)) {
//            items(aiAgents) {
//                if(aiAgent==null){
//                    aiAgent = it
//                }
//                StudioListItem(
//                    modifier = Modifier.clickable {
//                        aiAgent = it
//                    },
//                    selected = it == aiAgent,
//                    headlineContent = { Text(it.name) }
//                )
//            }
//        }
        OutlinedCard(Modifier.weight(1f))  {
            AIAgentListComponent{
                aiAgent = it
            }
        }
        Spacer(Modifier.width(8.dp))
//        aiAgent?.let { aiAgent ->
//            VerticalDivider()
//            AIAgentConfigOptionView(
//                viewModel.currentAccessKey!!,
//                modifier = Modifier.weight(2f),
//                configs = aiAgent.configs,
//                aiAgent.references
//            )
//        }
        Card(Modifier.weight(2f).fillMaxHeight()) {
            if(viewModel.currentAccessKey!=null&&aiAgent!=null) {
//                VerticalDivider()
                AIAgentGuideComponent(
                    viewModel.currentAccessKey!!,
                    aiAgent!!,

                )
            }
        }
    }
}

@Composable
fun AIAgentConfigOptionView(
    accessKey: AIPortToolAccessKey,
    modifier: Modifier = Modifier,
    configs:List<AIAgentConfig>,
    references: List<AIAgentReference>? = null
){
    var accessKeyCredential by remember { mutableStateOf("") }
    LaunchedEffect(accessKey){
        AccessKeyRepository.getAccessKeyCredential(accessKey){ data->
            data?.let {
                accessKeyCredential = it.secretKey.substring(4)
            }
        }
    }
    val listState = rememberLazyListState()
    val uriHandler = LocalUriHandler.current
    Column( modifier.padding(8.dp)) {
        references?.let {
            if (it.isNotEmpty()) Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ){
                TextButton(
                    onClick = {
                        uriHandler.openUri(it[0].url)
                    }
                ) {
                    Text(it[0].name)
                    Icon(painterResource(Res.drawable.docs), contentDescription = "")
                }
            }
        }
        LazyColumn(
            state = listState,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(configs) { option ->

                ElevatedCard{
                    var endpoint = AppInfo.MCPDIRECT_GATEWAY_ENDPOINT
                    if(endpoint.endsWith("/")){
                        endpoint = endpoint.substring(0, endpoint.length - 1)
                    }
                    val keyName = accessKey.name.replace(" ","_")
                    val config = option.config
                        .replace($$"${MCPDIRECT_KEY_NAME}",keyName)
                        .replace($$"${MCPDIRECT_URL}", endpoint)
                        .replace($$"${MCPDIRECT_KEY}",accessKeyCredential)
                    Row(
                        Modifier.padding(start = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(option.title)
                        Spacer(Modifier.weight(1f))
                        option.deeplink?.let { deeplink ->
                            TextButton(onClick = {
                                uriHandler.openUri(deeplink.deeplink(
                                    keyName,
                                    accessKeyCredential,
                                    endpoint
                                    ))
                            }){
                                deeplink.icon?.let { icon ->
                                    Res.allDrawableResources[icon]?.let {
                                        Image(painterResource(it),contentDescription = "")
                                    }
                                }?: Text(deeplink.name)
                            }
                        }
                        IconButton(onClick = {
                            getPlatform().copyToClipboard(config)
                        }){
                            Icon(painterResource(Res.drawable.content_copy), contentDescription = "Copy")
                        }
                    }
                    HorizontalDivider()
                    SelectionContainer(Modifier.padding(16.dp)) {
                        Text(config, style = MaterialTheme.typography.bodyMedium)
                    }
                    option.paths?.let { paths ->
                        HorizontalDivider()
                        for (path in paths) {
                            Text(path.os,modifier = Modifier.padding(start = 16.dp,top=16.dp))
                            Text(path.path,modifier = Modifier.padding(start = 32.dp),style = MaterialTheme.typography.bodyMedium)
                        }
                        Spacer(Modifier.height(16.dp))
                    }
                }
            }
        }
    }

}