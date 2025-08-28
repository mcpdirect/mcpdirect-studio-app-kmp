//package ai.mcpdirect.studio.app.
//
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.grid.GridCells
//import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
//import androidx.compose.foundation.lazy.grid.items
//import androidx.compose.foundation.lazy.items
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.ArrowBack
//import androidx.compose.material3.* // ktlint-disable no-wildcard-imports
//import androidx.compose.runtime.* // ktlint-disable no-wildcard-imports
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import ai.mcpdirect.studio.app.data.model.Agent
//import ai.mcpdirect.studio.app.data.model.ToolLogEntry
//import ai.mcpdirect.studio.app.data.model.ToolMaker
//import ai.mcpdirect.studio.app.mcp.ToolsLogbookViewModel
//import java.time.format.DateTimeFormatter
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun ToolsLogbookScreen(viewModel: ToolsLogbookViewModel = remember { ToolsLogbookViewModel() }) {
//    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
//        Text("Tools Logbook", style = MaterialTheme.typography.headlineMedium)
//
//        Spacer(Modifier.height(16.dp))
//
//        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
//            Button(onClick = { viewModel.setView(ToolsLogbookViewModel.LogbookView.MAKER_VIEW) }) {
//                Text("Maker View")
//            }
//            Button(onClick = { viewModel.setView(ToolsLogbookViewModel.LogbookView.AGENT_VIEW) }) {
//                Text("Agent View")
//            }
//            Button(onClick = { viewModel.setView(ToolsLogbookViewModel.LogbookView.LOG_LIST_VIEW) }) {
//                Text("All Logs")
//            }
//        }
//
//        Spacer(Modifier.height(16.dp))
//
//        when (viewModel.currentView.value) {
//            ToolsLogbookViewModel.LogbookView.MAKER_VIEW -> {
//                OutlinedTextField(
//                    value = viewModel.makerFilter.value,
//                    onValueChange = { viewModel.setMakerFilter(it) },
//                    label = { Text("Filter by Maker Name") },
//                    modifier = Modifier.fillMaxWidth()
//                )
//                Spacer(Modifier.height(8.dp))
//                LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier.fillMaxSize()) {
//                    items(viewModel.filteredToolMakers) {
//                        MakerTile(it) {
//                            viewModel.setMakerFilter(it.name)
//                            viewModel.setView(ToolsLogbookViewModel.LogbookView.LOG_LIST_VIEW)
//                        }
//                    }
//                }
//            }
//            ToolsLogbookViewModel.LogbookView.AGENT_VIEW -> {
//                OutlinedTextField(
//                    value = viewModel.agentFilter.value,
//                    onValueChange = { viewModel.setAgentFilter(it) },
//                    label = { Text("Filter by Agent Name") },
//                    modifier = Modifier.fillMaxWidth()
//                )
//                Spacer(Modifier.height(8.dp))
//                LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier.fillMaxSize()) {
//                    items(viewModel.filteredAgents) {
//                        AgentTile(it) {
//                            viewModel.setAgentFilter(it.name)
//                            viewModel.setView(ToolsLogbookViewModel.LogbookView.LOG_LIST_VIEW)
//                        }
//                    }
//                }
//            }
//            ToolsLogbookViewModel.LogbookView.LOG_LIST_VIEW -> {
//                Row(modifier = Modifier.fillMaxWidth()) {
//                    OutlinedTextField(
//                        value = viewModel.toolNameFilter.value,
//                        onValueChange = { viewModel.setToolNameFilter(it) },
//                        label = { Text("Filter by Tool Name") },
//                        modifier = Modifier.weight(1f)
//                    )
//                    Spacer(Modifier.width(8.dp))
//                    if (viewModel.makerFilter.value.isNotEmpty() || viewModel.agentFilter.value.isNotEmpty()) {
//                        Button(onClick = {
//                            viewModel.setMakerFilter("")
//                            viewModel.setAgentFilter("")
//                            viewModel.setView(ToolsLogbookViewModel.LogbookView.MAKER_VIEW)
//                        }) {
//                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
//                        }
//                    }
//                }
//                Spacer(Modifier.height(8.dp))
//                LazyColumn(modifier = Modifier.fillMaxSize()) {
//                    items(viewModel.filteredToolLogs) {
//                        ToolLogCard(it)
//                    }
//                }
//            }
//        }
//    }
//}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun MakerTile(maker: ToolMaker, onClick: (ToolMaker) -> Unit) {
//    Card(modifier = Modifier.padding(8.dp).fillMaxWidth().clickable { onClick(maker) }) {
//        Column(modifier = Modifier.padding(16.dp)) {
//            Text(maker.name, style = MaterialTheme.typography.headlineSmall)
//            Text("Logs: ${maker.logTimes}", style = MaterialTheme.typography.bodyMedium)
//            Text("Last Log: ${maker.lastLogTimestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))}", style = MaterialTheme.typography.bodySmall)
//        }
//    }
//}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun AgentTile(agent: Agent, onClick: (Agent) -> Unit) {
//    Card(modifier = Modifier.padding(8.dp).fillMaxWidth().clickable { onClick(agent) }) {
//        Column(modifier = Modifier.padding(16.dp)) {
//            Text(agent.name, style = MaterialTheme.typography.headlineSmall)
//            Text("Logs: ${agent.logTimes}", style = MaterialTheme.typography.bodyMedium)
//            Text("Last Log: ${agent.lastLogTimestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))}", style = MaterialTheme.typography.bodySmall)
//        }
//    }
//}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun ToolLogCard(log: ToolLogEntry) {
//    Card(modifier = Modifier.padding(8.dp).fillMaxWidth()) {
//        Column(modifier = Modifier.padding(16.dp)) {
//            Text("Tool: ${log.toolName}", style = MaterialTheme.typography.titleMedium)
//            Text("Agent: ${log.agentName}", style = MaterialTheme.typography.bodyMedium)
//            Text("Maker: ${log.makerName}", style = MaterialTheme.typography.bodyMedium)
//            Text("Timestamp: ${log.timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))}", style = MaterialTheme.typography.bodySmall)
//            Text("Input: ${log.input}", style = MaterialTheme.typography.bodySmall)
//            Text("Output: ${log.output}", style = MaterialTheme.typography.bodySmall)
//        }
//    }
//}
