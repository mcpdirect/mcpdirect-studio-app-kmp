package ai.mcpdirect.studio.app.mcp

import ai.mcpdirect.studio.app.model.MCPServer
import ai.mcpdirect.studio.app.model.MCPServerConfig
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mcpdirectstudioapp.composeapp.generated.resources.Res
import mcpdirectstudioapp.composeapp.generated.resources.delete
import org.jetbrains.compose.resources.painterResource

@Composable
fun ConfigMCPServerDialog(
    mcpServer: MCPServer,
    onConfirmRequest: (MCPServer,MCPServerConfig) -> Unit,
    onDismissRequest: () -> Unit,
) {
    var newServerTransport by remember { mutableStateOf(mcpServer.transport)  }
    var newServerUrl by remember { mutableStateOf(mcpServer.url?:"")}
    var newServerCommand by remember { mutableStateOf(mcpServer.command?:"")}
    val newServerArgs = remember { mutableStateListOf<String>()}
    mcpServer.args?.let {
        newServerArgs.addAll(it)
    }
    val newServerEnv = remember { mutableStateListOf<Pair<String, String>>()}
    mcpServer.env?.let {
        newServerEnv.addAll(it.entries.map { it.toPair() })
    }
    var isCommandValid by remember {mutableStateOf(true)}
    var isUrlValid by remember {mutableStateOf(true)}
    val formScrollState = rememberScrollState()
    fun onNewServerCommandChange(command: String) { newServerCommand = command }
    //    fun onNewServerArgsChange(args: List<String>) { newServerArgs = args }
    fun onNewServerUrlChange(url: String) { newServerUrl = url }
    fun onNewServerTypeChange(type: Int) { newServerTransport = type }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            Text("Config MCP Server of ${mcpServer.name}")
        } },
        text = {
            Column (modifier = Modifier.height(500.dp),){
                Column(Modifier.verticalScroll(formScrollState)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Server Type:")
                        Spacer(modifier = Modifier.width(8.dp))
                        RadioButton(selected = newServerTransport == 0, onClick = { onNewServerTypeChange(0) })
                        Text("Stdio")
                        Spacer(modifier = Modifier.width(16.dp))
                        RadioButton(selected = newServerTransport == 1, onClick = { onNewServerTypeChange(1) })
                        Text("SSE")
                        RadioButton(selected = newServerTransport == 2, onClick = { onNewServerTypeChange(2) })
                        Text("Streamable Http")
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    if (newServerTransport == 0) {
                        OutlinedTextField(
                            value = newServerCommand,
                            onValueChange = { onNewServerCommandChange(it) },
                            label = { Text("Command") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            isError = !isCommandValid,
                            supportingText = {
                                if (!isCommandValid) {
                                    Text("Command cannot be empty for Stdio type", color = MaterialTheme.colorScheme.error)
                                }
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Arguments:")
                        Column {
                            newServerArgs.forEachIndexed { index, arg ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedTextField(
                                        value = arg,
                                        onValueChange = { newServerArgs[index] = it },
                                        modifier = Modifier.weight(0.45f),
                                        singleLine = true
                                    )
                                    IconButton(onClick = { newServerArgs.removeAt(index) }) {
                                        Icon(painterResource(Res.drawable.delete), contentDescription = "Remove")
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                            }
                            Button(onClick = { newServerArgs.add("") }) {
                                Text("Add Argument")
                            }
                        }
                    } else {
                        OutlinedTextField(
                            value = newServerUrl,
                            onValueChange = { onNewServerUrlChange(it) },
                            label = { Text("URL") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            isError = !isUrlValid,
                            supportingText = {
                                if (!isUrlValid) {
                                    Text("URL cannot be empty for SSE/Streamable type", color = MaterialTheme.colorScheme.error)
                                }
                            }
                        )

                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Environment Variables:")
                    Column {
                        newServerEnv.forEachIndexed { index, pair ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = pair.first,
                                    onValueChange = { newServerEnv[index] = it to pair.second },
                                    label = { Text("Key") },
                                    modifier = Modifier.weight(0.45f),
                                    singleLine = true
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                OutlinedTextField(
                                    value = pair.second,
                                    onValueChange = { newServerEnv[index] = pair.first to it },
                                    label = { Text("Value") },
                                    modifier = Modifier.weight(0.45f),
                                    singleLine = true
                                )
                                IconButton(onClick = { newServerEnv.removeAt(index) }) {
                                    Icon(painterResource(Res.drawable.delete), contentDescription = "Remove")
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                        Button(onClick = { newServerEnv.add("" to "") }) {
                            Text("Add Environment Variable")
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                enabled = (newServerTransport==0&&newServerCommand.isNotBlank()&&isCommandValid) ||(newServerUrl.isNotBlank()&&isUrlValid),
                onClick = {
                    val config = MCPServerConfig()
                    config.transport = newServerTransport
                    config.url = newServerUrl
                    config.command = newServerCommand
                    if(newServerArgs.isNotEmpty()) config.args = newServerArgs
                    if(newServerEnv.isNotEmpty()) config.env = newServerEnv.associate { it.first to it.second }
                    onConfirmRequest(mcpServer,config)
                    onDismissRequest()
                }
            ) {
                Text("Submit")
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismissRequest() }) {
                Text("Cancel")
            }
        }
    )
}