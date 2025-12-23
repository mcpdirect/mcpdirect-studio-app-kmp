package ai.mcpdirect.studio.app.mcp.openapi

import ai.mcpdirect.mcpdirectstudioapp.getPlatform
import ai.mcpdirect.studio.app.compose.JsonTreeView
import ai.mcpdirect.studio.app.compose.StudioActionBar
import ai.mcpdirect.studio.app.compose.StudioBoard
import ai.mcpdirect.studio.app.compose.YamlTextField
import ai.mcpdirect.studio.app.generalViewModel
import ai.mcpdirect.studio.app.model.AIPortServiceResponse
import ai.mcpdirect.studio.app.model.OpenAPIServer
import ai.mcpdirect.studio.app.model.OpenAPIServerDoc
import ai.mcpdirect.studio.app.model.repository.StudioRepository
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import mcpdirectstudioapp.composeapp.generated.resources.Res
import mcpdirectstudioapp.composeapp.generated.resources.arrow_back
import mcpdirectstudioapp.composeapp.generated.resources.edit
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
    openAPIServer: OpenAPIServer?=null,
    modifier: Modifier = Modifier,
    onBack:(()->Unit)?=null,
    onConfirmRequest: (yaml:String) -> Unit,
){
    val viewModel by remember { mutableStateOf(ConfigOpenAPIServerViewModel()) }
    val serverDoc = viewModel.serverDoc
    var value by remember { mutableStateOf("") }
    var yaml by remember { mutableStateOf("") }
    Column(modifier) {
        StudioActionBar(
            openAPIServer?.name?:"New OpenAPI Server",
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
            val json = getPlatform().convertYamlToJson(yaml)
            if(json.startsWith("{")) JsonTreeView(json, Modifier.weight(1f).padding(16.dp))
            else StudioBoard(Modifier.weight(1f).padding(16.dp)) {
                Text(json, color = MaterialTheme.colorScheme.error)
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
            else Text("Please complete required inputs and MCP server name before confirm")
        }
    }

}