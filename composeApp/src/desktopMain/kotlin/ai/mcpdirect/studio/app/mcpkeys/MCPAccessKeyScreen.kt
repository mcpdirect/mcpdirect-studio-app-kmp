package ai.mcpdirect.studio.app.mcpkeys

import ai.mcpdirect.backend.dao.entity.account.AIPortAccessKeyCredential
import ai.mcpdirect.studio.app.compose.StudioCard
import ai.mcpdirect.studio.app.compose.TooltipIconButton
import ai.mcpdirect.studio.app.generalViewModel
import ai.mcpdirect.studio.app.mcpAccessKeyViewModel
import ai.mcpdirect.studio.app.viewmodel.MCPKeyDialog
import ai.mcpdirect.studio.app.viewmodel.MCPKeyNameError
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.IconButton
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
import org.jetbrains.compose.resources.stringResource

var mcpKey by mutableStateOf<AIPortAccessKeyCredential?>(null)
var dialog by mutableStateOf<MCPKeyDialog>(MCPKeyDialog.None)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun MCPAccessKeyScreen(onToolPermissionConfigClick: (key: AIPortAccessKeyCredential) -> Unit) {
    val viewModel = mcpAccessKeyViewModel
//    LaunchedEffect(viewModel) {
        viewModel.refreshMCPAccessKeys()
//    }
    Scaffold(
        snackbarHost = { SnackbarHost(generalViewModel.snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.mcp_keys)) },
                actions = {
                    if(viewModel.accessKeys.isNotEmpty())
                        Button(onClick = {
                            dialog = MCPKeyDialog.GenerateMCPKey
                        }){
                            Text("Generate MCP Key")
                        }
                }
            )
        }) { paddingValues ->
        when(dialog){
            MCPKeyDialog.None -> {}
            MCPKeyDialog.GenerateMCPKey -> {GenerateMCPKeyDialog(onToolPermissionConfigClick)}
            MCPKeyDialog.DisplayMCPKey -> {
                if(mcpKey!=null) ShowMCPKeyDialog()
            }

            MCPKeyDialog.EditMCPKeyName -> {
                if(mcpKey!=null) EditMCPKeyNameDialog()
            }
        }

        if(viewModel.loadding){
            Column(
                Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(48.dp),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }else if(viewModel.accessKeys.isEmpty())
            Column(
                Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(onClick = {
                    dialog = MCPKeyDialog.GenerateMCPKey
                }){
                    Text("Generate your first MCP Key")
                }
            }
        else {
            Box(Modifier.fillMaxSize().padding(paddingValues)) {
                StudioCard(modifier = Modifier.fillMaxHeight().padding(8.dp)) {
                    LazyColumn {
                        viewModel.accessKeys.forEach {
                            item {
                                ListItem(
                                    headlineContent = { Text(it.name) },
                                    trailingContent = {
                                        Row {
                                            TooltipIconButton(
                                                Res.drawable.edit,
                                                tooltipText = "Edit MCP Key name",
                                                onClick = {
                                                    mcpKey = it
                                                    dialog = MCPKeyDialog.EditMCPKeyName
                                                })
                                            TooltipIconButton(
                                                Res.drawable.visibility,
                                                tooltipText = "Display MCP Key",
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
                                                tooltipText = "Edit Tool Permissions",
                                                onClick = {
                                                onToolPermissionConfigClick(it)
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
                                                                color = androidx.compose.material.MaterialTheme.colors.onSurface.copy(alpha = 0.12f),
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
            }
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
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun GenerateMCPKeyDialog(onToolPermissionConfigClick: (key: AIPortAccessKeyCredential) -> Unit) {
    val viewModel = mcpAccessKeyViewModel
    val nameFocusRequester = remember { FocusRequester() }
    val addFocusRequester = remember { FocusRequester() }
    val formScrollState = rememberScrollState()

    AlertDialog(
        onDismissRequest = { dialog= MCPKeyDialog.GenerateMCPKey },
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
                    dialog= MCPKeyDialog.GenerateMCPKey
                    viewModel.generateMCPKey(onToolPermissionConfigClick)
                }
            ) {
                Text("Generate")
            }
        },
        dismissButton = {
            TextButton(
                onClick = { dialog= MCPKeyDialog.GenerateMCPKey }) {
                Text("Cancel")
            }
        }
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ShowMCPKeyDialog() {
    val viewModel = mcpAccessKeyViewModel
    val key = viewModel.getMCPAccessKeyFromLocal(mcpKey!!.id)
    mcpKey!!.secretKey = key
    AlertDialog(
        onDismissRequest = {
            mcpKey==null
            dialog= MCPKeyDialog.DisplayMCPKey},
        title = { Text("The MCP Key of ${mcpKey!!.name}") },
        text = {
            StudioCard(
                modifier = Modifier.fillMaxWidth(),
            ){

                Text(text = key ?: "Key not found in local!",
                    modifier = Modifier.padding(24.dp),
                    style = MaterialTheme.typography.bodyMedium)
            }
        },
        confirmButton = {
            Button(
                enabled = key!=null,
                onClick = {
                    generalViewModel.copyToClipboard(mcpKey!!)
                    mcpKey = null
                    dialog= MCPKeyDialog.DisplayMCPKey
                }
            ) {
                Text("Copy as MCP Server Config")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    mcpKey=null
                    dialog= MCPKeyDialog.DisplayMCPKey
                }) {
                Text("Cancel")
            }
        }
    )
}


@OptIn(ExperimentalMaterialApi::class)
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
                onClick = { dialog= MCPKeyDialog.None}) {
                Text("Cancel")
            }
        }
    )
}