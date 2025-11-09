package ai.mcpdirect.studio.app.agent

import ai.mcpdirect.mcpdirectstudioapp.JSON
import ai.mcpdirect.mcpdirectstudioapp.getPlatform
import ai.mcpdirect.studio.app.Screen
import ai.mcpdirect.studio.app.UIState
import ai.mcpdirect.studio.app.auth.authViewModel
import ai.mcpdirect.studio.app.compose.*
import ai.mcpdirect.studio.app.generalViewModel
import ai.mcpdirect.studio.app.mcp.ConfigMCPServerDialog
import ai.mcpdirect.studio.app.mcp.ConfigMCPServerFromTemplatesDialog
import ai.mcpdirect.studio.app.mcp.ConnectMCPServerDialog
import ai.mcpdirect.studio.app.mcp.EditMCPServerNameDialog
import ai.mcpdirect.studio.app.mcp.EditMCPServerTagsDialog
import ai.mcpdirect.studio.app.model.AIPortServiceResponse
import ai.mcpdirect.studio.app.model.MCPServer
import ai.mcpdirect.studio.app.model.account.AIPortUser
import ai.mcpdirect.studio.app.model.aitool.AIPortMCPServerConfig
import ai.mcpdirect.studio.app.model.aitool.AIPortToolMaker
import ai.mcpdirect.studio.app.template.ConnectMCPTemplateDialog
import ai.mcpdirect.studio.app.template.CreateMCPTemplateDialog
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
import kotlinx.serialization.json.encodeToJsonElement
import mcpdirectstudioapp.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource

enum class MyStudioScreenDialog {
    None,
    ConnectMCP,
//    ConfigMCP,
    ConnectMCPTemplate,
    CreateMCPTemplate
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
//        MyStudioScreenDialog.ConfigMCP -> {
//            when(val toolMaker = myStudioViewModel.toolMaker){
//                is MCPServer -> ConfigMCPServerDialog(
//                    toolMaker,
//                    onDismissRequest = { dialog = MyStudioScreenDialog.None },
//                    onConfirmRequest = { it ->
//                        if(toolMaker.id<0) myStudioViewModel.configMCPServer(it)
//                        else {
//                            val config = AIPortMCPServerConfig()
//                            config.transport = it.transport
//                            config.url = it.url
//                            config.command = it.command
//                            config.args = JSON.encodeToJsonElement(it.args).toString()
//                            config.env = JSON.encodeToJsonElement(it.env).toString()
//                            myStudioViewModel.configMCPServer(
//                                myStudioViewModel.toolAgent,
//                                config
//                            )
//                        }
//                    }
//                )
//            }
//        }
        MyStudioScreenDialog.ConnectMCPTemplate -> {
            mcpTemplateListViewModel.toolMakerTemplate?.let {
                template ->
                ConnectMCPTemplateDialog(
                    template,
                    onDismissRequest = {dialog=MyStudioScreenDialog.None},
                    onConfirmRequest = { name, config ->
                        myStudioViewModel.createToolMakerByTemplate(
                            template.id,template.agentId,name,config
                        ){ code, message, toolMaker ->
                            if(code==0)toolMaker?.let {
                                generalViewModel.toolAgent(it.agentId){
                                        code, message, data ->
                                    if(code==0){
                                        data?.let {
                                            myStudioViewModel.connectToolMaker(
                                                it.engineId,
                                                toolMaker.id,
                                                toolMaker.agentId
                                            ){ code, message, mcpServer ->
                                                generalViewModel.refreshToolMakers()
                                                myStudioViewModel.queryMCPTools(toolMaker)
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
        MyStudioScreenDialog.CreateMCPTemplate -> {
            CreateMCPTemplateDialog(
                myStudioViewModel.toolMaker,
                onConfirmRequest = { name,type,agentId,config,inputs ->
                    dialog = MyStudioScreenDialog.None
                    mcpTemplateListViewModel.createToolMakerTemplate(name,type,agentId,config,inputs)
                },
                onDismissRequest = {
                    dialog = MyStudioScreenDialog.None
                }
            )
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
                        generalViewModel.currentScreen(
                            Screen.ConnectMCP,
                            it.name,
                            Screen.MyStudio

                        )
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
    var showEditServerNameDialog by remember { mutableStateOf(false) }
    var showEditServerTagsDialog by remember { mutableStateOf(false) }
    var showEditServerConfigDialog by remember { mutableStateOf(false) }
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
            if(myStudioViewModel.toolMaker.id>0L) {
                Button(onClick = {
                    onDialogRequest(MyStudioScreenDialog.CreateMCPTemplate)
                }) {
                    Text("Create MCP Template")
                }
                Spacer(Modifier.width(8.dp))
            }
            Button(
                onClick = {onDialogRequest(MyStudioScreenDialog.ConnectMCP)}
            ){
                Text("Connect MCP Server")
            }
        }
        Row {
            Column(Modifier.weight(1.0f)) {
                StudioActionBar(
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
                        val me = it.id<Int.MAX_VALUE||it.userId==authViewModel.user.id
                        var user by remember { mutableStateOf<AIPortUser?>(null) }
                        if(!me){
                            generalViewModel.user(it.userId){
                                    code, message, data ->
                                if(code== AIPortServiceResponse.SERVICE_SUCCESSFUL&&data!=null){
                                    user = data
                                }
                            }
//                            generalViewModel.team(it.userId,it.templateId){
//                                    code, message, data ->
//                            }
                        }
                        StudioListItem(
                            modifier = Modifier.clickable(
                                enabled = it.id!=myStudioViewModel.toolMaker.id
                            ){
                                myStudioViewModel.toolMaker(it)
                            },
                            selected = myStudioViewModel.toolMaker.id == it.id,
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
                                if (it.id > 0) StudioIcon(
                                    icon = Res.drawable.cloud_done,
                                    contentDescription = "on MCPdirect"
                                ) else StudioIcon(
                                    Res.drawable.devices,
                                    contentDescription = "On local device"
                                )
                            },
                            headlineContent = {Text(it.name)},
                            supportingContent = {
                                Row {
                                    if (me) it.tags?.let {
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
                    StudioActionBar(
                        actions = {
                            TooltipIconButton(
                                Res.drawable.refresh,
                                contentDescription = "Refresh MCP Server Tools",
                                onClick = {
                                    myStudioViewModel.queryMCPTools(it)
                                })
                            if(it.userId==authViewModel.user.id) {
                                if (it.id > Int.MAX_VALUE) TooltipIconButton(
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
//                                    onDialogRequest(MyStudioScreenDialog.ConfigMCP)
                                        showEditServerConfigDialog = true
                                    })
                                TooltipIconButton(
                                    Res.drawable.cloud_upload,
                                    contentDescription = "Publish to MCPdirect",
                                    onClick = {
                                        myStudioViewModel.publishMCPTools(it)
                                    })
                            }
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
            if(showEditServerNameDialog){
                EditMCPServerNameDialog(
                    myStudioViewModel.toolMaker,
                    onDismissRequest = {
                        showEditServerNameDialog = false
                    },
                    onConfirmRequest = { toolMaker,toolMakerName ->
                        if(toolMaker.id>Int.MAX_VALUE) {
                            myStudioViewModel.modifyMCPServerName(
                                toolAgent,toolMaker, toolMakerName)
                        }else myStudioViewModel.modifyMCPServerNameForStudio(
                            toolAgent, toolMaker as MCPServer,toolMakerName
                        )
                    },
                )
            }else if(showEditServerTagsDialog){
                EditMCPServerTagsDialog(
                    myStudioViewModel.toolMaker,
                    onDismissRequest = {
                        showEditServerTagsDialog = false
                    },
                    onConfirmRequest = { toolMaker,toolMakerTags ->
                        myStudioViewModel.modifyToolMakerTags(toolMaker,toolMakerTags)
                    }
                )
            }else if(showEditServerConfigDialog){
                when(val toolMaker = myStudioViewModel.toolMaker){
                    is MCPServer -> ConfigMCPServerDialog(
                        toolMaker,
                        onDismissRequest = { showEditServerConfigDialog=false },
                        onConfirmRequest = {
                            if(toolMaker.id<0) myStudioViewModel.modifyMCPServerConfigForStudio(
                                toolAgent,toolMaker,it
                            ) else {
                                val config = AIPortMCPServerConfig()
                                config.transport = it.transport
                                config.url = it.url
                                config.command = it.command
                                config.args = JSON.encodeToJsonElement(it.args).toString()
                                config.env = JSON.encodeToJsonElement(it.env).toString()
                                myStudioViewModel.modifyMCPServerConfig(
                                    myStudioViewModel.toolAgent,
                                    config
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ToolMakerByTemplateListView(
    onDialogRequest: (dialog: MyStudioScreenDialog) -> Unit
){
    var showEditServerNameDialog by remember { mutableStateOf(false) }
    var showEditServerTagsDialog by remember { mutableStateOf(false) }
    var showConfigServerFromTemplateDialog  by remember { mutableStateOf(false) }
    var selectedToolMaker by remember { mutableStateOf(AIPortToolMaker()) }
    var statusMessage by remember { mutableStateOf<String?>(null) }
    mcpTemplateListViewModel.toolMakerTemplate?.let {
        generalViewModel.topBarActions = {
            TextButton(
                onClick = {onDialogRequest(MyStudioScreenDialog.ConnectMCPTemplate)}
            ){
                Text("Connect MCP Template")
            }
        }
    }
    mcpTemplateListViewModel.toolMakerTemplate?.let {
            template ->
        Row {
            LazyColumn(Modifier.weight(1.0f)) {
                items(generalViewModel.toolMakers) { maker ->
                    if (maker.templateId == template.id) {
                        StudioListItem(
                            modifier = Modifier.clickable(
                                onClick = {
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
            }else selectedToolMaker.let {
                Column(Modifier.weight(2.0f)) {
                    StudioActionBar(
                        actions = {
                            TooltipIconButton(
                                Res.drawable.plug_connect,
                                contentDescription = "Connect MCP Server"
                            ) {
                                statusMessage = null
                                generalViewModel.toolAgent(selectedToolMaker.agentId) {
                                    code, message, data ->
                                    if (code == 0) {
                                        data?.let {
                                            myStudioViewModel.connectToolMaker(
                                                it.engineId,
                                                selectedToolMaker.id,
                                                it.id
                                            ) { code, message, mcpServer ->
                                                if(code!= AIPortServiceResponse.SERVICE_SUCCESSFUL){
                                                    statusMessage = message
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            TooltipIconButton(
                                Res.drawable.refresh,
                                contentDescription = "Refresh MCP Server Tools",
                                onClick = {
                                    myStudioViewModel.queryMCPTools(it)
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
                        onConfirmRequest = {
                                toolMaker,toolMakerName ->
                            generalViewModel.toolAgent(selectedToolMaker.agentId) {
                                    code, message, data ->
                                if (code == 0&&data!=null) {
                                    if(toolMaker.id>Int.MAX_VALUE) {
                                        myStudioViewModel.modifyMCPServerName(
                                            data,toolMaker, toolMakerName)
                                    }else myStudioViewModel.modifyMCPServerNameForStudio(
                                        data, toolMaker as MCPServer,toolMakerName
                                    )
                                }
                            }
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
                        selectedToolMaker,
                        template,
                        onConfirmRequest = { toolMaker,config->
                            generalViewModel.toolAgent(toolMaker.agentId) {
                                    code, message, data ->
                                if (code == 0&&data!=null) {
                                    myStudioViewModel.modifyMCPServerConfig(
                                        data,config
                                    )
//                                    data?.let {
//                                        myStudioViewModel.connectToolMaker(
//                                            it.engineId,
//                                            toolMaker.id,
//                                            toolMaker.agentId
//                                        ){
//                                            code, message, mcpServer ->
//                                        }
//                                    }
                                }
                            }
                        },
                        onDismissRequest = {
                            showConfigServerFromTemplateDialog = false
                        }
                    )
                }
            }
        }
    }
//        }



}