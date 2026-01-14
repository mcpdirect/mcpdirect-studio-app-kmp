package ai.mcpdirect.studio.app.virtualmcp

import ai.mcpdirect.studio.app.compose.StudioActionBar
import ai.mcpdirect.studio.app.compose.StudioSearchbar
import ai.mcpdirect.studio.app.mcp.component.MCPServersComponent
import ai.mcpdirect.studio.app.model.aitool.AIPortToolMaker
import ai.mcpdirect.studio.app.virtualmcp.view.ToolMakerView
import ai.mcpdirect.studio.app.virtualmcp.view.ToolMakerViewModel
import ai.mcpdirect.studio.app.virtualmcp.view.VirtualToolMakersView
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mcpdirectstudioapp.composeapp.generated.resources.Res
import mcpdirectstudioapp.composeapp.generated.resources.collapse_all
import mcpdirectstudioapp.composeapp.generated.resources.expand_all
import mcpdirectstudioapp.composeapp.generated.resources.reset_settings
import org.jetbrains.compose.resources.painterResource

@Composable
fun VirtualMCPScreen(
    toolMaker: AIPortToolMaker?,
    paddingValues: PaddingValues = PaddingValues(),
) {
    val viewModel by remember {mutableStateOf(VirtualMCPScreenViewModel())}
    val grantViewModels = remember { mutableMapOf<Long, ToolMakerViewModel>() }
    val toolMakerCandidates by viewModel.toolMarkerCandidates.collectAsState()
    val ids by viewModel.toolMarkerCandidateIds.collectAsState()
    Row(Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),) {
        OutlinedCard(Modifier.weight(1f).fillMaxHeight()) {
            VirtualToolMakersView(toolMaker,toolMaker==null){
                viewModel.currentToolMaker(it)
            }
        }
        Column(Modifier.weight(2f)) {
            Card(Modifier.weight(1f)) {
                viewModel.currentToolMaker?.let { key ->
                    StudioActionBar(
                        "Tools (${viewModel.virtualToolCount})"
                    ){
                        IconButton(onClick = {
                            grantViewModels.clear()
//                            viewModel.resetAllPermissions()
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
                                grantViewModel = ToolMakerViewModel()
                                grantViewModel.toolMaker(toolMaker)
                                grantViewModel.selectTools(viewModel.virtualTools)
//                                grantViewModel.selectedToolCount = viewModel.virtualTools.values.count {
//                                    it.status > 0 && it.makerId == toolMaker.id
//                                }
                                grantViewModels[toolMaker.id] = grantViewModel
                            }
                            ToolMakerView(
                                grantViewModel,
                                {
//                                    viewModel.resetPermissions(toolMaker)
                                    if(toolMaker.type==0) grantViewModel.selectTools(viewModel.virtualTools)
                                    else grantViewModel.selectTools(viewModel.virtualTools)
                                }
                            ){ permitted, tools ->
//                                viewModel.permit(permitted, tools)
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            Button(onClick = { }, Modifier.fillMaxWidth()) {
                Text("Save")
            }
        }
        OutlinedCard(Modifier.weight(1f).fillMaxHeight()) {
            MCPServersComponent(
                showVirtualMCP = false,
                selectedMCPServers = ids,
                modifier = Modifier.fillMaxHeight()
            ) { selected, toolMaker ->
                if(selected) viewModel.nominate(toolMaker)
                else {
                    val v = grantViewModels.remove(toolMaker.id)
                    if(v!=null){
//                        viewModel.permit(false,v.tools.value)
                    }
                    viewModel.cancelNomination(toolMaker)
                }
            }
        }
    }
}