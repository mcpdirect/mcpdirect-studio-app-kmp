package ai.mcpdirect.studio.app.mcpkey

import ai.mcpdirect.mcpdirectstudioapp.AppInfo
import ai.mcpdirect.mcpdirectstudioapp.getPlatform
import ai.mcpdirect.studio.app.Screen
import ai.mcpdirect.studio.app.compose.StudioBoard
import ai.mcpdirect.studio.app.compose.StudioCard
import ai.mcpdirect.studio.app.compose.TooltipIconButton
import ai.mcpdirect.studio.app.generalViewModel
import ai.mcpdirect.studio.app.model.aitool.AIPortToolAccessKeyCredential
import ai.mcpdirect.studio.app.model.aitool.AIPortToolPermission
import ai.mcpdirect.studio.app.model.aitool.AIPortVirtualToolPermission
import ai.mcpdirect.studio.app.tool.ToolDetailsView
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import mcpdirectstudioapp.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource

sealed class MCPKeyDialog() {
    object None : MCPKeyDialog()
    object GenerateMCPKey : MCPKeyDialog()
    object DisplayMCPKey : MCPKeyDialog()
    object EditMCPKeyName : MCPKeyDialog()
}
sealed class MCPKeyNameError() {
    object None : MCPKeyNameError()
    object Invalid : MCPKeyNameError()
    object Duplicate : MCPKeyNameError()
}
//private var mcpKey by mutableStateOf<AIPortAccessKey?>(null)
//private var dialog by mutableStateOf<MCPKeyDialog>(MCPKeyDialog.None)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MCPAccessKeyScreen(
    dialog: MCPKeyDialog
) {
//    val viewModel = mcpAccessKeyViewModel
    val viewModel by remember { mutableStateOf(MCPAccessKeyViewModel()) }
    val accessKeys by viewModel.accessKeys.collectAsState()
    var dialog by remember { mutableStateOf(dialog) }
    LaunchedEffect(viewModel) {
        viewModel.refreshMCPAccessKeys()
    }

    if(accessKeys.isEmpty()) Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            dialog = MCPKeyDialog.GenerateMCPKey
        }){
            Text("Generate your first MCP Key")
        }
    } else {
        LaunchedEffect(null) {
            generalViewModel.topBarActions = {
                TextButton(onClick = {
                    dialog = MCPKeyDialog.GenerateMCPKey
                }) {
                    Text("Generate MCP Key")
                }
            }
        }
        DisposableEffect(null){
            onDispose {
                generalViewModel.topBarActions = {}
            }
        }
        Row(Modifier.fillMaxSize().padding(8.dp)){
            LazyColumn(Modifier.weight(1.0f).padding(vertical = 8.dp)) {
                items(accessKeys){
                    ListItem(
                        modifier = Modifier.clickable(
                            onClick = {viewModel.mcpKey(it)}
                        ),
                        headlineContent = { Text(it.name) },
                        trailingContent = {
                            if (it.status == 0) Icon(painter = painterResource(Res.drawable.block),
                                contentDescription = "",
                                tint = Color.Red)
//                            Row {
//                                TooltipIconButton(
//                                    Res.drawable.edit,
//                                    contentDescription = "Edit MCP Key name",
//                                    onClick = {
//                                        viewModel.mcpKey = it
//                                        dialog = MCPKeyDialog.EditMCPKeyName
//                                    })
//                                TooltipIconButton(
//                                    Res.drawable.visibility,
//                                    contentDescription = "Display MCP Key",
//                                    onClick = {
//                                        viewModel.mcpKey = it
//                                        dialog = MCPKeyDialog.DisplayMCPKey
//                                    })
//                                if (it.status == 0) IconButton(
//                                    onClick = { viewModel.setMCPKeyStatus(it,1) }) {
//                                    Icon(painter = painterResource(Res.drawable.block),
//                                        contentDescription = "Enable MCP Key",
//                                        tint = Color.Red)
//                                } else IconButton(
//                                    onClick = { viewModel.setMCPKeyStatus(it,0) }) {
//                                    Icon(painter = painterResource(Res.drawable.check),
//                                        contentDescription = "Disable MCP Key",
//                                        tint = Color(0xFF63A002))
//                                }
//                                TooltipIconButton(
//                                    Res.drawable.shield_toggle,
//                                    contentDescription = "Edit Tool Permissions",
//                                    onClick = {
////                                    toolPermissionViewModel.accessKey = it
//                                        generalViewModel.currentScreen(Screen.ToolPermission(it),
//                                            "Tool Permissions for Key #${it.name}",
//                                            Screen.MCPAccessKey())
//                                    })
//                            }
                        },
//                    supportingContent = {
//                        val summaries = viewModel.toolPermissionMakerSummary.filter {
//                                summary ->
//                            summary.accessKeyId==it.id
//                        }
//                        if(summaries.isNotEmpty()){
//                            FlowRow(
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .wrapContentHeight(align = Alignment.Top),
//                                horizontalArrangement = Arrangement.Start
//                            ) {
//                                summaries.forEach {
//                                    val maker = generalViewModel.toolMaker(it.makerId)
//                                    println("summary maker $maker")
//                                    if(maker!=null)
//                                        Box(
//                                            Modifier.border(
//                                                width = 1.dp,
//                                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
//                                                shape = RoundedCornerShape(8.dp)
//                                            ).padding(all = 4.dp)
//                                        ) {
//                                            Text(
//                                                "${maker.name}(${it.count})",
//                                                style = MaterialTheme.typography.bodySmall,
//                                                color = MaterialTheme.colorScheme.primary,
//                                            )
//                                        }
//                                    Spacer(Modifier.width(4.dp))
//                                }
//                            }
//                        }
//                    }
                    )
                }
            }
            OutlinedCard(Modifier.fillMaxHeight().weight(2.0f)) {
                var toolPermission by remember { mutableStateOf<AIPortToolPermission?>(null) }
                toolPermission?.let {
                    if(it is AIPortVirtualToolPermission){
                        ToolDetailsView(it.originalToolId, Modifier.fillMaxSize()){
                            toolPermission=null
                        }
                    }else{
                        ToolDetailsView(it.toolId, Modifier.fillMaxSize()){
                            toolPermission=null
                        }
                    }
                }?: viewModel.mcpKey?.let{
                    Row(Modifier.fillMaxWidth()) {
                        Spacer(Modifier.weight(1.0f))
                        TooltipIconButton(
                            Res.drawable.edit,
                            contentDescription = "Edit MCP Key name",
                            onClick = {
//                                viewModel.mcpKey = it
                                dialog = MCPKeyDialog.EditMCPKeyName
                            })
                        TooltipIconButton(
                            Res.drawable.visibility,
                            contentDescription = "Display MCP Key",
                            onClick = {
//                                viewModel.mcpKey = it
                                dialog = MCPKeyDialog.DisplayMCPKey
                            })
                        if (it.status == 0) IconButton(
                            onClick = { viewModel.setMCPKeyStatus(it,1) }) {
                            Icon(painter = painterResource(Res.drawable.block),
                                contentDescription = "Enable MCP Key",
                                tint = Color.Red)
                        } else IconButton(
                            onClick = { viewModel.setMCPKeyStatus(it,0) }) {
                            Icon(painter = painterResource(Res.drawable.check),
                                contentDescription = "Disable MCP Key",
                                tint = Color(0xFF63A002))
                        }
                        TooltipIconButton(
                            Res.drawable.shield_toggle,
                            contentDescription = "Edit Tool Permissions",
                            onClick = {
//                                    toolPermissionViewModel.accessKey = it
                                generalViewModel.currentScreen(Screen.ToolPermission(it),
                                    "Tool Permissions for Key #${it.name}",
                                    Screen.MCPAccessKey())
                            })
                    }
                    HorizontalDivider()
                    LazyColumn {
                        items(viewModel.toolPermissions){
                            ListItem(
                                modifier = Modifier.clickable(
                                    onClick = {toolPermission=it}
                                ),
                                headlineContent = { Text(it.name) },
                                supportingContent = {
                                    if(it is AIPortVirtualToolPermission){
                                        Text("Virtual MCP")
                                    }
                                },
                                trailingContent = {
                                    Icon(painter = painterResource(Res.drawable.info),
                                        contentDescription = "Tool Details")
                                }
                            )
                        }
                    }
                }?:StudioBoard(Modifier.weight(2.0f)) {
                    Text("Select a MCPdirect Key to view")
                }
            }

        }
    }
    when(dialog){
        MCPKeyDialog.None -> {}
        MCPKeyDialog.GenerateMCPKey -> {GenerateMCPKeyDialog(
            viewModel
        ){ dialog= MCPKeyDialog.None }}
        MCPKeyDialog.DisplayMCPKey -> {
            if(viewModel.mcpKey!=null) ShowMCPKeyDialog(
                viewModel
            ){
//                viewModel.mcpKey==null
                dialog= MCPKeyDialog.None
            }
        }

        MCPKeyDialog.EditMCPKeyName -> {
            if(viewModel.mcpKey!=null) EditMCPKeyNameDialog(
                viewModel
            ){ dialog= MCPKeyDialog.None }
        }
    }
}

@Composable
fun MCPKeyNameErrors(error:MCPKeyNameError) {
    when(error){
        MCPKeyNameError.None -> {Text("")}
        MCPKeyNameError.Invalid -> {
            Text("MCP Key Name cannot be empty and the max length is 32")
        }
        MCPKeyNameError.Duplicate -> {
            Text("MCP Key Name duplicated")
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenerateMCPKeyDialog(
    viewModel: MCPAccessKeyViewModel,
    onDismissRequest: () -> Unit,
) {
//    val viewModel = mcpAccessKeyViewModel
    val nameFocusRequester = remember { FocusRequester() }
    val addFocusRequester = remember { FocusRequester() }
    val formScrollState = rememberScrollState()

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Generate MCP Key") },
        text = {
            Column(Modifier.verticalScroll(formScrollState)) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth().focusRequester(nameFocusRequester),
                    value = viewModel.mcpKeyName,
                    onValueChange = { viewModel.onMCPKeyNameChange(it) },
                    label = { Text("MCP Key Name") },
                    singleLine = true,
                    isError = viewModel.mcpKeyNameErrors!= MCPKeyNameError.None,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Send
                    ),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            addFocusRequester.requestFocus(FocusDirection.Next)
                        }
                    ),
                    supportingText = {
                        MCPKeyNameErrors(viewModel.mcpKeyNameErrors)
                    }
                )
            }
            LaunchedEffect(Unit) {
                nameFocusRequester.requestFocus()
            }
        },
        confirmButton = {
            Button(
                enabled = viewModel.mcpKeyNameErrors== MCPKeyNameError.None,
                modifier = Modifier.focusRequester(addFocusRequester),
                onClick = {
                    onDismissRequest()
                    viewModel.generateMCPKey()
                }
            ) {
                Text("Generate")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest) {
                Text("Cancel")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowMCPKeyDialog(
    viewModel: MCPAccessKeyViewModel,
    onDismissRequest: () -> Unit,
) {
    var key by remember { mutableStateOf<AIPortToolAccessKeyCredential?>(null) }

    viewModel.getMCPAccessKeyCredential(viewModel.mcpKey!!){
        key = it
    }
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("The MCP Key of ${viewModel.mcpKey!!.name}") },
        text = {
            StudioCard(
                modifier = Modifier.fillMaxWidth(),
            ){
                SelectionContainer {
                    Text(text = key?.secretKey ?: "",
                        modifier = Modifier.padding(24.dp),
                        style = MaterialTheme.typography.bodyMedium)
                }
            }
        },
        confirmButton = {
            Button(
                enabled = key!=null,
                onClick = {
                    key?.let {
                        val keyName = it.name.trim().lowercase().replace(" ","_")
                        val secretKey = it.secretKey.substring(4)
                        val text ="""{"mcpServers":{"$keyName":{"url":"${AppInfo.MCPDIRECT_GATEWAY_ENDPOINT}$secretKey/sse"}}}""".trimIndent()

                        getPlatform().copyToClipboard(text)
                        onDismissRequest()
                    }
                }
            ) {
                Text("Copy as MCP Server Config")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest) {
                Text("Cancel")
            }
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditMCPKeyNameDialog(
    viewModel: MCPAccessKeyViewModel,
    onDismissRequest: () -> Unit,
) {
//    val viewModel = mcpAccessKeyViewModel
    val nameFocusRequester = remember { FocusRequester() }
    val addFocusRequester = remember { FocusRequester() }
    val formScrollState = rememberScrollState()

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Change MCP Key Name") },
        text = {
            Column(Modifier.verticalScroll(formScrollState)) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth().focusRequester(nameFocusRequester),
                    value = viewModel.mcpKeyName,
                    onValueChange = { viewModel.onMCPKeyNameChange(it) },
                    label = { Text("MCP Key of ${viewModel.mcpKey!!.name}") },
                    singleLine = true,
                    isError = viewModel.mcpKeyNameErrors!= MCPKeyNameError.None,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Send
                    ),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            addFocusRequester.requestFocus(FocusDirection.Next)
                        }
                    ),
                    supportingText = {
                        MCPKeyNameErrors(viewModel.mcpKeyNameErrors)
                    }
                )
            }
            LaunchedEffect(Unit) {
                nameFocusRequester.requestFocus()
            }
        },
        confirmButton = {
            Button(
                enabled = viewModel.mcpKeyNameErrors== MCPKeyNameError.None,
                modifier = Modifier.focusRequester(addFocusRequester),
                onClick = {
                    onDismissRequest()
                    viewModel.setMCPKeyName(viewModel.mcpKey!!)
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text("Cancel")
            }
        }
    )
}