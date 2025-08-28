//package ai.mcpdirect.studio.app.permission
//
//import ToolPermissionViewModel
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
////import androidx.compose.material.Card
//import androidx.compose.material.Chip
//import androidx.compose.material.ExperimentalMaterialApi
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import ai.mcpdirect.backend.dao.entity.aitool.AIPortTool
//import mcpdirectstudioapp.composeapp.generated.resources.Res
//import mcpdirectstudioapp.composeapp.generated.resources.arrow_back
//import mcpdirectstudioapp.composeapp.generated.resources.info
//import org.jetbrains.compose.resources.painterResource
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun ToolPermissionScreen(
//    viewModel: ToolPermissionViewModel,
//    onBack: () -> Unit
//) {
//    // 加载数据
//    LaunchedEffect(viewModel) {
//        viewModel.loadKeyPermissions()
//    }
//
//    // 对话框状态
//    var showDiscardDialog by remember { mutableStateOf(false) }
//
//
//    if (showDiscardDialog) {
//        AlertDialog(
//            onDismissRequest = { showDiscardDialog = false },
//            title = { Text("Discard Changes?") },
//            text = { Text("You have unsaved changes. Are you sure you want to discard them?") },
//            confirmButton = {
//                TextButton(
//                    onClick = {
//                        showDiscardDialog = false
//                        //Save
//                        if (viewModel.hasUnsavedChanges()) {
//                            showDiscardDialog = true
//                        } else {
//                            onBack()
//                        }
//                    }
//                ) {
//                    Text("Save")
//                }
//            },
//            dismissButton = {
//                TextButton(
//                    onClick = {
//                        showDiscardDialog = false
//                        onBack()
//                    }
//                ) {
//                    Text("Discard")
//                }
//            }
//        )
//    }
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("Tool Permissions for Key #${viewModel.apiKey.value!!.name}") },
//                navigationIcon = {
//                    IconButton(onClick = {
//                        if (viewModel.hasUnsavedChanges()) {
//                            showDiscardDialog = true
//                        } else {
//                            onBack()
//                        }
//                    }) {
//                        Icon(painterResource(Res.drawable.arrow_back), contentDescription = "Back")
//                    }
//                },
//                actions = {
//                    Button(onClick = {}){
//                        Text("Save")
//                    }
//                }
//            )
//        },
////        floatingActionButton = {
////            ExtendedFloatingActionButton(
////                onClick = {
////                    viewModel.savePermissions()
////                    onBack()
////                },
////                icon = { Icon(Icons.Default.Save, contentDescription = "Save") },
////                text = { Text("Save Changes") },
////                enabled = viewModel.hasUnsavedChanges() && !viewModel.isLoading.value
////            )
////        }
//    ) { paddingValues ->
//        Column(modifier = Modifier
//            .padding(paddingValues)
////            .padding(16.dp)
//        ) {
//            if(viewModel.apiKey.value==null){
//
//            }
//            // 代理商和制造商下拉选择行
//            Row {
//                AgentDropdown(
//                    viewModel = viewModel,
//                    modifier = Modifier.weight(1f)
//                )
//            }
//
//                    // 制造商下拉选择
//                 Row {
//                     MakerDropdown(
//                         viewModel = viewModel,
//                         modifier = Modifier.weight(1f)
//                     )
//                 }
//            Spacer(modifier = Modifier.height(16.dp))
//
//            // 工具列表
//            ToolList(viewModel)
//        }
//    }
//}
//
//@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
//@Composable
//private fun AgentDropdown(
//    viewModel: ToolPermissionViewModel,
//    modifier: Modifier = Modifier
//) {
//    var searchText by remember { mutableStateOf("") }
//
//    Row {
//            ExposedDropdownMenuBox(
//                modifier = Modifier.width(180.dp),
//                expanded = viewModel.agentsDropdownExpanded.value,
//                onExpandedChange = { expanded ->
//                    viewModel.agentsDropdownExpanded.value = expanded
//                    if (!expanded) searchText = ""
//                }
//            ) {
//                Button(
//                    modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable),
//                    onClick = {viewModel.agentsDropdownExpanded.value=true}){
//                    Text("Filter by Agents")
//                }
//
//                ExposedDropdownMenu(
//                    expanded = viewModel.agentsDropdownExpanded.value,
//                    onDismissRequest = {
//                        viewModel.agentsDropdownExpanded.value = false
//                        searchText = ""
//                    },
//                    modifier = Modifier.heightIn(max = 400.dp).widthIn(300.dp)
//                ) {
//                    // 搜索框
//                    OutlinedTextField(
//                        value = searchText,
//                        onValueChange = { searchText = it },
//                        label = { Text("Search agents") },
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(horizontal = 16.dp, vertical = 8.dp)
//                    )
//
//                    // 全选选项
//                    DropdownMenuItem(
//                        text = {
//                            Row(verticalAlignment = Alignment.CenterVertically) {
//                                Checkbox(
//                                    checked = viewModel.selectedAgents.size == viewModel.agents.size,
//                                    onCheckedChange = { checked ->
//                                        viewModel.toggleAllAgents(checked)
//                                    }
//                                )
//                                Text("Select All Agents")
//                            }
//                        },
//                        onClick = {
//                            viewModel.toggleAllAgents(
//                                viewModel.selectedAgents.size != viewModel.agents.size
//                            )
//                        }
//                    )
//
//                    HorizontalDivider()
//
//                    // 代理商列表
//                    viewModel.agents
//                        .filter { it.name.contains(searchText, ignoreCase = true) }
//                        .forEach { agent ->
//                            DropdownMenuItem(
//                                text = {
//                                    Row(verticalAlignment = Alignment.CenterVertically) {
//                                        Checkbox(
//                                            checked = agent.id in viewModel.selectedAgents,
//                                            onCheckedChange  = { checked ->
//                                                if (checked) {
//                                                    viewModel.selectedAgents.add(agent.id)
//                                                } else {
//                                                    viewModel.selectedAgents.remove(agent.id)
//                                                }
//                                            }
//                                        )
//                                        Column {
//                                            Text(agent.name)
//                                            Text(agent.device, style = MaterialTheme.typography.bodySmall)
//                                        }
//                                    }
//                                },
//                                onClick = {
//                                    // 切换当前代理商的选中状态
//                                    if (agent.id in viewModel.selectedAgents) {
//                                        viewModel.selectedAgents.remove(agent.id)
//                                    } else {
//                                        viewModel.selectedAgents.add(agent.id)
//                                    }
//
//                                }
//                            )
//                        }
//                }
//            }
//        FlowRow(
//            modifier = Modifier
////                .fillMaxWidth()
//                .wrapContentHeight(align = Alignment.Top),
//            horizontalArrangement = Arrangement.Start
//        ) {
//            viewModel.selectedAgents.forEach { id ->
//
//                val agent = viewModel.agents
//                    .first { it.id == id }
//
//                Chip(
//                    modifier = Modifier
//                        .padding(horizontal = 4.dp)
//                        .align(Alignment.CenterVertically),
//                    onClick = { /* do something */ }
//                ) {
//                    Text(agent.name, style = MaterialTheme.typography.bodySmall)
//                }
//            }
//        }
//    }
//}
//
//@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
//@Composable
//private fun MakerDropdown(
//    viewModel: ToolPermissionViewModel,
//    modifier: Modifier = Modifier
//) {
//    var searchText by remember { mutableStateOf("") }
//    val visibleMakers = viewModel.getVisibleMakers()
//
//        ExposedDropdownMenuBox(
//            modifier = Modifier.width(180.dp),
//            expanded = viewModel.makersDropdownExpanded.value,
//            onExpandedChange = { expanded ->
//                viewModel.makersDropdownExpanded.value = expanded
//                if (!expanded) searchText = ""
//            }
//        ) {
//            Button(
//                modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable),
//                onClick = {viewModel.makersDropdownExpanded.value=true}){
//                Text("Filter by Makers")
//            }
//            ExposedDropdownMenu(
//                expanded = viewModel.makersDropdownExpanded.value,
//                onDismissRequest = {
//                    viewModel.makersDropdownExpanded.value = false
//                    searchText = ""
//                },
//                modifier = Modifier.heightIn(max = 400.dp).widthIn(300.dp)
//            ) {
//                // 搜索框
//                OutlinedTextField(
//                    value = searchText,
//                    onValueChange = { searchText = it },
//                    label = { Text("Search makers") },
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(horizontal = 16.dp, vertical = 8.dp)
//                )
//
//                // 全选选项
//                DropdownMenuItem(
//                    text = {
//                        Row(verticalAlignment = Alignment.CenterVertically) {
//                            Checkbox(
//                                checked = visibleMakers.isNotEmpty() &&
//                                         viewModel.selectedMakers.size == visibleMakers.size,
//                                onCheckedChange = { checked ->
//                                    viewModel.toggleAllMakers(checked)
//                                },
//                                enabled = visibleMakers.isNotEmpty()
//                            )
//                            Text("Select All Makers")
//                        }
//                    },
//                    onClick = {
//                        viewModel.toggleAllMakers(
//                            viewModel.selectedMakers.size != visibleMakers.size
//                        )
//                    },
//                    enabled = visibleMakers.isNotEmpty()
//                )
//
//                HorizontalDivider()
//
//                // 制造商列表
//                visibleMakers
//                    .filter { it.name.contains(searchText, ignoreCase = true) }
//                    .forEach { maker ->
//                        DropdownMenuItem(
//                            text = {
//                                Row(verticalAlignment = Alignment.CenterVertically) {
//                                    Checkbox(
//                                        checked = maker.id in viewModel.selectedMakers,
//                                        onCheckedChange = { checked ->
//                                            if (checked) {
//                                                viewModel.selectedMakers.add(maker.id)
//                                            } else {
//                                                viewModel.selectedMakers.remove(maker.id)
//                                            }
//                                            // 清除工具选择
////                                            viewModel.selectedTools.clear()
//                                        }
//                                    )
//                                    Column {
//                                        Text(maker.name)
//                                        Text("Type: ${maker.type}", style = MaterialTheme.typography.bodySmall)
//                                    }
//                                }
//                            },
//                            onClick = {
//                                if (maker.id in viewModel.selectedMakers) {
//                                    viewModel.selectedMakers.remove(maker.id)
//                                } else {
//                                    viewModel.selectedMakers.add(maker.id)
//                                }
//                                // 清除工具选择
//                                viewModel.selectedTools.clear()
//                            }
//                        )
//                    }
//            }
//        }
////    }
//
//    FlowRow(
//        modifier = Modifier
////                .fillMaxWidth()
//            .wrapContentHeight(align = Alignment.Top),
//        horizontalArrangement = Arrangement.Start
//    ) {
//        viewModel.selectedMakers.forEach { id ->
//
//            val maker = viewModel.makers
//                .first { it.id == id }
//
//            Chip(
//                modifier = Modifier
//                    .padding(horizontal = 4.dp)
//                    .align(Alignment.CenterVertically),
//                onClick = { /* do something */ }
//            ) {
//                Text(maker.name, style = MaterialTheme.typography.bodySmall)
//            }
//        }
//    }
//}
//
//@Composable
//private fun ToolList(viewModel: ToolPermissionViewModel) {
//    val toolsToShow = remember(viewModel.selectedMakers, viewModel.selectedAgents) {
//        when {
//            viewModel.selectedMakers.isNotEmpty() -> {
//                viewModel.tools.filter { it.makerId in viewModel.selectedMakers }
//            }
//            viewModel.selectedAgents.isNotEmpty() -> {
//                val makerIds = viewModel.makers
//                    .filter { it.agentId in viewModel.selectedAgents }
//                    .map { it.id }
//                viewModel.tools.filter { it.makerId in makerIds }
//            }
//            else -> viewModel.tools
//        }
//    }
//    Card(
//        modifier = Modifier
//            .padding(8.dp)
//            .fillMaxWidth(),
//        elevation = CardDefaults.cardElevation(4.dp)
//    ) {
//        Column(Modifier.fillMaxHeight(1.0f)) {
//            // 全选工具选项
//            Row(
//                verticalAlignment = Alignment.CenterVertically,
//                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
//            ) {
//                    Checkbox(
//                        checked = toolsToShow.isNotEmpty() &&
//                                toolsToShow.all { it.id in viewModel.selectedTools },
//                        onCheckedChange = {
//                            val allSelected = toolsToShow.all { it.id in viewModel.selectedTools }
//                            toolsToShow.forEach { tool ->
//                                if (allSelected) {
//                                    viewModel.selectedTools.remove(tool.id)
//                                } else {
//                                    viewModel.selectedTools.add(tool.id)
//                                }
//                            }
//                        }, // 由父Row处理点击
//                        enabled = toolsToShow.isNotEmpty()
//                    )
//                    Text(
//                        text = if (toolsToShow.isEmpty()) "No tools available"
//                        else "Select All (${toolsToShow.size})",
//                        style = MaterialTheme.typography.bodyLarge
//                    )
//            }
//            HorizontalDivider()
//            // 工具列表
//            LazyColumn {
//                items(toolsToShow) { tool ->
//                    ToolItem(tool, viewModel)
//                }
//            }
//        }
//    }
//}
//
//@Composable
//private fun ToolItem(tool: AIPortTool, viewModel: ToolPermissionViewModel) {
//    Row(
//        verticalAlignment = Alignment.CenterVertically,
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(vertical = 8.dp)
//    ) {
//        Checkbox(
//            checked = tool.id in viewModel.selectedTools,
//            onCheckedChange = {
//                if (tool.id in viewModel.selectedTools) {
//                    viewModel.selectedTools.remove(tool.id)
//                } else {
//                    viewModel.selectedTools.add(tool.id)
//                }
//            } // 由父Row处理点击
//        )
//
//        Column(modifier = Modifier.weight(1f)) {
//            Text(tool.name, style = MaterialTheme.typography.bodyLarge)
//            viewModel.getMakerById(tool.makerId)?.let { maker ->
//                Text("by ${maker.name}", style = MaterialTheme.typography.bodySmall)
//            }
//        }
//
//        IconButton(onClick = { /* 查看工具详情 */ }) {
//            Icon(painterResource(Res.drawable.info), contentDescription = "Details")
//        }
//    }
//
//    HorizontalDivider()
//}