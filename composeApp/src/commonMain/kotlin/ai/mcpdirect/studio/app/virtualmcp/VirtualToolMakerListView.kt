package ai.mcpdirect.studio.app.virtualmcp

import ai.mcpdirect.studio.app.Screen
import ai.mcpdirect.studio.app.compose.StudioListItem
import ai.mcpdirect.studio.app.compose.Tag
import ai.mcpdirect.studio.app.compose.TooltipIconButton
import ai.mcpdirect.studio.app.generalViewModel
import ai.mcpdirect.studio.app.mcp.EditMCPServerNameDialog
import ai.mcpdirect.studio.app.mcp.EditMCPServerTagsDialog
import ai.mcpdirect.studio.app.model.aitool.AIPortToolMaker
import ai.mcpdirect.studio.app.model.aitool.AIPortToolMaker.Companion.STATUS_OFF
import ai.mcpdirect.studio.app.model.aitool.AIPortVirtualTool
import ai.mcpdirect.studio.app.model.repository.ToolRepository
import ai.mcpdirect.studio.app.model.repository.UserRepository
import ai.mcpdirect.studio.app.tool.ToolDetailsView
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import mcpdirectstudioapp.composeapp.generated.resources.Res
import mcpdirectstudioapp.composeapp.generated.resources.arrow_back
import mcpdirectstudioapp.composeapp.generated.resources.badge
import mcpdirectstudioapp.composeapp.generated.resources.block
import mcpdirectstudioapp.composeapp.generated.resources.cancel
import mcpdirectstudioapp.composeapp.generated.resources.check
import mcpdirectstudioapp.composeapp.generated.resources.delete
import mcpdirectstudioapp.composeapp.generated.resources.draft
import mcpdirectstudioapp.composeapp.generated.resources.info
import mcpdirectstudioapp.composeapp.generated.resources.play_circle
import mcpdirectstudioapp.composeapp.generated.resources.restart_alt
import mcpdirectstudioapp.composeapp.generated.resources.sell
import mcpdirectstudioapp.composeapp.generated.resources.settings
import mcpdirectstudioapp.composeapp.generated.resources.stop_circle
import org.jetbrains.compose.resources.painterResource

@Composable
fun VirtualToolMakerListView() {
    val viewModel = virtualMakerViewModel
//    val myAccountId = authViewModel.user.id
    LaunchedEffect(viewModel) {
        viewModel.queryToolMakers()
        generalViewModel.topBarActions = {
            TextButton(onClick = { viewModel.showAddServerDialog = true }) {
                Text("Create Virtual MCP Server")
            }
        }
    }
    DisposableEffect(null){
        onDispose {
            generalViewModel.topBarActions = {}
        }
    }
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
        LazyColumn{
            items(viewModel.virtualMakers.filter {
                UserRepository.me(it.userId)
            }) { maker ->
                StudioListItem(
                    modifier = Modifier.fillMaxWidth().clickable(onClick = {
                        viewModel.selectVirtualMaker(maker)
                        viewModel.queryVirtualMakerTools()
                    }),
                    headlineContent = {
                        Text(maker.name)
                    },
                    supportingContent = {
                        Text("Tags: ${maker.tags}")
                    },
                    trailingContent = {
                        if (maker.status == STATUS_OFF) Icon(painter = painterResource(Res.drawable.block),
                            contentDescription = "Click to enable",
                            tint = Color.Red)
                        else Icon(painter = painterResource(Res.drawable.check),
                            contentDescription = "Click to disable",
                            tint = Color(0xFF63A002))
                    }
                )
            }
        }
    }

    if (viewModel.showAddServerDialog) {
        AddVirtualMCPServerDialog(viewModel)
    }
//    else if(viewModel.showEditServerNameDialog){
//        EditVirtualMCPServerNameDialog(viewModel)
//    }
//    else if(viewModel.showEditServerTagsDialog){
//        EditVirtualMCPServerTagsDialog(viewModel)
//    }

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
}

@Composable
fun VirtualToolMakerDetailView() {
    var showEditServerNameDialog by remember { mutableStateOf(false) }
    var showEditServerTagsDialog by remember { mutableStateOf(false) }
    var tool by remember { mutableStateOf<AIPortVirtualTool?>(null) }
    val viewModel = virtualMakerViewModel
    viewModel.selectedVirtualMaker?.let {
        maker ->
        tool?.let {
            ToolDetailsView(it.toolId){
                tool = null
            }
        }?:Column{
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
                    text = maker.name,
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
//            Spacer(modifier = Modifier.weight(1.0f))
                TooltipIconButton(
                    Res.drawable.settings,
                    contentDescription = "Config tools",
                    onClick = {
                        generalViewModel.currentScreen(
                            Screen.VirtualMCPToolConfig,
                            "Tool Config for ${maker.name}",
                            Screen.MCPTools
                        )
                    }
                )
                TooltipIconButton(
                    Res.drawable.restart_alt,
                    contentDescription = "Refresh",
                    onClick = { }
                )
                TooltipIconButton(
                    Res.drawable.sell,
                    contentDescription = "Edit tags",
                    onClick = {
                        viewModel.onServerTagChange(maker.tags ?: "")
                        viewModel.showEditServerTagsDialog = true
                    })
                TooltipIconButton(
                    Res.drawable.badge,
                    contentDescription = "Edit name",
                    onClick = {
//                        viewModel.onServerNameChange(maker.name)
//                        viewModel.showEditServerNameDialog = true
                        showEditServerNameDialog = true
                    })
                Spacer(
                    Modifier.border(
                        1.dp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                        shape = RectangleShape
                    )
                )
                if (maker.status == STATUS_OFF) TooltipIconButton(
                    Res.drawable.play_circle,
                    contentDescription = "Enable",
                    tint = MaterialTheme.colorScheme.primary,
                    onClick = { viewModel.setServerStatus(maker.id, 1) })
                else TooltipIconButton(
                    Res.drawable.stop_circle,
                    contentDescription = "Disable",
                    tint = MaterialTheme.colorScheme.error,
                    onClick = { viewModel.setServerStatus(maker.id, 0) })
                TooltipIconButton(
                    Res.drawable.delete,
                    contentDescription = "Deprecate",
                    onClick = { viewModel.setServerStatus(maker.id, -1) })

            }
            HorizontalDivider()
            LazyColumn {
                items(viewModel.selectedVirtualMakerTools) {
                    var maker by remember { mutableStateOf<AIPortToolMaker?>(null) }
                    LaunchedEffect(null){
                        ToolRepository.tool(it.toolId){
                            if(it.successful()) it.data?.let {
                                maker = ToolRepository.toolMaker(it.makerId)
                            }
                        }
                    }
                    ListItem(
                        modifier = Modifier.clickable {
                            tool = it
                        },
                        headlineContent = {Text(it.name) },
                        supportingContent = {
                            maker?.let {
                                Text(it.name)
                            }
                        }
                    )
//                    VirtualToolItem(it){
//                        tool = it
//                    }
                }
            }
        }

        if(showEditServerNameDialog){
            EditMCPServerNameDialog(
                maker,
                onDismissRequest = {
                    showEditServerNameDialog = false
                },
                onConfirmRequest = {
                  toolMaker,toolMakerName ->
//                    viewModel.onServerNameChange(serverName)
                    viewModel.updateServerName(toolMaker,toolMakerName)
                },
            )
        }else if(showEditServerTagsDialog){
            EditMCPServerTagsDialog(
                maker,
                onDismissRequest = {
                    showEditServerTagsDialog = false
                },
                onConfirmRequest = {
                    toolMaker,toolMakerTags ->
                    viewModel.updateServerTags(toolMaker,toolMakerTags)
                }
            )
        }
    }
}


@Composable
private fun VirtualToolItem(
    tool: AIPortVirtualTool,
    onClick:()->Unit,
) {
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
                if (tool.status == 0) Tag(
                    "inactive",
                    toggleColor = MaterialTheme.colorScheme.error,
                )
                else Tag(
                    "active",
                    toggleColor = MaterialTheme.colorScheme.primary,
                )
            }
        }
        TooltipIconButton(
            Res.drawable.info,
            contentDescription = "Tool details",
//            onClick = {
//                toolDetailViewModel.toolId = tool.toolId
//                toolDetailViewModel.toolName = tool.name
//                generalViewModel.currentScreen(Screen.ToolDetails,
//                    "Tool Details of ${tool.name}",Screen.MCPTools)
//            }
            onClick = onClick
        )
    }
}

@Composable
fun AddVirtualMCPServerDialog(viewModel: VirtualMakerViewModel) {
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
                        AssistChip(
                            modifier = Modifier.padding(horizontal = 2.dp)
                                .align(Alignment.CenterVertically),
                            colors = AssistChipDefaults.assistChipColors(containerColor = ButtonDefaults.buttonColors().containerColor),
                            onClick = { viewModel.removeServerTag(it)},
                            leadingIcon = {
                                Icon(
                                    painterResource(
                                        Res.drawable.cancel),
                                    contentDescription = "Delete tag",
                                    tint = ButtonDefaults.buttonColors().contentColor
                                )
                            },
                            label = {
                                Text(
                                    it,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = ButtonDefaults.buttonColors().contentColor
                                )
                            }
                        )
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