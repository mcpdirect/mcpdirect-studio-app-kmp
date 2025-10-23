package ai.mcpdirect.studio.app.mcp

import ai.mcpdirect.mcpdirectstudioapp.getPlatform
import ai.mcpdirect.studio.app.UIState
import ai.mcpdirect.studio.app.compose.*
import ai.mcpdirect.studio.app.generalViewModel
import ai.mcpdirect.studio.app.model.MCPServer
import ai.mcpdirect.studio.tool.MCPTool
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mcpdirectstudioapp.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource

enum class ConnectMCPScreenDialog {
    None,
    ConnectMCP,
    ConfigMCP
}
@Composable
fun ConnectMCPScreen(){
    LaunchedEffect(null) {
    }

    var dialog by remember { mutableStateOf(ConnectMCPScreenDialog.None) }
    val uiState = connectMCPViewModel.uiState
    generalViewModel.topBarActions = {
        TextButton(
            onClick = { dialog = ConnectMCPScreenDialog.ConnectMCP }
        ) {
            Text("Connect MCP Server")
        }
    }
//    if (uiState == UIState.Loading) LinearProgressIndicator(
//        modifier = Modifier.fillMaxWidth().height(4.dp),
//        color = MaterialTheme.colorScheme.primary
//    ) else LinearProgressIndicator(
//        progress = { 1.0f },
//        modifier = Modifier.fillMaxWidth().height(4.dp),
//        color = MaterialTheme.colorScheme.primary
//    )
    Row(Modifier.fillMaxSize()) {
        LazyColumn(Modifier.wrapContentHeight().width(300.dp).padding(start = 8.dp, top = 16.dp, bottom = 16.dp)) {
            val makers = connectMCPViewModel.toolMakers
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
                    StudioToolbar(
                        actions = {
                            TooltipIconButton(
                                Res.drawable.refresh,
                                contentDescription = "Refresh MCP Server Tools",
                                onClick = {
                                    connectMCPViewModel.queryMCPTools(it)
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
            val toolMaker = connectMCPViewModel.toolMaker
            when (toolMaker) {
                is MCPServer -> ConfigMCPServerDialog(
                    toolMaker,
                    onDismissRequest = { dialog = ConnectMCPScreenDialog.None },
                    onConfirmRequest = { config ->
                        connectMCPViewModel.configMCPServer(config)
                    }
                )
            }
        }
    }
}