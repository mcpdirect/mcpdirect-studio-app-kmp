package ai.mcpdirect.studio.app.tips

import ai.mcpdirect.mcpdirectstudioapp.JSON
import ai.mcpdirect.studio.app.compose.*
import ai.mcpdirect.studio.app.mcp.ConfigMCPServerView
import ai.mcpdirect.studio.app.model.MCPServer
import ai.mcpdirect.studio.app.model.MCPServerConfig
import ai.mcpdirect.studio.app.model.OpenAPIServer
import ai.mcpdirect.studio.app.model.aitool.AIPortMCPServer
import ai.mcpdirect.studio.app.model.aitool.AIPortTool
import ai.mcpdirect.studio.app.model.aitool.AIPortToolMaker
import ai.mcpdirect.studio.app.model.aitool.AIPortToolMaker.Companion.ERROR
import ai.mcpdirect.studio.app.model.aitool.AIPortToolMaker.Companion.STATUS_OFF
import ai.mcpdirect.studio.app.model.aitool.AIPortToolMaker.Companion.STATUS_WAITING
import ai.mcpdirect.studio.app.model.repository.StudioRepository
import ai.mcpdirect.studio.app.model.repository.ToolRepository
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
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import mcpdirectstudioapp.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource

@Composable

fun QuickStartScreen() {
    val viewModel by remember { mutableStateOf(QuickStartViewModel()) }

    var stepIndex by remember { mutableStateOf(0) }
    val steps = listOf(
        "1. Connect MCP servers to MCPdirect",
        "2. Generate MCPdirect key for MCP servers access",
        "3. Integrate MCPdirect with AI Agents"
    )
    Column(Modifier.fillMaxSize().padding(8.dp)) {
        Row(
            Modifier.fillMaxWidth().padding(top = 8.dp,bottom = 16.dp),
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
//                            viewModel.grantToolPermissions()
                        }
                    ){
                        Text("Next")
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
    viewModel: QuickStartViewModel
){
    val toolAgents by viewModel.toolAgents.collectAsState()
    val localToolAgent by StudioRepository.localToolAgent.collectAsState()
    val currentToolAgent by viewModel.currentToolAgent.collectAsState()
    val currentToolMaker by viewModel.currentToolMaker.collectAsState()
    var action by remember { mutableStateOf(ConnectMCPViewAction.MAIN) }
    Row(modifier, horizontalArrangement = Arrangement.spacedBy(8.dp)){
    OutlinedCard(Modifier.fillMaxHeight().weight(1f)) {
        val toolMakers by viewModel.toolMakers.collectAsState()
        StudioActionBar (
            "Installed MCP servers",
        ){
            IconButton(onClick = {
                viewModel.currentToolMaker(null)
            }) {
                Icon(painterResource(Res.drawable.add), contentDescription = "")
            }
        }
        HorizontalDivider()
        if(toolMakers.isEmpty()) StudioBoard(Modifier.weight(1f)) {
            Icon(
                painterResource(Res.drawable.inbox_empty),
                contentDescription = null,
                modifier = Modifier.size(80.dp).padding(bottom = 16.dp)
            )
            Text("No MCP server installed.")
            Text("Install one from MCP catalog.")
        } else LazyColumn(Modifier.weight(1f)) {
            items(toolMakers) { toolMaker ->
                StudioListItem(
                    selected = currentToolMaker?.id==toolMaker.id,
                    modifier = Modifier.clickable {
                        action = ConnectMCPViewAction.MAIN
                        viewModel.currentToolMaker(toolMaker)
                    },
                    headlineContent = { Text(toolMaker.name) },
                    leadingContent = {
                        if (toolMaker.status == STATUS_OFF) Icon(
                            painterResource(Res.drawable.mobiledata_off),
                            contentDescription = "Disconnect",
                            Modifier.size(48.dp).padding( 12.dp),
                            tint = MaterialTheme.colorScheme.error
                        ) else if(toolMaker.status== STATUS_WAITING){
                            CircularProgressIndicator(
                                Modifier.size(48.dp).padding( 8.dp),
                            )
                        } else if (toolMaker.errorCode == ERROR) Icon(
                            painterResource(Res.drawable.error),
                            contentDescription = "Error",
                            Modifier.size(48.dp).padding( 12.dp),
                            tint = MaterialTheme.colorScheme.error
                        )else {
                            LaunchedEffect(toolMaker){
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
                    supportingContent = { Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        when (toolMaker) {
                            is MCPServer -> {
                                if (toolMaker.transport == 0) {
                                    Tag("STDIO")
                                } else if (toolMaker.transport == 1) {
                                    Tag("SSE")
                                } else if (toolMaker.transport == 2) {
                                    Tag("Streamable HTTP")
                                }
                            }
                            is OpenAPIServer -> {
                                Tag("OpenAPI")
                            }
                        }
                    } }
                )
            }
        }
        HorizontalDivider()
        TextButton (
            modifier = Modifier.padding(horizontal = 8.dp),
            onClick = {}
        ){
            if(currentToolAgent.id==0L&&toolAgents.isNotEmpty())
                viewModel.currentToolAgent(toolAgents[0])

            Text(if(localToolAgent.id==currentToolAgent.id) "This Device" else currentToolAgent.name)
            Spacer(Modifier.weight(1f))
            Icon(painterResource(Res.drawable.more), contentDescription = "")
        }
    }
    OutlinedCard(Modifier.weight(2f)) {
        currentToolMaker?.let { toolMaker ->
            when(action){
                ConnectMCPViewAction.MAIN -> {
                    MCPServerMainView(toolMaker,viewModel,Modifier.weight(2f)){
                        action = it
                    }
                }
                ConnectMCPViewAction.CONFIG_MCP ->{
                    ConfigMCPServerView(
                        toolMaker as MCPServer, Modifier.weight(2f),
                        onBack = {action= ConnectMCPViewAction.MAIN}
                    ){ config,changed ->
                        println(JSON.encodeToString(config))
                        viewModel.modifyMCPServerConfig(
                            toolMaker,config.name,
                            config = if(changed) config else null
                        ){ if(it.successful()) action = ConnectMCPViewAction.MAIN }
                    }
                }
                ConnectMCPViewAction.CONFIG_MCP_TEMPLATE ->{

                }
                ConnectMCPViewAction.CONFIG_OPENAPI ->{}
            }

        }?: MCPServerCatalogView(viewModel,Modifier.weight(2f))
    }
} }

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
            Modifier.padding(start = 16.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(toolMaker.name, style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.weight(1f))
            IconButton(onClick = {
                viewModel.modifyToolMakerStatus(
                    toolMaker, 1
                )
            }) {
                Icon(
                    painterResource(Res.drawable.restart_alt), contentDescription = "",
                    Modifier.size(24.dp)
                )
            }
            IconButton(onClick = {
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
        }
        HorizontalDivider()
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
                Card(modifier = Modifier.fillMaxSize().padding(16.dp)){
                    StudioActionBar(tool.name) {
                        SecondaryTabRow(
                            tabIndex,
                            Modifier.width(300.dp),
                            containerColor = CardDefaults.cardColors().containerColor,
                            contentColor = CardDefaults.cardColors().contentColor,
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
                        .padding(16.dp) // Add padding to prevent content from going under the scrollbar
                )  {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ){
                        var index = 1
                        tools.forEach { tool ->
                            if (tool.makerId == toolMaker.id) TextButton(
                                shape = OutlinedTextFieldDefaults.shape,
                                border = BorderStroke(1.dp, ButtonDefaults.textButtonColors().contentColor),
                                onClick = {currentTool = tool}
                            ){
                                Text("${index++}. ${tool.name}")
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
fun MCPServerCatalogView(
    viewModel: QuickStartViewModel,
    modifier: Modifier
){Column(modifier.fillMaxHeight()) {
    Row{
        var currentMCPServer by remember { mutableStateOf(AIPortMCPServer()) }
        Column(modifier = Modifier.weight(0.8f))  {
            StudioActionBar("MCP catalog")
            HorizontalDivider()
            LazyColumn{
                items(mcpServerCatalog) { mcpServer ->
                    if (mcpServer.id <100) {
                        when(mcpServer.id){
                            0L -> StudioListItem(
                                selected = currentMCPServer.id==mcpServer.id,
                                headlineContent = { Text("MCP Server") },
                                modifier = Modifier.clickable {currentMCPServer = mcpServer }
                            )
                            1L -> StudioListItem(
                                selected = currentMCPServer.id==mcpServer.id,
                                headlineContent = { Text("OpenAPI") },
                                modifier = Modifier.clickable {currentMCPServer = mcpServer }
                            )
                            -1L -> HorizontalDivider()
                        }
                    }else StudioListItem(
                        selected = currentMCPServer.id==mcpServer.id,
                        headlineContent = { Text(mcpServer.name) },
                        modifier = Modifier.clickable {currentMCPServer = mcpServer }
                    )

                }
            }
        }

        fun installMCPServer(config: MCPServerConfig){
            viewModel.installMCPServer(config){
                if(it.successful()) it.data?.let { data ->
                    viewModel.currentToolMaker(data)
                    viewModel.selectToolMaker(true,data)
                }
            }
        }
        VerticalDivider()
        when(currentMCPServer.id){
            0L -> ConfigMCPServerView(modifier = Modifier.weight(2f)){ config,changed ->
                installMCPServer(config)
            }
            1L -> {}
            else -> ConfigMCPServerView(currentMCPServer,Modifier.weight(2f)){ config ->
                installMCPServer(config)
            }
        }
    }
} }

@Composable
fun GenerateMCPdirectKeyView(
    modifier: Modifier,
    viewModel: QuickStartViewModel
){
    val accessKeys by viewModel.accessKeys.collectAsState()
    Row(modifier.fillMaxSize()){
        OutlinedCard(Modifier.weight(1f).fillMaxHeight()) {
            Row(Modifier.padding(start = 16.dp, end = 4.dp),verticalAlignment = Alignment.CenterVertically) {
                Text("Generate MCPdirect Key", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.weight(1f))
                IconButton(onClick = {
                }) {
                    Icon(painterResource(Res.drawable.add), contentDescription = "")
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
        }
        Spacer(Modifier.width(8.dp))
//        OutlinedCard(Modifier.weight(2f).fillMaxHeight()) {
//            Row(Modifier.height(48.dp).padding(start = 16.dp, end = 4.dp),verticalAlignment = Alignment.CenterVertically) {
//                Text("Select the tools that MCPdirect key can access", style = MaterialTheme.typography.titleLarge)
//                Spacer(Modifier.weight(1f))
//            }
//            HorizontalDivider()
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
//        }
    }
}

@Composable
fun ConfigAIAgentView(
    modifier: Modifier,
    viewModel: QuickStartViewModel
){ OutlinedCard(modifier) {
    var aiAgent by remember { mutableStateOf<AIAgent?>(null) }
    Row {
        LazyColumn(Modifier.weight(1f)) {
            items(aiAgents) {
                if(aiAgent==null){
                    aiAgent = it
                }
                StudioListItem(
                    modifier = Modifier.clickable {
                        aiAgent = it
                    },
                    selected = it == aiAgent,
                    headlineContent = { Text(it.name) }
                )
            }
        }
        aiAgent?.let { aiAgent ->
            VerticalDivider()
            AIAgentConfigOptionView(
                modifier = Modifier.weight(2f),
                configs = aiAgent.configs,
                aiAgent.references
            )
        }
    }
} }

@Composable
fun AIAgentConfigOptionView(
    modifier: Modifier = Modifier,
    configs:List<AIAgentConfig>,
    references: List<AIAgentReference>? = null
){
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
                    Row(
                        Modifier.padding(start = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(option.title)
                        Spacer(Modifier.weight(1f))
                        option.deeplink?.let { deeplink ->
                            deeplink.icon?.let { icon ->
                                Res.allDrawableResources[icon]?.let {
                                    Image(painterResource(it),contentDescription = "")
                                }
                            }?: TextButton(onClick = {

                            }){
                                Text(deeplink.name)
                            }
                        }
                        IconButton(onClick = {

                        }){
                            Icon(painterResource(Res.drawable.content_copy), contentDescription = "Copy")
                        }
                    }
                    HorizontalDivider()
                    SelectionContainer(Modifier.padding(16.dp)) {
                        Text(option.config, style = MaterialTheme.typography.bodyMedium)
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