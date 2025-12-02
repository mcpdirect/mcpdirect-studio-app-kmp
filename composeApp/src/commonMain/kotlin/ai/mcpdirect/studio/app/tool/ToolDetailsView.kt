package ai.mcpdirect.studio.app.tool

import ai.mcpdirect.mcpdirectstudioapp.JSON
import ai.mcpdirect.studio.app.model.aitool.AIPortTool
import ai.mcpdirect.studio.app.model.repository.ToolRepository
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import mcpdirectstudioapp.composeapp.generated.resources.Res
import mcpdirectstudioapp.composeapp.generated.resources.arrow_back
import org.jetbrains.compose.resources.painterResource

@Serializable
data class ToolDetails(val description: String,val inputSchema:String)

@Composable
fun ToolDetailsView(
    toolId: Long,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit
) {
//    val viewModel by remember { mutableStateOf(ToolDetailsViewModel(toolId)) }
    var tool by remember { mutableStateOf<AIPortTool>(AIPortTool()) }
    var toolDetails by remember { mutableStateOf(ToolDetails("","{}")) }
    val scrollState = rememberScrollState()
    LaunchedEffect(null){
        ToolRepository.tool(toolId){
            if(it.successful()) it.data?.let{
                tool = it
                val json = JSON.parseToJsonElement(it.metaData)
                val description = json.jsonObject["description"]?.jsonPrimitive?.content
                val inputSchema = json.jsonObject["requestSchema"]?.jsonPrimitive?.content
                toolDetails = ToolDetails(description?:"",inputSchema?:"{}")
            }
        }
    }
    Column(modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = onBackClick,
            ){
                Icon(painterResource(Res.drawable.arrow_back), contentDescription = null)
            }
            Text(tool.name, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.weight(1f))
        }
        HorizontalDivider()
        Text(
            text = toolDetails.description,
            modifier = Modifier.padding(8.dp).verticalScroll(scrollState)
        )
    }
}