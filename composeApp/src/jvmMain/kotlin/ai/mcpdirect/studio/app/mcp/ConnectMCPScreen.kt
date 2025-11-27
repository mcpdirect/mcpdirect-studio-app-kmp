package ai.mcpdirect.studio.app.mcp

import ai.mcpdirect.mcpdirectstudioapp.JSON
import ai.mcpdirect.studio.app.auth.authViewModel
import ai.mcpdirect.studio.app.compose.*
import ai.mcpdirect.studio.app.generalViewModel
import ai.mcpdirect.studio.app.model.AIPortServiceResponse
import ai.mcpdirect.studio.app.model.MCPServer
import ai.mcpdirect.studio.app.model.account.AIPortUser
import ai.mcpdirect.studio.app.model.aitool.AIPortMCPServerConfig
import ai.mcpdirect.studio.app.model.aitool.AIPortToolMaker.Companion.STATUS_ERROR
import ai.mcpdirect.studio.app.model.aitool.AIPortToolMaker.Companion.STATUS_OFF
import ai.mcpdirect.studio.app.model.aitool.AIPortToolMaker.Companion.STATUS_ON
import ai.mcpdirect.studio.app.model.aitool.AIPortToolMaker.Companion.STATUS_WAITING
import ai.mcpdirect.studio.app.model.repository.UserRepository
import ai.mcpdirect.studio.app.template.CreateMCPTemplateDialog
import ai.mcpdirect.studio.app.template.mcpTemplateListViewModel
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.serialization.json.encodeToJsonElement
import mcpdirectstudioapp.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource

enum class ConnectMCPScreenDialog {
    None,
    ConnectMCP,
    ConfigMCP,
    CreateMCPTemplate,
    EditMCPServerName,
    EditMCPServerTags,
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConnectMCPScreen(){
    var dialog by remember { mutableStateOf(ConnectMCPScreenDialog.None) }
    val makers = connectMCPViewModel.toolMakers
    if(makers.isEmpty()) Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = { dialog = ConnectMCPScreenDialog.ConnectMCP }){
            Text("Connect your first MCP Server for this Studio")
        }
    } else Row(Modifier.fillMaxSize()) {
        LaunchedEffect(null) {
            generalViewModel.topBarActions = {
                if (connectMCPViewModel.toolMaker.id > 0L) {
                    Button(
                        onClick = { dialog = ConnectMCPScreenDialog.CreateMCPTemplate }
                    ) {
                        Text("Create MCP Template")
                    }
                    Spacer(Modifier.width(8.dp))
                }
                Button(
                    onClick = { dialog = ConnectMCPScreenDialog.ConnectMCP }
                ) {
                    Text("Connect MCP Server")
                }
            }
        }
        DisposableEffect(null){
            onDispose {
                generalViewModel.topBarActions = {}
            }
        }
        LazyColumn(Modifier.wrapContentHeight().width(300.dp).padding(start = 8.dp, top = 16.dp, bottom = 16.dp)) {
            items(makers) {
                val me = it.id<Int.MAX_VALUE|| UserRepository.me(it.userId)
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
                        enabled = it.id != connectMCPViewModel.toolMaker.id && it.status<STATUS_WAITING
                    ) {
                        connectMCPViewModel.toolMaker(it)
                    },
                    selected = connectMCPViewModel.toolMaker.id == it.id,
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
                        if (it.status == STATUS_OFF) StudioIcon(
                            Res.drawable.mobiledata_off,
                            contentDescription = "Disconnect",
                            tint = MaterialTheme.colorScheme.error
                        ) else if(it.status==STATUS_WAITING){
                            CircularProgressIndicator(
                                Modifier.size(16.dp)
                            )
                        } else if (it.status == STATUS_ERROR) StudioIcon(
                            Res.drawable.error,
                            contentDescription = "Disconnect",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                )
            }
        }
        StudioCard(Modifier.fillMaxSize().padding(top = 8.dp, bottom = 8.dp, end = 8.dp)) {
            if (connectMCPViewModel.toolMaker.id == 0L) {
                StudioBoard(Modifier.weight(2.0f)) {
                    Text("Select a MCP server to view")
                }
            } else connectMCPViewModel.toolMaker.let {
                val me = it.id<Int.MAX_VALUE|| UserRepository.me(it.userId)
                Column(Modifier.weight(2.0f)) {
                    StudioActionBar(
                        navigationIcon = {
                            Spacer(Modifier.width(8.dp))
                            when(it.status){
                                STATUS_OFF->{
                                    Text("Not start")
                                    TooltipIconButton(
                                        Res.drawable.play_circle,
                                        "Start",
                                        MaterialTheme.colorScheme.primary,
                                        onClick = {
                                            connectMCPViewModel.modifyToolMakerStatus(it,1)
                                        }
                                    )
                                }
                                STATUS_ON->{
                                    Text("Running", color = MaterialTheme.colorScheme.primary)
                                    TooltipIconButton(
                                        Res.drawable.stop_circle,
                                        "Stop",
                                        MaterialTheme.colorScheme.error,
                                        onClick = {
                                            connectMCPViewModel.modifyToolMakerStatus(it,0)
                                        }
                                    )
                                    TooltipIconButton(
                                        Res.drawable.restart_alt,
                                        "Restart",
                                        MaterialTheme.colorScheme.primary,
                                        onClick = {
                                            connectMCPViewModel.modifyToolMakerStatus(it,1)
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
                                            connectMCPViewModel.modifyToolMakerStatus(it,1)
                                        }
                                    )
                                }
                            }

                        },
                        actions = {
                            Spacer(Modifier.weight(1.0f))
                            if(it.status==STATUS_ON) {
                                TooltipIconButton(
                                    Res.drawable.refresh,
                                    contentDescription = "Refresh MCP Server Tools",
                                    onClick = {
                                        connectMCPViewModel.queryMCPTools(it)
                                    })

                                if (me) {
                                    if (it.id > Int.MAX_VALUE) {
                                        TooltipIconButton(
                                            Res.drawable.sell,
                                            contentDescription = "Edit tags",
                                            onClick = {
                                                dialog = ConnectMCPScreenDialog.EditMCPServerTags
                                            })
                                    }
                                    TooltipIconButton(
                                        Res.drawable.badge,
                                        contentDescription = "Edit name",
                                        onClick = {
                                            dialog = ConnectMCPScreenDialog.EditMCPServerName
                                        })
                                    TooltipIconButton(
                                        Res.drawable.data_object,
                                        contentDescription = "Config MCP Server",
                                        onClick = {
                                            dialog = ConnectMCPScreenDialog.ConfigMCP
                                        })
                                    TooltipIconButton(
                                        Res.drawable.cloud_upload,
                                        contentDescription = "Publish to MCPdirect",
                                        onClick = {
                                            connectMCPViewModel.publishMCPTools(it)
                                        })
                                }
                            }
                        }
                    )
                    HorizontalDivider()
                    when (it.status) {
                        STATUS_ERROR -> {
                            if (it is MCPServer) {
                                it.statusMessage?.let {
                                    StudioBoard {
                                        Text(it, color = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        }

                        else -> LazyColumn(Modifier.weight(1.0f)) {
                            items(connectMCPViewModel.tools) {
                                ListItem(
                                    modifier = Modifier.clickable {

                                    },
                                    leadingContent = {
                                        if (it.lastUpdated == -1L) StudioIcon(
                                            Res.drawable.check_indeterminate_small,
                                            "Abandoned"
                                        ) else if (it.lastUpdated == 1L) StudioIcon(
                                            Res.drawable.add,
                                            "New tool"
                                        ) else if (it.lastUpdated > 1) StudioIcon(
                                            Res.drawable.sync,
                                            "Tool updated"
                                        )
                                    },
                                    headlineContent = { Text(it.name) },
                                    trailingContent = {
                                        Icon(
                                            painterResource(Res.drawable.info),
                                            contentDescription = "Details"
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
    when (dialog) {
        ConnectMCPScreenDialog.None -> {}
        ConnectMCPScreenDialog.ConnectMCP -> ConnectMCPServerDialog(
            onDismissRequest = { dialog = ConnectMCPScreenDialog.None },
            onConfirmRequest = { configs ->
                connectMCPViewModel.connectMCPServer(configs)
            }
        )
        ConnectMCPScreenDialog.ConfigMCP -> {
            when(val toolMaker = connectMCPViewModel.toolMaker){
                is MCPServer -> ConfigMCPServerDialog(
                    toolMaker,
                    onDismissRequest = { dialog = ConnectMCPScreenDialog.None },
                    onConfirmRequest = {
                        mcpServer,mcpServerConfig->
                        if(mcpServer.id>Int.MAX_VALUE) {
                            val config = AIPortMCPServerConfig()
                            config.id = toolMaker.id
                            config.transport = mcpServerConfig.transport
                            config.url = mcpServerConfig.url
                            config.command = mcpServerConfig.command
                            config.args = JSON.encodeToJsonElement(mcpServerConfig.args).toString()
                            config.env = JSON.encodeToJsonElement(mcpServerConfig.env).toString()
                            connectMCPViewModel.modifyMCPServerConfig(
                                config
                            )
                        }else{
                            connectMCPViewModel.modifyMCPServerConfigForStudio(mcpServer,mcpServerConfig)
                        }
                    }
                )
            }
        }
        ConnectMCPScreenDialog.CreateMCPTemplate -> {
            if(connectMCPViewModel.toolMaker.id>0L) CreateMCPTemplateDialog(
                connectMCPViewModel.toolMaker,
                onConfirmRequest = { name,type,agentId,config,inputs ->
                    dialog =ConnectMCPScreenDialog.None
                    mcpTemplateListViewModel.createToolMakerTemplate(name,type,agentId,config,inputs)
                },
                onDismissRequest = {
                    dialog = ConnectMCPScreenDialog.None
                }
            )
        }
        ConnectMCPScreenDialog.EditMCPServerName->{
            EditMCPServerNameDialog(
                connectMCPViewModel.toolMaker,
                onDismissRequest = {
                    dialog = ConnectMCPScreenDialog.None
                },
                onConfirmRequest = { toolMaker,toolMakerName ->
                    if(toolMaker.id>Int.MAX_VALUE) {
                        connectMCPViewModel.modifyMCPServerName(toolMaker, toolMakerName)
                    }else connectMCPViewModel.modifyMCPServerNameForStudio(toolMaker as MCPServer,toolMakerName
                    )
                },
            )
        }
        ConnectMCPScreenDialog.EditMCPServerTags->{
            EditMCPServerTagsDialog(
                connectMCPViewModel.toolMaker,
                onDismissRequest = {
                    dialog = ConnectMCPScreenDialog.None
                },
                onConfirmRequest = { toolMaker,toolMakerTags ->
                    connectMCPViewModel.modifyToolMakerTags(toolMaker,toolMakerTags)
                }
            )
        }
    }
}