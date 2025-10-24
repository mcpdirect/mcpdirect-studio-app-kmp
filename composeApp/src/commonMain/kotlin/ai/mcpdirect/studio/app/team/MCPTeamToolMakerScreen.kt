package ai.mcpdirect.studio.app.team

import ai.mcpdirect.mcpdirectstudioapp.getPlatform
import ai.mcpdirect.studio.app.Screen
import ai.mcpdirect.studio.app.UIState
import ai.mcpdirect.studio.app.auth.authViewModel
import ai.mcpdirect.studio.app.compose.SearchView
import ai.mcpdirect.studio.app.compose.StudioCard
import ai.mcpdirect.studio.app.compose.Tag
import ai.mcpdirect.studio.app.generalViewModel
import ai.mcpdirect.studio.app.model.aitool.AIPortToolMaker
import ai.mcpdirect.studio.app.theme.purple.selectedListItemColors
import ai.mcpdirect.studio.app.tool.toolDetailViewModel
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import mcpdirectstudioapp.composeapp.generated.resources.Res
import mcpdirectstudioapp.composeapp.generated.resources.arrow_back
import mcpdirectstudioapp.composeapp.generated.resources.info
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MCPToolMakerTeamScreen() {
    val viewModel = mcpTeamToolMakerViewModel
    val team = mcpTeamViewModel.mcpTeam!!
    Scaffold(
        snackbarHost = { SnackbarHost(generalViewModel.snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("${stringResource(Screen.MCPTeamToolMaker.title)} for team ${team.name}") },
                navigationIcon = {
                    IconButton(onClick = {
                        generalViewModel.previousScreen()
                    }) {
                        Icon(painterResource(Res.drawable.arrow_back), contentDescription = "Back")
                    }
                },
                actions = {
                    Button(
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
            )
        }
    ) { padding ->
        when(viewModel.uiState){
            is UIState.Error -> {}
            UIState.Idle -> {}
            UIState.Loading -> {
                Column(
                    Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(48.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            UIState.Success -> {
                Column(modifier = Modifier.padding(padding)) {
                    SearchView(
                        query = viewModel.searchQuery,
                        onQueryChange = { viewModel.updateSearchQuery(it) },
                        placeholder = "Search MCP teams..."
                    )
                    StudioCard(
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxSize()
                    ){
                        Row(modifier = Modifier.fillMaxWidth()) {
                            LazyColumn(modifier = Modifier.weight(3.0f)) {
                                items(generalViewModel.toolMakers) {
                                    if(it.userId==authViewModel.user.id)
                                        ToolMakerItem(it) {
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
                                    if(it.type==0) LazyColumn {
                                        viewModel.virtualTools.forEach {
                                            item {
                                                Row (verticalAlignment = Alignment.CenterVertically,){
                                                    Text(it.name,
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis)
                                                    Spacer(Modifier.weight(1.0f))
                                                    IconButton(onClick = {
                                                        toolDetailViewModel.toolId = it.toolId
                                                        toolDetailViewModel.toolName = it.name
                                                        generalViewModel.currentScreen(Screen.ToolDetails,
                                                            "Tool Details of ${it.name}",
                                                        Screen.MCPTeamToolMaker)
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
                                                    IconButton(onClick = {
                                                        toolDetailViewModel.toolId = it.id
                                                        toolDetailViewModel.toolName = it.name
                                                        generalViewModel.currentScreen(Screen.ToolDetails,
                                                            "Tool Details of ${it.name}",
                                                            Screen.MCPTeamToolMaker)                                                    }) {
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
            }
        }
    }
    LaunchedEffect(null){
        viewModel.uiState = UIState.Loading
        viewModel.refreshTeamToolMakers(team){
            code, message ->
            if(code==0) viewModel.uiState = UIState.Success
        }
    }
}


@Composable
private fun ToolMakerItem(
    maker: AIPortToolMaker,
    onClick: () -> Unit
) {
    val viewModel = mcpTeamToolMakerViewModel
    val isVirtualMCP = maker.type== AIPortToolMaker.TYPE_VIRTUAL
    val localToolAgentId = getPlatform().toolAgentId

    ListItem(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        overlineContent = {
            if(maker.userId == authViewModel.user.id) Text("Me")
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
            else maker.agentName?.let { Text(it) }
        },
        trailingContent = {
            if(!isVirtualMCP&&maker.agentStatus==0)
                Tag("offline", color = MaterialTheme.colorScheme.error,)
            else when(maker.status){
                0-> Tag("inactive", color = MaterialTheme.colorScheme.error)
                1-> Tag("active",)
            }
        },
        colors = if(viewModel.toolMaker!=null&&viewModel.toolMaker!!.id==maker.id)
            selectedListItemColors
        else ListItemDefaults.colors()
    )
}