package ai.mcpdirect.studio.app.mcp.openapi

import ai.mcpdirect.mcpdirectstudioapp.getPlatform
import ai.mcpdirect.studio.app.model.AIPortServiceResponse
import ai.mcpdirect.studio.app.model.OpenAPIServerDoc
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ConnectOpenAPIServerViewModel: ViewModel() {
//    var config by mutableStateOf<OpenAPIServerConfig?>(null)
//        private set
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
        isNameError = v.isEmpty()||v.length>32
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
    fun parseYaml(studioId:Long,doc:String){
        viewModelScope.launch {
//            var docUri:String? = null
//            if(doc.startsWith("http://")||doc.startsWith("https://")){
//                docUri=doc
//            }
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
            }
        }
    }
}