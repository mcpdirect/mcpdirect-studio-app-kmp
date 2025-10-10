package ai.mcpdirect.studio.app.key

import ai.mcpdirect.studio.app.accessKeyToolPermissionViewModel
import ai.mcpdirect.studio.app.compose.StudioCard
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import mcpdirectstudioapp.composeapp.generated.resources.keyboard_arrow_right
import mcpdirectstudioapp.composeapp.generated.resources.reset_settings
import mcpdirectstudioapp.composeapp.generated.resources.uncheck_box
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun AccessKeyToolPermissionScreen(
    onBack: () -> Unit
) {
    var showPermissionChangedDialog by remember { mutableStateOf(false) }
    val viewModel = accessKeyToolPermissionViewModel;
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
                    }){
                        Text("Save")
                    }
                }
            )
        }
    ) { paddingValues ->
        Row(modifier = Modifier.padding(paddingValues)) {
            LazyColumn(Modifier.width(200.dp)) {
                viewModel.toolAgents.forEach {
                    item {
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
                                if(viewModel.toolAgent==it)
                                    Icon(painterResource(Res.drawable.keyboard_arrow_right),
                                        contentDescription = "Current Tool Agent")
                            },
                            colors = ListItemDefaults.colors(
                                containerColor = if(viewModel.toolAgent==it)
                                    MaterialTheme.colorScheme.surfaceContainer
                                else Color.Transparent
                            )
                        )
                    }
                }
            }
            StudioCard(Modifier.padding(8.dp).fillMaxSize()) {
                Row {
                    LazyColumn(Modifier.width(200.dp)) {
                        viewModel.toolMakers.forEach {
                            item {
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
                                        if(viewModel.toolMaker==it)
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
                    }

                    VerticalDivider()
                    Column {
                        Row (verticalAlignment = Alignment.CenterVertically,){
//                            val allChecked = viewModel.toolsSelected()
//                            Checkbox(
//                                checked = viewModel.countToolPermissions()>0,
//                                onCheckedChange = {
//                                        checked ->
//                                    viewModel.selectAllTools(checked)
//                                },
//                            )
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

                            Text("${viewModel.countToolPermissions()} Selected")
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