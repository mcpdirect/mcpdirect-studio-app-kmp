package ai.mcpdirect.studio.app.team

import ai.mcpdirect.studio.app.compose.StudioCard
import ai.mcpdirect.studio.app.compose.Tag
import ai.mcpdirect.studio.app.generalViewModel
import ai.mcpdirect.studio.app.model.account.AIPortTeam
import ai.mcpdirect.studio.app.model.aitool.AIPortToolMakerTemplate
import ai.mcpdirect.studio.app.model.repository.ToolRepository
import ai.mcpdirect.studio.app.model.repository.UserRepository
import ai.mcpdirect.studio.app.theme.purple.selectedListItemColors
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mcpdirectstudioapp.composeapp.generated.resources.Res
import mcpdirectstudioapp.composeapp.generated.resources.arrow_back
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MCPTeamToolMakerTemplateScreen(
    team: AIPortTeam
) {
//    val viewModel = mcpTeamToolMakerTemplateViewModel
    val viewModel by remember { mutableStateOf(MCPTeamToolMakerTemplateViewModel()) }
    val toolMakerTemplates by viewModel.toolMakerTemplates.collectAsState()
//    val team = mcpTeamViewModel.mcpTeam!!
//    when(viewModel.uiState){
//        is UIState.Error -> {}
//        UIState.Idle -> {}
//        UIState.Loading -> {
//            Column(
//                Modifier.fillMaxSize(),
//                verticalArrangement = Arrangement.Center,
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                CircularProgressIndicator(
//                    modifier = Modifier.size(48.dp),
//                    color = MaterialTheme.colorScheme.primary
//                )
//            }
//        }
//        UIState.Success -> {
//
//        }
//    }
    LaunchedEffect(null){
        ToolRepository.loadToolMakerTemplates()
        viewModel.refreshTeamToolMakerTemplates(team)
        generalViewModel.topBarActions = {
            TextButton(
                onClick = {viewModel.saveTeamToolMakerTemplates(team){
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
                    items(toolMakerTemplates) {
                        if(UserRepository.me(it.userId))
                            ToolMakerTemplateItem(it,viewModel) {
                                viewModel.toolMakerTemplate(it)
                            }
                        HorizontalDivider()
                    }
                }
                viewModel.toolMakerTemplate?.let {
                    VerticalDivider()
                    Column(Modifier.weight(5.0f)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            IconButton(onClick = { viewModel.toolMakerTemplate(null)}) {
                                Icon(painterResource(Res.drawable.arrow_back), contentDescription = "Back")
                            }
                        }
                        HorizontalDivider()
                    }
                }
            }
        }
    }
//    LaunchedEffect(null){
////        viewModel.uiState = UIState.Loading
////        generalViewModel.refreshToolMakerTemplates()
//        ToolRepository.loadToolMakerTemplates()
//        viewModel.refreshTeamToolMakerTemplates(team)
//    }
}


@Composable
fun ToolMakerTemplateItem(
    template: AIPortToolMakerTemplate,
    viewModel: MCPTeamToolMakerTemplateViewModel,
    onClick: () -> Unit
) {
//    val viewModel = mcpTeamToolMakerTemplateViewModel
    ListItem(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        overlineContent = {
            if(UserRepository.me(template.userId)) Text("Me")
        },
        leadingContent = {
            Checkbox(
                checked = viewModel.toolMakerTemplateSelected(template),
                onCheckedChange = {
                    viewModel.selectToolMakerTemplate(it,template)
                }
            )
        },
        headlineContent = {
            Text(template.name)
        },
        supportingContent = {

//            if(isVirtualMCP) Text("Virtual MCP Server")
//            else if (maker.agentId == localToolAgentId)
//                Text("Local",color = MaterialTheme.colorScheme.primary)
//            else maker.agentName?.let { Text(it) }
        },
        trailingContent = {
//            if(!isVirtualMCP&&maker.agentStatus==0)
//                Tag("offline", color = MaterialTheme.colorScheme.error,)
//            else
                when(template.status){
                0-> Tag("inactive", toggleColor = MaterialTheme.colorScheme.error)
                1-> Tag("active",)
            }
        },
        colors = if(viewModel.toolMakerTemplate!=null&&viewModel.toolMakerTemplate!!.id==template.id)
            selectedListItemColors
        else ListItemDefaults.colors()
    )
}