package ai.mcpdirect.studio.app.agent

//import ai.mcpdirect.studio.app.template.mcpTemplateListViewModel
import ai.mcpdirect.mcpdirectstudioapp.JSON
import ai.mcpdirect.mcpdirectstudioapp.getPlatform
import ai.mcpdirect.studio.app.UIState
import ai.mcpdirect.studio.app.agent.ToolProviderType.None
import ai.mcpdirect.studio.app.compose.*
import ai.mcpdirect.studio.app.generalViewModel
import ai.mcpdirect.studio.app.mcp.*
import ai.mcpdirect.studio.app.mcp.openapi.ConnectOpenAPIServerDialog
import ai.mcpdirect.studio.app.model.AIPortServiceResponse
import ai.mcpdirect.studio.app.model.MCPServer
import ai.mcpdirect.studio.app.model.OpenAPIServer
import ai.mcpdirect.studio.app.model.ToolMakerTemplateConfig
import ai.mcpdirect.studio.app.model.account.AIPortUser
import ai.mcpdirect.studio.app.model.aitool.AIPortTool
import ai.mcpdirect.studio.app.model.aitool.AIPortToolAgent
import ai.mcpdirect.studio.app.model.aitool.AIPortToolMaker
import ai.mcpdirect.studio.app.model.aitool.AIPortToolMaker.Companion.STATUS_WAITING
import ai.mcpdirect.studio.app.model.repository.StudioRepository
import ai.mcpdirect.studio.app.model.repository.ToolRepository
import ai.mcpdirect.studio.app.model.repository.UserRepository
import ai.mcpdirect.studio.app.template.ConnectMCPTemplateDialog
import ai.mcpdirect.studio.app.template.CreateMCPTemplateDialog
import ai.mcpdirect.studio.app.template.MCPTemplateListView
import ai.mcpdirect.studio.app.template.MCPTemplateListViewModel
import ai.mcpdirect.studio.app.tool.ToolDetails
import ai.mcpdirect.studio.app.tool.ToolDetailsView
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import mcpdirectstudioapp.composeapp.generated.resources.*
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

enum class MyStudioScreenDialog {
    None,
    ConnectMCP,
    ConnectOpenAPI,
    ConnectMCPTemplate,
    CreateMCPTemplate
}

@Composable
fun MyStudioScreen(
    toolAgent: AIPortToolAgent?,
    toolMaker: AIPortToolMaker?,
    dialog: MyStudioScreenDialog = MyStudioScreenDialog.None
){
    val mcpTemplateListViewModel by remember { mutableStateOf(MCPTemplateListViewModel()) }
    val myStudioViewModel = remember { MyStudioViewModel() }
    toolAgent?.let {
        myStudioViewModel.toolAgent(it)
    }
    toolMaker?.let {
        myStudioViewModel.toolMaker(it)
    }
    val toolProviderType = when(dialog){
        MyStudioScreenDialog.ConnectOpenAPI -> ToolProviderType.OpenAPIServer
        MyStudioScreenDialog.ConnectMCP -> ToolProviderType.MCPServer
        else -> None
    }
    var dialog by remember { mutableStateOf(dialog) }
    var currentTabIndex by remember { mutableStateOf(0) }
    LaunchedEffect(myStudioViewModel){
        myStudioViewModel.refreshToolAgents()
        myStudioViewModel.refreshToolMakers()
        toolAgent?.let { myStudioViewModel.toolAgent(it) }
        toolMaker?.let { myStudioViewModel.toolMaker(it) }
    }
    Row(Modifier.fillMaxSize()){
        Column(
            Modifier.width(300.dp)
                .padding(start = 8.dp, top = 16.dp, bottom = 16.dp)
        ) {
            val tabs = listOf("My Studios", "MCP Templates")
            SecondaryTabRow(selectedTabIndex = currentTabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = currentTabIndex == index,
                        onClick = {
                            currentTabIndex = index
//                            generalViewModel.topBarActions = {}
                        },
                        text = { Text(title) }
                    )
                }
            }
            // Content based on selected tab
            when (currentTabIndex) {
                0 -> ToolAgentListView(myStudioViewModel)
                1 -> MCPTemplateListView(mcpTemplateListViewModel)
            }
        }

        OutlinedCard(Modifier.fillMaxSize().padding(top = 8.dp, bottom = 8.dp, end = 8.dp)) {
            when (currentTabIndex) {
                0 -> ToolMakerListView(myStudioViewModel,toolProviderType){
                    dialog = it
                }
                1 -> ToolMakerByTemplateListView(mcpTemplateListViewModel,myStudioViewModel){
                    dialog = it
                }
            }
        }
    }

    when(dialog){
        MyStudioScreenDialog.None ->{}
        MyStudioScreenDialog.ConnectMCP -> ConnectMCPServerDialog(
            title = myStudioViewModel.toolAgent.value.name,
            onDismissRequest = {dialog=MyStudioScreenDialog.None},
            onConfirmRequest = { configs ->
                myStudioViewModel.connectMCPServerToStudio(configs)
            }
        )
        MyStudioScreenDialog.ConnectMCPTemplate -> {
            mcpTemplateListViewModel.toolMakerTemplate?.let {
                template ->
                var toolAgent by remember { mutableStateOf<AIPortToolAgent?>(null) }
                LaunchedEffect(null){
                    StudioRepository.toolAgent(template.agentId){
                        code, message, data ->
                        if(code==0&&data!=null){
                            toolAgent = data
                        }
                    }
                }
                toolAgent?.let {
                    ConnectMCPTemplateDialog(
                        toolAgent!!,
                        template,
                        onDismissRequest = {dialog=MyStudioScreenDialog.None},
                        onConfirmRequest = { name, inputs ->
                            myStudioViewModel.createToolMakerByTemplate(
                                toolAgent!!,template,name,inputs
                            ){ code, message, toolMaker ->
                                if(code==0)toolMaker?.let {
                                    myStudioViewModel.toolAgent(it.agentId){
                                            code, message, data ->
                                        if(code==0){
                                            data?.let {
                                                myStudioViewModel.connectToolMakerToStudio(
                                                    it,
                                                    toolMaker,
                                                ){ code, message, mcpServer ->
//                                                generalViewModel.refreshToolMakers()
                                                    myStudioViewModel.queryMCPToolsFromStudio(toolMaker)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }
        MyStudioScreenDialog.CreateMCPTemplate -> {
            val toolMaker = myStudioViewModel.toolMaker.value
            val toolAgent = myStudioViewModel.toolAgent.value
            CreateMCPTemplateDialog(
                toolMaker,
                onConfirmRequest = { name,type,config,inputs ->
                    dialog = MyStudioScreenDialog.None
                    mcpTemplateListViewModel.createToolMakerTemplate(toolAgent,name,type,config,inputs)
                },
                onDismissRequest = {
                    dialog = MyStudioScreenDialog.None
                }
            )
        }
        MyStudioScreenDialog.ConnectOpenAPI ->{
            ConnectOpenAPIServerDialog(
                toolAgent = myStudioViewModel.toolAgent.value,
                onDismissRequest = {dialog=MyStudioScreenDialog.None},
                onConfirmRequest = { name,config->
                    dialog = MyStudioScreenDialog.None
                    myStudioViewModel.connectOpenAPIServerToStudio(name,config)
                }
            )
        }
    }
}
@Composable
fun ToolAgentListView(
    myStudioViewModel: MyStudioViewModel
){
    LaunchedEffect(myStudioViewModel) {
        myStudioViewModel.refreshToolAgents()
    }
    val uiState = myStudioViewModel.uiState
//    val me = UserRepository.me.value
    val localToolAgent by myStudioViewModel.localToolAgent.collectAsState()
    val toolAgents by myStudioViewModel.toolAgents.collectAsState()
    val toolAgent by myStudioViewModel.toolAgent.collectAsState()
    LazyColumn{
        items(toolAgents){
            println("${it.id},${it.name}")
            if(it.id!=0L&& UserRepository.me(it.userId)) StudioListItem(
                selected = it.id==toolAgent.id,
                modifier = Modifier.clickable(
//                    enabled = uiState !is UIState.Loading && it.id!=toolAgent.id
                    enabled = it.id!=toolAgent.id
                ){
//                    if(it.id== getPlatform().toolAgentId)
//                        generalViewModel.currentScreen(
//                            Screen.ConnectMCP,
//                            it.name,
//                            Screen.MyStudio
//
//                        )
//                    else
                    myStudioViewModel.toolAgent(it)
                },
                headlineContent = {Text(it.name)},
                supportingContent = {
                    if(it.id == localToolAgent.id)
                        Tag("This device")
                },
                trailingContent = {
                    if(it.status==0) Icon(
                        painterResource(Res.drawable.cloud_off),
                        contentDescription = "Offline",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            )
        }
    }
}
sealed class ToolProviderType(val title:String,val icon: DrawableResource){
    object None: ToolProviderType("Select Tool Provider",Res.drawable.menu)
    object MCPServer: ToolProviderType("MCP Server",Res.drawable.plug_connect)
    object OpenAPIServer: ToolProviderType("OpenAPI Server",Res.drawable.openapi)
}
enum class ToolMakerListViewDialog {
    None,
    EditServerName,
    EditServerTags,
    EditServerConfig,
    DeleteServer,
}
@Composable
fun ToolMakerListView(
    myStudioViewModel: MyStudioViewModel,
    toolProviderType: ToolProviderType,
    onDialogRequest: (dialog: MyStudioScreenDialog) -> Unit
){
    var dialog by remember { mutableStateOf<ToolMakerListViewDialog>(ToolMakerListViewDialog.None) }

    val localToolAgent by myStudioViewModel.localToolAgent.collectAsState()
    val toolAgents by myStudioViewModel.toolAgents.collectAsState()
    val uriHandler = LocalUriHandler.current
    val uiState = myStudioViewModel.uiState
    val toolAgent by myStudioViewModel.toolAgent.collectAsState()
    val toolMaker by myStudioViewModel.toolMaker.collectAsState()
    var tool by remember { mutableStateOf<AIPortTool?>(null) }
    if(toolAgents.isEmpty()&&getPlatform().type == 0) Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Please download MCPdirect Studio to start")
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                uriHandler.openUri("https://github.com/mcpdirect/mcpdirect-studio-app-kmp/releases")
            }
        ){
            Text("Download")
        }
    } else if(toolAgent.id<1) StudioBoard {
        Text("Select a MCPdircect Studio to view")
    } else if(uiState is UIState.Error &&uiState.code<0) StudioBoard {
        Text("Access MCPdirect Studio of ${toolAgent.name} failed")
        Button(
            onClick = {
                myStudioViewModel.queryToolMakersFromStudio(toolAgent,true)
            }
        ){
            Text("Refresh")
        }
    } else{
        LaunchedEffect(null) {
            generalViewModel.topBarActions = {
                if (toolMaker.id > 0L && toolMaker.mcp()) {
                    Button(onClick = {
                        onDialogRequest(MyStudioScreenDialog.CreateMCPTemplate)
                    }) {
                        Text("Create MCP Template")
                    }
                    Spacer(Modifier.width(8.dp))
                }
                var showMenu by remember { mutableStateOf(false)}
                TextButton(onClick = {
                    showMenu = true
                }) {
                    Text("Connect MCP")
                    Icon(
                        painterResource(Res.drawable.keyboard_arrow_right),
                        contentDescription = ""
                    )
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = {showMenu=false}
                ){
                    DropdownMenuItem(
                        { Text("MCP Server") },
                        onClick = { onDialogRequest(MyStudioScreenDialog.ConnectMCP) }
                    )
                    DropdownMenuItem(
                        { Text("OpenAPI Server") },
                        onClick = { onDialogRequest(MyStudioScreenDialog.ConnectOpenAPI) }
                    )
                }
            }
        }
        DisposableEffect(null){
            onDispose {
                generalViewModel.topBarActions = {}
            }
        }
        Row {
            var toolProviderType by remember { mutableStateOf<ToolProviderType>(toolProviderType) }
            Column(Modifier.weight(1.0f)) {
//                var title by remember { mutableStateOf(None.title) }
                StudioActionBar(
                    toolProviderType.title,
                    navigationIcon = {
                        if(toolProviderType == None){
                            Icon(painterResource(None.icon),
                                contentDescription = None.title,
                                Modifier.padding(12.dp))
                        }else {
                            var showMenu by remember { mutableStateOf(false) }
                            TooltipIconButton(
                                None.icon,
                                contentDescription = None.title,
                                onClick = {
                                    showMenu = true
                                }
                            )
                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text(ToolProviderType.MCPServer.title) },
                                    leadingIcon = {
                                        Icon(painterResource(ToolProviderType.MCPServer.icon),
                                            ToolProviderType.MCPServer.title)
                                    },
                                    onClick = {
                                        toolProviderType = ToolProviderType.MCPServer
                                        showMenu = false
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text(ToolProviderType.OpenAPIServer.title) },
                                    leadingIcon = {
                                        Icon(
                                            painterResource(ToolProviderType.OpenAPIServer.icon),
                                            ToolProviderType.OpenAPIServer.title,
                                            Modifier.size(24.dp),
                                        )
                                    },
                                    onClick = {
                                        toolProviderType = ToolProviderType.OpenAPIServer
                                        showMenu = false
                                    }
                                )
                            }
                        }
                    },
                    actions = {
                        if(toolAgent.id!=localToolAgent.id) TooltipIconButton(
                            Res.drawable.refresh,
                            contentDescription = "Refresh",
                            onClick = {
                                myStudioViewModel.queryToolMakersFromStudio(
                                    toolAgent,true
                                )
                            }
                        )
                    }
                )
                HorizontalDivider()
                val makers by myStudioViewModel.mcpServers.collectAsState()
                val openapiMakers by myStudioViewModel.openapiServers.collectAsState()
                when(toolProviderType){
                    None -> Column{
                        ListItem(
                            { Text(ToolProviderType.MCPServer.title) },
                            Modifier.clickable(
                                onClick = {toolProviderType = ToolProviderType.MCPServer}
                            ),
                            leadingContent = {
                                Icon(painterResource(ToolProviderType.MCPServer.icon),
                                    contentDescription = ToolProviderType.MCPServer.title)
                            }
                        )
                        ListItem(
                            { Text(ToolProviderType.OpenAPIServer.title) },
                            Modifier.clickable(
                                onClick = {toolProviderType= ToolProviderType.OpenAPIServer}
                            ),
                            leadingContent = {
                                Icon(painterResource(ToolProviderType.OpenAPIServer.icon),
                                    contentDescription = ToolProviderType.OpenAPIServer.title,
                                    modifier = Modifier.size(24.dp))
                            }
                        )
                    }
                    ToolProviderType.MCPServer -> LazyColumn {
                        myStudioViewModel.resetToolMaker()
                        items(makers){
                            if(it.templateId==0L) ToolMakerItem(
                                myStudioViewModel,
                                it,
                                it.id==toolMaker.id && it.status<STATUS_WAITING
                            )
                        }
                    }
                    ToolProviderType.OpenAPIServer -> LazyColumn {
                        myStudioViewModel.resetToolMaker()
                        items(openapiMakers){
                            ToolMakerItem(
                                myStudioViewModel,
                                it,
                                it.id == toolMaker.id && it.status<STATUS_WAITING
                            )
                        }
                    }
                }
            }
            VerticalDivider()
            if(toolMaker.id==0L){
                StudioBoard(Modifier.weight(2.0f)) {
                    Text("Select a tool provider to view")
                }
            }else {
                val me = toolMaker.id<Int.MAX_VALUE|| UserRepository.me(toolMaker.userId)
                tool?.let{
                    StudioToolDetailsView(toolAgent,toolMaker,it,Modifier.weight(2.0f)){
                        tool = null
                    }
                }?:Column(Modifier.weight(2.0f)) {
                    StudioActionBar(
                        navigationIcon = {
                            Spacer(Modifier.width(16.dp))
                            when(toolMaker.status){
                                0->{
                                    Text("Not start")
                                    TooltipIconButton(
                                        Res.drawable.play_circle,
                                        "Start",
                                        MaterialTheme.colorScheme.primary,
                                        onClick = {
                                            myStudioViewModel.modifyToolMakerStatus(
                                                toolAgent,toolMaker,1)
                                        }
                                    )
                                }
                                1->{
                                    Text("Running", color = MaterialTheme.colorScheme.primary)
                                    TooltipIconButton(
                                        Res.drawable.stop_circle,
                                        "Stop",
                                        MaterialTheme.colorScheme.error,
                                        onClick = {
                                            myStudioViewModel.modifyToolMakerStatus(
                                                toolAgent,toolMaker,0)
                                        }
                                    )
                                    TooltipIconButton(
                                        Res.drawable.restart_alt,
                                        "Restart",
                                        MaterialTheme.colorScheme.primary,
                                        onClick = {
                                            myStudioViewModel.modifyToolMakerStatus(
                                                toolAgent,toolMaker,1)
                                        }
                                    )
                                }
                                else->{
                                    Text("Error", color = MaterialTheme.colorScheme.error)
                                    TooltipIconButton(
                                        Res.drawable.play_circle,
                                        "Start",
                                        MaterialTheme.colorScheme.primary,
                                        onClick = {
                                            myStudioViewModel.modifyToolMakerStatus(
                                                toolAgent,toolMaker,1)
                                        }
                                    )
                                }
                            }

                        },
                        actions = {
                            if(toolMaker.status==1) {
                                if (me) {
                                    TooltipIconButton(
                                        Res.drawable.cloud_upload,
                                        contentDescription = "Publish to MCPdirect",
                                        onClick = {
                                            myStudioViewModel.publishMCPTools(toolMaker)
                                        })
                                }
                                TooltipIconButton(
                                    Res.drawable.refresh,
                                    contentDescription = "Refresh Tools",
                                    onClick = {
                                        myStudioViewModel.queryMCPToolsFromStudio(toolMaker)
                                    })
                            }
                            if (me) {
                                if (toolMaker.id > Int.MAX_VALUE) TooltipIconButton(
                                    Res.drawable.sell,
                                    contentDescription = "Edit tags",
                                    onClick = {
//                                        showEditServerTagsDialog = true
                                        dialog = ToolMakerListViewDialog.EditServerTags
                                    })
                                TooltipIconButton(
                                    Res.drawable.badge,
                                    contentDescription = "Edit name",
                                    onClick = {
//                                        showEditServerNameDialog = true
                                        dialog = ToolMakerListViewDialog.EditServerName
                                    })
                                TooltipIconButton(
                                    Res.drawable.data_object,
                                    contentDescription = "Config MCP Server",
                                    onClick = {
//                                    onDialogRequest(MyStudioScreenDialog.ConfigMCP)
//                                        showEditServerConfigDialog = true
                                        dialog = ToolMakerListViewDialog.EditServerConfig
                                    })
                                TooltipIconButton(
                                    Res.drawable.delete,
                                    contentDescription = "Delete MCP Server",
                                    onClick = {
//                                        showDeleteServerConfigDialog = true
                                        dialog = ToolMakerListViewDialog.DeleteServer
                                    })
                            }
                        }
                    )
                    HorizontalDivider()
                    when(toolMaker.status){
                        -1 -> {
                            if(toolMaker is MCPServer){
                                (toolMaker as MCPServer).statusMessage?.let{
                                    StudioBoard {
                                        Text(it, color = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        }
                        else -> LazyColumn(Modifier.weight(1.0f)) {
                            items(myStudioViewModel.tools){
                                ListItem(
                                    modifier = Modifier.clickable{
                                        tool = it
                                    },
                                    leadingContent = {
                                        if(it.lastUpdated==-1L)StudioIcon(
                                            Res.drawable.check_indeterminate_small,
                                            "Abandoned"
                                        ) else if(it.lastUpdated==1L)StudioIcon(
                                            Res.drawable.add,
                                            "New tool"
                                        ) else if(it.lastUpdated>1)StudioIcon(
                                            Res.drawable.sync,
                                            "Tool updated"
                                        )
                                    },
                                    headlineContent = {Text(it.name)},
                                    trailingContent = {Icon(painterResource(Res.drawable.info),
                                        contentDescription = "Details")}
                                )
                            }
                        }
                    }
                }
            }
            when(dialog){
                ToolMakerListViewDialog.EditServerName->
                EditMCPServerNameDialog(
                    toolMaker,
                    onDismissRequest = {
//                        showEditServerNameDialog = false
                        dialog = ToolMakerListViewDialog.None
                    },
                    onConfirmRequest = { toolMaker,toolMakerName ->
                        if(toolMaker.id>Int.MAX_VALUE) {
                            myStudioViewModel.modifyMCPServerName(
                                toolAgent,toolMaker, toolMakerName)
                        }else myStudioViewModel.modifyMCPServerNameForStudio(
                            toolAgent, toolMaker,toolMakerName
                        )
                    },
                )

                ToolMakerListViewDialog.EditServerTags ->
                    EditMCPServerTagsDialog(
                        toolMaker,
                        onDismissRequest = {
//                            showEditServerTagsDialog = false
                            dialog = ToolMakerListViewDialog.None
                        },
                        onConfirmRequest = { toolMaker,toolMakerTags ->
                            myStudioViewModel.modifyToolMakerTags(toolMaker,toolMakerTags)
                        }
                    )
                ToolMakerListViewDialog.EditServerConfig -> when(toolMaker){
                    is MCPServer -> ConfigMCPServerDialog(
                        toolMaker as MCPServer,
                        onDismissRequest = { dialog = ToolMakerListViewDialog.None },
                        onConfirmRequest = {
                                mcpServer,mcpServerCOnfig->
//                            if(mcpServer.id>Int.MAX_VALUE) {
//                                val config = AIPortMCPServerConfig()
//                                config.id = toolMaker.id
//                                config.transport = mcpServerCOnfig.transport
//                                config.url = mcpServerCOnfig.url
//                                config.command = mcpServerCOnfig.command
//                                config.args = JSON.encodeToJsonElement(mcpServerCOnfig.args).toString()
//                                config.env = JSON.encodeToJsonElement(mcpServerCOnfig.env).toString()
//                                myStudioViewModel.modifyMCPServerConfig(
//                                    toolAgent,
//                                    toolMaker,
//                                    config
//                                )
//                            }else{
                                myStudioViewModel.modifyMCPServerConfigForStudio(
                                    toolAgent,
                                    mcpServer,
                                    mcpServerCOnfig
                                )
//                            }
                        }
                    )
                }
                ToolMakerListViewDialog.DeleteServer -> when(toolMaker) {
                    is MCPServer -> ConfirmDialog(
                        "Remove MCP Server",
                        "${toolMaker.name} will be removed, Please confirm",
                        {
                            myStudioViewModel.removeMCPServer(
                                toolAgent,toolMaker
                            )
                        },
                        {dialog = ToolMakerListViewDialog.None}
                    )
                    is OpenAPIServer -> ConfirmDialog(
                        "Remove Open API Server",
                        "${toolMaker.name} will be removed, Please confirm",
                        {
                            myStudioViewModel.removeOpenAPIServer(
                                toolAgent,toolMaker
                            )
                        },
                        {dialog = ToolMakerListViewDialog.None}
                    )
                }
                else -> {}
            }
        }
    }
}
@Composable
fun ToolMakerItem(
    viewModel: MyStudioViewModel,
    toolMaker: AIPortToolMaker,
    selected:Boolean
    ){
    val me = toolMaker.id<Int.MAX_VALUE|| UserRepository.me(toolMaker.userId)
    var user by remember { mutableStateOf<AIPortUser?>(null) }
    LaunchedEffect(null){
        if(!me&&toolMaker.userId>Int.MAX_VALUE)UserRepository.user(toolMaker.userId){
                code, message, data ->
            if(code== AIPortServiceResponse.SERVICE_SUCCESSFUL&&data!=null){
                user = data
            }
        }
    }
    StudioListItem(
        modifier = Modifier.clickable(
            enabled = !selected
        ){
            viewModel.toolMaker(toolMaker)
        },
        selected = selected,
        overlineContent = {
            if(me){
                Text("Me")
            }else user?.let{
                TooltipText(
                    it.name,
                    contentDescription = it.account
                )
            }
        },
        leadingContent = {
            if (toolMaker.id > 0) StudioIcon(
                icon = Res.drawable.cloud_done,
                contentDescription = "on MCPdirect"
            ) else StudioIcon(
                Res.drawable.devices,
                contentDescription = "On local device"
            )
        },
        headlineContent = {Text(toolMaker.name)},
        supportingContent = {
            Row {
                if (me) toolMaker.tags?.let {
                    StudioIcon(
                        Res.drawable.sell,
                        "Tags",
                        modifier = Modifier.size(18.dp)
                    )
                    Text(it, modifier = Modifier.padding(start = 4.dp))
                } else {
                    StudioIcon(
                        Res.drawable.groups,
                        "Team",
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        },
        trailingContent = {
            if (toolMaker.status == AIPortToolMaker.STATUS_OFF) StudioIcon(
                Res.drawable.mobiledata_off,
                contentDescription = "Disconnect",
                tint = MaterialTheme.colorScheme.error
            ) else if(toolMaker.status== AIPortToolMaker.STATUS_WAITING){
                CircularProgressIndicator(
                    Modifier.size(16.dp)
                )
            } else if (toolMaker.status == AIPortToolMaker.STATUS_ERROR) StudioIcon(
                Res.drawable.error,
                contentDescription = "Disconnect",
                tint = MaterialTheme.colorScheme.error
            )
        }
    )
}
@Composable
fun ToolMakerByTemplateListView(
    mcpTemplateListViewModel: MCPTemplateListViewModel,
    myStudioViewModel: MyStudioViewModel,
    onDialogRequest: (dialog: MyStudioScreenDialog) -> Unit
){
    mcpTemplateListViewModel.toolMakerTemplate?.let {
            template ->
        var showEditServerNameDialog by remember { mutableStateOf(false) }
        var showEditServerTagsDialog by remember { mutableStateOf(false) }
        var showConfigServerFromTemplateDialog  by remember { mutableStateOf(false) }
        var selectedToolMaker by remember { mutableStateOf(AIPortToolMaker()) }
        var statusMessage by remember { mutableStateOf<String?>(null) }
        val toolMakers by myStudioViewModel.toolMakers.collectAsState()
        var toolAgent by remember { mutableStateOf<AIPortToolAgent?>(null) }
        var tool by remember { mutableStateOf<AIPortTool?>(null) }
        DisposableEffect(null){
            onDispose {
                generalViewModel.topBarActions = {}
            }
        }
        LaunchedEffect(null){
            StudioRepository.toolAgent(template.agentId){
                code, message, data ->
                if(code==0&&data!=null){
                    toolAgent = data
                }
            }
            generalViewModel.topBarActions = {
                Button(
                    onClick = {onDialogRequest(MyStudioScreenDialog.ConnectMCPTemplate)}
                ){
                    Text("Connect MCP Template")
                }
            }
        }
        Row {
            LazyColumn(Modifier.weight(1.0f)) {
                items(toolMakers) { maker ->
                    if (maker.templateId == template.id) {
                        StudioListItem(
                            modifier = Modifier.clickable(
                                onClick = {
                                    statusMessage = null
                                    selectedToolMaker = maker
                                    myStudioViewModel.toolMaker(maker)
                                }
                            ),
                            selected = maker.id == selectedToolMaker.id,
                            headlineContent = { Text(maker.name) }
                        )
                    }
                }
            }
            VerticalDivider()
            if(selectedToolMaker.id==0L){
                StudioBoard(Modifier.weight(2.0f)) {
                    Text("Select a MCP server to view")
                }
            } else tool?.let{
                StudioToolDetailsView(
                    toolAgent!!,selectedToolMaker,it,Modifier.weight(2.0f)
                ){
                    tool = null
                }
            }?: selectedToolMaker.let {
                Column(Modifier.weight(2.0f)) {
                    StudioActionBar(
                        actions = {
                            TooltipIconButton(
                                Res.drawable.plug_connect,
                                contentDescription = "Connect MCP Server"
                            ) {
                                myStudioViewModel.connectToolMakerToStudio(
                                    toolAgent!!,
                                    selectedToolMaker,
                                ) { code, message, mcpServer ->
                                    if(code!= AIPortServiceResponse.SERVICE_SUCCESSFUL){
                                        statusMessage = message
                                    }
                                }
                            }
                            TooltipIconButton(
                                Res.drawable.refresh,
                                contentDescription = "Refresh MCP Server Tools",
                                onClick = {
                                    myStudioViewModel.queryMCPToolsFromStudio(it)
                                })
                            TooltipIconButton(
                                Res.drawable.sell,
                                contentDescription = "Edit tags",
                                onClick = {
                                    showEditServerTagsDialog = true
                                })
                            TooltipIconButton(
                                Res.drawable.badge,
                                contentDescription = "Edit name",
                                onClick = {
                                    showEditServerNameDialog = true
                                })
                            TooltipIconButton(
                                Res.drawable.data_object,
                                contentDescription = "Config MCP Server",
                                onClick = {
                                    showConfigServerFromTemplateDialog = true
                                })
                            TooltipIconButton(
                                Res.drawable.cloud_upload,
                                contentDescription = "Publish to MCPdirect",
                                onClick = {
                                    myStudioViewModel.publishMCPTools(it)
                                })
                        }
                    )
                    HorizontalDivider()
                    statusMessage?.let{
                        StudioBoard {
                            Text(it, color = MaterialTheme.colorScheme.error)
                        }
                    }?:LazyColumn(Modifier.weight(1.0f)) {
                        items(myStudioViewModel.tools){
                            ListItem(
                                modifier = Modifier.clickable{
                                    tool = it
                                },
                                leadingContent = {
                                    if(it.lastUpdated==-1L)StudioIcon(
                                        Res.drawable.check_indeterminate_small,
                                        "Abandoned"
                                    ) else if(it.lastUpdated==1L)StudioIcon(
                                        Res.drawable.add,
                                        "New tool"
                                    ) else if(it.lastUpdated>1)StudioIcon(
                                        Res.drawable.sync,
                                        "Tool updated"
                                    )
                                },
                                headlineContent = {Text(it.name)},
                                trailingContent = {Icon(painterResource(Res.drawable.info),
                                    contentDescription = "Details")}
                            )
                        }
                    }
                }
                if(showEditServerNameDialog){
                    EditMCPServerNameDialog(
                        selectedToolMaker,
                        onDismissRequest = {
                            showEditServerNameDialog = false
                        },
                        onConfirmRequest = { toolMaker,toolMakerName ->
                            if(toolMaker.id>Int.MAX_VALUE) {
                                myStudioViewModel.modifyMCPServerName(
                                    toolAgent!!,toolMaker, toolMakerName)
                            }else myStudioViewModel.modifyMCPServerNameForStudio(
                                toolAgent!!, toolMaker,toolMakerName
                            )
                        },
                    )
                }else if(showEditServerTagsDialog){
                    EditMCPServerTagsDialog(
                        selectedToolMaker,
                        onDismissRequest = {
                            showEditServerTagsDialog = false
                        },
                        onConfirmRequest = { toolMaker,toolMakerTags ->
                            myStudioViewModel.modifyToolMakerTags(toolMaker,toolMakerTags)
                        }
                    )
                } else if(showConfigServerFromTemplateDialog){
                    ConfigMCPServerFromTemplatesDialog(
                        toolAgent!!,
                        selectedToolMaker,
                        onConfirmRequest = { toolMaker,inputs->
                            myStudioViewModel.modifyMCPServerConfig(
                                toolAgent!!,toolMaker,inputs
                            )
                        },
                        onDismissRequest = {
                            showConfigServerFromTemplateDialog = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun StudioToolDetailsView(
    toolAgent: AIPortToolAgent,
    toolMaker: AIPortToolMaker,
    tool: AIPortTool,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit
) {
//    val viewModel by remember { mutableStateOf(ToolDetailsViewModel(toolId)) }
//    var tool by remember { mutableStateOf(AIPortTool()) }
    var toolDetails by remember { mutableStateOf(ToolDetails("","{}")) }
    val scrollState = rememberScrollState()
    LaunchedEffect(null){
        StudioRepository.getToolFromStudio(toolAgent,toolMaker,tool){
            if(it.successful()) it.data?.let{
//                tool = it
                val json = JSON.parseToJsonElement(it.metaData)
                val description = json.jsonObject["description"]?.jsonPrimitive?.content
                val inputSchema = json.jsonObject["requestSchema"]?.jsonPrimitive?.content
                toolDetails = ToolDetails(description?:"",inputSchema?:"{}")
            }
        }
    }
    Column(modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = onBackClick,
            ){
                Icon(painterResource(Res.drawable.arrow_back), contentDescription = null)
            }
            Text(tool.name, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.weight(1f))
        }
        HorizontalDivider()
        Text(
            text = toolDetails.description,
            modifier = Modifier.padding(8.dp).verticalScroll(scrollState)
        )
    }
}