//package ai.mcpdirect.studio.app
//
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.rememberScrollState
//import androidx.compose.foundation.verticalScroll
//import androidx.compose.material.Card
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.*
//import androidx.compose.material.icons.automirrored.filled.List
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.unit.dp
//
//import ai.mcpdirect.studio.app.mcp.MCPServerIntegrationViewModel
//import ai.mcpdirect.studio.dao.entity.MCPServer
//import ai.mcpdirect.studio.tool.AITool
//
//@Composable
//fun MCPServerIntegrationScreen() {
//    val viewModel = remember { MCPServerIntegrationViewModel() }
//    val serverDetailsScrollState = rememberScrollState()
//    val serverListScrollState = rememberScrollState()
//
//    Column(modifier = Modifier.fillMaxSize()) {
//        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
//            Text("MCP Servers", style = MaterialTheme.typography.headlineMedium)
//            Button(onClick = { viewModel.showAddServerDialog = true }) {
//                Text("Add Server")
//            }
//        }
//        if (viewModel.selectedServer == null) {
//            // Server List View
//            Column(modifier = Modifier.fillMaxSize().verticalScroll(serverListScrollState)) {
//                if (viewModel.servers.isNotEmpty()) {
//                    viewModel.servers.forEach { server ->
//                        ServerListItem(server) { serverName ->
//                            viewModel.selectServer(serverName)
//                        }
//                    }
//                } else {
//                    Text("No Server available.", modifier = Modifier.padding(16.dp))
//                }
//            }
//        } else {
//            // Server Details View
//            Column(modifier = Modifier.fillMaxSize().verticalScroll(serverDetailsScrollState)) {
//                viewModel.selectedServer?.let { server ->
//                    ServerDetailsCard(server, viewModel) { viewModel.selectServer(null) }
//                    Spacer(modifier = Modifier.height(16.dp))
//                    ToolListCard(server.tools)
//                }
//            }
//        }
//    }
//
//    if (viewModel.showAddServerDialog) {
//        AddServerDialog(viewModel)
//    }
//
//    // Error Dialog
//    if (viewModel.errorMessage != null) {
//        AlertDialog(
//            onDismissRequest = { viewModel.errorMessage = null },
//            title = { Text("Error") },
//            text = { Text(viewModel.errorMessage!!) },
//            confirmButton = {
//                Button(onClick = { viewModel.errorMessage = null }) {
//                    Text("OK")
//                }
//            }
//        )
//    }
//}
//
//@Composable
//fun ServerListItem(server: MCPServer, onServerSelected: (String) -> Unit) {
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(8.dp)
//            .clickable { onServerSelected(server.name) },
//        elevation = 4.dp
//    ) {
//        Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
//            Column {
//                Text(server.name, style = MaterialTheme.typography.labelLarge)
//                if(server.command!=null)
//                    Text("Command: ${server.command} ${server.args.joinToString(" ")}", style = MaterialTheme.typography.bodySmall)
//                else
//                    Text("URL: ${server.url}", style = MaterialTheme.typography.bodySmall)
//            }
//            Icon(
//                imageVector = when (server.status) {
//                    1 -> Icons.Default.CheckCircle
//                    -1-> Icons.Default.Error
//                    0 -> Icons.Default.HourglassEmpty
//                    else -> Icons.Default.DeviceUnknown
//                },
//                contentDescription = "Server Status",
//                tint = when (server.status) {
//                    1 -> Color.Green
//                    -1-> Color.Red
//                    0 -> Color.Blue
//                    else -> { Color.Gray}
//                }
//            )
//        }
//    }
//}
//
//@Composable
//fun ServerDetailsCard(server: MCPServer, viewModel: MCPServerIntegrationViewModel, onBack: () -> Unit) {
//    var published by remember { mutableStateOf(server.id>0) }
//    Text(server.name,modifier = Modifier.padding(all = 16.dp),style = MaterialTheme.typography.headlineSmall)
//    Card(
//        modifier = Modifier.fillMaxWidth().padding(8.dp),
//        elevation = 4.dp
//    ) {
//        Column(modifier = Modifier.padding(16.dp)) {
//
//            Spacer(modifier = Modifier.height(8.dp))
////            Text("Name: ${server.name}")
//            if(server.command!=null&&server.command.isNotEmpty()){
//                Text("Command: ${server.command}")
//                if(server.args!=null&&server.args.isNotEmpty())
//                    Text("Arguments: ${server.args.joinToString(" ")}")
//            }else{
//                Text("URL: ${server.url}")
//            }
//            Text("Status: ${server.status}")
//            if (server.env!=null&&server.env.isNotEmpty()) {
//                Text("Environment Variables:")
//                server.env.forEach { Text("- $it") }
//            }
//            Spacer(modifier = Modifier.height(16.dp))
//            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
//                Button(onClick = onBack) {
//                    Text("Back")
//                }
//                Row {
//                    if(!published||server.updatable)
//                        Button(onClick = { viewModel.publishServer(server,{published=true}) }) {
//                            Text(when(server.updatable){
//                                true -> "Publish"
//                                else -> "Publish New Tools"
//                            })
//                    }
//                    if (published) { // Assuming 0L means not yet published/discontinued
//                        Button(onClick = { viewModel.discontinueServer(server.name) }) {
//                            Text("Discontinue")
//                        }
//                    }
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun ToolListCard(tools: Collection<AITool?>?) {
//    Spacer(modifier = Modifier.height(16.dp))
//    Text("Available Tools",modifier = Modifier.padding(all = 16.dp), style = MaterialTheme.typography.headlineSmall)
//    Column(modifier = Modifier.padding(8.dp)) {
//        Spacer(modifier = Modifier.height(8.dp))
//        if (tools?.isNotEmpty() == true) {
//            tools.forEach {
//                if(it!=null) {
//                    ToolCard(it)
//                    Spacer(modifier = Modifier.height(16.dp))
//                }
//            }
//        } else {
//            Text("No tools available for this server.")
//        }
//    }
//}
//
//@Composable
//fun ToolCard(tool: AITool) {
//    Card(
//        modifier = Modifier.fillMaxWidth(),
//        elevation = 2.dp
//    ) {
//        Column(modifier = Modifier.padding(16.dp)) {
//            Text(tool.name(), style = MaterialTheme.typography.labelMedium)
//            Text(tool.description(), style = MaterialTheme.typography.bodyMedium)
//            Text("Parameters Schema:", style = MaterialTheme.typography.labelMedium)
//            Text(tool.inputSchema(), style = MaterialTheme.typography.bodySmall)
//        }
//    }
//}
//
//@Composable
//fun AddServerDialog(viewModel: MCPServerIntegrationViewModel) {
//    val jsonScrollState = rememberScrollState()
//    val formScrollState = rememberScrollState()
//    AlertDialog(
//        onDismissRequest = { viewModel.dismissAddServerDialog() },
//        title = { Text("Add New MCP Server") },
//        text = {
//            Column (modifier = Modifier.height(500.dp),){
//                TabRow(selectedTabIndex = if (viewModel.showJsonView) 1 else 0) {
//                    Tab(
//                        selected = !viewModel.showJsonView,
//                        onClick = { viewModel.showJsonView = false; viewModel.convertFormToJson() },
//                        text = { Text("Form") },
//                        icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Form View") }
//                    )
//                    Tab(
//                        selected = viewModel.showJsonView,
//                        onClick = { viewModel.showJsonView = true },
//                        text = { Text("JSON") },
//                        icon = { Icon(Icons.Default.Description, contentDescription = "JSON View") }
//                    )
//                }
//
//                if (!viewModel.showJsonView) {
//                    Column(Modifier.verticalScroll(formScrollState)) {
//                        OutlinedTextField(
//                            value = viewModel.newServerName,
//                            onValueChange = { viewModel.onNewServerNameChange(it) },
//                            label = { Text("Server Name") },
//                            modifier = Modifier.fillMaxWidth(),
//                            singleLine = true,
//                            isError = !viewModel.isServerNameValid && viewModel.showValidationError
//                        )
//                        if (!viewModel.isServerNameValid && viewModel.showValidationError) {
//                            Text("Server Name cannot be empty", color = MaterialTheme.colorScheme.error)
//                        }
//                        Spacer(modifier = Modifier.height(8.dp))
//                        Row(verticalAlignment = Alignment.CenterVertically) {
//                            Text("Server Type:")
//                            Spacer(modifier = Modifier.width(8.dp))
//                            RadioButton(selected = viewModel.newServerType == "stdio", onClick = { viewModel.onNewServerTypeChange("stdio") })
//                            Text("Stdio")
//                            Spacer(modifier = Modifier.width(16.dp))
//                            RadioButton(selected = viewModel.newServerType == "sse", onClick = { viewModel.onNewServerTypeChange("sse") })
//                            Text("SSE")
//                        }
//                        Spacer(modifier = Modifier.height(8.dp))
//
//                        if (viewModel.newServerType == "stdio") {
//                            OutlinedTextField(
//                                value = viewModel.newServerCommand,
//                                onValueChange = { viewModel.onNewServerCommandChange(it) },
//                                label = { Text("Command") },
//                                modifier = Modifier.fillMaxWidth(),
//                                singleLine = true,
//                                isError = !viewModel.isCommandValid && viewModel.showValidationError
//                            )
//                            if (!viewModel.isCommandValid && viewModel.showValidationError) {
//                                Text("Command cannot be empty for Stdio type", color = MaterialTheme.colorScheme.error)
//                            }
//                            Spacer(modifier = Modifier.height(8.dp))
//                            Text("Arguments:")
//                            Column {
//                                viewModel.newServerArgs.forEachIndexed { index, arg ->
//                                    Row(
//                                        modifier = Modifier.fillMaxWidth(),
//                                        verticalAlignment = Alignment.CenterVertically
//                                    ) {
//                                        OutlinedTextField(
//                                            value = arg,
//                                            onValueChange = { viewModel.newServerArgs[index] = it },
//                                            modifier = Modifier.weight(0.45f),
//                                            singleLine = true
//                                        )
//                                        IconButton(onClick = { viewModel.newServerArgs.removeAt(index) }) {
//                                            Icon(Icons.Default.Remove, contentDescription = "Remove")
//                                        }
//                                    }
//                                    Spacer(modifier = Modifier.height(4.dp))
//                                }
//                                Button(onClick = { viewModel.newServerArgs.add("") }) {
//                                    Text("Add Argument")
//                                }
//                            }
//                        } else {
//                            OutlinedTextField(
//                                value = viewModel.newServerUrl,
//                                onValueChange = { viewModel.onNewServerUrlChange(it) },
//                                label = { Text("URL") },
//                                modifier = Modifier.fillMaxWidth(),
//                                singleLine = true,
//                                isError = !viewModel.isUrlValid && viewModel.showValidationError
//                            )
//                            if (!viewModel.isUrlValid && viewModel.showValidationError) {
//                                Text("URL cannot be empty for SSE type", color = MaterialTheme.colorScheme.error)
//                            }
//                        }
//
//                        Spacer(modifier = Modifier.height(8.dp))
//                        Text("Environment Variables:")
//                        Column {
//                            viewModel.newServerEnv.forEachIndexed { index, pair ->
//                                Row(
//                                    modifier = Modifier.fillMaxWidth(),
//                                    verticalAlignment = Alignment.CenterVertically
//                                ) {
//                                    OutlinedTextField(
//                                        value = pair.first,
//                                        onValueChange = { viewModel.newServerEnv[index] = it to pair.second },
//                                        label = { Text("Key") },
//                                        modifier = Modifier.weight(0.45f),
//                                        singleLine = true
//                                    )
//                                    Spacer(modifier = Modifier.width(8.dp))
//                                    OutlinedTextField(
//                                        value = pair.second,
//                                        onValueChange = { viewModel.newServerEnv[index] = pair.first to it },
//                                        label = { Text("Value") },
//                                        modifier = Modifier.weight(0.45f),
//                                        singleLine = true
//                                    )
//                                    IconButton(onClick = { viewModel.newServerEnv.removeAt(index) }) {
//                                        Icon(Icons.Default.Remove, contentDescription = "Remove")
//                                    }
//                                }
//                                Spacer(modifier = Modifier.height(4.dp))
//                            }
//                            Button(onClick = { viewModel.newServerEnv.add("" to "") }) {
//                                Text("Add Environment Variable")
//                            }
//                        }
//                    }
//                } else {
//                    Column (Modifier.verticalScroll(jsonScrollState)){
//                        OutlinedTextField(
//                            value = viewModel.serverJsonString,
//                            onValueChange = { viewModel.serverJsonString = it },
//                            label = { Text("Server JSON") },
//                            modifier = Modifier.fillMaxWidth().height(300.dp)
//                        )
//                        Spacer(modifier = Modifier.height(8.dp))
//                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
//                            Button(onClick = { viewModel.pasteJsonFromClipboard() }) {
//                                Icon(Icons.Default.ContentPaste, contentDescription = "Paste from Clipboard")
//                                Text("Paste from Clipboard")
//                            }
////                            Button(onClick = { viewModel.convertJsonToForm() }) {
////                                Text("Load JSON")
////                            }
//                            Button(onClick = { viewModel.convertFormToJson() }) {
//                                Text("Generate JSON")
//                            }
//                        }
//                    }
//                }
//            }
//        },
//        confirmButton = {
//            Button(onClick = { viewModel.addServer() }) {
//                Text("Add")
//            }
//        },
//        dismissButton = {
//            Button(onClick = { viewModel.dismissAddServerDialog() }) {
//                Text("Cancel")
//            }
//        }
//    )
//}
