package ai.mcpdirect.studio.app.team

import ai.mcpdirect.mcpdirectstudioapp.getPlatform
import ai.mcpdirect.studio.app.UIState
import ai.mcpdirect.studio.app.compose.StudioCard
import ai.mcpdirect.studio.app.compose.Tag
import ai.mcpdirect.studio.app.generalViewModel
import ai.mcpdirect.studio.app.model.account.AIPortTeam
import ai.mcpdirect.studio.app.model.aitool.AIPortTool
import ai.mcpdirect.studio.app.model.aitool.AIPortToolMaker
import ai.mcpdirect.studio.app.model.aitool.AIPortToolMaker.Companion.STATUS_OFF
import ai.mcpdirect.studio.app.model.aitool.AIPortToolMaker.Companion.STATUS_ON
import ai.mcpdirect.studio.app.model.repository.UserRepository
import ai.mcpdirect.studio.app.theme.purple.selectedListItemColors
import ai.mcpdirect.studio.app.tool.ToolDetailsView
//import ai.mcpdirect.studio.app.tool.toolDetailViewModel
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import mcpdirectstudioapp.composeapp.generated.resources.Res
import mcpdirectstudioapp.composeapp.generated.resources.arrow_back
import mcpdirectstudioapp.composeapp.generated.resources.info
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MCPTeamToolMakerScreen(
    team: AIPortTeam
) {
    val viewModel by remember { mutableStateOf(MCPTeamToolMakerViewModel()) }
    val toolMakers by viewModel.toolMakers.collectAsState()
    var tool by remember { mutableStateOf<AIPortTool?>(null) }
    LaunchedEffect(null){
        generalViewModel.topBarActions = {
            TextButton(
                onClick = {viewModel.saveTeamToolMakers(team){
                        code, message ->
                    if(code==0){
                        generalViewModel.previousScreen()
                    }
                } }
            ){
                Text("Share")
            }
        }
    }
    DisposableEffect(null){
        onDispose {
            generalViewModel.topBarActions = {}
        }
    }
    Column{
//                SearchView(
//                    query = viewModel.searchQuery,
//                    onQueryChange = { viewModel.updateSearchQuery(it) },
//                    placeholder = "Search MCP teams..."
//                )
        StudioCard(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxSize()
        ){
            Row(modifier = Modifier.fillMaxWidth()) {
                LazyColumn(modifier = Modifier.weight(3.0f)) {
                    items(toolMakers) {
                        if(UserRepository.me(it.userId))
                            ToolMakerItem(it,viewModel) {
                                viewModel.toolMaker(it)
                            }
                        HorizontalDivider()
                    }
                }
                viewModel.toolMaker?.let {
                    VerticalDivider()
                    Column(Modifier.weight(5.0f)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            IconButton(onClick = { viewModel.toolMaker(null)}) {
                                Icon(painterResource(Res.drawable.arrow_back), contentDescription = "Back")
                            }
                        }
                        HorizontalDivider()
                        tool?.let{
                            ToolDetailsView(
                                it.id,
                                Modifier.weight(5.0f)
                            ){
                                tool = null
                            }
                        }?:if(it.type==0) LazyColumn {
                            viewModel.virtualTools.forEach {
                                item {
                                    Row (verticalAlignment = Alignment.CenterVertically,){
                                        Text(it.name,
                                            style = MaterialTheme.typography.bodyMedium,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis)
                                        Spacer(Modifier.weight(1.0f))
                                        IconButton(onClick = {
                                            tool = it
//                                            toolDetailViewModel.toolId = it.toolId
//                                            toolDetailViewModel.toolName = it.name
//                                            generalViewModel.currentScreen(Screen.ToolDetails,
//                                                "Tool Details of ${it.name}",
//                                                Screen.MCPTeamToolMaker(team))
                                        }) {
                                            Icon(painterResource(Res.drawable.info), contentDescription = "Details")
                                        }
                                    }
                                }
                            }
                        } else LazyColumn {
                            viewModel.tools.forEach {
                                item {
                                    Row (verticalAlignment = Alignment.CenterVertically,){
                                        Text(it.name,
                                            style = MaterialTheme.typography.bodyMedium,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis)
                                        Spacer(Modifier.weight(1.0f))
                                        IconButton(onClick = { tool = it}){
//                                            toolDetailViewModel.toolId = it.id
//                                            toolDetailViewModel.toolName = it.name
//                                            generalViewModel.currentScreen(Screen.ToolDetails,
//                                                "Tool Details of ${it.name}",
//                                                Screen.MCPTeamToolMaker(team))                                                    }) {
                                            Icon(painterResource(Res.drawable.info), contentDescription = "Details")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    LaunchedEffect(null){
        viewModel.uiState = UIState.Loading
        viewModel.refreshTeamToolMakers(team)
    }
}


@Composable
fun ToolMakerItem(
    maker: AIPortToolMaker,
    viewModel: MCPTeamToolMakerViewModel,
    onClick: () -> Unit
) {
//    val viewModel = mcpTeamToolMakerViewModel
    val isVirtualMCP = maker.virtual()
    val localToolAgentId = getPlatform().toolAgentId

    ListItem(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        overlineContent = {
            if(UserRepository.me(maker.userId)) Text("Me")
        },
        leadingContent = {
            Checkbox(
                checked = viewModel.toolMakerSelected(maker),
                onCheckedChange = {
                    viewModel.selectToolMaker(it,maker)
                }
            )
        },
        headlineContent = {
            maker.name?.let { Text(it) }
        },
        supportingContent = {
            if(isVirtualMCP) Text("Virtual MCP Server")
            else if (maker.agentId == localToolAgentId)
                Text("This device",color = MaterialTheme.colorScheme.primary)
//            else maker.agentName?.let { Text(it) }
        },
        trailingContent = {
//            if(!isVirtualMCP&&maker.agentStatus==0)
//                Tag("offline", color = MaterialTheme.colorScheme.error,)
//            else
            when(maker.status){
                STATUS_OFF-> Tag("inactive", toggleColor = MaterialTheme.colorScheme.error)
                STATUS_ON-> Tag("active",)
            }
        },
        colors = if(viewModel.toolMaker!=null&&viewModel.toolMaker!!.id==maker.id)
            selectedListItemColors
        else ListItemDefaults.colors()
    )
}