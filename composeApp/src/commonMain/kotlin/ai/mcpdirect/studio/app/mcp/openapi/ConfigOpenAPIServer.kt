package ai.mcpdirect.studio.app.mcp.openapi

import ai.mcpdirect.mcpdirectstudioapp.getPlatform
import ai.mcpdirect.studio.app.compose.StudioActionBar
import ai.mcpdirect.studio.app.compose.YamlTextField
import ai.mcpdirect.studio.app.generalViewModel
import ai.mcpdirect.studio.app.model.AIPortServiceResponse
import ai.mcpdirect.studio.app.model.OpenAPIServerConfig
import ai.mcpdirect.studio.app.model.OpenAPIServerDoc
import ai.mcpdirect.studio.app.model.repository.StudioRepository
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
    val isConfigError by derivedStateOf {
        isNameError||isUrlError
    }

    fun onNameChange(value:String){
        val v = value.trim()
        isNameError = v.isEmpty()||v.length>20
        if(!isNameError){
            name = v.replace(" ","_");
        }
    }

    fun onUrlChange(value:String){
        val u = value.trim()
        isUrlError = !u.startsWith("http://")&&!u.startsWith("https://")
        url = u
    }
    fun onSecurityChange(keyName:String,value:String){
        securities[keyName]=value
    }
    fun parseYaml(
        studioId:String,doc:String,
        onResponse:((code:Int,message:String?,serverDoc: OpenAPIServerDoc?)->Unit)? = null
    ){
        viewModelScope.launch {
            getPlatform().parseOpenAPIDocFromStudio(
                studioId,doc,
            ){
                if(it.code== AIPortServiceResponse.SERVICE_SUCCESSFUL) it.data?.let {
                    serverDoc = it
                    it.servers?.let {
                        for (server in it) {
                            if(server.url!=null){
//                                url = server.url!!
                                onUrlChange(server.url!!)
                                break;
                            }
                        }
                    }
                    it.securities?.let {
                        it.forEach {
                            it.value.key?.let {
                                securities[it]=""
                            }
                        }
                    }
                }
                onResponse?.let { resp ->
                    resp(it.code,it.message,it.data)
                }
            }
        }
    }
}

@Composable
fun ConfigOpenAPIServerView(
    title:String?=null,
    config:OpenAPIServerConfig?=null,
    modifier: Modifier = Modifier,
    onBack:(()->Unit)?=null,
    onConfirmRequest: (yaml:String) -> Unit,
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
        HorizontalDivider()
        if(serverDoc!=null){
            if(serverDoc.doc!=null) yaml = serverDoc.doc!!
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                value = viewModel.name,
                onValueChange = { viewModel.onNameChange(it)},
                label = { Text("OpenAPI Server Name") },
                isError = viewModel.isNameError,
                supportingText = {
                    Text("Name must not be empty and length < 21")
                },
                singleLine = true
            )
            var textFieldSize by remember { mutableStateOf(Size.Zero) }
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .onGloballyPositioned { coordinates ->
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
                            OutlinedTextField(
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
                                shape = MaterialTheme.shapes.extraLarge
                            )
                        }
                    }
                }
            }
        }
        else YamlTextField(value,onValueChange = { yaml = it }, Modifier.weight(1f))
        val enabled = serverDoc==null
        Button(
            enabled = enabled,
            modifier =Modifier.padding(16.dp).fillMaxWidth(),
            onClick = {
                if(serverDoc==null){
                    viewModel.parseYaml(
                        StudioRepository.localToolAgent.value.engineId,
                        yaml
                    )
                }
            }
        ){
            if(serverDoc==null) Text("Parse OpenAPI doc")
            else if(enabled)Text("Confirm")
            else Text("Please complete required inputs before confirm")
        }
    }

}