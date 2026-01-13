package ai.mcpdirect.studio.app.key

import ai.mcpdirect.studio.app.compose.StudioActionBar
import ai.mcpdirect.studio.app.compose.StudioBoard
import ai.mcpdirect.studio.app.compose.StudioListItem
import ai.mcpdirect.studio.app.key.component.MCPdirectKeysComponent
import ai.mcpdirect.studio.app.key.view.GrantToolPermissionView
import ai.mcpdirect.studio.app.key.view.GrantToolPermissionViewModel
import ai.mcpdirect.studio.app.model.aitool.AIPortToolAccessKey
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import mcpdirectstudioapp.composeapp.generated.resources.collapse_all
import mcpdirectstudioapp.composeapp.generated.resources.expand_all
import mcpdirectstudioapp.composeapp.generated.resources.reset_settings
import mcpdirectstudioapp.composeapp.generated.resources.search
import mcpdirectstudioapp.composeapp.generated.resources.search_off
import org.jetbrains.compose.resources.painterResource

@Composable
fun MCPdirectKeyScreen(
    accessKey: AIPortToolAccessKey?,
    paddingValues: PaddingValues = PaddingValues(),
){
    val viewModel by remember {mutableStateOf(MCPdirectKeyScreenViewModel())}
    val grantViewModels = remember { mutableMapOf<Long, GrantToolPermissionViewModel>() }
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
                            var grantViewModel = grantViewModels[toolMaker.id]
                            if(grantViewModel == null) {
                                grantViewModel = GrantToolPermissionViewModel()
                                grantViewModel.toolMaker(toolMaker)
                                if(toolMaker.type==0){
                                    grantViewModel.selectTools(viewModel.virtualToolPermissions)
                                    grantViewModel.selectedToolCount = viewModel.virtualToolPermissions.values.count {
                                        it.status > 0 && it.makerId == toolMaker.id
                                    }
                                }else {
                                    grantViewModel.selectTools(viewModel.toolPermissions)
                                    grantViewModel.selectedToolCount = viewModel.toolPermissions.values.count {
                                        it.status > 0 && it.makerId == toolMaker.id
                                    }
                                }
                                grantViewModels[toolMaker.id] = grantViewModel
                            }
                            GrantToolPermissionView(
//                            toolMaker,
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
//                MCPdirectKeyToolPermissionView(key, Modifier.padding(horizontal = 16.dp))
                }
            }
            Spacer(Modifier.height(8.dp))
            Button(onClick = { viewModel.savePermissions() }, Modifier.fillMaxWidth()){
                Text("Grant")
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
                val ids by viewModel.toolMarkerCandidateIds.collectAsState()
                LazyColumn(Modifier.padding(start=16.dp,end=16.dp)) {
                    items(toolMakerCandidates){ toolMaker ->
                        var checked by remember{ mutableStateOf(false) }
                        LaunchedEffect(ids){
                            checked = toolMaker.id in ids
                        }
                        Row(
                            Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Checkbox(
                                checked = checked,
                                onCheckedChange = {
                                    checked = it
                                    if(checked) viewModel.nominate(toolMaker)
                                    else {
                                        val v = grantViewModels.remove(toolMaker.id)
                                        if(v!=null){
                                            viewModel.permit(false,v.tools.value)
                                        }
                                        viewModel.cancelNomination(toolMaker)
                                    }
                                },
                            )
                            Text(toolMaker.name)
                        }
//                        StudioListItem(
////                        selected = viewModel.selectedToolMaker(toolMaker),
////                        modifier = Modifier.clickable {
////                            viewModel.selectToolMaker(toolMaker,multiSelectable)
////                        },
//                            headlineContent = { Text(toolMaker.name, style = MaterialTheme.typography.bodyMedium) },
//                            leadingContent = {
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
////                                IconButton(onClick = {viewModel.nominate(toolMaker)}){
////                                    Icon(painterResource(Res.drawable.move_left),
////                                        contentDescription = null, Modifier.size(24.dp))
////                                }
//                            }
//                        )
                    }
                }
            }
//            MCPServerCandidateView()
        }
    }
}