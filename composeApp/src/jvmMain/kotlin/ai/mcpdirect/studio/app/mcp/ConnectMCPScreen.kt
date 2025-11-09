package ai.mcpdirect.studio.app.mcp

import ai.mcpdirect.mcpdirectstudioapp.JSON
import ai.mcpdirect.studio.app.UIState
import ai.mcpdirect.studio.app.agent.MyStudioScreenDialog
import ai.mcpdirect.studio.app.agent.myStudioViewModel
import ai.mcpdirect.studio.app.compose.*
import ai.mcpdirect.studio.app.generalViewModel
import ai.mcpdirect.studio.app.model.MCPServer
import ai.mcpdirect.studio.app.model.aitool.AIPortMCPServerConfig
import ai.mcpdirect.studio.app.template.CreateMCPTemplateDialog
import ai.mcpdirect.studio.app.template.mcpTemplateListViewModel
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
@Composable
fun ConnectMCPScreen(){
    var dialog by remember { mutableStateOf(ConnectMCPScreenDialog.None) }
//    val uiState = connectMCPViewModel.uiState
    val makers = connectMCPViewModel.toolMakers
//    if(uiState== UIState.Loading) Column(
//        Modifier.fillMaxSize(),
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        CircularProgressIndicator(
//            modifier = Modifier.size(48.dp),
//            color = MaterialTheme.colorScheme.primary
//        )
//    } else
    if(makers.isEmpty()) Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = { dialog = ConnectMCPScreenDialog.ConnectMCP }){
            Text("Connect your first MCP Server for this Studio")
        }
    } else Row(Modifier.fillMaxSize()) {
        generalViewModel.topBarActions = {
            if(connectMCPViewModel.toolMaker.id>0L){
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
        LazyColumn(Modifier.wrapContentHeight().width(300.dp).padding(start = 8.dp, top = 16.dp, bottom = 16.dp)) {
            items(makers) {
                StudioListItem(
                    modifier = Modifier.clickable(
                        enabled = it.id != connectMCPViewModel.toolMaker.id && it.status>-1
                    ) {
                        connectMCPViewModel.toolMaker(it)
                    },
                    selected = connectMCPViewModel.toolMaker.id == it.id,
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
                    supportingContent
                    = { if(it.tags!=null&& it.tags!!.isNotBlank()) Text(it.tags!!) },
                    trailingContent = {
                        if (it.status == 0) StudioIcon(
                            Res.drawable.mobiledata_off,
                            contentDescription = "Disconnect",
                            tint = MaterialTheme.colorScheme.error
                        ) else if(it.status==Int.MIN_VALUE){
                            CircularProgressIndicator(
                                Modifier.size(16.dp)
                            )
                        } else if (it.status < 0) StudioIcon(
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
                Column(Modifier.weight(2.0f)) {
                    StudioActionBar(
                        actions = {
                            TooltipIconButton(
                                Res.drawable.refresh,
                                contentDescription = "Refresh MCP Server Tools",
                                onClick = {
                                    connectMCPViewModel.queryMCPTools(it)
                                })
                            if(it.id>Int.MAX_VALUE) {
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
                                Res.drawable.edit,
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
                    )
                    HorizontalDivider()
                    when (it.status) {
                        -1 -> {
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
            when (val toolMaker = connectMCPViewModel.toolMaker) {
                is MCPServer -> ConfigMCPServerDialog(
                    toolMaker,
                    onDismissRequest = { dialog = ConnectMCPScreenDialog.None },
                    onConfirmRequest = { it ->
                        if(toolMaker.id<0L) connectMCPViewModel.configMCPServer(it)
                        else {
                            val config = AIPortMCPServerConfig()
                            config.transport = it.transport
                            config.url = it.url
                            config.command = it.command
                            config.args = JSON.encodeToJsonElement(it.args).toString()
                            config.env = JSON.encodeToJsonElement(it.env).toString()
                            myStudioViewModel.configMCPServer(
                                myStudioViewModel.toolAgent,
                                config
                            )
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

        }
        ConnectMCPScreenDialog.EditMCPServerTags->{
            if(connectMCPViewModel.toolMaker.id>Int.MAX_VALUE){
                EditMCPServerTagsDialog(
                    connectMCPViewModel.toolMaker,
                    onDismissRequest = {
                        dialog = ConnectMCPScreenDialog.None
                    },
                    onConfirmRequest = {
                            toolMaker,toolMakerTags ->
                        connectMCPViewModel.modifyMCPServerTags(toolMaker,toolMakerTags)
                    }
                )
            }
        }
    }
}