package ai.mcpdirect.studio.app.agent

import ai.mcpdirect.mcpdirectstudioapp.getPlatform
import ai.mcpdirect.studio.app.Screen
import ai.mcpdirect.studio.app.UIState
import ai.mcpdirect.studio.app.auth.authViewModel
import ai.mcpdirect.studio.app.compose.*
import ai.mcpdirect.studio.app.generalViewModel
import ai.mcpdirect.studio.app.mcp.ConfigMCPServerDialog
import ai.mcpdirect.studio.app.mcp.ConnectMCPServerDialog
import ai.mcpdirect.studio.app.model.MCPServer
import ai.mcpdirect.studio.app.model.aitool.AIPortToolMaker
import ai.mcpdirect.studio.app.template.ConnectMCPTemplateDialog
import ai.mcpdirect.studio.app.template.MCPTemplateListView
import ai.mcpdirect.studio.app.template.mcpTemplateListViewModel
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import mcpdirectstudioapp.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource

enum class MyStudioScreenDialog {
    None,
    ConnectMCP,
    ConfigMCP,
    ConnectMCPTemplate
}

@Composable
fun MyStudioScreen(){
    LaunchedEffect(null){
        generalViewModel.refreshToolAgents()
        generalViewModel.refreshToolMakers()
    }
    var dialog by remember { mutableStateOf(MyStudioScreenDialog.None) }
    var currentTabIndex by remember { mutableStateOf(0) }

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
                            generalViewModel.topBarActions = {}
                        },
                        text = { Text(title) }
                    )
                }
            }
            // Content based on selected tab
            when (currentTabIndex) {
                0 -> ToolAgentListView()
                1 -> MCPTemplateListView()
            }
        }

        StudioCard(Modifier.fillMaxSize().padding(top = 8.dp, bottom = 8.dp, end = 8.dp)) {
            when (currentTabIndex) {
                0 -> ToolMakerListView{
                    dialog = it
                }
                1 -> ToolMakerByTemplateListView{
                    dialog = it
                }
            }
        }
    }

    when(dialog){
        MyStudioScreenDialog.None ->{}
        MyStudioScreenDialog.ConnectMCP -> ConnectMCPServerDialog(
            title = myStudioViewModel.toolAgent.name,
            onDismissRequest = {dialog=MyStudioScreenDialog.None},
            onConfirmRequest = { configs ->
                myStudioViewModel.connectMCPServer(configs)
            }
        )
        MyStudioScreenDialog.ConfigMCP -> {
            when(val toolMaker = myStudioViewModel.toolMaker){
                is MCPServer -> ConfigMCPServerDialog(
                    toolMaker,
                    onDismissRequest = {dialog=MyStudioScreenDialog.None},
                    onConfirmRequest = { config ->
                        myStudioViewModel.configMCPServer(config)
                    }
                )
            }
        }
        MyStudioScreenDialog.ConnectMCPTemplate -> {
            mcpTemplateListViewModel.toolMakerTemplate?.let {
                template ->
                ConnectMCPTemplateDialog(
                    template,
                    onDismissRequest = {dialog=MyStudioScreenDialog.None},
                    onConfirmRequest = { name, config ->
                        myStudioViewModel.createToolMakerByTemplate(
                            template.id,template.agentId,name,config
                        ){ code, message, mcpServer ->
                            if(code==0)mcpServer?.let {
                                generalViewModel.toolAgent(it.agentId){
                                        code, message, data ->
                                    if(code==0){
                                        data?.let {
                                            myStudioViewModel.connectToolMaker(
                                                it.engineId,
                                                mcpServer.id,
                                                mcpServer.agentId
                                            ){ code, message, mcpServer ->
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
}
@Composable
fun ToolAgentListView(){
    val uiState = myStudioViewModel.uiState
    val toolAgents = generalViewModel.toolAgents
    LazyColumn{
        items(toolAgents){
            println("${it.id},${it.name}")
            if(it.id!=0L&&it.userId== authViewModel.user.id) StudioListItem(
                selected = it.id==myStudioViewModel.toolAgent.id,
                modifier = Modifier.clickable(
                    enabled = uiState !is UIState.Loading && it.id!=myStudioViewModel.toolAgent.id
                ){
                    if(it.id== getPlatform().toolAgentId)
                        generalViewModel.currentScreen(Screen.ConnectMCP)
                    else myStudioViewModel.toolAgent(it)
                },
                headlineContent = {Text(it.name)},
                supportingContent = {
                    if(it.id == getPlatform().toolAgentId)
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
@Composable
fun ToolMakerListView(
    onDialogRequest: (dialog: MyStudioScreenDialog) -> Unit
){
    val toolAgents = generalViewModel.toolAgents
    val uriHandler = LocalUriHandler.current
    val uiState = myStudioViewModel.uiState
    val toolAgent = myStudioViewModel.toolAgent
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
                myStudioViewModel.queryMCPServers(toolAgent)
            }
        ){
            Text("Refresh")
        }
    } else{
        generalViewModel.topBarActions = {
            TextButton(
                onClick = {onDialogRequest(MyStudioScreenDialog.ConnectMCP)}
            ){
                Text("Connect MCP Server")
            }
        }
        Row {
            Column(Modifier.weight(1.0f)) {
                StudioToolbar(
                    "",
                    actions = {
                        TooltipIconButton(
                            Res.drawable.refresh,
                            contentDescription = "Refresh MCP Servers",
                            onClick = {
                                myStudioViewModel.queryMCPServers(
                                    myStudioViewModel.toolAgent
                                )
                            })
                    }
                )
                HorizontalDivider()
                LazyColumn {
                    val makers = myStudioViewModel.toolMakers
                    items(makers){
                        StudioListItem(
                            modifier = Modifier.clickable(
                                enabled = it.id!=myStudioViewModel.toolMaker.id
                            ){
                                myStudioViewModel.toolMaker(it)
                            },
                            selected = myStudioViewModel.toolMaker.id == it.id,
                            leadingContent = {
                                if (it.id > 0) StudioIcon(
                                    icon = Res.drawable.cloud_done,
                                    contentDescription = "on MCPdirect"
                                ) else StudioIcon(
                                    Res.drawable.devices,
                                    contentDescription = "On local device"
                                )
                            },
                            headlineContent = {Text(it.name?:"")},
                            supportingContent = { it.tags?.let { Text(it) } },
                            trailingContent = {
                                if (it.status == 0) StudioIcon(
                                    Res.drawable.mobiledata_off,
                                    contentDescription = "Disconnect",
                                    tint = MaterialTheme.colorScheme.error
                                ) else if (it.status < 0) StudioIcon(
                                    Res.drawable.error,
                                    contentDescription = "Disconnect",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        )
                    }
                }
            }
            VerticalDivider()
            if(myStudioViewModel.toolMaker.id==0L){
                StudioBoard(Modifier.weight(2.0f)) {
                    Text("Select a MCP server to view")
                }
            }else myStudioViewModel.toolMaker.let {
                Column(Modifier.weight(2.0f)) {
                    StudioToolbar(
                        actions = {
                            TooltipIconButton(
                                Res.drawable.refresh,
                                contentDescription = "Refresh MCP Server Tools",
                                onClick = {
                                    myStudioViewModel.queryMCPTools(it)
                                })
                            TooltipIconButton(
                                Res.drawable.edit,
                                contentDescription = "Config MCP Server",
                                onClick = {
                                    onDialogRequest(MyStudioScreenDialog.ConfigMCP)
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
                    when(it.status){
                        -1 -> {
                            if(it is MCPServer){
                                it.statusMessage?.let{
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
        }
    }
}

@Composable
fun ToolMakerByTemplateListView(
    onDialogRequest: (dialog: MyStudioScreenDialog) -> Unit
){
    var selectedToolMaker by remember { mutableStateOf(AIPortToolMaker()) }
    mcpTemplateListViewModel.toolMakerTemplate?.let {
        generalViewModel.topBarActions = {
            TextButton(
                onClick = {onDialogRequest(MyStudioScreenDialog.ConnectMCPTemplate)}
            ){
                Text("Connect MCP Template")
            }
        }
    }
    Column(Modifier.fillMaxSize()) {
        mcpTemplateListViewModel.toolMakerTemplate?.let {
            template ->
            Row(Modifier.fillMaxWidth().padding(8.dp)) {

                Text(template.name)

                Spacer(Modifier.weight(1.0f))
                if(selectedToolMaker.id!=0L) {
                    TooltipIconButton(
                        Res.drawable.plug_connect,
                        contentDescription = "Connect MCP Server"
                    ) {
                        generalViewModel.toolAgent(selectedToolMaker.agentId) { code, message, data ->
                            if (code == 0) {
                                data?.let {
                                    myStudioViewModel.connectToolMaker(
                                        it.engineId,
                                        selectedToolMaker.id,
                                        it.id
                                    ) { code, message, mcpServer ->
                                    }
                                }
                            }
                        }
                    }
                    TooltipIconButton(
                        Res.drawable.refresh,
                        contentDescription = "Refresh MCP Tools"
                    ) {
                        generalViewModel.toolAgent(selectedToolMaker.agentId) { code, message, data ->
                            if (code == 0) {
                                data?.let {
                                    getPlatform().queryMCPToolsFromStudio(
                                        it.engineId,
                                        selectedToolMaker.id,
                                    ){

                                    }
                                }
                            }
                        }
                    }
                }
            }
            HorizontalDivider()

            LazyColumn {
                items(generalViewModel.toolMakers) {
                    maker ->
                    if (maker.templateId==template.id){
                        StudioListItem(
                            modifier = Modifier.clickable(
                                onClick = {
                                    selectedToolMaker = maker
                                }
                            ),
                            selected = maker.id == selectedToolMaker.id,
                            headlineContent = { Text(maker.name) }
                        )
                    }
                }
            }
        }
    }
}