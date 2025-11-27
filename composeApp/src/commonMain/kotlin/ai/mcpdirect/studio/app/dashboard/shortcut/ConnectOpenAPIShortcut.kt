package ai.mcpdirect.studio.app.dashboard.shortcut

import ai.mcpdirect.studio.app.Screen
import ai.mcpdirect.studio.app.agent.MyStudioScreenDialog
import ai.mcpdirect.studio.app.agent.MyStudioViewModel
import ai.mcpdirect.studio.app.auth.authViewModel
import ai.mcpdirect.studio.app.compose.Tag
import ai.mcpdirect.studio.app.compose.Wizard
import ai.mcpdirect.studio.app.compose.WizardStep
import ai.mcpdirect.studio.app.generalViewModel
import ai.mcpdirect.studio.app.model.aitool.AIPortToolAgent
import ai.mcpdirect.studio.app.model.aitool.AIPortToolMaker
import ai.mcpdirect.studio.app.model.repository.UserRepository
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import mcpdirectstudioapp.composeapp.generated.resources.Res
import mcpdirectstudioapp.composeapp.generated.resources.check_box
import mcpdirectstudioapp.composeapp.generated.resources.cloud_off
import mcpdirectstudioapp.composeapp.generated.resources.uncheck_box
import org.jetbrains.compose.resources.painterResource

class ConnectOpenAPIShortcut : Shortcut {
    override val title = "Connect OpenAPI"
    @Composable
    override fun wizard() {
        val myStudioViewModel by remember { mutableStateOf(MyStudioViewModel()) }
        LaunchedEffect(myStudioViewModel) {
            myStudioViewModel.refreshToolAgents()
        }
        val toolAgents by myStudioViewModel.toolAgents.collectAsState()
        var toolAgent by remember { mutableStateOf<AIPortToolAgent?>(null) }
//        val viewModel by remember { mutableStateOf(ConnectOpenAPIServerViewModel()) }
//        var yaml by remember { mutableStateOf("")}
        val wizardSteps = remember {
            listOf(
                WizardStep("Select a MCPdirect Studio") {
                    wizardViewModel ->
                    if(toolAgents.size>1) {
                        val localToolAgent by myStudioViewModel.localToolAgent.collectAsState()
                        LazyColumn {
                            items(toolAgents) {
                                println("${it.id},${it.name}")
                                if (it.id != 0L && UserRepository.me(it.userId)) ListItem(
                                    modifier = Modifier.clickable(
                                        enabled = it.status == AIPortToolMaker.STATUS_ON
                                    ) {
                                        toolAgent = it
                                        wizardViewModel.nextStep++
                                    },
                                    leadingContent = {
                                        if(toolAgent==null||toolAgent!!.id!=it.id){
                                            Icon(
                                                painterResource(Res.drawable.uncheck_box),
                                                ""
                                            )
                                        }else{
                                            Icon(
                                                painterResource(Res.drawable.check_box),
                                                ""
                                            )
                                        }
                                    },
                                    headlineContent = { Text(it.name, softWrap = false, overflow = TextOverflow.MiddleEllipsis) },
                                    supportingContent = {
                                        if (it.id == localToolAgent.id)
                                            Tag("This device")
                                    },
                                    trailingContent = {
                                        if (it.status == 0) Icon(
                                            painterResource(Res.drawable.cloud_off),
                                            contentDescription = "Offline",
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    },
                                )
                            }
                        }
                    }
                },
//                WizardStep("Parse OpenAPI Doc") {
//                    wizardViewModel ->
//                    OutlinedTextField(
//                        modifier = Modifier.fillMaxWidth().height(200.dp),
//                        value = yaml,
//                        onValueChange = { yaml = it },
//                        label = { Text("OpenAPI YAML") },
//                        supportingText = {
//                            Text("Input OpenAPI YAML url or document")
//                        },
//                    )
//                    Button(
//                        onClick = {
//                            viewModel.parseYaml(toolAgent!!.engineId,yaml){
//                                code, message, serverDoc ->
//                                serverDoc?.let{
//                                    wizardViewModel.nextStep++
//                                }
//                            }
//                        }
//                    ){
//                        Text("Parse")
//                    }
//                },
//                WizardStep("Config OpenAPI Server") {
//                    val scrollState = rememberScrollState()
//                    Column(
//                        Modifier.weight(1.0f).verticalScroll(scrollState),
//                    ){
//                        OutlinedTextField(
//                            modifier = Modifier.fillMaxWidth(),
//                            value = viewModel.name,
//                            onValueChange = { viewModel.onNameChange(it)},
//                            label = { Text("OpenAPI Server Name") },
//                            placeholder = {Text("eg. openapi_mcp")},
//                            isError = viewModel.isNameError,
//                            supportingText = {
//                                Text("Name must not be empty and length < 21")
//                            },
//                        )
//                        Spacer(Modifier.height(8.dp))
//                        var textFieldSize by remember { mutableStateOf(Size.Zero) }
//                        OutlinedTextField(
//                            modifier = Modifier.fillMaxWidth().onGloballyPositioned { coordinates ->
//                                textFieldSize = coordinates.size.toSize()
//                            },
//                            value = viewModel.url,
//                            onValueChange = { viewModel.onUrlChange(it)},
//                            label = { Text("OpenAPI Server URL") },
//                            isError = viewModel.isUrlError,
//                            supportingText = {
//                                Text("URL must start with http:// or https://")
//                            },
//                            trailingIcon = {
//                                viewModel.serverDoc?.servers?.let { servers ->
//                                    var showMenu by remember { mutableStateOf(false) }
//                                    IconButton(
//                                        onClick = {showMenu=true}
//                                    ){
//                                        Icon(
//                                            painterResource(Res.drawable.keyboard_arrow_down),
//                                            contentDescription = ""
//                                        )
//                                    }
//                                    DropdownMenu(
//                                        modifier = Modifier
//                                            .width(with(LocalDensity.current) { textFieldSize.width.toDp() }),
//                                        expanded = showMenu,
//                                        onDismissRequest = {showMenu=false}
//                                    ){
//                                        servers.forEach { server ->
//                                            server.url?.let { url ->
//                                                DropdownMenuItem(
//                                                    { Text(url) },
//                                                    onClick = {
//                                                        showMenu = false
//                                                        viewModel.onUrlChange(url)
//                                                    }
//                                                )
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                        )
//                        viewModel.serverDoc?.securities?.let{
//                            it.forEach { security ->
//                                Spacer(Modifier.height(8.dp))
//                                OutlinedTextField(
//                                    modifier = Modifier.fillMaxWidth(),
//                                    value = viewModel.securities[security.key]?:"",
//                                    onValueChange = {viewModel.onSecurityChange(security.key,it)},
//                                    label = {Text(security.key)},
//                                    supportingText = {
//                                        security.value.description?.let {
//                                            Text(it)
//                                        }
//                                    }
//                                )
//                            }
//                        }
//                    }
//                },
                WizardStep("Connect OpenAPI Server") { wizardViewModel ->
                    wizardViewModel.nextStep++
                    Column(
                        modifier = Modifier.weight(1.0f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
//                        Text(
//                            text = "Config Complete!",
//                            style = MaterialTheme.typography.headlineMedium
//                        )
                        Text(
                            text = "Click finish button to connect a OpenAPI Server as MCP"
                        )
                    }
                }
            )
        }
//    Modifier.weight(3.0f)
        Wizard(
            wizardSteps,
            onFinish = {
//                    val securities = mutableMapOf<String,String>()
//                    viewModel.securities.forEach {
//                        securities[it.key] = it.value
//                    }
//                    val config = OpenAPIServerConfig()
//                    config.url = viewModel.url
//                    config.securities = securities
//                    if(yaml.startsWith("http://")||yaml.startsWith("https://")){
//                        config.docUri = yaml
//                    }else config.doc = yaml
//                    myStudioViewModel.connectOpenAPIServerToStudio(viewModel.name,config)
                generalViewModel.currentScreen(Screen.MyStudio(
                    toolAgent,null, MyStudioScreenDialog.ConnectOpenAPI))
            }
        )
    }
}