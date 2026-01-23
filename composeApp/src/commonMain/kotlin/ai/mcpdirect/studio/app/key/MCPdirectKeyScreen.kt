package ai.mcpdirect.studio.app.key

import ai.mcpdirect.studio.app.compose.StudioActionBar
import ai.mcpdirect.studio.app.compose.StudioSearchbar
import ai.mcpdirect.studio.app.key.component.AIAgentGuideComponent
import ai.mcpdirect.studio.app.key.component.AIAgentListComponent
import ai.mcpdirect.studio.app.key.component.MCPdirectKeysComponent
import ai.mcpdirect.studio.app.key.view.GrantToolPermissionView
import ai.mcpdirect.studio.app.key.view.GrantToolPermissionViewModel
import ai.mcpdirect.studio.app.mcp.component.MCPServersComponent
import ai.mcpdirect.studio.app.model.aitool.AIPortToolAccessKey
import ai.mcpdirect.studio.app.tips.AIAgent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mcpdirectstudioapp.composeapp.generated.resources.Res
import mcpdirectstudioapp.composeapp.generated.resources.arrow_back
import mcpdirectstudioapp.composeapp.generated.resources.arrow_forward
import mcpdirectstudioapp.composeapp.generated.resources.collapse_all
import mcpdirectstudioapp.composeapp.generated.resources.expand_all
import mcpdirectstudioapp.composeapp.generated.resources.keyboard_arrow_left
import mcpdirectstudioapp.composeapp.generated.resources.keyboard_arrow_right
import mcpdirectstudioapp.composeapp.generated.resources.reset_settings
import org.jetbrains.compose.resources.painterResource

@Composable
fun MCPdirectKeyScreen(
    accessKey: AIPortToolAccessKey?,
    integrationGuide: Boolean=false,
    paddingValues: PaddingValues = PaddingValues(),
){
    val viewModel by remember {mutableStateOf(MCPdirectKeyScreenViewModel())}
    val grantViewModels = remember { mutableMapOf<Long, GrantToolPermissionViewModel>() }
    val toolMakerCandidates by viewModel.toolMarkerCandidates.collectAsState()
    val ids by viewModel.toolMarkerCandidateIds.collectAsState()
    var showAIAgent by remember { mutableStateOf(integrationGuide) }
    var aiAgent by remember { mutableStateOf<AIAgent?>(null) }
    Row(Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),) {
        OutlinedCard(Modifier.weight(1f).fillMaxHeight()) {
            MCPdirectKeysComponent(
                accessKey = accessKey,
                showKeyGeneration = accessKey==null&&!integrationGuide,
                modifier = Modifier.fillMaxHeight()
            ){
                viewModel.accessKey(it)
            }
        }
        if(showAIAgent){
            Card(Modifier.fillMaxHeight().weight(2f)) {
                if(aiAgent!=null){
                    AIAgentGuideComponent(
                        viewModel.accessKey,aiAgent!!
                    ){
                        TextButton(
                            modifier = Modifier.height(32.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp),
                            onClick = { showAIAgent = false }
                        ) {
                            Icon(
                                painterResource(Res.drawable.arrow_back),
                                contentDescription = null,
                                Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                "Grant tool permissions",
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                    }
                }
            }
            OutlinedCard(Modifier.weight(1f).fillMaxHeight()) {
                AIAgentListComponent(Modifier.fillMaxWidth()) {
                    aiAgent = it
                }
            }
        }else {
            Card(Modifier.fillMaxHeight().weight(2f)) {
                viewModel.accessKey?.let { key ->
                    StudioActionBar(
                        "${key.name} (${viewModel.toolPermissionCount + viewModel.virtualToolPermissionCount})"
                    ) {
                        TextButton(
                            modifier = Modifier.height(32.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp),
                            onClick = { showAIAgent = true }
                        ) {
                            Text(
                                "Integrate with AI Agents",
                                style = MaterialTheme.typography.bodySmall,
                            )
                            Spacer(Modifier.width(4.dp))
                            Icon(painterResource(
                                Res.drawable.arrow_forward),
                                contentDescription = null,
                                Modifier.size(20.dp)
                            )
                        }
//                        IconButton(onClick = {
//                            grantViewModels.clear()
//                            viewModel.resetAllPermissions()
//                        }) {
//                            Icon(painterResource(Res.drawable.reset_settings), contentDescription = null)
//                        }
//                        IconButton(
//                            onClick = {
//                                viewModel.expanded = !viewModel.expanded
//                                grantViewModels.values.forEach {
//                                    it.expanded = viewModel.expanded
//                                }
//                            },
//                            modifier = Modifier.size(32.dp)
//                        ) {
//                            val icon = if (viewModel.expanded) Res.drawable.collapse_all else Res.drawable.expand_all
//                            Icon(painterResource(icon), contentDescription = null, Modifier.size(20.dp))
//                        }
                    }
                    Row(
                        Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        StudioSearchbar(modifier = Modifier.weight(1f)) {
                            viewModel.toolMakerCandidateFilter.value = it
                        }
//                        Spacer(Modifier.width(8.dp))
                        Button(
                            onClick = { viewModel.savePermissions() },
                            modifier = Modifier.height(40.dp)
                        ) {
                            Text("Grant")
                        }

                        IconButton(
                            onClick = {
                                grantViewModels.clear()
                                viewModel.resetAllPermissions()
                            },
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(painterResource(Res.drawable.reset_settings), contentDescription = null)
                        }
                        IconButton(
                            onClick = {
                                viewModel.expanded = !viewModel.expanded
                                grantViewModels.values.forEach {
                                    it.expanded = viewModel.expanded
                                }
                            },
                            modifier = Modifier.size(40.dp)
                        ) {
                            val icon = if (viewModel.expanded) Res.drawable.collapse_all else Res.drawable.expand_all
                            Icon(painterResource(icon), contentDescription = null, Modifier.size(20.dp))
                        }
                    }
                    LazyColumn(Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(toolMakerCandidates) { toolMaker ->
                            var grantViewModel = grantViewModels[toolMaker.id]
                            if (grantViewModel == null) {
                                grantViewModel = GrantToolPermissionViewModel()
                                grantViewModel.toolMaker(toolMaker)
                                if (toolMaker.type == 0) {
                                    grantViewModel.selectTools(viewModel.virtualToolPermissions)
                                } else {
                                    grantViewModel.selectTools(viewModel.toolPermissions)
                                }
                                grantViewModels[toolMaker.id] = grantViewModel
                            }
                            GrantToolPermissionView(
                                grantViewModel,
                                {
                                    viewModel.resetPermissions(toolMaker)
                                    if (toolMaker.type == 0) grantViewModel.selectTools(viewModel.virtualToolPermissions)
                                    else grantViewModel.selectTools(viewModel.toolPermissions)
                                }
                            ) { permitted, tools ->
                                viewModel.permit(permitted, tools)
                            }
                        }
                    }
                }
            }
            OutlinedCard(Modifier.weight(1f).fillMaxHeight()) {
                MCPServersComponent(selectedMCPServers = ids, modifier = Modifier.fillMaxHeight())
                { selected, toolMaker ->
                    if (selected) viewModel.nominate(toolMaker)
                    else {
                        val v = grantViewModels.remove(toolMaker.id)
                        if (v != null) {
                            viewModel.permit(false, v.tools.value)
                        }
                        viewModel.cancelNomination(toolMaker)
                    }
                }
            }
        }
    }
}