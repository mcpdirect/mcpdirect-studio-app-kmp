package ai.mcpdirect.studio.app.virtual

import ai.mcpdirect.backend.dao.entity.aitool.AIPortTool
import ai.mcpdirect.backend.dao.entity.aitool.AIPortToolMaker
import ai.mcpdirect.studio.app.Screen
import ai.mcpdirect.studio.app.compose.SearchView
import ai.mcpdirect.studio.app.compose.StudioCard
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.IconButton
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
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VirtualMakerToolConfigScreen(
    viewModel: VirtualMakerViewModel,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("${stringResource(Screen.VirtualMCP.title)} Tool Config for #${viewModel.selectedVirtualMaker!!.name}" ) },
                actions = {
                    IconButton(onClick = { viewModel.queryToolMakers() }) {
                        Icon(
                            painterResource(Res.drawable.refresh),
                            contentDescription = "Refresh MCP Server"
                        )
                    }
                    Button(onClick = {
                        onBack()
                        viewModel.modifyVirtualMakerTools()
                    }){
                        Text("Save")
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
//                        if (viewModel.hasUnsavedChanges()) {
//                            showDiscardDialog = true
//                        } else {
                            onBack()
//                        }
                    }) {
                        Icon(painterResource(Res.drawable.arrow_back), contentDescription = "Back")
                    }
                },
            )
        }
    ) { padding ->
        when {
            viewModel.selectedMakerTool != null -> ToolDetailView(viewModel, padding)
            else -> MakerListView(viewModel, padding)
        }
    }

//    LaunchedEffect(Unit) {
//        viewModel.queryToolMakers(AIPortToolMaker.TYPE_VIRTUAL)
//    }
}

@Composable
private fun MakerListView(
    viewModel: VirtualMakerViewModel,
    padding: PaddingValues
) {

    Column(modifier = Modifier.padding(padding)) {
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


@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun MakerItem(
    maker: AIPortToolMaker,
    onClick: () -> Unit
) {
    ListItem(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        headlineContent = {
            Text(maker.name)
        },
        supportingContent = {
            Text("Tags: ${maker.tags}")
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
                    Box(
                        Modifier.border(
                            width = 1.dp,
                            color = androidx.compose.material.MaterialTheme.colors.onSurface.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(8.dp)
                        )
                    ) {
                        if (tool.status == 0) Text(
                            "inactive",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(2.dp)
                        )
                        else Text(
                            "active",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(2.dp)
                        )
                    }
                }

//                Text("Tags: ${tool.tags}", style = MaterialTheme.typography.bodySmall)
            }

            IconButton(onClick = {
                viewModel.selectedMakerTool = tool
            }) {
                Icon(painterResource(Res.drawable.info), contentDescription = "Details")
            }
        }
    }
}


@Composable
private fun ToolDetailView(
    viewModel: VirtualMakerViewModel,
    padding: PaddingValues
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier.fillMaxSize().padding(padding),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            androidx.compose.material3.IconButton(onClick = {
                viewModel.selectedMakerTool = null
                viewModel.selectedMakerToolMetadata = null
            }) {
                Icon(painterResource(Res.drawable.arrow_back), contentDescription = "Back")
            }
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
