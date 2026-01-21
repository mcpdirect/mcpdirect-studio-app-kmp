package ai.mcpdirect.studio.app.key.component

import ai.mcpdirect.mcpdirectstudioapp.JSON
import ai.mcpdirect.studio.app.compose.StudioActionBar
import ai.mcpdirect.studio.app.compose.StudioListItem
import ai.mcpdirect.studio.app.compose.StudioSearchbar
import ai.mcpdirect.studio.app.tips.AIAgent
import ai.mcpdirect.studio.app.tips.aiAgentIntegrationGuide
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AIAgentListComponent(
    modifier: Modifier= Modifier,
    onAIAgentChange:(aiAgent:AIAgent)->Unit
){
    var aiAgents by remember {mutableStateOf<List<AIAgent>>(emptyList())}
    var aiAgent by remember { mutableStateOf<AIAgent?>(null) }
    var filter by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    LaunchedEffect(Unit) {
        aiAgents = JSON.decodeFromString(aiAgentIntegrationGuide)
        if(aiAgents.isNotEmpty()){
            aiAgent = aiAgents[0]
            onAIAgentChange(aiAgent!!)
        }

    }
    Column(modifier) {
        StudioActionBar("AI Agents")
//        HorizontalDivider()
        StudioSearchbar(modifier = Modifier.padding(start=16.dp, bottom = 16.dp, end = 16.dp)) {
            filter = it
        }
        Box(Modifier.weight(1f)){
            LazyColumn(state=listState) {
                items(aiAgents) { agent->
//                if(aiAgent==null){
//                    aiAgent = it
//                }
                    if(agent.name.contains(filter,true))StudioListItem(
                        modifier = Modifier.clickable {
                            aiAgent = agent
                            onAIAgentChange(agent)
                        },
                        selected = agent == aiAgent,
                        headlineContent = { Text(agent.name) }
                    )
                }
            }
            VerticalScrollbar(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .fillMaxHeight(),
                adapter = rememberScrollbarAdapter(listState)
            )
        }

    }
}