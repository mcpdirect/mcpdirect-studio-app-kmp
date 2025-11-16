package ai.mcpdirect.studio.app.mcp.openapi

import ai.mcpdirect.studio.app.model.OpenAPIServerConfig
import ai.mcpdirect.studio.app.model.aitool.AIPortToolAgent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.geometry.Size
import mcpdirectstudioapp.composeapp.generated.resources.Res
import mcpdirectstudioapp.composeapp.generated.resources.keyboard_arrow_down
import org.jetbrains.compose.resources.painterResource

@Composable
fun ConnectOpenAPIServerDialog(
    toolAgent: AIPortToolAgent,
    onConfirmRequest: (OpenAPIServerConfig) -> Unit,
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
            if(viewModel.config==null){
                OutlinedTextField(
                    modifier = Modifier.height(800.dp),
                    value = yaml,
                    onValueChange = { yaml = it },
                    label = { Text("OpenAPI YAML") },
                    supportingText = {
                        Text("Input OpenAPI YAML url or document")
                    },
                )
            }else Column(Modifier.fillMaxSize()){
                OutlinedTextField(
                    modifier = Modifier.height(800.dp),
                    value = viewModel.name,
                    onValueChange = { viewModel.onNameChange(it)},
                    label = { Text("OpenAPI Server Name") },
                    isError = viewModel.isNameError,
                    supportingText = {
                        Text("Name must not be empty and length < 33")
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
                        viewModel.config?.servers?.let { servers ->
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
                                    server.url?.let {
                                        DropdownMenuItem(
                                            { ListItem(
                                                headlineContent = {Text(it)},
                                                supportingContent = {
                                                    server.description?.let{
                                                        Text(it)
                                                    }
                                                }
                                            ) },
                                            onClick = {
                                                viewModel.onUrlChange(it)
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                )
                viewModel.config?.securities?.let{
                    it.forEach { security ->
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = viewModel.securities[security.key]?:"",
                            onValueChange = {viewModel.onSecurityChange(security.key,it)},
                            label = {Text(security.key)}
                        )
                    }
                }
            }

        },
        confirmButton = {
            if(viewModel.config==null){
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