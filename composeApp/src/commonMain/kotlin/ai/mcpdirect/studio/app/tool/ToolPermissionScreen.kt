package ai.mcpdirect.studio.app.tool

import ai.mcpdirect.studio.app.compose.StudioCard
import ai.mcpdirect.studio.app.generalViewModel
import ai.mcpdirect.studio.app.model.account.AIPortAccessKey
import ai.mcpdirect.studio.app.model.aitool.AIPortToolMaker
import ai.mcpdirect.studio.app.model.repository.TeamRepository
import ai.mcpdirect.studio.app.model.repository.ToolRepository
import ai.mcpdirect.studio.app.theme.purple.selectedListItemColors
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
import mcpdirectstudioapp.composeapp.generated.resources.check_box
import mcpdirectstudioapp.composeapp.generated.resources.info
import mcpdirectstudioapp.composeapp.generated.resources.keyboard_arrow_right
import mcpdirectstudioapp.composeapp.generated.resources.reset_settings
import mcpdirectstudioapp.composeapp.generated.resources.uncheck_box
import org.jetbrains.compose.resources.painterResource
import kotlin.collections.listOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolPermissionScreen(
    accessKey: AIPortAccessKey
) {
    var showPermissionChangedDialog by remember { mutableStateOf(false) }
    val viewModel by remember { mutableStateOf(ToolPermissionViewModel(accessKey)) }
    val toolMaker by viewModel.toolMaker.collectAsState()
    val tools by viewModel.tools.collectAsState()
    val virtualTools by viewModel.virtualTools.collectAsState()
    LaunchedEffect(viewModel){
        viewModel.refresh()
        viewModel.refreshTeamToolMakers()
        viewModel.refreshTeamToolMakerTemplates()
        generalViewModel.topBarActions =  {
            IconButton(onClick = {
                viewModel.resetAllPermissions()
            }) {
                Icon(painterResource(Res.drawable.reset_settings), contentDescription = "Reset To Default")
            }
            Button(onClick = {
                viewModel.savePermissions()
                generalViewModel.previousScreen()
            }){
                Text("Save")
            }
        }
    }

    DisposableEffect(null){
        onDispose {
            generalViewModel.topBarActions = {}
        }
    }
    Row{
        var currentTabIndex by remember { mutableStateOf(0) }
        Column(Modifier.width(300.dp)) {

            val tabs = listOf("My Studio", "My Team")
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
                0 -> AgentList(viewModel)
                1 -> TeamList(viewModel)
            }

        }

        StudioCard(Modifier.padding(8.dp).fillMaxSize()) {
            val toolMakersFromAgent by viewModel.toolMakersFromAgent.collectAsState()
            val toolMakersFromTeam by viewModel.toolMakersFromTeam.collectAsState()
//            var toolMakers:List<AIPortToolMaker> = listOf()
//            if(currentTabIndex==0) toolMakers = ToolRepository.toolMakers(viewModel.toolAgent.value)
//            else toolMakers = TeamRepository.toolMakers(viewModel.team.value)
            Row {
                LazyColumn(Modifier.width(250.dp)) {
                    items(if(currentTabIndex==0) toolMakersFromAgent else toolMakersFromTeam){
                        ListItem(
                            modifier = Modifier.clickable{
                                viewModel.selectToolMaker(it)
                            },
                            headlineContent = { Text(
                                it.name,
                                style = MaterialTheme.typography.titleSmall,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis) },
                            supportingContent = {
                                val count = viewModel.countToolPermissions(it)
                                if(count>0) {
                                    Text("${count}")
                                } },
                            trailingContent = {
                                if(toolMaker.id==it.id)
                                    Icon(painterResource(Res.drawable.keyboard_arrow_right),
                                        contentDescription = "Current Tool Maker")
                            },
                            colors = ListItemDefaults.colors(
                                containerColor = if(viewModel.toolMaker==it)
                                    MaterialTheme.colorScheme.surfaceContainer
                                else Color.Transparent
                            )
                        )
                    }
                }

                VerticalDivider()
                Column {
                    Row (verticalAlignment = Alignment.CenterVertically,){
                        if(viewModel.countToolPermissions()>0)
                            IconButton(onClick = {
                                viewModel.selectAllTools(false)
                            }) {
                                Icon(painterResource(Res.drawable.check_box), contentDescription = "deselect All")
                            }
                        else
                            IconButton(onClick = {
                                viewModel.selectAllTools(true)
                            }) {
                                Icon(painterResource(Res.drawable.uncheck_box), contentDescription = "Select All")
                            }

                        Text("${viewModel.countToolPermissions()} tool(s) selected")
                        Spacer(Modifier.weight(1.0f))
                        IconButton(onClick = {
                            viewModel.resetPermissions()
                        }) {
                            Icon(painterResource(Res.drawable.reset_settings), contentDescription = "Reset To Default")
                        }

                    }
                    HorizontalDivider()
                    toolMaker.let {
                        if(it.type==0) LazyColumn {
                            virtualTools.forEach {
                                item {
                                    Row (verticalAlignment = Alignment.CenterVertically,){
                                        Checkbox(
                                            checked = viewModel.toolSelected(it),
                                            onCheckedChange = {
                                                    checked ->
                                                viewModel.selectTool(checked,it)
                                            },
                                        )
                                        Text(it.name,
                                            style = MaterialTheme.typography.bodyMedium,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis)
                                        Spacer(Modifier.weight(1.0f))
                                        IconButton(onClick = {
//                                            toolDetailViewModel.toolId = it.toolId
//                                            toolDetailViewModel.toolName = it.name
//                                            generalViewModel.currentScreen(Screen.ToolDetails,
//                                                "Tool Description of ${it.name}",
//                                                Screen.ToolPermission(accessKey))
                                        }) {
                                            Icon(painterResource(Res.drawable.info), contentDescription = "Details")
                                        }
                                    }
                                }
                            }
                        } else LazyColumn {
                            tools.forEach {
                                item {
                                    Row (verticalAlignment = Alignment.CenterVertically,){
                                        Checkbox(
                                            checked = viewModel.toolSelected(it),
                                            onCheckedChange = {
                                                    checked ->
                                                viewModel.selectTool(checked,it)
                                            },
                                        )
                                        Text(it.name,
                                            style = MaterialTheme.typography.bodyMedium,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis)
                                        Spacer(Modifier.weight(1.0f))
                                        IconButton(onClick = {
//                                            toolDetailViewModel.toolId = it.id
//                                            toolDetailViewModel.toolName = it.name
//                                            generalViewModel.currentScreen(Screen.ToolDetails,
//                                                previousScreen = Screen.ToolPermission(accessKey))
                                        }) {
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

    if (showPermissionChangedDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionChangedDialog = false },
            title = { Text("Discard Changes?") },
            text = { Text("You have unsaved changes. Are you sure you want to discard them?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.savePermissions()
                        generalViewModel.previousScreen()
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        generalViewModel.previousScreen()
                    }
                ) {
                    Text("Discard")
                }
            }
        )
    }
}

@Composable
private fun AgentList(
    viewModel: ToolPermissionViewModel
){
    val toolAgent by viewModel.toolAgent.collectAsState()
    val toolAgents by viewModel.toolAgents.collectAsState()
    LazyColumn() {
        items(toolAgents){
            ListItem(
                modifier = Modifier.clickable{
                    viewModel.selectToolAgent(it)
                },
                headlineContent = { Text(
                    it.name,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis) },
                supportingContent = {
                    val count = viewModel.countToolPermissions(it)
                    if(count>0) {
                        Text("${count}")
                    } },
                trailingContent = {
                    if(toolAgent.id==it.id)
                        Icon(painterResource(Res.drawable.keyboard_arrow_right),
                            contentDescription = "Current Tool Agent")
                },
                colors = if(toolAgent.id==it.id) selectedListItemColors
                    else ListItemDefaults.colors()
            )
        }
    }
}

@Composable
private fun TeamList(
    viewModel: ToolPermissionViewModel
){
    viewModel.refreshTeams()
    val teams by viewModel.teams.collectAsState()
    LazyColumn(Modifier.fillMaxHeight()) {
        items(teams){
            ListItem(
                modifier = Modifier.clickable{
                    viewModel.selectTeam(it)
                },
                overlineContent = {
                    Text("${it.ownerName} (${it.ownerAccount})")
                },
                headlineContent = { Text(
                    it.name,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis) },
                supportingContent = {
                    val count = viewModel.countToolPermissions(it)
                    if(count>0) {
                        Text("${count}")
                    }
                                    },
//                trailingContent = {
//                    if(viewModel.team!=null&&viewModel.team!!.id==it.id)
//                        Icon(painterResource(Res.drawable.keyboard_arrow_right),
//                            contentDescription = "Current Tool Agent")
//                },
                colors = if(viewModel.team==it) selectedListItemColors
                else ListItemDefaults.colors()
            )
        }
    }
}