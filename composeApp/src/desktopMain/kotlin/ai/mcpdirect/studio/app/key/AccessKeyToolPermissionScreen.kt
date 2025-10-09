package ai.mcpdirect.studio.app.key

import ai.mcpdirect.backend.dao.entity.aitool.AIPortVirtualToolPermission
import ai.mcpdirect.studio.app.accessKeyToolPermissionViewModel
import ai.mcpdirect.studio.app.compose.StudioCard
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import mcpdirectstudioapp.composeapp.generated.resources.Res
import mcpdirectstudioapp.composeapp.generated.resources.arrow_back
import mcpdirectstudioapp.composeapp.generated.resources.keyboard_arrow_right
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun AccessKeyToolPermissionScreen(
    onBack: () -> Unit
) {
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
                            onBack()
                    }) {
                        Icon(painterResource(Res.drawable.arrow_back), contentDescription = "Back")
                    }
                },
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
                            headlineContent = { Text(it.name) },
                            trailingContent = {
                                if(viewModel.toolAgent==it)
                                    Icon(painterResource(Res.drawable.keyboard_arrow_right),
                                        contentDescription = "Current Tool Agent")
                            }
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
                                    headlineContent = { Text(it.name) },
                                    trailingContent = {
                                        if(viewModel.toolMaker==it)
                                            Icon(painterResource(Res.drawable.keyboard_arrow_right),
                                                contentDescription = "Current Tool Maker")
                                    }
                                )
                            }
                        }
                    }

                    VerticalDivider()
                    Column {
                        Row (verticalAlignment = Alignment.CenterVertically,){
                            Checkbox(
                                checked = false,
                                onCheckedChange = {

                                },
                            )
                            Text(
                                text = "Select All Tools",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                        HorizontalDivider()
                        viewModel.toolMaker?.let {
                            if(it.type==0) LazyColumn {
                                viewModel.tools.forEach {
                                    item {
                                        Row (verticalAlignment = Alignment.CenterVertically,){
                                            Checkbox(
                                                checked = viewModel.virtualToolPermissions.containsKey(it.id),
                                                onCheckedChange = {

                                                },
                                            )
                                            Text(it.name)
                                        }
                                    }
                                }
                            } else LazyColumn {
                                viewModel.tools.forEach {
                                    item {
                                        Row (verticalAlignment = Alignment.CenterVertically,){
                                            Checkbox(
                                                checked = viewModel.toolPermissions.containsKey(it.id),
                                                onCheckedChange = {

                                                },
                                            )
                                            Text(it.name)
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