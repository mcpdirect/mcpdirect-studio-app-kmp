package ai.mcpdirect.studio.app.tool

import ai.mcpdirect.studio.app.auth.authViewModel
import ai.mcpdirect.studio.app.compose.StudioCard
import ai.mcpdirect.studio.app.compose.StudioIcon
import ai.mcpdirect.studio.app.compose.StudioListItem
import ai.mcpdirect.studio.app.compose.TooltipIconButton
import ai.mcpdirect.studio.app.compose.TooltipText
import ai.mcpdirect.studio.app.generalViewModel
import ai.mcpdirect.studio.app.model.account.AIPortTeam
import ai.mcpdirect.studio.app.model.account.AIPortUser
import ai.mcpdirect.studio.app.model.aitool.AIPortToolAgent
import ai.mcpdirect.studio.app.model.aitool.AIPortToolMaker
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import mcpdirectstudioapp.composeapp.generated.resources.Res
import mcpdirectstudioapp.composeapp.generated.resources.diversity_3
import mcpdirectstudioapp.composeapp.generated.resources.graph_5
import mcpdirectstudioapp.composeapp.generated.resources.keyboard_arrow_right
import mcpdirectstudioapp.composeapp.generated.resources.person
import mcpdirectstudioapp.composeapp.generated.resources.share
import org.jetbrains.compose.resources.painterResource

private enum class MCPToolsScreenDialog {
    NONE,
    MCPServerTemplate
}
private var dialog by mutableStateOf(MCPToolsScreenDialog.NONE)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MCPToolsScreen() {
    LaunchedEffect(null){
        generalViewModel.refreshToolAgents()
        generalViewModel.refreshTeams()
        generalViewModel.topBarActions = {}
    }
    when(dialog){
        MCPToolsScreenDialog.NONE -> {}
        MCPToolsScreenDialog.MCPServerTemplate -> {
            mcpToolsViewModel.toolMaker?.let {
                toolMaker ->
                CreateMCPTemplateDialog(
                    toolMaker,
                    onConfirmRequest = { name,type,agentId,config,inputs ->
                        dialog = MCPToolsScreenDialog.NONE
                        mcpTemplateListViewModel.createToolMakerTemplate(name,type,agentId,config,inputs)
                    },
                    onDismissRequest = {
                        dialog = MCPToolsScreenDialog.NONE
                    }
                )
//                mcpToolsViewModel.mcpServerConfig?.let {
//                    CreateMCPTemplateDialog(
//                        toolMaker,
//                        config = it,
//                        onConfirmRequest = { name,type,agentId,config,inputs ->
//                            dialog = MCPToolsScreenDialog.NONE
//                            mcpTemplateListViewModel.createToolMakerTemplate(name,type,agentId,config,inputs)
//                        },
//                        onDismissRequest = {
//                            dialog = MCPToolsScreenDialog.NONE
//                        }
//                    )
//                }
            }
        }
    }
    Row(Modifier.fillMaxSize()){
        var currentTabIndex by remember { mutableStateOf(0) }
        Column(Modifier.width(300.dp)) {

            val tabs = listOf("MCP Server", "MCP Template")
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
                0 -> MCPServerList()
                1 -> MCPTemplateList()
            }

        }


        StudioCard(Modifier.fillMaxSize().padding(8.dp).weight(2.0f)) {
            when (currentTabIndex) {
                0 -> MCPServerItem()
                1 -> MCPTemplateItem()
            }
        }
    }
}

@Composable
fun MCPServerList(){
    LaunchedEffect(null){
        generalViewModel.refreshToolMakers()
    }
    val viewModel = mcpToolsViewModel
    LazyColumn {
        items(generalViewModel.toolMakers){
            val me = it.userId== authViewModel.user.id
            val team: AIPortTeam? = generalViewModel.team(it.teamId)
            StudioListItem(
                selected = viewModel.toolMaker?.id == it.id,
                modifier = Modifier.clickable{
                    viewModel.toolMaker(it)
                },
                overlineContent = {
                    Row {
                        StudioIcon(
                            Res.drawable.person,
                            "Owner",
                            modifier = Modifier.size(18.dp).padding(end = 4.dp)
                        )
                        if(me) Text("Me")
                        else if(team!=null) TooltipText(
                            team.ownerName,
                            contentDescription = team.ownerAccount,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                },
                headlineContent = { Text(
                    it.name?:"",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis) },
                supportingContent = {
                    if(me) Row{
                        StudioIcon(
                            Res.drawable.graph_5,
                            "From My Studio",
                            MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp).padding(end = 4.dp)
                        )
                        Text(
                            if(it.type== AIPortToolMaker.TYPE_VIRTUAL)
                                "Virtual MCP"
                            else it.agentName,
                            color = MaterialTheme.colorScheme.primary,
                            style=MaterialTheme.typography.bodySmall
                        )

                    } else if(team!=null) Row{
                        StudioIcon(
                            Res.drawable.diversity_3,
                            "From Team",
                            MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(20.dp).padding(end = 4.dp)
                        )
                        Text(
                            team.name,
                            color = MaterialTheme.colorScheme.secondary,
                            style=MaterialTheme.typography.bodySmall)
                    }

                },
                trailingContent = {
                    if(viewModel.toolMaker!=null&&viewModel.toolMaker!!.id==it.id)
                        Icon(painterResource(Res.drawable.keyboard_arrow_right),
                            contentDescription = "Current Tool Maker")
                },
                colors = ListItemDefaults.colors(
                    containerColor = if(viewModel.toolMaker==it)
                        MaterialTheme.colorScheme.surfaceContainer
                    else Color.Transparent
                )
            )
            HorizontalDivider()
        }
    }
}

@Composable
fun MCPServerItem(){
    mcpToolsViewModel.toolMaker?.let {
        if(it.teamId==0L&&it.type != AIPortToolMaker.TYPE_VIRTUAL) {
            generalViewModel.topBarActions = {
                TextButton(onClick = {
                    dialog = MCPToolsScreenDialog.MCPServerTemplate
                    mcpToolsViewModel.getMCPServerConfig(it.id)
                }) {
                    Text("Create MCP Server Template")
                }
            }
        } else generalViewModel.topBarActions = {}

        Column {
            Row {

            }
        }
    }
}

@Composable
fun MCPTemplateList(){
    val viewModel = mcpTemplateListViewModel
    LaunchedEffect(viewModel){
        viewModel.queryToolMakerTemplates()
    }
    LazyColumn {
        items(viewModel.toolMakerTemplates){ template ->
            val me = template.userId== authViewModel.user.id
            var toolAgent by remember { mutableStateOf<AIPortToolAgent?>(null) }
            var user by remember { mutableStateOf<AIPortUser?>(null)}
            LaunchedEffect(null){
                generalViewModel.toolAgent(template.agentId){
                    code, message, data ->
                    toolAgent = data
                }
                generalViewModel.user(template.userId){
                    code, message, data ->
                    user = data;
                }
            }
            toolAgent?.let { agent ->
                StudioListItem(
                    selected = viewModel.toolMakerTemplate?.id == template.id,
                    modifier = Modifier.clickable{
                        viewModel.toolMakerTemplate(template)
                    },
                    overlineContent = {
                        Row {
                            StudioIcon(
                                Res.drawable.person,
                                "Owner",
                                modifier = Modifier.size(18.dp).padding(end = 4.dp)
                            )
                            if(me) Text("Me")
                            else user?.let {
                                TooltipText(
                                    it.name,
                                    contentDescription = it.account,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    },
                    headlineContent = { Text(
                        template.name,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis) },
                    supportingContent = {
                        Row{
                            StudioIcon(
                                Res.drawable.graph_5,
                                "From My Studio",
                                MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp).padding(end = 4.dp)
                            )
                            Text(
                                agent.name,
                                color = MaterialTheme.colorScheme.primary,
                                style=MaterialTheme.typography.bodySmall
                            )

                        }

                    },
                    trailingContent = {
                        if(viewModel.toolMakerTemplate!=null&&viewModel.toolMakerTemplate!!.id==template.id)
                            Icon(painterResource(Res.drawable.keyboard_arrow_right),
                                contentDescription = "")
                    },
                    colors = ListItemDefaults.colors(
                        containerColor = if(viewModel.toolMakerTemplate==template)
                            MaterialTheme.colorScheme.surfaceContainer
                        else Color.Transparent
                    )
                )
                HorizontalDivider()
            }
        }
    }
}

@Composable
fun MCPTemplateItem(){
    mcpTemplateListViewModel.toolMakerTemplate?.let {
        Column {
            Row(Modifier.padding(start = 8.dp, end = 8.dp),
                verticalAlignment = Alignment.CenterVertically){
                Text(it.name)
                Spacer(Modifier.weight(1.0f))
                TooltipIconButton(
                    Res.drawable.share,
                    "Share MCP Template to Team",
                    onClick = {}
                )
            }
            HorizontalDivider()
        }
    }
}