package ai.mcpdirect.studio.app.key

import ai.mcpdirect.studio.app.compose.StudioActionBar
import ai.mcpdirect.studio.app.compose.StudioBoard
import ai.mcpdirect.studio.app.compose.StudioListItem
import ai.mcpdirect.studio.app.key.component.MCPdirectKeysComponent
import ai.mcpdirect.studio.app.key.view.ToolMakerPermissionView
import ai.mcpdirect.studio.app.model.aitool.AIPortToolAccessKey
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import mcpdirectstudioapp.composeapp.generated.resources.Res
import mcpdirectstudioapp.composeapp.generated.resources.close
import mcpdirectstudioapp.composeapp.generated.resources.move_left
import mcpdirectstudioapp.composeapp.generated.resources.reset_settings
import mcpdirectstudioapp.composeapp.generated.resources.search
import mcpdirectstudioapp.composeapp.generated.resources.search_off
import mcpdirectstudioapp.composeapp.generated.resources.shield_toggle
import org.jetbrains.compose.resources.painterResource

@Composable
fun MCPdirectKeyScreen(
    accessKey: AIPortToolAccessKey?,
    paddingValues: PaddingValues = PaddingValues(),
){
    val viewModel by remember {mutableStateOf(MCPdirectKeyScreenViewModel())}
    val toolMakers by viewModel.toolMarkerCandidates.collectAsState()
    val toolMakerCandidates by viewModel.toolMarkers.collectAsState()
//    val accessKeysViewModel by remember {mutableStateOf(MCPdirectKeysComponentViewModel())}
//    val toolPermissionsViewModel by remember {mutableStateOf(MCPdirectKeyToolPermissionViewModel())}
//    val mcpServersViewModel by remember { mutableStateOf(MCPServersComponentViewModel()) }
//    var currentAccessKey by remember { mutableStateOf<AIPortToolAccessKey?>(null) }
//    LaunchedEffect(currentAccessKey){
//        toolPermissionsViewModel.accessKey(currentAccessKey)
//    }

    Row(Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),) {
        OutlinedCard(Modifier.weight(1f).fillMaxHeight()) {
            MCPdirectKeysComponent(
                accessKey = accessKey,
                showKeyGeneration = accessKey==null,
//                accessKeysViewModel
            ){
//                currentAccessKey = it
                viewModel.accessKey(it)
            }
        }
        Card(Modifier.weight(2f).fillMaxHeight()) {
            viewModel.accessKey?.let { key ->
                StudioActionBar(
                    "Tool Permissions (${viewModel.toolPermissionCount})"
                ){
                    IconButton(onClick = {}){
                        Icon(painterResource(Res.drawable.reset_settings),contentDescription = null)
                    }
                }
                Box(modifier = Modifier.padding(16.dp)){
                    Row(
                        Modifier.background(
                            MaterialTheme.colorScheme.surfaceContainerHigh,
                            ButtonDefaults.shape
                        ).clip(ButtonDefaults.shape),
                        verticalAlignment = Alignment.CenterVertically,
                    ){
                        val value by viewModel.toolMakerCandidateFilter.collectAsState()
                        Icon(painterResource(Res.drawable.search), contentDescription = null,
                            Modifier.padding(12.dp))
                        BasicTextField(
                            modifier=Modifier.weight(1f).padding(end = 4.dp),
                            value = value,
                            onValueChange = {
//                                value = it
                                viewModel.toolMakerCandidateFilter.value = it
                            },
                        )
                        if(value.isNotEmpty()) IconButton(onClick = {
//                            value=""
                            viewModel.toolMakerCandidateFilter.value = ""
                        }){
                            Icon(painterResource(Res.drawable.close), contentDescription = null)
                        }
                    }
                }
                LazyColumn(Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(toolMakers){ toolMaker->
                        ToolMakerPermissionView(
                            toolMaker,viewModel.toolPermissions
                        ){ permitted, tools ->
                            viewModel.permit(permitted, tools)
                        }
                    }
                }
//                MCPdirectKeyToolPermissionView(key, Modifier.padding(horizontal = 16.dp))
            }
        }
        OutlinedCard(Modifier.weight(1f).fillMaxHeight()) {
            StudioActionBar(
                title = "MCP Servers",
            )
            HorizontalDivider()
            Box(modifier = Modifier.padding(16.dp)){
                Row(
                    Modifier.background(
                        MaterialTheme.colorScheme.surfaceContainerHigh,
                        ButtonDefaults.shape
                    ).clip(ButtonDefaults.shape),
                    verticalAlignment = Alignment.CenterVertically,
                ){
                    val value by viewModel.toolMakerFilter.collectAsState()
                    Icon(painterResource(Res.drawable.search), contentDescription = null,
                        Modifier.padding(12.dp))
                    BasicTextField(
                        modifier=Modifier.weight(1f).padding(end = 4.dp),
                        value = value,
                        onValueChange = {
//                            value = it
                            viewModel.toolMakerFilter.value = it
                        },
                    )
                    if(value.isNotEmpty()) IconButton(onClick = {
//                        value=""
                        viewModel.toolMakerFilter.value = ""
                    }){
                        Icon(painterResource(Res.drawable.close), contentDescription = null)
                    }
                }
            }
            if(toolMakerCandidates.isEmpty()) StudioBoard(Modifier.weight(1f)) {
                Icon(
                    painterResource(Res.drawable.search_off),
                    contentDescription = null,
                    modifier = Modifier.size(80.dp).padding(bottom = 16.dp)
                )
                Text("No MCP server found.")
            } else {
                LazyColumn(Modifier.padding(start=16.dp,end=16.dp)) {
                    items(toolMakerCandidates){ toolMaker ->
                        TextButton(onClick = {viewModel.nominate(toolMaker)},
                            contentPadding = PaddingValues(8.dp),
                            ){
//                            IconButton(onClick = {viewModel.nominate(toolMaker)}){
                                Icon(painterResource(Res.drawable.move_left),
                                    contentDescription = null, Modifier.size(24.dp))
//                            }
                            Spacer(Modifier.size(8.dp))
                            Text(toolMaker.name)
                        }
//                        StudioListItem(
////                        selected = viewModel.selectedToolMaker(toolMaker),
////                        modifier = Modifier.clickable {
////                            viewModel.selectToolMaker(toolMaker,multiSelectable)
////                        },
//                            headlineContent = { Text(toolMaker.name, style = MaterialTheme.typography.bodyMedium) },
//                            leadingContent = {
////                            if(multiSelectable)Checkbox(
////                                checked = viewModel.selectedToolMaker(toolMaker),
////                                onCheckedChange = { checked->
////                                    if(checked) viewModel.selectToolMaker(toolMaker,multiSelectable)
////                                    else viewModel.unselectToolMaker(toolMaker)
////                                },
////                            )
//                                IconButton(onClick = {viewModel.nominate(toolMaker)}){
//                                    Icon(painterResource(Res.drawable.move_left),
//                                        contentDescription = null, Modifier.size(24.dp))
//                                }
//                            }
//                        )
                    }
                }
            }
//            MCPServerCandidateView()
        }
    }
}