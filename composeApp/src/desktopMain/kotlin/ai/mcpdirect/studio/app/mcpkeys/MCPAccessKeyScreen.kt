package ai.mcpdirect.studio.app.mcpkeys

import ai.mcpdirect.backend.dao.entity.account.AIPortAccessKeyCredential
import ai.mcpdirect.studio.app.compose.StudioCard
import ai.mcpdirect.studio.app.generalViewModel
import ai.mcpdirect.studio.app.mcpAccessKeyViewModel
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import mcpdirectstudioapp.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun MCPAccessKeyScreen(onToolPermissionConfigClick: (key: AIPortAccessKeyCredential) -> Unit) {
    val viewModel = mcpAccessKeyViewModel
    LaunchedEffect(viewModel) {
        viewModel.refreshMCPAccessKeys()
    }
    Scaffold(
        snackbarHost = { SnackbarHost(generalViewModel.snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.mcp_keys)) },
                actions = {
                    if(viewModel.accessKeys.isNotEmpty())
                        Button(onClick = {
                            viewModel.showGenerateMCPKeyDialog = true
                        }){
                            Text("Generate MCP Key")
                        }
                }
            )
        }) { paddingValues ->
        if(viewModel.showGenerateMCPKeyDialog){
            GenerateMCPKeyDialog(onToolPermissionConfigClick)
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
                    viewModel.showGenerateMCPKeyDialog = true
                }){
                    Text("Generate your first MCP Key")
                }
            }
        else {
            Column(Modifier.padding(paddingValues)) {
                StudioCard(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                    LazyColumn {
                        viewModel.accessKeys.forEach {
                            item {
                                ListItem(
                                    headlineContent = { Text(it.name) },
                                    trailingContent = {
                                        IconButton(onClick = {
                                            onToolPermissionConfigClick(it)
                                        }) {
                                            Icon(painterResource(Res.drawable.shield_toggle),
                                                contentDescription = "Edit Tool Permissions")
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
//    LaunchedEffect(viewModel) {
//        viewModel.refreshMCPAccessKeys()
//    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun GenerateMCPKeyDialog(onToolPermissionConfigClick: (key: AIPortAccessKeyCredential) -> Unit) {
    val viewModel = mcpAccessKeyViewModel
    val nameFocusRequester = remember { FocusRequester() }
    val addFocusRequester = remember { FocusRequester() }
    val formScrollState = rememberScrollState()

    AlertDialog(
        onDismissRequest = { viewModel.showGenerateMCPKeyDialog=false },
        title = { Text("Generate MCP Key") },
        text = {
            Column(Modifier.verticalScroll(formScrollState)) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth().focusRequester(nameFocusRequester),
                    value = viewModel.mcpKeyName,
                    onValueChange = { viewModel.onMCPKeyNameChange(it) },
                    label = { Text("MCP Key Name") },
                    singleLine = true,
                    isError = viewModel.mcpKeyNameErrors,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Send
                    ),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            addFocusRequester.requestFocus(FocusDirection.Next)
                        }
                    ),
                    supportingText = {
                        if (viewModel.mcpKeyNameErrors) {
                            Text("MCP Key Name cannot be empty and the max length is 32")
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
                enabled = !viewModel.mcpKeyNameErrors,
                modifier = Modifier.focusRequester(addFocusRequester),
                onClick = {
                    viewModel.generateMCPKey(onToolPermissionConfigClick)
                }
            ) {
                Text("Generate")
            }
        },
        dismissButton = {
            TextButton(
                onClick = { viewModel.showGenerateMCPKeyDialog =false }) {
                Text("Cancel")
            }
        }
    )
}