package ai.mcpdirect.studio.app.mcp.openapi

import ai.mcpdirect.mcpdirectstudioapp.getPlatform
import ai.mcpdirect.studio.app.compose.StudioActionBar
import ai.mcpdirect.studio.app.compose.YamlTextField
import ai.mcpdirect.studio.app.generalViewModel
import ai.mcpdirect.studio.app.model.AIPortServiceResponse
import ai.mcpdirect.studio.app.model.OpenAPIServerConfig
import ai.mcpdirect.studio.app.model.OpenAPIServerDoc
import ai.mcpdirect.studio.app.model.aitool.AIPortToolAgent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import mcpdirectstudioapp.composeapp.generated.resources.Res
import mcpdirectstudioapp.composeapp.generated.resources.arrow_back
import mcpdirectstudioapp.composeapp.generated.resources.edit
import mcpdirectstudioapp.composeapp.generated.resources.keyboard_arrow_down
import org.jetbrains.compose.resources.painterResource

class ConfigOpenAPIServerViewModel: ViewModel() {
    var serverDoc by mutableStateOf<OpenAPIServerDoc?>(null)
    var name by mutableStateOf("")
        private set
    var isNameError by mutableStateOf(true)

    var url by mutableStateOf("")
        private set
    var isUrlError by mutableStateOf(true)
        private set
    var securities = mutableStateMapOf<String,String>()
        private set
    var securityErrorCount by mutableStateOf(0)
    val isConfigError by derivedStateOf {
        isNameError||isUrlError||securityErrorCount>0
    }

    fun onNameChange(value:String){
        val v = value.trim()
        isNameError = v.isEmpty()||v.length>20
        if(!isNameError){
            name = v.replace(" ","_")
        }
    }

    fun onUrlChange(value:String){
        val u = value.trim()
        isUrlError = !u.startsWith("http://")&&!u.startsWith("https://")
        url = u
    }
    fun onSecurityChange(keyName:String,value:String){
        if(value.isBlank()) securityErrorCount++
        else securities[keyName]=value.trim()
    }
    private fun handleParseYamlResponse(
        resp: AIPortServiceResponse<OpenAPIServerDoc>,
        onResponse: ((resp: AIPortServiceResponse<OpenAPIServerDoc>) -> Unit)? = null
    ){
        if(resp.code== AIPortServiceResponse.SERVICE_SUCCESSFUL) resp.data?.let { data ->
            serverDoc = data
            data.servers?.let {
                for (server in it) {
                    if(server.url!=null){
                        onUrlChange(server.url!!)
                        break
                    }
                }
            }
            data.securities?.let {
                it.forEach { entry ->
                    entry.value.key?.let { key ->
                        securities[key]=""
                    }
                }
            }
        }
        onResponse?.let { onResponse ->
            onResponse(resp)
        }
    }
    fun parseYaml(
        doc:String,toolAgent: AIPortToolAgent?=null,
        onResponse: ((resp: AIPortServiceResponse<OpenAPIServerDoc>) -> Unit)? = null
    ){
        viewModelScope.launch {
            if(toolAgent!=null) getPlatform().parseOpenAPIDocFromStudio(
                toolAgent.engineId,doc
            ){
                handleParseYamlResponse(it,onResponse)
            } else getPlatform().parseOpenAPIDoc(doc){
                handleParseYamlResponse(it,onResponse)
            }
        }
    }
}

@Composable
fun ConfigOpenAPIServerView(
    title:String?=null,
    toolAgent: AIPortToolAgent?=null,
    config:OpenAPIServerConfig?=null,
    modifier: Modifier = Modifier,
    onBack:(()->Unit)?=null,
    onConfirmRequest: (config:OpenAPIServerConfig) -> Unit,
){
    val viewModel by remember { mutableStateOf(ConfigOpenAPIServerViewModel()) }
    val serverDoc = viewModel.serverDoc
    var value by remember { mutableStateOf("") }
    var yaml by remember { mutableStateOf("") }
    LaunchedEffect(config) {
        if(config!=null){
            config.docUri?.let{
                yaml = it
            }
            config.doc?.let {
                yaml = it
            }
            viewModel.onUrlChange(config.url?:"")
            viewModel.onNameChange(config.name?:"")
            val doc = OpenAPIServerDoc()
            doc.securities = mutableMapOf()
            config.securities?.let { securities->
                securities.forEach { entry->
                    val security = OpenAPIServerDoc.Security()
                    security.key = entry.key
                    security.description = entry.value
                    viewModel.onSecurityChange(entry.key,entry.value)
                    doc.securities?.let { docSecurities->
                        docSecurities[entry.key] = security
                    }
                }
            }
            viewModel.serverDoc = doc
        }
    }

    Column(modifier) {
        StudioActionBar(
            title?:"New OpenAPI Server",
            navigationIcon = {
                onBack?.let {
                    IconButton(onClick = onBack) {
                        Icon(painterResource(Res.drawable.arrow_back),contentDescription = "Back")
                    }
                }
            }
        ) {
            if(serverDoc==null)TextButton(onClick = {
                val text = getPlatform().pasteFromClipboard()
                if(text==null){
                    generalViewModel.showSnackbar("Clipboard is empty")
                }else value=text
            }) {
                Text("Paste from Clipboard")
            } else IconButton(onClick = {
                value = yaml
                viewModel.serverDoc = null
            }) {
                Icon(painterResource(Res.drawable.edit),contentDescription = "Edit")
            }
        }
        HorizontalDivider(Modifier.padding(bottom = 16.dp))
        if(serverDoc!=null){
            if(serverDoc.doc!=null) yaml = serverDoc.doc!!
            serverDoc.paths?.let { paths->
                Card(
                    modifier = modifier.weight(weight = 1f)
                        .padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = "Tools",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                    HorizontalDivider()
                    Box(modifier = Modifier.fillMaxSize()) {
                        val scrollState = rememberScrollState()
                        Column (
                            modifier = Modifier
                                .verticalScroll(scrollState)
                                .padding(8.dp) // Add padding to prevent content from going under the scrollbar
                        )  {
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                            ){
                                var index = 1
                                paths.entries.forEach { entry ->
                                    TextButton(
                                        shape = OutlinedTextFieldDefaults.shape,
                                        border = BorderStroke(1.dp, ButtonDefaults.textButtonColors().contentColor),
                                        contentPadding = PaddingValues(8.dp,4.dp),
                                        onClick = {}
                                    ){
                                        Column{
                                            Text("${index++}. ${entry.key}",
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text("${entry.value.method?.uppercase()} ${entry.value.path}",
                                                style = MaterialTheme.typography.labelSmall)
                                        }
                                    }

                                }
                            }
                        }
                        VerticalScrollbar(
                            modifier = Modifier.align(Alignment.CenterEnd),
                            adapter = rememberScrollbarAdapter(scrollState = scrollState)
                        )
                    }
//                    LazyColumn {
//                        items(paths.entries.toList()) { entry->
//                            Column(Modifier.padding(horizontal = 16.dp,vertical = 8.dp)) {
//                                Text(entry.key,style = MaterialTheme.typography.bodyLarge,)
//                                Text("${entry.value.method?.uppercase()} ${entry.value.path}",
//                                    style = MaterialTheme.typography.bodySmall)
//                            }
//                        }
//                    }
                }
            }
            Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),) {
                OutlinedTextField(
                    modifier = Modifier.weight(1f),
                    value = viewModel.name,
                    onValueChange = { viewModel.onNameChange(it) },
                    label = { Text("OpenAPI Server Name") },
                    isError = viewModel.isNameError,
                    supportingText = {
                        Text("Name cannot be empty and length < 21")
                    },
                    singleLine = true
                )
                var textFieldSize by remember { mutableStateOf(Size.Zero) }
                OutlinedTextField(
                    modifier = Modifier.weight(1f)
                        .onGloballyPositioned { coordinates ->
                            textFieldSize = coordinates.size.toSize()
                        },
                    value = viewModel.url,
                    onValueChange = { viewModel.onUrlChange(it) },
                    label = { Text("OpenAPI Server URL") },
                    isError = viewModel.isUrlError,
                    supportingText = {
                        Text("URL must start with http:// or https://")
                    },
                    trailingIcon = {
                        viewModel.serverDoc?.servers?.let { servers ->
                            var showMenu by remember { mutableStateOf(false) }
                            IconButton(
                                onClick = { showMenu = true }
                            ) {
                                Icon(
                                    painterResource(Res.drawable.keyboard_arrow_down),
                                    contentDescription = ""
                                )
                            }
                            DropdownMenu(
                                modifier = Modifier
                                    .width(with(LocalDensity.current) { textFieldSize.width.toDp() }),
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false }
                            ) {
                                servers.forEach { server ->
                                    server.url?.let { url ->
                                        DropdownMenuItem(
                                            { Text(url) },
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
            }
            Column(
                modifier = modifier.weight(weight = 1f)
                    .padding(horizontal = 16.dp)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline,
                        shape = MaterialTheme.shapes.small
                    )
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                Text(
                    text = "Securities",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp,vertical = 8.dp)
                )
                HorizontalDivider()
                viewModel.serverDoc?.securities?.let{ securities->
                    LazyColumn {
                        items(securities.toList()){ pair ->
                            val key = pair.first
                            val value = viewModel.securities[key]
                            TextField(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp,vertical = 8.dp),
                                value = value?:"",
                                onValueChange = {viewModel.onSecurityChange(key,it)},
                                label = {Text(key)},
                                isError = value.isNullOrBlank(),
                                supportingText = {
                                    pair.second.description?.let {
                                        Text(it)
                                    }
                                },
//                                shape = MaterialTheme.shapes.extraLarge
                            )
                        }
                    }
                }
            }
        }
        else {
            YamlTextField(value, onValueChange = { yaml = it }, Modifier.weight(1f))
        }
        val enabled = serverDoc==null
        if(serverDoc==null) Button(
            modifier =Modifier.padding(16.dp).fillMaxWidth(),
            onClick = {
                viewModel.parseYaml(yaml,toolAgent)
            }
        ){ Text("Parse OpenAPI doc") }
        else {
            Button(
                enabled = !viewModel.isConfigError,
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                onClick = {

                }
            ) {
                if (viewModel.isConfigError) Text("Please complete required inputs before confirm")
                else Text("Confirm")
            }
        }
    }

}