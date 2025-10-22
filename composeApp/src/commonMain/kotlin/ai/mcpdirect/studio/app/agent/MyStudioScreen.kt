package ai.mcpdirect.studio.app.agent

import ai.mcpdirect.mcpdirectstudioapp.getPlatform
import ai.mcpdirect.studio.app.UIState
import ai.mcpdirect.studio.app.compose.StudioBoard
import ai.mcpdirect.studio.app.compose.StudioCard
import ai.mcpdirect.studio.app.compose.StudioIcon
import ai.mcpdirect.studio.app.compose.StudioListItem
import ai.mcpdirect.studio.app.compose.StudioToolbar
import ai.mcpdirect.studio.app.compose.TooltipIcon
import ai.mcpdirect.studio.app.compose.TooltipIconButton
import ai.mcpdirect.studio.app.generalViewModel
import ai.mcpdirect.studio.app.mcp.ConfigMCPServerDialog
import ai.mcpdirect.studio.app.mcp.ConnectMCPServerDialog
import ai.mcpdirect.studio.app.model.MCPServer
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mcpdirectstudioapp.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource

enum class MyStudioScreenDialog {
    None,
    ConnectMCP,
    ConfigMCP
}
@Composable
fun MyStudioScreen(
    paddingValues: PaddingValues
){
    LaunchedEffect(null){
        generalViewModel.refreshToolAgents()
        generalViewModel.refreshToolMakers()
    }
    var dialog by remember { mutableStateOf(MyStudioScreenDialog.None) }
    val uiState = myStudioViewModel.uiState
    Row(Modifier.fillMaxSize().padding(paddingValues)){
        LazyColumn(Modifier.wrapContentHeight().width(250.dp).padding(start = 8.dp, top = 16.dp, bottom = 16.dp)) {
            items(generalViewModel.toolAgents){
                if(it.id!=0L&&it.id!= getPlatform().toolAgentId) StudioListItem(
                    selected = it.id==myStudioViewModel.toolAgent.id,
                    modifier = Modifier.clickable(
                        enabled = uiState !is UIState.Loading && it.id!=myStudioViewModel.toolAgent.id
                    ){
                        myStudioViewModel.toolAgent(it)
                    },
                    headlineContent = {Text(it.name)},
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
        StudioCard(Modifier.fillMaxSize().padding(top = 8.dp, bottom = 8.dp, end = 8.dp)) {
            val toolAgent = myStudioViewModel.toolAgent
            if(toolAgent.id<1) StudioBoard {
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
                        onClick = {dialog=MyStudioScreenDialog.ConnectMCP}
                    ){
                        Text("Connect MCP Server")
                    }
                }
//                Column {
//                    StudioToolbar(
//                        myStudioViewModel.toolAgent.name,
//                        actions = {
//                            IconButton(onClick = {
//
//                            }){
//                                Icon(painterResource(Res.drawable.refresh),
//                                    contentDescription = "Refresh MCP Servers")
//                            }
//                        }
//                    )
//                    HorizontalDivider()
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
//                            val agent =myStudioViewModel.toolAgent
//                            val makers = generalViewModel.toolMakers.filter {
//                                if(agent.id==0L) it.agentId == authViewModel.user.id else it.agentId==agent.id
//                            }
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
                                                dialog = MyStudioScreenDialog.ConfigMCP
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
//                }
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
            val toolMaker = myStudioViewModel.toolMaker
            when(toolMaker){
                is MCPServer -> ConfigMCPServerDialog(
                    toolMaker,
                    onDismissRequest = {dialog=MyStudioScreenDialog.None},
                    onConfirmRequest = { config ->
                        myStudioViewModel.configMCPServer(config)
                    }
                )
            }
        }
    }
}