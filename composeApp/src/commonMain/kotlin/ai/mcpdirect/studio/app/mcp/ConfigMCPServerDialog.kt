package ai.mcpdirect.studio.app.mcp

import ai.mcpdirect.studio.app.compose.Tag
import ai.mcpdirect.studio.app.model.MCPServer
import ai.mcpdirect.studio.app.model.MCPServerConfig
import ai.mcpdirect.studio.app.model.repository.StudioRepository
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
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
                                    BasicTextField(
                                        value = arg,
                                        onValueChange = { newServerArgs[index] = it },
                                        modifier = Modifier.weight(1f),
                                    )
//                                    OutlinedTextField(
//                                        value = arg,
//                                        onValueChange = { newServerArgs[index] = it },
//                                        modifier = Modifier.weight(0.45f),
//                                        singleLine = true
//                                    )
                                    IconButton(onClick = { newServerArgs.removeAt(index) }) {
                                        Icon(painterResource(Res.drawable.delete), contentDescription = "Remove")
                                    }
                                }
                                HorizontalDivider()
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

@Composable
fun ConfigMCPServerView(
    mcpServer: MCPServer?=null,
    modifier: Modifier = Modifier,
){
    var name by remember { mutableStateOf("") }
    var isNameError by remember { mutableStateOf(true) }
    var transport by remember { mutableStateOf(mcpServer?.transport?:0)  }
    var url by remember { mutableStateOf(mcpServer?.url?:"")}
    var command by remember { mutableStateOf(mcpServer?.command?:"")}
    val args = remember { mutableStateListOf<String>()}
    mcpServer?.args?.let {
        args.addAll(it)
    }
    val newServerEnv = remember { mutableStateListOf<Pair<String, String>>()}
    mcpServer?.env?.let {
        newServerEnv.addAll(it.entries.map { it.toPair() })
    }
    var isCommandValid by remember {mutableStateOf(true)}
    var isUrlError by remember {mutableStateOf(true)}
    val formScrollState = rememberScrollState()
    fun onNewServerCommandChange(value: String) { command = value }
    fun onNewServerUrlChange(value: String) {
        url = value
        isUrlError = !(url.trim().startsWith("http://")|| url.trim().startsWith("https://"))
    }
    fun onNewServerTypeChange(type: Int) { transport = type }
    fun onNameChange(value:String){
        isNameError = value.isBlank()||value.length>20
        name = value
    }
    Column(
        modifier.verticalScroll(formScrollState),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = name,
            onValueChange = { onNameChange(it)},
            label = { Text("MCP Server Name") },
            isError = isNameError,
            supportingText = {
                Text("Name must not be empty and length < 21")
            },
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text("Type: ")
            Row(verticalAlignment = Alignment.CenterVertically,){
                RadioButton(selected = transport == 0, onClick = { onNewServerTypeChange(0) })
                Text("STDIO")
            }
            Row(verticalAlignment = Alignment.CenterVertically,) {
                RadioButton(selected = transport == 1, onClick = { onNewServerTypeChange(1) })
                Text("SSE")
            }
            Row(verticalAlignment = Alignment.CenterVertically,) {
                RadioButton(selected = transport == 2, onClick = { onNewServerTypeChange(2) })
                Text("Streamable Http")
            }
        }

        if (transport == 0) {
            OutlinedTextField(
                value = command,
                onValueChange = { onNewServerCommandChange(it) },
                label = { Text("Command") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = !isCommandValid,
                supportingText = {
                    Text("Command must not be empty")
                },
                trailingIcon = {
                    if(command == "node"||command=="npx"||command=="npm"){
                        var version by remember { mutableStateOf<String?>(null) }
                        LaunchedEffect(null){
                            StudioRepository.checkMCPServerRTMFromStudio(
                                StudioRepository.localToolAgent.value,command
                            ){
                                version = it.data
                            }
                        }
                        version?.let{
                            Box(Modifier.padding(end=16.dp)) {
                                Tag("$command $version installed")
                            }
                        }?:Button(
                            onClick = {}
                        ){
                            Text("Install $command")
                        }
                    }
                }
            )
            OutlinedTextField(
                label = { Text("Arguments") },
                value = args.joinToString(separator = "\n"),
                placeholder = { Text("arg1\narg2\narg3") },
                onValueChange = {},
                modifier = Modifier.fillMaxWidth().height(150.dp),
                supportingText = {Text("Each argument on a new line")}
            )
        } else {
            OutlinedTextField(
                value = url,
                onValueChange = { onNewServerUrlChange(it) },
                label = { Text("URL") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = isUrlError,
                supportingText = {
                    Text("URL must not be empty", color = MaterialTheme.colorScheme.error)
                }
            )

        }
        OutlinedTextField(
            label = {Text(if(transport==0) "Environment Variables" else "Headers")},
            placeholder = { Text("KEY1=value1\nKEY2=value2\nKEY3=value3") },
            value = "",
            onValueChange = {},
            modifier = Modifier.fillMaxWidth().height(150.dp),
            supportingText = {Text("Each KEY=value on a new line")}
        )
    }
}

@Composable
fun ConfigMCPServerJSONView(
    mcpServer: MCPServer?=null,
    modifier: Modifier = Modifier,
){
    val prettyJson = Json { prettyPrint = true }
    var serverJson = mapOf<String, JsonElement>(
        "mcpServers" to JsonObject(
            mapOf()
        )
    )
    var serverJsonString by remember { mutableStateOf("") }
    var isJsonError by remember { mutableStateOf(false) }
    var configError by remember { mutableStateOf<String?>(null) }

    fun onJsonValueChange(value:String){
        try {
            serverJson = prettyJson.decodeFromString(value)
            serverJsonString = prettyJson.encodeToString(serverJson)
            isJsonError = false
        }catch(e: Exception){
            isJsonError = true
            serverJsonString = value
        }
    }
    OutlinedTextField(
        modifier = modifier.fillMaxSize(),
        value = serverJsonString,
        onValueChange = { onJsonValueChange(it) },
        label = { Text("MCP Servers Configuration") },
        placeholder = { Text("""
{
  "mcpServers": {
    "stdio-server-example": {
      "command": "npx",
      "args": ["-y", "mcp-server-example"],
      "env": {
        "ENV_VARIABLE": "env-variable"
      }
    },
    "sse-server-example": {
      "type": "sse",
      "url": "http://localhost:3000"
    },
    "streamable-http-example": {
      "type": "streamableHttp",
      "url": "http://localhost:3001",
      "headers": {
        "Content-Type": "application/json",
        "Authorization": "Bearer your-token"
      }
    }
  }
}
                """.trimIndent(), style = MaterialTheme.typography.bodyMedium) },
        isError = isJsonError||configError!=null,
        supportingText = {
            if(configError!=null) {Text(configError!!)}
            else if(isJsonError)Text("invalid json format")
        }
    )
}