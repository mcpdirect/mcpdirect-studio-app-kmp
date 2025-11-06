package ai.mcpdirect.studio.app.mcpkey

import ai.mcpdirect.mcpdirectstudioapp.AppInfo
import ai.mcpdirect.mcpdirectstudioapp.getPlatform
import ai.mcpdirect.studio.app.Screen
import ai.mcpdirect.studio.app.UIState
import ai.mcpdirect.studio.app.compose.StudioCard
import ai.mcpdirect.studio.app.compose.TooltipIconButton
import ai.mcpdirect.studio.app.generalViewModel
import ai.mcpdirect.studio.app.model.account.AIPortAccessKey
import ai.mcpdirect.studio.app.model.account.AIPortAccessKeyCredential
import ai.mcpdirect.studio.app.tool.toolPermissionViewModel
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
private var mcpKey by mutableStateOf<AIPortAccessKey?>(null)
private var dialog by mutableStateOf<MCPKeyDialog>(MCPKeyDialog.None)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MCPAccessKeyScreen() {

    val viewModel = mcpAccessKeyViewModel
    LaunchedEffect(viewModel) {
        generalViewModel.refreshToolMakers()
        viewModel.refreshMCPAccessKeys()
    }

    if(viewModel.uiState== UIState.Loading) Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(48.dp),
            color = MaterialTheme.colorScheme.primary
        )
    } else if(viewModel.accessKeys.isEmpty()) Column(
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
        generalViewModel.topBarActions = {
            TextButton(onClick = {
                dialog = MCPKeyDialog.GenerateMCPKey
            }){
                Text("Generate MCP Key")
            }
        }
        StudioCard(modifier = Modifier.fillMaxHeight()) {
            LazyColumn {
                items(viewModel.accessKeys){
                    ListItem(
                        headlineContent = { Text(it.name) },
                        trailingContent = {
                            Row {
                                TooltipIconButton(
                                    Res.drawable.edit,
                                    contentDescription = "Edit MCP Key name",
                                    onClick = {
                                        mcpKey = it
                                        dialog = MCPKeyDialog.EditMCPKeyName
                                    })
                                TooltipIconButton(
                                    Res.drawable.visibility,
                                    contentDescription = "Display MCP Key",
                                    onClick = {
                                        mcpKey = it
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
                                        toolPermissionViewModel.accessKey = it
                                        generalViewModel.currentScreen(Screen.ToolPermission,
                                            "Tool Permissions for Key #${it.name}",
                                            Screen.MCPAccessKey)
                                    })
                            }
                        },
                        supportingContent = {
                            val summaries = viewModel.toolPermissionMakerSummary.filter {
                                    summary ->
                                summary.accessKeyId==it.id
                            }
                            if(summaries.isNotEmpty()){
                                FlowRow(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .wrapContentHeight(align = Alignment.Top),
                                    horizontalArrangement = Arrangement.Start
                                ) {
                                    summaries.forEach {
                                        val maker = generalViewModel.toolMaker(it.makerId)
                                        println("summary maker $maker")
                                        if(maker!=null)
                                            Box(
                                                Modifier.border(
                                                    width = 1.dp,
                                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                                                    shape = RoundedCornerShape(8.dp)
                                                ).padding(all = 4.dp)
                                            ) {
                                                Text(
                                                    "${maker.name}(${it.count})",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.primary,
                                                )
                                            }
                                        Spacer(Modifier.width(4.dp))
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }

    }
    when(dialog){
        MCPKeyDialog.None -> {}
        MCPKeyDialog.GenerateMCPKey -> {GenerateMCPKeyDialog()}
        MCPKeyDialog.DisplayMCPKey -> {
            if(mcpKey!=null) ShowMCPKeyDialog()
        }

        MCPKeyDialog.EditMCPKeyName -> {
            if(mcpKey!=null) EditMCPKeyNameDialog()
        }
    }
}

@Composable
fun MCPKeyNameErrors() {
    when(mcpAccessKeyViewModel.mcpKeyNameErrors){
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
fun GenerateMCPKeyDialog() {
    val viewModel = mcpAccessKeyViewModel
    val nameFocusRequester = remember { FocusRequester() }
    val addFocusRequester = remember { FocusRequester() }
    val formScrollState = rememberScrollState()

    AlertDialog(
        onDismissRequest = { dialog= MCPKeyDialog.None },
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
                        MCPKeyNameErrors()
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
                    dialog= MCPKeyDialog.None
                    viewModel.generateMCPKey()
                }
            ) {
                Text("Generate")
            }
        },
        dismissButton = {
            TextButton(
                onClick = { dialog= MCPKeyDialog.None }) {
                Text("Cancel")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowMCPKeyDialog() {
//    val viewModel = mcpAccessKeyViewModel
//    val key = viewModel.getMCPAccessKeyFromLocal(mcpKey!!.id)
//    mcpKey!!.secretKey = key?:""
    var key by remember { mutableStateOf<AIPortAccessKeyCredential?>(null) }
    mcpAccessKeyViewModel.getMCPAccessKeyCredential(mcpKey!!.id){
        key = it
    }
    AlertDialog(
        onDismissRequest = {
            mcpKey==null
            dialog= MCPKeyDialog.None},
        title = { Text("The MCP Key of ${mcpKey!!.name}") },
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
//                        var host: String? = getPlatform().getenv("AI_MCPDIRECT_GATEWAY_HOST")
//                        if (host == null) {
//                            host = "https://connect.mcpdirect.ai/"
//                        }
                        val keyName = it.name.trim().lowercase().replace(" ","_")
                        val secretKey = it.secretKey.substring(4)
                        val text ="""{"mcpServers":{"$keyName":{"url":"${AppInfo.MCPDIRECT_GATEWAY_ENDPOINT}$secretKey/sse"}}}""".trimIndent()

                        getPlatform().copyToClipboard(text)
                        mcpKey = null
                        dialog= MCPKeyDialog.DisplayMCPKey
                    }
                }
            ) {
                Text("Copy as MCP Server Config")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    mcpKey=null
                    dialog= MCPKeyDialog.None
                }) {
                Text("Cancel")
            }
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditMCPKeyNameDialog() {
    val viewModel = mcpAccessKeyViewModel
    val nameFocusRequester = remember { FocusRequester() }
    val addFocusRequester = remember { FocusRequester() }
    val formScrollState = rememberScrollState()

    AlertDialog(
        onDismissRequest = { dialog= MCPKeyDialog.EditMCPKeyName },
        title = { Text("Change MCP Key Name") },
        text = {
            Column(Modifier.verticalScroll(formScrollState)) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth().focusRequester(nameFocusRequester),
                    value = viewModel.mcpKeyName,
                    onValueChange = { viewModel.onMCPKeyNameChange(it) },
                    label = { Text("MCP Key of ${mcpKey!!.name}") },
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
                        MCPKeyNameErrors()
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
                    viewModel.setMCPKeyName(mcpKey!!)
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(
                onClick = { dialog= MCPKeyDialog.None}
            ) {
                Text("Cancel")
            }
        }
    )
}