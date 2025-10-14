package ai.mcpdirect.studio.app.key

import ai.mcpdirect.backend.dao.entity.aitool.AIPortToolMaker
import ai.mcpdirect.studio.app.Screen
import ai.mcpdirect.studio.app.accessKeyToolPermissionViewModel
import ai.mcpdirect.studio.app.authViewModel
import ai.mcpdirect.studio.app.compose.StudioCard
import ai.mcpdirect.studio.app.generalViewModel
import ai.mcpdirect.studio.app.theme.purple.selectedListItemColors
import ai.mcpdirect.studio.app.toolDetailViewModel
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.IconButton
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import mcpdirectstudioapp.composeapp.generated.resources.Res
import mcpdirectstudioapp.composeapp.generated.resources.arrow_back
import mcpdirectstudioapp.composeapp.generated.resources.check_box
import mcpdirectstudioapp.composeapp.generated.resources.info
import mcpdirectstudioapp.composeapp.generated.resources.keyboard_arrow_right
import mcpdirectstudioapp.composeapp.generated.resources.reset_settings
import mcpdirectstudioapp.composeapp.generated.resources.uncheck_box
import org.jetbrains.compose.resources.painterResource
import kotlin.collections.listOf

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun AccessKeyToolPermissionScreen(
    onBack: () -> Unit
) {
    var showPermissionChangedDialog by remember { mutableStateOf(false) }
    val viewModel = accessKeyToolPermissionViewModel
    LaunchedEffect(viewModel) {
        viewModel.refresh()
    }
    Scaffold(
        snackbarHost = { SnackbarHost(viewModel.snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Tool Permissions for Key #${viewModel.accessKey!!.name}") },
                navigationIcon = {
                    IconButton(onClick = {
                        if (viewModel.permissionsChanged()) {
                            showPermissionChangedDialog = true
                        } else {
                            onBack()
                        }
                    }) {
                        Icon(painterResource(Res.drawable.arrow_back), contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        viewModel.resetAllPermissions()
                    }) {
                        Icon(painterResource(Res.drawable.reset_settings), contentDescription = "Reset To Default")
                    }
                    Button(onClick = {
                        viewModel.savePermissions()
                        onBack()
                    }){
                        Text("Save")
                    }
                }
            )
        }
    ) { paddingValues ->
        Row(modifier = Modifier.padding(paddingValues)) {
            var currentTabIndex by remember { mutableStateOf(0) }
            Column(Modifier.width(200.dp)) {

                val tabs = listOf("My Studio", "My MCP Team")
                TabRow(selectedTabIndex = currentTabIndex) {
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
                    0 -> AgentList()
                    1 -> TeamList()
                }

            }

            StudioCard(Modifier.padding(8.dp).fillMaxSize()) {
                var toolMakers:List<AIPortToolMaker> = listOf()
                if(currentTabIndex==0) viewModel.toolAgent?.let {
                    toolMakers = generalViewModel.toolMakers(it)
                }else viewModel.team?.let {
                    toolMakers = generalViewModel.toolMakers(it)
                }
                Row {
                    LazyColumn(Modifier.width(200.dp)) {
                        items(toolMakers){
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
                        viewModel.toolMaker?.let {
                            if(it.type==0) LazyColumn {
                                viewModel.virtualTools.forEach {
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
                                                toolDetailViewModel.toolId = it.toolId
                                                toolDetailViewModel.toolName = it.name
                                                generalViewModel.currentScreen = Screen.ToolDetails
                                                generalViewModel.backToScreen = Screen.ToolPermission
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
                                                toolDetailViewModel.toolId = it.id
                                                toolDetailViewModel.toolName = it.name
                                                generalViewModel.currentScreen = Screen.ToolDetails
                                                generalViewModel.backToScreen = Screen.ToolPermission
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
                        onBack()
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        onBack()
                    }
                ) {
                    Text("Discard")
                }
            }
        )
    }
}

@Composable
fun AgentList(){
    val viewModel = accessKeyToolPermissionViewModel
    LazyColumn() {
        items(viewModel.toolAgents){
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
                    if(viewModel.toolAgent!=null&&viewModel.toolAgent!!.id==it.id)
                        Icon(painterResource(Res.drawable.keyboard_arrow_right),
                            contentDescription = "Current Tool Agent")
                },
                colors = if(viewModel.toolAgent==it) selectedListItemColors
                    else ListItemDefaults.colors()
            )
        }
    }
}

@Composable
fun TeamList(){
    generalViewModel.refreshTeams()
    val viewModel = accessKeyToolPermissionViewModel
    LazyColumn(Modifier.fillMaxHeight()) {
        items(generalViewModel.teams){
            if(it.ownerId != authViewModel.userInfo.value!!.id)ListItem(
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
//                    val count = viewModel.countToolPermissions(it)
//                    if(count>0) {
//                        Text("${count}")
//                    }
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