package ai.mcpdirect.studio.app.virtualmcp

import ai.mcpdirect.backend.dao.entity.aitool.AIPortToolMaker
import ai.mcpdirect.backend.dao.entity.aitool.AIPortVirtualTool
import ai.mcpdirect.studio.app.Screen
import ai.mcpdirect.studio.app.compose.SearchView
import ai.mcpdirect.studio.app.compose.StudioCard
import ai.mcpdirect.studio.app.compose.TooltipIconButton
import ai.mcpdirect.studio.app.generalViewModel
import ai.mcpdirect.studio.app.toolDetailViewModel
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Chip
import androidx.compose.material.ChipDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.IconButton
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import mcpdirectstudioapp.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VirtualMakerScreen(
    viewModel: VirtualMakerViewModel,
    onConfigClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Screen.VirtualMCP.title) ) },
                actions = {
                    IconButton(onClick = { viewModel.showAddServerDialog = true }) {
                        Icon(
                            painterResource(Res.drawable.add),
                            contentDescription = "Add Virtual MCP Server"
                        )
                    }
                }
            )
        }
    ) { padding ->
//        when {
//            viewModel.selectedMaker.value != null -> ToolsView(viewModel,padding)
//            else -> MakerListView(viewModel, padding)
//        }
        MakerListView(viewModel, padding,onConfigClick)
    }
    if (viewModel.showAddServerDialog) {
        AddServerDialog(viewModel)
    }else if(viewModel.showEditServerNameDialog){
        EditServerNameDialog(viewModel)
    }else if(viewModel.showEditServerTagsDialog){
        EditServerTagsDialog(viewModel)
    }

    // Error Dialog
    if (viewModel.errorMessage != null) {
        AlertDialog(
            onDismissRequest = { viewModel.errorMessage = null },
            title = { Text("Error") },
            text = { Text(viewModel.errorMessage!!) },
            confirmButton = {
                Button(onClick = { viewModel.errorMessage = null }) {
                    Text("OK")
                }
            }
        )
    }
    LaunchedEffect(Unit) {
        viewModel.queryToolMakers()
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AddServerDialog(viewModel: VirtualMakerViewModel) {
    val nameFocusRequester = remember { FocusRequester() }
    val tagFocusRequester = remember { FocusRequester() }
    val addFocusRequester = remember { FocusRequester() }
    val formScrollState = rememberScrollState()

    AlertDialog(
        onDismissRequest = { viewModel.showAddServerDialog=false },
        title = { Text("Add New Virtual MCP Server") },
        text = {
            Column(Modifier.verticalScroll(formScrollState)) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth().focusRequester(nameFocusRequester),
                    value = viewModel.serverName,
                    onValueChange = { viewModel.onServerNameChange(it) },
                    label = { Text("Server Name") },
                    singleLine = true,
                    isError = viewModel.serverNameErrors && viewModel.showValidationError,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Send
                    ),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            tagFocusRequester.requestFocus(FocusDirection.Next)
                        }
                    ),
                    supportingText = {
                        if (viewModel.serverNameErrors && viewModel.showValidationError) {
                            Text("Server Name cannot be empty and the max length is 32", color = MaterialTheme.colorScheme.error)
                        }
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth().focusRequester(tagFocusRequester),
                    value = viewModel.serverTag,
                    onValueChange = { viewModel.onServerTagChange(it) },
                    label = { Text("Server Tags") },
                    placeholder = {Text("Input server tag, end with \",\"")},
                    singleLine = true,
                    isError = viewModel.serverTagErrors && viewModel.showValidationError,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Send
                    ),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            if(viewModel.serverTag.isEmpty())
                                addFocusRequester.requestFocus(FocusDirection.Next)
                            else
                                viewModel.onServerTagChange(viewModel.serverTag+",")
                        }
                    )
                )
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(align = Alignment.Top),
                    horizontalArrangement = Arrangement.Start
                ) {
                    viewModel.serverTags.forEach {
                        Chip(
                            modifier = Modifier.padding(horizontal = 2.dp)
                                .align(Alignment.CenterVertically),
                            colors = ChipDefaults.chipColors(backgroundColor = ButtonDefaults.buttonColors().containerColor),
                            onClick = { viewModel.removeServerTag(it)},
                            leadingIcon = {
                                Icon(
                                    painterResource(
                                        Res.drawable.cancel),
                                    contentDescription = "Delete tag",
                                    tint = ButtonDefaults.buttonColors().contentColor
                                )
                            }
                        ) {
                            Text(
                                it,
                                style = MaterialTheme.typography.bodySmall,
                                color = ButtonDefaults.buttonColors().contentColor
                            )
                        }
                    }
                }
            }
            LaunchedEffect(Unit) {
                nameFocusRequester.requestFocus()
            }
        },
        confirmButton = {
            Button(
                enabled = !viewModel.serverNameErrors,
                modifier = Modifier.focusRequester(addFocusRequester),
                onClick = {
                    viewModel.showAddServerDialog=false
                    viewModel.createServer()
                }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            Button(
                colors = ButtonDefaults.textButtonColors(),
                onClick = { viewModel.showAddServerDialog=false }) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun MakerListView(
    viewModel: VirtualMakerViewModel,
    padding: PaddingValues,
    onConfigClick: () -> Unit
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
                if (viewModel.virtualMakers.isEmpty()) {
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
                        items(viewModel.virtualMakers) { maker ->
                            MakerItem(maker) {
                                viewModel.selectVirtualMaker(maker)
                                viewModel.queryVirtualMakerTools()
                            }
                        }
                    }
                    if (viewModel.selectedVirtualMaker != null) {
                        VerticalDivider()
                        Column(modifier = Modifier.weight(5.0f)) {
                            MakerDetailView(viewModel, onConfigClick)
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
        trailingContent = {
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
    onConfigClick: () -> Unit
) {
    val maker = viewModel.selectedVirtualMaker!!
    Column{
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = { viewModel.selectVirtualMaker(null) }) {
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
            TooltipIconButton(
                Res.drawable.settings,
                tooltipText = "Config tools",
                onClick = { onConfigClick() }
            )
            TooltipIconButton(
                Res.drawable.restart_alt,
                tooltipText = "Refresh",
                onClick = { }
            )
            TooltipIconButton(
                Res.drawable.sell,
                tooltipText = "Edit tags",
                onClick = {
                viewModel.onServerTagChange(maker.tags)
                viewModel.showEditServerTagsDialog = true
            })
            TooltipIconButton(
                Res.drawable.badge,
                tooltipText = "Edit name",
                onClick = {
                viewModel.onServerNameChange(maker.name)
                viewModel.showEditServerNameDialog = true
            })
            Spacer(Modifier.border(1.dp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f), shape = RectangleShape))
            if (maker.status == 0) TooltipIconButton(
                Res.drawable.play_circle,
                tooltipText = "Click to start",
                iconTint = MaterialTheme.colorScheme.primary,
                onClick = { viewModel.setServerStatus(maker.id,1) })
            else TooltipIconButton(
                Res.drawable.stop_circle,
                tooltipText = "Click to stop",
                iconTint = MaterialTheme.colorScheme.error,
                onClick = { viewModel.setServerStatus(maker.id,0) })
            TooltipIconButton(
                Res.drawable.delete,
                tooltipText = "Deprecate",
                onClick = { viewModel.setServerStatus(maker.id,-1) })

        }
        HorizontalDivider()
        LazyColumn {
            items(viewModel.selectedVirtualMakerTools) { tool ->
                ToolItem(tool, viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun EditServerNameDialog(viewModel: VirtualMakerViewModel) {
    val nameFocusRequester = remember { FocusRequester() }
    val addFocusRequester = remember { FocusRequester() }
    val formScrollState = rememberScrollState()

    AlertDialog(
        onDismissRequest = { viewModel.showEditServerNameDialog=false },
        title = { Text("Add New Virtual MCP Server") },
        text = {
            Column(Modifier.verticalScroll(formScrollState)) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth().focusRequester(nameFocusRequester),
                    value = viewModel.serverName,
                    onValueChange = { viewModel.onServerNameChange(it) },
                    label = { Text("Server Name") },
                    singleLine = true,
                    isError = viewModel.serverNameErrors && viewModel.showValidationError,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Send
                    ),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            addFocusRequester.requestFocus(FocusDirection.Next)
                        }
                    ),
                    supportingText = {
                        if (viewModel.serverNameErrors && viewModel.showValidationError) {
                            Text("Server Name cannot be empty and the max length is 32", color = MaterialTheme.colorScheme.error)
                        }
                    }
                )
            }
            LaunchedEffect(Unit) {
                nameFocusRequester.requestFocus()
            }
        },
        confirmButton = {
            Button(
                enabled = !viewModel.serverNameErrors,
                modifier = Modifier.focusRequester(addFocusRequester),
                onClick = {
                    viewModel.showEditServerNameDialog =false
                    viewModel.updateServerName()
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(
                colors = ButtonDefaults.textButtonColors(),
                onClick = { viewModel.showEditServerNameDialog =false }) {
                Text("Cancel")
            }
        }
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun EditServerTagsDialog(viewModel: VirtualMakerViewModel) {
    val tagFocusRequester = remember { FocusRequester() }
    val addFocusRequester = remember { FocusRequester() }
    val formScrollState = rememberScrollState()

    AlertDialog(
        onDismissRequest = { viewModel.showEditServerTagsDialog=false },
        title = { Text("Add New Virtual MCP Server") },
        text = {
            Column(Modifier.verticalScroll(formScrollState)) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth().focusRequester(tagFocusRequester),
                    value = viewModel.serverTag,
                    onValueChange = { viewModel.onServerTagChange(it) },
                    label = { Text("Server Tags") },
                    placeholder = {Text("Input server tag, end with \",\"")},
                    singleLine = true,
                    isError = viewModel.serverTagErrors && viewModel.showValidationError,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Send
                    ),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            if(viewModel.serverTag.isEmpty())
                                addFocusRequester.requestFocus(FocusDirection.Next)
                            else
                                viewModel.onServerTagChange(viewModel.serverTag+",")
                        }
                    )
                )
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(align = Alignment.Top),
                    horizontalArrangement = Arrangement.Start
                ) {
                    viewModel.serverTags.forEach {
                        Chip(
                            modifier = Modifier.padding(horizontal = 2.dp)
                                .align(Alignment.CenterVertically),
                            colors = ChipDefaults.chipColors(backgroundColor = ButtonDefaults.buttonColors().containerColor),
                            onClick = { viewModel.removeServerTag(it)},
                            leadingIcon = {
                                Icon(
                                    painterResource(
                                        Res.drawable.cancel),
                                    contentDescription = "Delete tag",
                                    tint = ButtonDefaults.buttonColors().contentColor
                                )
                            }
                        ) {
                            Text(
                                it,
                                style = MaterialTheme.typography.bodySmall,
                                color = ButtonDefaults.buttonColors().contentColor
                            )
                        }
                    }
                }
            }
            LaunchedEffect(Unit) {
                tagFocusRequester.requestFocus()
            }
        },
        confirmButton = {
            Button(
                enabled = !viewModel.serverNameErrors,
                modifier = Modifier.focusRequester(addFocusRequester),
                onClick = {
                    viewModel.showEditServerTagsDialog = false
                    viewModel.updateServerTags()
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(
                colors = ButtonDefaults.textButtonColors(),
                onClick = { viewModel.showEditServerTagsDialog = false }) {
                Text("Cancel")
            }
        }
    )
}


@Composable
private fun ToolItem(tool: AIPortVirtualTool, viewModel: VirtualMakerViewModel) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(PaddingValues(start = 16.dp,end=8.dp))

    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row {
                Spacer(modifier = Modifier.width(8.dp))
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
        }
        TooltipIconButton(
            Res.drawable.info,
            tooltipText = "Tool details",
            onClick = {
                toolDetailViewModel.toolId = tool.toolId
                toolDetailViewModel.toolName = tool.name
                generalViewModel.currentScreen = Screen.ToolDetails
                generalViewModel.backToScreen = Screen.VirtualMCP
            }
        )
    }
}
