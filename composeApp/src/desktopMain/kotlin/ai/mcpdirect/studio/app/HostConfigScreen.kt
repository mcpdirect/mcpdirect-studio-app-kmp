package ai.mcpdirect.studio.app

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.* // ktlint-disable no-wildcard-imports
import androidx.compose.runtime.* // ktlint-disable no-wildcard-imports
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ai.mcpdirect.studio.app.data.model.Agent
import ai.mcpdirect.studio.app.data.model.ToolLogEntry
import ai.mcpdirect.studio.app.data.model.ToolMaker
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolsLogbookScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Config MCPdirect host", style = MaterialTheme.typography.headlineMedium)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MakerTile(maker: ToolMaker, onClick: (ToolMaker) -> Unit) {
    Card(modifier = Modifier.padding(8.dp).fillMaxWidth().clickable { onClick(maker) }) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(maker.name, style = MaterialTheme.typography.headlineSmall)
            Text("Logs: ${maker.logTimes}", style = MaterialTheme.typography.bodyMedium)
            Text("Last Log: ${maker.lastLogTimestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))}", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgentTile(agent: Agent, onClick: (Agent) -> Unit) {
    Card(modifier = Modifier.padding(8.dp).fillMaxWidth().clickable { onClick(agent) }) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(agent.name, style = MaterialTheme.typography.headlineSmall)
            Text("Logs: ${agent.logTimes}", style = MaterialTheme.typography.bodyMedium)
            Text("Last Log: ${agent.lastLogTimestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))}", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolLogCard(log: ToolLogEntry) {
    Card(modifier = Modifier.padding(8.dp).fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Tool: ${log.toolName}", style = MaterialTheme.typography.titleMedium)
            Text("Agent: ${log.agentName}", style = MaterialTheme.typography.bodyMedium)
            Text("Maker: ${log.makerName}", style = MaterialTheme.typography.bodyMedium)
            Text("Timestamp: ${log.timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))}", style = MaterialTheme.typography.bodySmall)
            Text("Input: ${log.input}", style = MaterialTheme.typography.bodySmall)
            Text("Output: ${log.output}", style = MaterialTheme.typography.bodySmall)
        }
    }
}
