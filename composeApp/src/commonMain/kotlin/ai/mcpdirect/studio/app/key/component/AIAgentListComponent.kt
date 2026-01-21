package ai.mcpdirect.studio.app.key.component

import ai.mcpdirect.mcpdirectstudioapp.JSON
import ai.mcpdirect.studio.app.compose.StudioActionBar
import ai.mcpdirect.studio.app.compose.StudioListItem
import ai.mcpdirect.studio.app.tips.AIAgent
import ai.mcpdirect.studio.app.tips.aiAgentIntegrationGuide
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@Composable
fun AIAgentListComponent(
    modifier: Modifier,
    onAIAgentChange:(aiAgent:AIAgent)->Unit
){
    var aiAgents by remember {mutableStateOf<List<AIAgent>>(emptyList())}
    var aiAgent by remember { mutableStateOf<AIAgent?>(null) }
    LaunchedEffect(Unit) {
        aiAgents = JSON.decodeFromString(aiAgentIntegrationGuide)
        if(aiAgents.isNotEmpty()){
            aiAgent = aiAgents[0]
            onAIAgentChange(aiAgent!!)
        }

    }
    Column(modifier) {
        StudioActionBar("AI Agents")
        HorizontalDivider()
        LazyColumn(Modifier.weight(1f)) {
            items(aiAgents) {
//                if(aiAgent==null){
//                    aiAgent = it
//                }
                StudioListItem(
                    modifier = Modifier.clickable {
                        aiAgent = it
                        onAIAgentChange(it)
                    },
                    selected = it == aiAgent,
                    headlineContent = { Text(it.name) }
                )
            }
        }
    }
}