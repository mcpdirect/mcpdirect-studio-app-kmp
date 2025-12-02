//package ai.mcpdirect.studio.app.tool
//
//import ai.mcpdirect.mcpdirectstudioapp.JSON
//import ai.mcpdirect.studio.app.model.repository.ToolRepository
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.setValue
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import kotlinx.coroutines.launch
//import kotlinx.serialization.Serializable
//import kotlinx.serialization.json.jsonObject
//import kotlinx.serialization.json.jsonPrimitive
//
//@Serializable
//data class ToolDetails(val description: String,val inputSchema:String)
////val toolDetailViewModel = ToolDetailViewModel()
//class ToolDetailsViewModel(val toolId:Long): ViewModel() {
//    var toolDetails by mutableStateOf(ToolDetails("","{}"))
//        private set
////    var toolId by mutableStateOf(0L)
////    var toolName by mutableStateOf("")
//    fun queryToolDetails(){
//    if(toolId>Int.MAX_VALUE)
//        viewModelScope.launch {
//            ToolRepository.tool(toolId){
//                if(it.successful()) it.data?.let{
//                    val json = JSON.parseToJsonElement(it.metaData)
//                    val description = json.jsonObject["description"]?.jsonPrimitive?.content
//                    val inputSchema = json.jsonObject["requestSchema"]?.jsonPrimitive?.content
//                    toolDetails = ToolDetails(description?:"",inputSchema?:"{}")
//                }
//            }
//        }
//    }
//}