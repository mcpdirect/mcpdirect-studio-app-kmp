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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.unit.dp
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonPrimitive
import mcpdirectstudioapp.composeapp.generated.resources.Res
import mcpdirectstudioapp.composeapp.generated.resources.delete
import org.jetbrains.compose.resources.painterResource

//
//@Serializable
//data class ConfigData(
//    val mcpServers: Map<String, ServerData>
//)
//
//@Serializable
//data class ServerData(
//    val url: String? = null,
//    val command: String? = null,
//    val args: List<String>? = null,
//    val env: Map<String, String>? = null
//)

@Composable
fun ConnectMCPServerDialog(
//    toolAgent: AIPortToolAgent,
    title:String?=null,
    onConfirmRequest: (List<MCPServerConfig>) -> Unit,
    onDismissRequest: () -> Unit,
) {
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

    AlertDialog(
        onDismissRequest =  onDismissRequest ,
        title = { Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            Text("Connect MCP Servers${title?.let { " to ${it}" }?:""}")
        } },
        text = {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth().height(500.dp),
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
        },
        confirmButton = {
            Button(
                enabled = !isJsonError,
                onClick = {
                    configError = null
                    val configs = mutableMapOf<String,MCPServerConfig>()
                    val servers = serverJson["mcpServers"]
                    servers?.let {
                        if(servers is JsonObject) servers.entries.forEach {
                            val server = it.value
                            if(it.key.isBlank()){
                                configError = "MCP server name cannot be blank"
                                return@Button
                            }
                            if(server is JsonObject){
                                val config = MCPServerConfig()
                                config.name = it.key

                                val url = server.get("url")
                                if(url is JsonElement && url.jsonPrimitive.isString){
                                    config.url = url.jsonPrimitive.content.trim()
                                }
                                val command = server.get("command")
                                if(command is JsonPrimitive&&command.jsonPrimitive.isString) {
                                    config.command = command.jsonPrimitive.content.trim()
                                }
                                if((config.command==null||config.command!!.isBlank())
                                    &&(config.url==null||config.url!!.isBlank())){
                                    configError = "${config.name}'s command and url cannot be blank both"
                                    return@Button
                                }
                                val type = server.get("type")
                                config.transport = -1
                                if(type is JsonPrimitive&&type.jsonPrimitive.isString) {
                                    when(type.jsonPrimitive.content.trim().lowercase()){
                                        "stdio" -> config.transport = 0
                                        "sse" -> config.transport = 1
                                        "streamablehttp" -> config.transport = 2
                                    }
                                }
                                if(config.transport == 0) {
                                    config.url = null
                                    if(config.command==null||config.command!!.isBlank()){
                                        configError = "${config.name}'s command cannot be blank"
                                        return@Button
                                    }
                                } else if(config.transport > 0){
                                    config.command = null
                                    if(config.url==null||config.url!!.isBlank()){
                                        configError = "${config.name}'s url cannot be blank"
                                        return@Button
                                    }
                                } else if((config.command==null||config.command!!.isBlank())
                                    &&(config.url!=null&&config.url!!.isNotBlank())){
                                    if(config.url!!.endsWith("/sse")) config.transport = 1
                                    else if(config.url!!.endsWith("/mcp")) config.transport = 2
                                    else {
                                        configError = "${config.name} miss type"
                                        return@Button
                                    }
                                }
                                val args = server.get("args")
                                if(args is JsonArray){
                                    val configArgs = mutableListOf<String>()
                                    args.forEach {
                                        if(it.jsonPrimitive.isString){
                                            configArgs.add(it.jsonPrimitive.content)
                                        }
                                    }
                                    config.args = configArgs
                                }
                                val configEnv = mutableMapOf<String,String>()
                                val env = server.get("env")
                                if(env is JsonObject){
                                    env.entries.forEach {
                                        if(it.value.jsonPrimitive.isString){
                                            configEnv[it.key] = it.value.jsonPrimitive.content
                                        }
                                    }
                                }
                                val headers = server.get("headers")
                                if(headers is JsonObject){
                                    headers.entries.forEach {
                                        if(it.value.jsonPrimitive.isString){
                                            configEnv[it.key] = it.value.jsonPrimitive.content
                                        }
                                    }
                                }
                                config.env = configEnv
                                configs[it.key] = config
                            }
                        }
                    }
                    onConfirmRequest(configs.values.toList())
                    onDismissRequest()
                }
            ) {
                Text("Submit")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun ConnectMCPServerDialog(
    mcpServer: MCPServer?,
    onConfirmRequest: (MCPServer?,MCPServerConfig) -> Unit,
    onDismissRequest: () -> Unit,
) {
    var newServerName by remember { mutableStateOf(mcpServer?.name?:"") }
    var newServerTransport by remember { mutableStateOf(mcpServer?.transport?:0)  }
    var newServerUrl by remember { mutableStateOf(mcpServer?.url?:"")}
    var newServerCommand by remember { mutableStateOf(mcpServer?.command?:"")}
    val newServerArgs = remember { mutableStateListOf<String>()}
    mcpServer?.args?.let {
        newServerArgs.addAll(it)
    }
    val newServerEnv = remember { mutableStateListOf<Pair<String, String>>()}
    mcpServer?.env?.let {
        newServerEnv.addAll(it.entries.map { it.toPair() })
    }
    var isNameValid by remember {mutableStateOf(true)}
    var isCommandValid by remember {mutableStateOf(true)}
    var isUrlValid by remember {mutableStateOf(true)}
    val formScrollState = rememberScrollState()
    fun onNewServerNameChange(name: String) {
        newServerName = name
        isNameValid = newServerName.isNotBlank() && newServerName.length<33
    }
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
            Text("Connect MCP Server${mcpServer?.let { " of "+it.name }}")
        } },
        text = {
            Column (modifier = Modifier.height(500.dp),){
                Column(Modifier.verticalScroll(formScrollState)) {
                    if(mcpServer != null){
                        OutlinedTextField(
                            value = newServerName,
                            onValueChange = { onNewServerCommandChange(it) },
                            label = { Text("Command") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            isError = !isNameValid,
                            supportingText = {
                                if (!isNameValid) {
                                    Text("name cannot be empty and length must be less 33", color = MaterialTheme.colorScheme.error)
                                }
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
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
                enabled = newServerName.isNotBlank()&&isNameValid
                        &&((newServerTransport==0&&newServerCommand.isNotBlank()&&isCommandValid)
                        || (newServerUrl.isNotBlank()&&isUrlValid)),
                onClick = {
                    val config = MCPServerConfig()
                    config.name = newServerName.trim()
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