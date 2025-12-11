package ai.mcpdirect.studio.app.mcp

import ai.mcpdirect.studio.app.model.MCPServerConfig
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonPrimitive

@Serializable
data class ConfigData(
    val mcpServers: Map<String, ServerData>
)

@Serializable
data class ServerData(
    val url: String? = null,
    val command: String? = null,
    val args: List<String>? = null,
    val env: Map<String, String>? = null
)

@Composable
fun ConnectMCPServerDialog(
//    toolAgent: AIPortToolAgent,
    title:String?=null,
    onConfirmRequest: (Map<String, MCPServerConfig>) -> Unit,
    onDismissRequest: () -> Unit,
) {
    val prettyJson = Json { prettyPrint = true }
    var serverJson = mapOf<String, JsonElement>(
        "mcpServers" to JsonObject(
            mapOf(
                "mcp" to JsonObject(mapOf(
                    "url" to JsonPrimitive(""),
                    "command" to JsonPrimitive(""),
                    "args" to JsonArray(listOf()),
                    "env" to JsonObject(mapOf())
                ))
            )
        )
    )
    var serverJsonString by remember { mutableStateOf(prettyJson.encodeToString(serverJson)) }
    var isJsonError by remember { mutableStateOf(false) }

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
// Example:JSON (stdio):
// {
//   "mcpServers": {
//     "stdio-server-example": {
//       "command": "npx",
//       "args": ["-y", "mcp-server-example"]
//     }
//   }
// }

// Example:JSON (sse):
// {
//   "mcpServers": {
//     "sse-server-example": {
//       "type": "sse",
//       "url": "http://localhost:3000"
//     }
//   }
// }

// Example:JSON (streamableHttp):
// {
//   "mcpServers": {
//     "streamable-http-example": {
//       "type": "streamableHttp",
//       "url": "http://localhost:3001",
//       "headers": {
//         "Content-Type": "application/json",
//         "Authorization": "Bearer your-token"
//       }
//     }
//   }
// }
                """.trimIndent()) },
                isError = isJsonError,
                supportingText = {
                    if(isJsonError)Text("invalid json format")
                }
            )
        },
        confirmButton = {
            Button(
                enabled = !isJsonError,
                onClick = {
                    val configs = mutableMapOf<String,MCPServerConfig>()
                    val servers = serverJson["mcpServers"]
                    servers?.let {
                        if(servers is JsonObject){
                            servers.entries.forEach {
                                val server = it.value
                                if(server is JsonObject){
                                    val config = MCPServerConfig()
                                    val url = server.get("url")
                                    if(url is JsonElement && url.jsonPrimitive.isString){
                                        config.url = url.jsonPrimitive.content
                                    }
                                    val command = server.get("command")
                                    if(command is JsonPrimitive&&command.jsonPrimitive.isString) {
                                        config.command = command.jsonPrimitive.content
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
                    }
                    onConfirmRequest(configs)
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