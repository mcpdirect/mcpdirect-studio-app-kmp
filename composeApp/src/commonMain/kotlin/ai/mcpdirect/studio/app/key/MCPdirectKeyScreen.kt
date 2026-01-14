package ai.mcpdirect.studio.app.key

import ai.mcpdirect.studio.app.compose.StudioActionBar
import ai.mcpdirect.studio.app.compose.StudioSearchbar
import ai.mcpdirect.studio.app.key.component.MCPdirectKeysComponent
import ai.mcpdirect.studio.app.key.view.GrantToolPermissionView
import ai.mcpdirect.studio.app.key.view.GrantToolPermissionViewModel
import ai.mcpdirect.studio.app.mcp.component.MCPServersComponent
import ai.mcpdirect.studio.app.model.aitool.AIPortToolAccessKey
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mcpdirectstudioapp.composeapp.generated.resources.Res
import mcpdirectstudioapp.composeapp.generated.resources.collapse_all
import mcpdirectstudioapp.composeapp.generated.resources.expand_all
import mcpdirectstudioapp.composeapp.generated.resources.reset_settings
import org.jetbrains.compose.resources.painterResource

@Composable
fun MCPdirectKeyScreen(
    accessKey: AIPortToolAccessKey?,
    paddingValues: PaddingValues = PaddingValues(),
){
    val viewModel by remember {mutableStateOf(MCPdirectKeyScreenViewModel())}
    val grantViewModels = remember { mutableMapOf<Long, GrantToolPermissionViewModel>() }
    val toolMakerCandidates by viewModel.toolMarkerCandidates.collectAsState()
//    val toolMakers by viewModel.toolMarkers.collectAsState()
    val ids by viewModel.toolMarkerCandidateIds.collectAsState()
    Row(Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),) {
        OutlinedCard(Modifier.weight(1f).fillMaxHeight()) {
            MCPdirectKeysComponent(
                accessKey = accessKey,
                showKeyGeneration = accessKey==null,
            ){
                viewModel.accessKey(it)
            }
        }
        Column(Modifier.weight(2f)) {
            Card(Modifier.weight(1f)) {
                viewModel.accessKey?.let { key ->
                    StudioActionBar(
                        "Tool Permissions (${viewModel.toolPermissionCount+viewModel.virtualToolPermissionCount})"
                    ){
                        IconButton(onClick = {
                            grantViewModels.clear()
                            viewModel.resetAllPermissions()
                        }){
                            Icon(painterResource(Res.drawable.reset_settings),contentDescription = null)
                        }
                        IconButton(
                            onClick = {
                                viewModel.expanded = !viewModel.expanded
                                grantViewModels.values.forEach {
                                  it.expanded = viewModel.expanded
                                } },
                            modifier = Modifier.size(32.dp)
                        ) {
                            val icon = if(viewModel.expanded) Res.drawable.collapse_all else Res.drawable.expand_all
                            Icon(painterResource(icon),contentDescription = null, Modifier.size(20.dp))
                        }
                    }
                    StudioSearchbar(modifier = Modifier.padding(16.dp)) {
                        viewModel.toolMakerCandidateFilter.value = it
                    }
                    LazyColumn(Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(toolMakerCandidates){ toolMaker->
                            var grantViewModel = grantViewModels[toolMaker.id]
                            if(grantViewModel == null) {
                                grantViewModel = GrantToolPermissionViewModel()
                                grantViewModel.toolMaker(toolMaker)
                                if(toolMaker.type==0){
                                    grantViewModel.selectTools(viewModel.virtualToolPermissions)
//                                    grantViewModel.selectedToolCount = viewModel.virtualToolPermissions.values.count {
//                                        it.status > 0 && it.makerId == toolMaker.id
//                                    }
                                }else {
                                    grantViewModel.selectTools(viewModel.toolPermissions)
//                                    grantViewModel.selectedToolCount = viewModel.toolPermissions.values.count {
//                                        it.status > 0 && it.makerId == toolMaker.id
//                                    }
                                }
                                grantViewModels[toolMaker.id] = grantViewModel
                            }
                            GrantToolPermissionView(
                                grantViewModel,
                                {
                                    viewModel.resetPermissions(toolMaker)
                                    if(toolMaker.type==0) grantViewModel.selectTools(viewModel.virtualToolPermissions)
                                    else grantViewModel.selectTools(viewModel.toolPermissions)
                                }
                            ){ permitted, tools ->
                                viewModel.permit(permitted, tools)
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            Button(onClick = { viewModel.savePermissions() }, Modifier.fillMaxWidth()){
                Text("Grant")
            }
        }
        OutlinedCard(Modifier.weight(1f).fillMaxHeight()) {
            MCPServersComponent(selectedMCPServers = ids, modifier = Modifier.fillMaxHeight())
            { selected, toolMaker ->
                if(selected) viewModel.nominate(toolMaker)
                else {
                    val v = grantViewModels.remove(toolMaker.id)
                    if(v!=null){
                        viewModel.permit(false,v.tools.value)
                    }
                    viewModel.cancelNomination(toolMaker)
                }
            }
        }
//        OutlinedCard(Modifier.weight(1f).fillMaxHeight()) {
//            StudioActionBar(
//                title = "MCP Servers",
//            )
//            HorizontalDivider()
//            StudioSearchbar(modifier = Modifier.padding(16.dp)) {
//                viewModel.toolMakerFilter.value = it
//            }
//            if(toolMakers.isEmpty()) StudioBoard(Modifier.weight(1f)) {
//                Icon(
//                    painterResource(Res.drawable.search_off),
//                    contentDescription = null,
//                    modifier = Modifier.size(80.dp).padding(bottom = 16.dp)
//                )
//                Text("No MCP server found.")
//            } else {
//                val ids by viewModel.toolMarkerCandidateIds.collectAsState()
//                LazyColumn(Modifier.padding(start=16.dp,end=16.dp)) {
//                    items(toolMakers){ toolMaker ->
//                        var checked by remember{ mutableStateOf(false) }
//                        LaunchedEffect(ids){
//                            checked = toolMaker.id in ids
//                        }
//                        Row(
//                            Modifier.fillMaxWidth(),
//                            verticalAlignment = Alignment.CenterVertically,
//                            horizontalArrangement = Arrangement.spacedBy(8.dp)
//                        ) {
//                            Checkbox(
//                                checked = checked,
//                                onCheckedChange = {
//                                    checked = it
//                                    if(checked) viewModel.nominate(toolMaker)
//                                    else {
//                                        val v = grantViewModels.remove(toolMaker.id)
//                                        if(v!=null){
//                                            viewModel.permit(false,v.tools.value)
//                                        }
//                                        viewModel.cancelNomination(toolMaker)
//                                    }
//                                },
//                            )
//                            Text(toolMaker.name)
//                        }
//                    }
//                }
//            }
//        }
    }
}