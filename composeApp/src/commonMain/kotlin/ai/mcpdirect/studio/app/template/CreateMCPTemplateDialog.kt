package ai.mcpdirect.studio.app.template

import ai.mcpdirect.mcpdirectstudioapp.JSON
import ai.mcpdirect.studio.app.compose.TooltipIconButton
import ai.mcpdirect.studio.app.model.MCPServerConfig
import ai.mcpdirect.studio.app.model.aitool.AIPortToolMaker
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.serialization.json.*
import mcpdirectstudioapp.composeapp.generated.resources.Res
import mcpdirectstudioapp.composeapp.generated.resources.reset_settings

@Composable
fun CreateMCPTemplateDialog(
    toolMaker: AIPortToolMaker,
    config: MCPServerConfig,
    onConfirmRequest: (name:String,type:Int,agentId:Long,config:String,inputs:String) -> Unit,
    onDismissRequest: () -> Unit,
) {
//    val regex = Regex("""\$\{([^}]+)\}""")
    val regex = Regex("""\$\{([A-Za-z0-9_-]+)\}""")
    val templateInputs = remember { mutableStateListOf<String>()}
    var templateName by remember { mutableStateOf("${toolMaker.name}_template")}
    val prettyJson = Json { prettyPrint = true }
    var serverJson = mutableMapOf(
        "url" to JsonPrimitive(""),
        "command" to JsonPrimitive(""),
        "args" to JsonArray(listOf()),
        "env" to JsonObject(mapOf())
    )
    config.let {
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
    var isTemplateNameError by remember { mutableStateOf(false) }
    fun onJsonValueChange(value:String){
        try {
            serverJson = prettyJson.decodeFromString(value)
            serverJsonString = prettyJson.encodeToString(serverJson)
            isJsonError = false
            templateInputs.clear()
            templateInputs.addAll(regex.findAll(serverJsonString)
                .map { it.groupValues[1] }  // groupValues[0] is full match, [1] is the captured name
                .distinct()
                .toList())
        }catch(e: Exception){
            isJsonError = true
            serverJsonString = value
        }
    }
    fun onTemplateNameChange(value:String){
        templateName = value
        isTemplateNameError = value.isBlank()||value.length>32
    }

    AlertDialog(
        onDismissRequest =  onDismissRequest ,
        title = { Column{
            Text("Create MCP Server Template")
            Text("from ${toolMaker.name}", style = MaterialTheme.typography.titleLarge)
        } },
        text = {
            Column{
                val transportType = when(config.transport){
                    1 -> "SSE"
                    2 -> "Streamable Http"
                    else -> "Stdio"
                }
                Text("MCP Server Transport: $transportType")
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = templateName,
                    onValueChange = { onTemplateNameChange(it) },
                    label = { Text("Template Name") },
                    isError = isTemplateNameError,
                    supportingText = {
                        Text("The template must not be empty and the length less than 33")
                    }
                )
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
                            config.let {
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
                            serverJsonString = prettyJson.encodeToString(serverJson)
                        }
                    )
                    templateInputs.forEach {
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
                enabled = !isJsonError&&!isTemplateNameError&&templateInputs.isNotEmpty(),
                onClick = {
                    serverJson["transport"] = JsonPrimitive(config.transport)
                    val config = JSON.encodeToString(serverJson)
                    val inputs = templateInputs.joinToString(",")
                    onConfirmRequest(templateName.trim(),toolMaker.type,toolMaker.agentId,
                        config,inputs)
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