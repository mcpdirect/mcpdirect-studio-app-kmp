package ai.mcpdirect.studio.app.key

import ai.mcpdirect.studio.app.compose.StudioCard
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Chip
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ai.mcpdirect.backend.dao.entity.aitool.AIPortTool
import ai.mcpdirect.backend.dao.entity.aitool.AIPortToolMaker
import mcpdirectstudioapp.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource

val filterBy = mutableIntStateOf(0)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun AccessKeyPermissionScreen(
    viewModel: AccessKeyViewModel,
    onBack: () -> Unit
) {
    LaunchedEffect(viewModel) {
        viewModel.loadKeyPermissions()
    }
    if(viewModel.showSaveSuccess.value){
        viewModel.showSaveSuccess.value = false
        viewModel.keyPermissions.clear()
        viewModel.selectedTools.clear()
        viewModel.selectedAgents.clear()
        viewModel.selectedMakers.clear()
        onBack()
    }
    var showDiscardDialog by remember { mutableStateOf(false) }
    if (showDiscardDialog) {
        AlertDialog(
            onDismissRequest = { showDiscardDialog = false },
            title = { Text("Discard Changes?") },
            text = { Text("You have unsaved changes. Are you sure you want to discard them?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDiscardDialog = false
                        if (viewModel.hasUnsavedChanges()) {
                            showDiscardDialog = true
                        } else {
                            onBack()
                        }
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { 
                        showDiscardDialog = false 
                        onBack()
                    }
                ) {
                    Text("Discard")
                }
            }
        )
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tool Permissions for Key #${viewModel.apiKey.value!!.name}") },
                navigationIcon = {
                    IconButton(onClick = {
                        if (viewModel.hasUnsavedChanges()) {
                            showDiscardDialog = true
                        } else {
                            onBack()
                        }
                    }) {
                        Icon(painterResource(Res.drawable.arrow_back), contentDescription = "Back")
                    }
                },
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            val choices = remember {
                mutableStateListOf("Your Studio", "Tools Maker")
            }
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    SegmentedButton(
                        modifier = Modifier.width(150.dp),
                        selected = filterBy.intValue == 0,
                        onClick = {
                            filterBy.intValue = 0
                            viewModel.agentsDropdownExpanded.value=true
                                  },
                        shape = SegmentedButtonDefaults.itemShape(
                            index = 0,
                            count = choices.count()
                        )
                    ) {
                        AgentDropdown(viewModel = viewModel)
                    }
                SegmentedButton(
                    modifier = Modifier.width(150.dp),
                    selected = filterBy.intValue == 1,
                    onClick = {
                        filterBy.intValue = 1
                        viewModel.makersDropdownExpanded.value=true
                              },
                    shape = SegmentedButtonDefaults.itemShape(
                        index = 1,
                        count = choices.count()
                    )
                ) {
                    MakerDropdown(viewModel = viewModel)
                }
            }

            if(filterBy.value==0){
                FlowRow(
                    modifier = Modifier.wrapContentHeight(align = Alignment.Top),
                    horizontalArrangement = Arrangement.Start
                ) {
                    viewModel.selectedAgents.forEach { id ->
                        val agent = viewModel.agents.first { it.id == id }
                        Chip(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .align(Alignment.CenterVertically),
                            onClick = { }
                        ) {
                            Text(viewModel.getAgentName(agent), style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }else{
                FlowRow(
                    modifier = Modifier.fillMaxWidth().wrapContentHeight(align = Alignment.Top),
                    horizontalArrangement = Arrangement.End
                ) {
                    viewModel.selectedMakers.forEach { id ->
                        val maker = viewModel.makers.first { it.id == id }
                        Chip(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .align(Alignment.CenterVertically),
                            onClick = { }
                        ) {
                            Text(maker.name, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            ToolList(viewModel,onBack)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
private fun AgentDropdown(viewModel: AccessKeyViewModel) {
    var searchText by remember { mutableStateOf("") }

    ExposedDropdownMenuBox(
        expanded = viewModel.agentsDropdownExpanded.value,
        onExpandedChange = { expanded ->
            viewModel.agentsDropdownExpanded.value = expanded
            if (!expanded) searchText = ""
        }
    ) {
        Text(if(filterBy.intValue == 0) "Filter by Your Studio" else "Your Studio",
            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable))
        ExposedDropdownMenu(
            expanded = viewModel.agentsDropdownExpanded.value,
            onDismissRequest = {
                viewModel.agentsDropdownExpanded.value = false
                searchText = ""
            },
            modifier = Modifier.heightIn(max = 400.dp).widthIn(300.dp)
        ) {
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                label = { Text("Search agents") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            DropdownMenuItem(
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = viewModel.selectedAgents.size == viewModel.agents.size,
                            onCheckedChange = { checked ->
                                viewModel.toggleAllAgents(checked)
                            }
                        )
                        Text("Select All Agents")
                    }
                },
                onClick = {
                    viewModel.toggleAllAgents(
                        viewModel.selectedAgents.size != viewModel.agents.size
                    )
                }
            )

            HorizontalDivider()

            viewModel.agents
                .filter { it.name.contains(searchText, ignoreCase = true) }
                .forEach { agent ->
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = agent.id in viewModel.selectedAgents,
                                    onCheckedChange  = { checked ->
                                        if (checked) {
                                            viewModel.selectedAgents.add(agent.id)
                                        } else {
                                            viewModel.selectedAgents.remove(agent.id)
                                        }
                                    }
                                )
                                Column {
                                    Text(viewModel.getAgentName(agent))
                                    Text(agent.device, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        },
                        onClick = {
                            if (agent.id in viewModel.selectedAgents) {
                                viewModel.selectedAgents.remove(agent.id)
                            } else {
                                viewModel.selectedAgents.add(agent.id)
                            }
                        }
                    )
                }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
private fun MakerDropdown(viewModel: AccessKeyViewModel) {
    var searchText by remember { mutableStateOf("") }
    val visibleMakers = viewModel.getVisibleMakers()
    
    ExposedDropdownMenuBox(
        expanded = viewModel.makersDropdownExpanded.value,
        onExpandedChange = { expanded ->
            viewModel.makersDropdownExpanded.value = expanded
            if (!expanded) searchText = ""
        }
    ) {
        Text(if(filterBy.intValue == 1)
            "Filter by Tools Maker"
        else "Tools Makers",
            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable))
        ExposedDropdownMenu(
            expanded = viewModel.makersDropdownExpanded.value,
            onDismissRequest = {
                viewModel.makersDropdownExpanded.value = false
                searchText = ""
            },
            modifier = Modifier.heightIn(max = 400.dp).widthIn(300.dp)
        ) {
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                label = { Text("Search makers") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )
            
            DropdownMenuItem(
                text = { 
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = visibleMakers.isNotEmpty() && 
                                     viewModel.selectedMakers.size == visibleMakers.size,
                            onCheckedChange = { checked ->
                                viewModel.toggleAllMakers(checked)
                            },
                            enabled = visibleMakers.isNotEmpty()
                        )
                        Text("Select All Makers")
                    }
                },
                onClick = {
                    viewModel.toggleAllMakers(
                        viewModel.selectedMakers.size != visibleMakers.size
                    )
                },
                enabled = visibleMakers.isNotEmpty()
            )
            
            HorizontalDivider()
            
            visibleMakers
                .filter { it.name.contains(searchText, ignoreCase = true) }
                .forEach { maker ->
                    DropdownMenuItem(
                        text = { 
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = maker.id in viewModel.selectedMakers,
                                    onCheckedChange = { checked ->
                                        if (checked) {
                                            viewModel.selectedMakers.add(maker.id)
                                        } else {
                                            viewModel.selectedMakers.remove(maker.id)
                                        }
                                    }
                                )
                                Column {
                                    Text(maker.name)
                                    Text("Type: ${maker.type}", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        },
                        onClick = {
                            if (maker.id in viewModel.selectedMakers) {
                                viewModel.selectedMakers.remove(maker.id)
                            } else {
                                viewModel.selectedMakers.add(maker.id)
                            }
                            viewModel.selectedTools.clear()
                        }
                    )
                }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun ToolList(viewModel: AccessKeyViewModel,onBack: () -> Unit) {
    val selectedMakers = remember { viewModel.selectedMakers }
    val selectedAgents = remember { viewModel.selectedAgents }
    val tools = remember { viewModel.tools }
    val toolsByMaker = when (filterBy.value) {
        1 if selectedMakers.isNotEmpty() -> {
            tools
                .filter { it.makerId in selectedMakers }
                .groupBy { it.makerId }
        }
        0 if selectedAgents.isNotEmpty() -> {
            val makerIds = viewModel.makers
                .filter { it.agentId in selectedAgents }
                .map { it.id }
            tools
                .filter { it.makerId in makerIds }
                .groupBy { it.makerId }
        }
        else -> tools.groupBy { it.makerId }
    }
    // Track expanded/collapsed state for each maker
    val expandedMakers = remember { mutableStateMapOf<Long, Boolean>() }

    StudioCard(
        modifier = Modifier.padding(8.dp).fillMaxWidth(),
    ) {
        Column(Modifier.fillMaxHeight(1.0f)) {
            // Select all tools checkbox
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp, horizontal = 16.dp)
            ) {
                val allTools = toolsByMaker.values.flatten()
                Row (verticalAlignment = Alignment.CenterVertically,){
                    if(allTools.isNotEmpty())Checkbox(
                        checked = viewModel.selectedTools.isNotEmpty(),
                        onCheckedChange = {
                            val allSelected = allTools.all { it.id in viewModel.selectedTools }
                            allTools.forEach { tool ->
                                if (allSelected) {
                                    viewModel.selectedTools.remove(tool.id)
                                } else {
                                    viewModel.selectedTools.add(tool.id)
                                }
                            }
                        },
                    )
                    Text(
                        text = if (allTools.isEmpty()) "No tools available"
                        else "Selected (${viewModel.selectedTools.size})",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                Button(onClick = {
                    viewModel.savePermissions(onSuccess = onBack)
                }){
                    Text("Save")
                }
            }

            // Grouped tool list
            LazyColumn {
                toolsByMaker.forEach { (makerId, tools) ->
                    val maker = viewModel.getMakerById(makerId)
                    val isExpanded = expandedMakers[makerId] ?: false // Default to expanded
                    
                    item {
                        HorizontalDivider()
                        MakerHeader(
                            maker = maker,
                            tools = tools,
                            viewModel = viewModel,
                            isExpanded = isExpanded,
                            onToggleExpand = {
                                expandedMakers[makerId] = !isExpanded
                            }
                        )

                    }
                    
                    if (isExpanded) {
                        item{
                            HorizontalDivider(modifier = Modifier.shadow(
                                2.dp
                            ))
                        }
                        items(tools) { tool ->
                            ToolItem(tool, viewModel)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MakerHeader(
    maker: AIPortToolMaker?,
    tools: List<AIPortTool>,
    viewModel: AccessKeyViewModel,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit
) {
    Row(modifier = Modifier.clickable { onToggleExpand()}) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 16.dp)
        ) {
            // Expand/collapse icon
            Icon(
                painter = painterResource(
                    if (isExpanded) Res.drawable.keyboard_arrow_down else Res.drawable.keyboard_arrow_right
                ),
                contentDescription = if (isExpanded) "Collapse" else "Expand",
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))

            // Maker selection checkbox
            var checked = false
            for (tool in tools) {
                if(tool.id in viewModel.selectedTools){
                    checked = true
                    break;
                }
            }
            Checkbox(

//                checked = tools.isNotEmpty() &&
//                        tools.all { it.id in viewModel.selectedTools },
                checked = checked,
                onCheckedChange = {
                    val allSelected = tools.all { it.id in viewModel.selectedTools }
                    tools.forEach { tool ->
                        if (allSelected) {
                            viewModel.selectedTools.remove(tool.id)
                        } else {
                            viewModel.selectedTools.add(tool.id)
                        }
                    }
                },
                enabled = tools.isNotEmpty(),
                // Prevent checkbox from triggering expand/collapse
                modifier = Modifier.clickable { /* no-op - handled by parent */ }
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${maker?.name ?: "Unknown Maker"}(${tools.size})",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
//                    viewModel.agents.first { it.id == maker?.agentId }.name,
                    viewModel.getAgentName(maker?.agentId!!),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun ToolItem(tool: AIPortTool, viewModel: AccessKeyViewModel) {
    Box(modifier = Modifier.background(Color.White.copy(alpha = 0.5f))) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 16.dp)

        ) {
            Spacer(modifier = Modifier.width(32.dp))
            Checkbox(
                checked = tool.id in viewModel.selectedTools,
                onCheckedChange = {
                    if (tool.id in viewModel.selectedTools) {
                        viewModel.selectedTools.remove(tool.id)
                    } else {
                        viewModel.selectedTools.add(tool.id)
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

                Text("Tags: ${tool.tags}", style = MaterialTheme.typography.bodySmall)
            }

            IconButton(onClick = { /* Show tool details */ }) {
                Icon(painterResource(Res.drawable.info), contentDescription = "Details")
            }
        }
    }
}