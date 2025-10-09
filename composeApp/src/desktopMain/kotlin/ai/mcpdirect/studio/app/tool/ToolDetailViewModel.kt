package ai.mcpdirect.studio.app.tool

import ai.mcpdirect.studio.MCPDirectStudio
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class ToolMetaData(val description: String, val age: Int, val city: String)

class ToolDetailViewModel: ViewModel() {
    var toolMetadata by mutableStateOf<ToolMetaData?>(null)

    fun queryTool(id:Long){
        MCPDirectStudio.queryTools(id,null,null, null,null){
                code, message, data ->
            if(code==0&&data!=null&&data.isNotEmpty()){
                toolMetadata= Json.decodeFromString<ToolMetaData>(data[0].metaData)
            }
        }
    }
}