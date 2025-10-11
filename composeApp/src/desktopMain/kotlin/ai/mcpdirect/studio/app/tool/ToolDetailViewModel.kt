package ai.mcpdirect.studio.app.tool

import ai.mcpdirect.backend.dao.entity.aitool.AIPortTool
import ai.mcpdirect.studio.MCPDirectStudio
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

@Serializable
data class ToolMetaData(val description: String)

class ToolDetailViewModel: ViewModel() {
    var toolMetadata by mutableStateOf<ToolMetaData>(ToolMetaData(""))
    var toolId by mutableStateOf(0L)
    var toolName by mutableStateOf("")
    fun queryToolMetadata(){
        if(toolId>0)
            MCPDirectStudio.queryTools(toolId,null,null, null,null){
                    code, message, data ->
                if(code==0&&data!=null&&data.isNotEmpty()){
                    val json = Json.parseToJsonElement(data[0].metaData)
                    val description = json.jsonObject["description"]?.jsonPrimitive?.content
                    toolMetadata = ToolMetaData(description?:"")
                }
            }
    }
}