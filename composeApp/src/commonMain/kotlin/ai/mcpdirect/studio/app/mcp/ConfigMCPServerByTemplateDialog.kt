package ai.mcpdirect.studio.app.mcp

import ai.mcpdirect.mcpdirectstudioapp.JSON
import ai.mcpdirect.studio.app.model.aitool.AIPortToolAgent
import ai.mcpdirect.studio.app.model.aitool.AIPortToolMaker
import ai.mcpdirect.studio.app.model.repository.StudioRepository
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ConfigMCPServerByTemplateDialog(
    toolAgent: AIPortToolAgent,
    toolMaker: AIPortToolMaker,
    onConfirmRequest: (toolMaker:AIPortToolMaker,inputs:String) -> Unit,
    onDismissRequest: () -> Unit,
){
    val inputs = remember { mutableStateListOf<String>() }
    val inputMap = remember { mutableStateMapOf<String, String>() }
    val inputErrorMap = remember { mutableStateMapOf<String, Boolean>() }
    LaunchedEffect(null){
        StudioRepository.getToolMakerTemplateConfigFromStudio(
            toolAgent,toolMaker
        ){
            if(it.successful()&&it.data!=null){
                inputMap.putAll(JSON.decodeFromString(it.data!!.inputs!!))
                inputs.addAll(inputMap.map { it.key }.toList())
                inputs.forEach {
                    inputErrorMap[it]=false
                }
            }
        }
    }


    var isInputError by remember { mutableStateOf(false) }
    fun onTemplateVariableChange(key:String,value:String){
        inputMap[key] = value
        isInputError = value.isBlank()||value.length>500
        inputErrorMap[key] = isInputError
    }
    AlertDialog(
        onDismissRequest =  onDismissRequest ,
        title = { Column{
            Text("Config MCP Server Input")
            Text("to  of ${toolMaker.name}", style = MaterialTheme.typography.titleLarge)
        } },
        text = {
            Column{
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
                enabled = !isInputError&&inputMap.isNotEmpty(),
                onClick = {
//                    val temp = JSON.decodeFromString<MCPServerConfig>(template.config)
//                    val config = AIPortMCPServerConfig()
//                    config.id = toolMaker.id
//                    config.transport = temp.transport
//                    config.url = temp.url
//                    config.command = temp.command
//                    config.args = JSON.encodeToJsonElement(temp.args).toString()
//                    config.env = JSON.encodeToJsonElement(temp.env).toString()
//                    config.inputs = JSON.encodeToString(inputMap.toMap())
//                    onConfirmRequest(toolMaker,config)
                    onConfirmRequest(toolMaker,JSON.encodeToString(inputMap.toMap()))
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