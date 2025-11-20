package ai.mcpdirect.studio.app.mcp.openapi

import ai.mcpdirect.studio.app.model.OpenAPIServerConfig
import ai.mcpdirect.studio.app.model.aitool.AIPortToolAgent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import mcpdirectstudioapp.composeapp.generated.resources.Res
import mcpdirectstudioapp.composeapp.generated.resources.keyboard_arrow_down
import org.jetbrains.compose.resources.painterResource

@Composable
fun ConnectOpenAPIServerDialog(
    toolAgent: AIPortToolAgent,
    onConfirmRequest: (name:String,config:OpenAPIServerConfig) -> Unit,
    onDismissRequest: () -> Unit,
) {
    val viewModel by remember { mutableStateOf(ConnectOpenAPIServerViewModel()) }
    var yaml by remember { mutableStateOf("")}
    AlertDialog(
        onDismissRequest =  onDismissRequest ,
        title = { Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            Text("Connect OpenAPI Server to ${toolAgent.name}")
        } },
        text = {
            if(viewModel.serverDoc==null){
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth().height(700.dp),
                    value = yaml,
                    onValueChange = { yaml = it },
                    label = { Text("OpenAPI YAML") },
                    supportingText = {
                        Text("Input OpenAPI YAML url or document")
                    },
                )
            }else Column(Modifier.fillMaxSize().height(700.dp)){
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = viewModel.name,
                    onValueChange = { viewModel.onNameChange(it)},
                    label = { Text("OpenAPI Server Name") },
                    placeholder = {Text("eg. openapi_mcp")},
                    isError = viewModel.isNameError,
                    supportingText = {
                        Text("Name must not be empty and length < 21")
                    },
                )
                Spacer(Modifier.height(8.dp))
                var textFieldSize by remember { mutableStateOf(Size.Zero) }
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth().onGloballyPositioned { coordinates ->
                        textFieldSize = coordinates.size.toSize()
                    },
                    value = viewModel.url,
                    onValueChange = { viewModel.onUrlChange(it)},
                    label = { Text("OpenAPI Server URL") },
                    isError = viewModel.isUrlError,
                    supportingText = {
                        Text("URL must start with http:// or https://")
                    },
                    trailingIcon = {
                        viewModel.serverDoc?.servers?.let { servers ->
                            var showMenu by remember { mutableStateOf(false) }
                            IconButton(
                                onClick = {showMenu=true}
                            ){
                                Icon(
                                    painterResource(Res.drawable.keyboard_arrow_down),
                                    contentDescription = ""
                                )
                            }
                            DropdownMenu(
                                modifier = Modifier
                                    .width(with(LocalDensity.current) { textFieldSize.width.toDp() }),
                                expanded = showMenu,
                                onDismissRequest = {showMenu=false}
                            ){
                                servers.forEach { server ->
                                    server.url?.let { url ->
                                        DropdownMenuItem(
                                            { Text(url)
//                                                ListItem(
//                                                headlineContent = {Text(url)},
//                                                supportingContent = {
//                                                    server.description?.let{
//                                                        Text(it)
//                                                    }
//                                                }
//                                            )
                                            },
                                            onClick = {
                                                showMenu = false
                                                viewModel.onUrlChange(url)
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                )
                viewModel.serverDoc?.securities?.let{
                    it.forEach { security ->
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = viewModel.securities[security.key]?:"",
                            onValueChange = {viewModel.onSecurityChange(security.key,it)},
                            label = {Text(security.key)},
                            supportingText = {
                                security.value.description?.let {
                                    Text(it)
                                }
                            }
                        )
                    }
                }
            }

        },
        confirmButton = {
            if(viewModel.serverDoc==null){
                Button(
                    enabled = yaml.isNotEmpty(),
                    onClick = {
                        viewModel.parseYaml(toolAgent.engineId,yaml)
                    }
                ) {
                    Text("Parse YAML")
                }
            }else Button(
                enabled = !viewModel.isConfigError,
                onClick = {

                    val securities = mutableMapOf<String,String>()
                    viewModel.securities.forEach {
                        securities[it.key] = it.value
                    }
                    val config = OpenAPIServerConfig()
                    config.url = viewModel.url
                    config.securities = securities
                    if(yaml.startsWith("http://")||yaml.startsWith("https://")){
                        config.docUri = yaml
                    }else config.doc = yaml
                    onConfirmRequest(viewModel.name,config)
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