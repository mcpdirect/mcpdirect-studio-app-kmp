package ai.mcpdirect.studio.app.template

import ai.mcpdirect.mcpdirectstudioapp.JSON
import ai.mcpdirect.studio.app.compose.TooltipIconButton
import ai.mcpdirect.studio.app.model.MCPServerConfig
import ai.mcpdirect.studio.app.model.aitool.AIPortToolMaker
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
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
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonPrimitive
import mcpdirectstudioapp.composeapp.generated.resources.Res
import mcpdirectstudioapp.composeapp.generated.resources.reset_settings

@Composable
fun CreateMCPServerTemplateDialog(
    toolMaker: AIPortToolMaker,
    config: MCPServerConfig? = null,
    onConfirmRequest: (Map<String, MCPServerConfig>) -> Unit,
    onDismissRequest: () -> Unit,
) {
//    val regex = Regex("""\$\{([^}]+)\}""")
    val regex = Regex("""\$\{([A-Za-z0-9_-]+)\}""")
    val inputs = remember { mutableStateListOf<String>()}
    val prettyJson = Json { prettyPrint = true }
    var serverJson = mutableMapOf(
        "url" to JsonPrimitive(""),
        "command" to JsonPrimitive(""),
        "args" to JsonArray(listOf()),
        "env" to JsonObject(mapOf())
    )
    var serverTransport by remember { mutableStateOf(0)  }
    config?.let {
        serverTransport = it.transport
        it.url?.let {
            serverJson["url"] = JsonPrimitive(it)
        }
        it.command?.let {
            serverJson["command"]  = JsonPrimitive(it)
        }
        it.args?.let {
            serverJson["args"] = JSON.encodeToJsonElement(it)
        }
        it.env?.let {
            serverJson["env"] = JSON.encodeToJsonElement(it)
        }
    }
    var serverJsonString by remember { mutableStateOf(prettyJson.encodeToString(serverJson)) }
    var isJsonError by remember { mutableStateOf(false) }

    fun onJsonValueChange(value:String){
        try {
            serverJson = prettyJson.decodeFromString(value)
            serverJsonString = prettyJson.encodeToString(serverJson)
            isJsonError = false
            inputs.clear()
            inputs.addAll(regex.findAll(serverJsonString)
                .map { it.groupValues[1] }  // groupValues[0] is full match, [1] is the captured name
                .toList())
        }catch(e: Exception){
            isJsonError = true
            serverJsonString = value
        }
    }

    AlertDialog(
        onDismissRequest =  onDismissRequest ,
        title = { Column{
            Text("Create MCP Server Template")
            Text("from ${toolMaker.name}", style = MaterialTheme.typography.titleLarge)
        } },
        text = {
            Column{
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Server Type:")
                    Spacer(modifier = Modifier.width(8.dp))
                    RadioButton(selected = serverTransport == 0, onClick = { serverTransport = 0 })
                    Text("Stdio")
                    Spacer(modifier = Modifier.width(16.dp))
                    RadioButton(selected = serverTransport == 1, onClick = { serverTransport = 1 })
                    Text("SSE")
                    RadioButton(selected = serverTransport == 2, onClick = { serverTransport = 2 })
                    Text("Streamable Http")
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth().height(400.dp),
                    value = serverJsonString,
                    onValueChange = { onJsonValueChange(it) },
                    label = { Text("MCP Server Template") },
                    isError = isJsonError,
                    supportingText = {
                        if(isJsonError)Text("invalid json format")
                        else Text("the variable name consists only of allowed characters (A-Z, a-z, 0-9, -, and _)")
                    }
                )
                Text("Variables:", Modifier.padding(top = 8.dp, bottom = 8.dp))
                FlowRow(){
                    TooltipIconButton(
                        Res.drawable.reset_settings,
                        "Reset to default",
                        onClick = {

                        }
                    )
                    inputs.forEach {
                        AssistChip(
                            modifier = Modifier.padding(start = 4.dp),
                            onClick = {},
                            label = { Text(it) }
                        )
                    }
                }
            }
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
                                    val env = server.get("env")
                                    if(env is JsonObject){
                                        val configEnv = mutableMapOf<String,String>()
                                        env.entries.forEach {
                                            if(it.value.jsonPrimitive.isString){
                                                configEnv[it.key] = it.value.jsonPrimitive.content
                                            }
                                        }
                                        config.env = configEnv
                                    }
                                    configs[it.key] = config
                                }

                            }
                        }
                    }
                    onConfirmRequest(configs)
                    onDismissRequest()
                }
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancel")
            }
        }
    )
}