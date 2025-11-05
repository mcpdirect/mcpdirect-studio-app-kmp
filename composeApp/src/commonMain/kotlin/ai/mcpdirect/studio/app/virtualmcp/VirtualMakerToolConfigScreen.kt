package ai.mcpdirect.studio.app.virtualmcp

import ai.mcpdirect.studio.app.Screen
import ai.mcpdirect.studio.app.compose.SearchView
import ai.mcpdirect.studio.app.compose.StudioCard
import ai.mcpdirect.studio.app.compose.Tag
import ai.mcpdirect.studio.app.compose.TooltipIconButton
import ai.mcpdirect.studio.app.generalViewModel
import ai.mcpdirect.studio.app.model.aitool.AIPortTool
import ai.mcpdirect.studio.app.model.aitool.AIPortToolMaker
import ai.mcpdirect.studio.app.tool.toolDetailViewModel
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import mcpdirectstudioapp.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VirtualMakerToolConfigScreen() {
    val viewModel = virtualMakerViewModel
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("${stringResource(Screen.VirtualMCP.title)} Tool Config for #${viewModel.selectedVirtualMaker!!.name}" ) },
//                actions = {
//                    IconButton(onClick = { viewModel.queryToolMakers() }) {
//                        Icon(
//                            painterResource(Res.drawable.refresh),
//                            contentDescription = "Refresh MCP Server"
//                        )
//                    }
//                    Button(onClick = {
//                        onBack()
//                        viewModel.modifyVirtualMakerTools()
//                    }){
//                        Text("Save")
//                    }
//                },
//                navigationIcon = {
//                    IconButton(onClick = {
////                        if (viewModel.hasUnsavedChanges()) {
////                            showDiscardDialog = true
////                        } else {
//                            onBack()
////                        }
//                    }) {
//                        Icon(painterResource(Res.drawable.arrow_back), contentDescription = "Back")
//                    }
//                },
//            )
//        }
//    ) { padding ->
//        when {
//            viewModel.selectedMakerTool != null -> ToolDetailView(viewModel, padding)
//            else -> MakerListView(viewModel, padding)
//        }
//    }
    LaunchedEffect(Unit) {
        generalViewModel.topBarActions = {
            IconButton(onClick = { viewModel.queryToolMakers() }) {
                Icon(
                    painterResource(Res.drawable.refresh),
                    contentDescription = "Refresh MCP Server"
                )
            }
            Button(onClick = {
                viewModel.modifyVirtualMakerTools()
                generalViewModel.previousScreen()
            }){
                Text("Save")
            }
        }
    }
    when {
        viewModel.selectedMakerTool != null -> ToolDetailView()
        else -> MakerListView()
    }
}

@Composable
private fun MakerListView() {
    val viewModel = virtualMakerViewModel
    Column{
        SearchView(
            query = viewModel.searchQuery,
            onQueryChange = { viewModel.updateSearchQuery(it) },
            placeholder = "Search makers..."
        )
        StudioCard(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxSize()
        ){
            Row(modifier = Modifier.fillMaxWidth()) {
                if (viewModel.makers.isEmpty()) {
                    Column(modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceAround,
                        horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(painterResource(Res.drawable.draft),
                            contentDescription = "Empty",
                            modifier = Modifier.size(64.dp)
                        )
                    }
                }else {
                    LazyColumn(modifier = Modifier.weight(3.0f)) {
                        items(viewModel.makers) { maker ->
                            MakerItem(maker) {
                                viewModel.selectMaker(maker)
                                viewModel.queryTools()
                            }
                        }
                    }
                    if (viewModel.selectedMaker != null) {
                        VerticalDivider()
                        Column(modifier = Modifier.weight(5.0f)) {
                            MakerDetailView(viewModel)
                        }
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MakerItem(
    maker: AIPortToolMaker,
    onClick: () -> Unit
) {
    ListItem(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        headlineContent = {
            maker.name?.let { Text(it) }
        },
        supportingContent = {
            if(maker.tags!=null&&maker.tags!!.isNotBlank())Text("Tags: ${maker.tags!!}")
        },
        leadingContent = {
            if (maker.status == 0) Icon(painter = painterResource(Res.drawable.block),
                contentDescription = "Click to enable",
                tint = Color.Red)
            else Icon(painter = painterResource(Res.drawable.check),
                contentDescription = "Click to disable",
                tint = Color(0xFF63A002))
        }
    )
}

@Composable
private fun MakerDetailView(
    viewModel: VirtualMakerViewModel,
) {
    val maker = viewModel.selectedMaker!!

    Column{
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = { viewModel.selectMaker(null) }) {
                Icon(painterResource(Res.drawable.arrow_back), contentDescription = "Back")
            }
            Text(
                text = maker.name ?: "",
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
//            Spacer(modifier = Modifier.weight(1.0f))

            Row (verticalAlignment = Alignment.CenterVertically,){
                IconButton(onClick = {
                    viewModel.selectAllSelectedMakerTools()
                }) {
                    Icon(painterResource(Res.drawable.select_all), contentDescription = "Select All")
                }
                IconButton(onClick = {
                    viewModel.deselectAllSelectedMakerTools()
                }) {
                    Icon(painterResource(Res.drawable.deselect), contentDescription = "deselect All")
                }
            }
        }
        LazyColumn {
            items(    viewModel.selectedMakerTools) { tool ->
                ToolItem(tool, viewModel)
            }
        }
    }
}

@Composable
private fun ToolItem(tool: AIPortTool, viewModel: VirtualMakerViewModel) {
    Box(modifier = Modifier.background(Color.White.copy(alpha = 0.5f))) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)

        ) {
            Checkbox(
                checked = viewModel.newVirtualMakerTools.containsKey(tool.id),
                onCheckedChange = {
                    if (viewModel.newVirtualMakerTools.containsKey(tool.id)) {
                        viewModel.newVirtualMakerTools.remove(tool.id)
                    } else {
                        viewModel.newVirtualMakerTools[tool.id]=tool
                    }
                }
            )

            Column(modifier = Modifier.weight(1f)) {
                Row {
                    Text(tool.name, style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.width(8.dp))
                    if (tool.status == 0) Tag(
                        "inactive",
                        color = MaterialTheme.colorScheme.error,
                    )
                    else Tag(
                        "active",
                        color = MaterialTheme.colorScheme.primary,
                    )
                }

//                Text("Tags: ${tool.tags}", style = MaterialTheme.typography.bodySmall)
            }

            IconButton(onClick = {
                toolDetailViewModel.toolId = tool.id
                toolDetailViewModel.toolName = tool.name
                generalViewModel.currentScreen(Screen.ToolDetails,
                    "",Screen.VirtualMCPToolConfig)
            }) {
                Icon(painterResource(Res.drawable.info), contentDescription = "Details")
            }
        }
    }
}


@Composable
private fun ToolDetailView() {
    val viewModel = virtualMakerViewModel
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TooltipIconButton(
                icon = Res.drawable.arrow_back,
                contentDescription = "Back",
                onClick = {
                viewModel.selectedMakerTool = null
                viewModel.selectedMakerToolMetadata = null
            })
            Text(
                text = viewModel.selectedMakerTool?.name ?: "",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f)
            )
        }
        HorizontalDivider()
        Column(Modifier.padding(8.dp).fillMaxSize().verticalScroll(scrollState)) {
                viewModel.selectedMakerToolMetadata?.let {
                    Text(
                        text = it,
                        modifier = Modifier.padding(8.dp)
                    )
                }
//            CodeTextView(highlights = highlights.value)
        }
    }
    LaunchedEffect(Unit) {
        if(viewModel.selectedMakerTool!=null)
        viewModel.queryToolMetadata(viewModel.selectedMakerTool!!.id)
    }
}
