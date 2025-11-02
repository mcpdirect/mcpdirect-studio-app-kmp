package ai.mcpdirect.studio.app.tool

import ai.mcpdirect.studio.app.auth.authViewModel
import ai.mcpdirect.studio.app.compose.StudioCard
import ai.mcpdirect.studio.app.compose.StudioIcon
import ai.mcpdirect.studio.app.compose.StudioListItem
import ai.mcpdirect.studio.app.compose.Tag
import ai.mcpdirect.studio.app.compose.TooltipText
import ai.mcpdirect.studio.app.generalViewModel
import ai.mcpdirect.studio.app.model.account.AIPortTeam
import ai.mcpdirect.studio.app.model.aitool.AIPortToolMaker
import ai.mcpdirect.studio.app.template.CreateMCPServerTemplateDialog
import ai.mcpdirect.studio.app.template.mcpTemplateViewModel
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import mcpdirectstudioapp.composeapp.generated.resources.Res
import mcpdirectstudioapp.composeapp.generated.resources.diversity_3
import mcpdirectstudioapp.composeapp.generated.resources.graph_5
import mcpdirectstudioapp.composeapp.generated.resources.keyboard_arrow_right
import mcpdirectstudioapp.composeapp.generated.resources.person
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
        generalViewModel.refreshToolMakers()
        generalViewModel.refreshTeams()
    }
    when(dialog){
        MCPToolsScreenDialog.NONE -> {}
        MCPToolsScreenDialog.MCPServerTemplate -> {
            mcpToolsViewModel.toolMaker?.let {
                toolMaker ->
                mcpToolsViewModel.mcpServerConfig?.let {
                    CreateMCPServerTemplateDialog(
                        toolMaker,
                        config = it,
                        onConfirmRequest = {
                            dialog = MCPToolsScreenDialog.NONE
                        },
                        onDismissRequest = {
                            dialog = MCPToolsScreenDialog.NONE
                        }
                    )
                }
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
                        onClick = { currentTabIndex = index },
                        text = { Text(title) }
                    )
                }
            }

            // Content based on selected tab
            when (currentTabIndex) {
                0 -> MCPServerList()
                1 -> {}
            }

        }


        StudioCard(Modifier.fillMaxSize().padding(8.dp).weight(2.0f)) {
            when (currentTabIndex) {
                0 -> MCPServerItem()
                1 -> {}
            }
        }
    }
}

@Composable
fun MCPServerList(){
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
                            else it.agentName ?:"",
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