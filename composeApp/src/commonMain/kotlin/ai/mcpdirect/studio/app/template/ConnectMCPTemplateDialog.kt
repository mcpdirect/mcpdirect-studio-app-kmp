package ai.mcpdirect.studio.app.template

import ai.mcpdirect.mcpdirectstudioapp.JSON
import ai.mcpdirect.studio.app.model.MCPServerConfig
import ai.mcpdirect.studio.app.model.aitool.AIPortMCPServerConfig
import ai.mcpdirect.studio.app.model.aitool.AIPortToolMakerTemplate
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement

@Composable
fun ConnectMCPTemplateDialog(
    template: AIPortToolMakerTemplate,
    onConfirmRequest: (name:String,config: AIPortMCPServerConfig) -> Unit,
    onDismissRequest: () -> Unit,
){
    val inputs = template.inputs.split(",").toList()
    val inputMap = remember { mutableStateMapOf<String, String>() }
    val inputErrorMap = remember { mutableStateMapOf<String, Boolean>() }
    inputs.forEach {
        inputErrorMap[it]=false
    }
    var isInputError by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("")}
    var isNameError by remember { mutableStateOf(false) }
    fun onTemplateNameChange(value:String){
        name = value
        isNameError = value.isBlank()||value.length>32
    }
    fun onTemplateVariableChange(key:String,value:String){
        inputMap[key] = value
        isInputError = value.isBlank()||value.length>500
        inputErrorMap[key] = isInputError
    }
    AlertDialog(
        onDismissRequest =  onDismissRequest ,
        title = { Column{
            Text("Connect MCP Server")
            Text("to ${template.name}", style = MaterialTheme.typography.titleLarge)
        } },
        text = {
            Column{
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = name,
                    onValueChange = { onTemplateNameChange(it) },
                    label = { Text("MCP Server Name") },
                    isError = isNameError,
                    supportingText = {
                        Text("The mcp server name must not be empty and the length less than 33")
                    }
                )
                inputs.forEach { input ->
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = inputMap[input]?:"",
                        onValueChange = { onTemplateVariableChange(input,it) },
                        label = { Text(input) },
                        isError = inputErrorMap[input]?:false,
                        supportingText = {
                            Text("The variable must not be empty and the length less than 500")
                        }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                enabled = !isNameError&&!isInputError&&inputMap.isNotEmpty(),
                onClick = {
                    val temp = JSON.decodeFromString<MCPServerConfig>(template.config)
                    val config = AIPortMCPServerConfig()
                    config.transport = temp.transport
                    config.url = temp.url
                    config.command = temp.command
                    config.args = JSON.encodeToJsonElement(temp.args).toString()
                    config.env = JSON.encodeToJsonElement(temp.env).toString()
                    config.inputs = JSON.encodeToString(inputMap.toMap())
                    onConfirmRequest(name,config)
                    onDismissRequest()
                }
            ) {
                Text("Connect")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancel")
            }
        }
    )
}