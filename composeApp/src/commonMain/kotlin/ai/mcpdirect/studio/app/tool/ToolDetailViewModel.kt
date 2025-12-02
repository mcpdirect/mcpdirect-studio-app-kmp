//package ai.mcpdirect.studio.app.tool
//
//import ai.mcpdirect.mcpdirectstudioapp.getPlatform
//import ai.mcpdirect.mcpdirectstudioapp.JSON
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.setValue
//import androidx.lifecycle.ViewModel
//import kotlinx.serialization.Serializable
//import kotlinx.serialization.json.jsonObject
//import kotlinx.serialization.json.jsonPrimitive
//
//@Serializable
//data class ToolMetaData(val description: String)
//val toolDetailViewModel = ToolDetailViewModel()
//class ToolDetailViewModel: ViewModel() {
//    var toolMetadata by mutableStateOf(ToolMetaData(""))
//    var toolId by mutableStateOf(0L)
//    var toolName by mutableStateOf("")
//    fun queryToolMetadata(){
//        if(toolId>0)
//            getPlatform().getTool(toolId){ (code, message, data) ->
//                if(code==0&&data!=null){
//                    val json = JSON.parseToJsonElement(data.metaData)
//                    val description = json.jsonObject["description"]?.jsonPrimitive?.content
//                    toolMetadata = ToolMetaData(description?:"")
//                }
//            }
//    }
//}