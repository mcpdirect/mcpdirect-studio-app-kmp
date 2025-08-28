package ai.mcpdirect.studio.app.logbook
import ai.mcpdirect.studio.app.compose.StudioCard
import ai.mcpdirect.studio.handler.ToolLogHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
//import androidx.compose.material.Card
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import mcpdirectstudioapp.composeapp.generated.resources.Res
import mcpdirectstudioapp.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource
import java.text.SimpleDateFormat
import java.util.*

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun ToolsLogbookScreen(
//    viewModel: ToolsLogViewModel,
//    onBack: () -> Unit
//) {
//    val viewType by viewModel.viewType.collectAsState()
//    val selectedMaker by viewModel.selectedMaker.collectAsState()
//    val selectedClient by viewModel.selectedClient.collectAsState()
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("Tools Logbook") },
//                navigationIcon = {
//                    IconButton(onClick = onBack) {
//                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
//                    }
//                },
//                actions = {
//                    IconButton(onClick = {
//                        viewModel.switchViewType(
//                            if (viewType == ToolsLogViewModel.ViewType.MAKERS)
//                                ToolsLogViewModel.ViewType.CLIENTS
//                            else
//                                ToolsLogViewModel.ViewType.MAKERS
//                        )
//                    }) {
//                        Icon(
//                            painter = painterResource(if (viewType == ToolsLogViewModel.ViewType.MAKERS)
//                                Res.drawable.smart_toy
//                            else
//                                Res.drawable.engineering) ,
//                            contentDescription = "Switch View"
//                        )
//                    }
//                }
//            )
//        }
//    ) { padding ->
//        when {
//            selectedMaker != null -> MakerLogsView(viewModel, padding)
//            selectedClient != null -> ClientLogsView(viewModel, padding)
//            viewType == ToolsLogViewModel.ViewType.MAKERS -> MakersListView(viewModel, padding)
//            else -> ClientsListView(viewModel, padding)
//        }
//    }
//}
//
//@Composable
//private fun MakersListView(
//    viewModel: ToolsLogViewModel,
//    padding: PaddingValues
//) {
//    val makerSummaries by viewModel.makerSummaries.collectAsState()
//    val searchQuery by viewModel.searchQuery.collectAsState()
//
//    Column(modifier = Modifier.padding(padding)) {
//        SearchView(
//            query = searchQuery,
//            onQueryChange = { viewModel.updateSearchQuery(it) },
//            placeholder = "Search makers..."
//        )
//
//        LazyVerticalGrid(
//            columns = GridCells.Adaptive(minSize = 400.dp),
//            modifier = Modifier.fillMaxSize()
//        ) {
//            items(makerSummaries) { maker ->
//                MakerTile(maker) {
//                    viewModel.selectMaker(maker.name)
//                }
//            }
//        }
//    }
//}
//
//@Composable
//private fun ClientsListView(
//    viewModel: ToolsLogViewModel,
//    padding: PaddingValues
//) {
//    val agentSummaries by viewModel.clientSummaries.collectAsState()
//    val searchQuery by viewModel.searchQuery.collectAsState()
//
//    Column(modifier = Modifier.padding(padding)) {
//        SearchView(
//            query = searchQuery,
//            onQueryChange = { viewModel.updateSearchQuery(it) },
//            placeholder = "Search agents..."
//        )
//
//        LazyVerticalGrid(
//            columns = GridCells.Adaptive(minSize = 400.dp),
//            modifier = Modifier.fillMaxSize()
//        ) {
//            items(agentSummaries) { agent ->
//                ClientTile(agent) {
//                    viewModel.selectClient(agent.name)
//                }
//            }
//        }
//    }
//}
//
//@Composable
//private fun MakerTile(
//    maker: MakerSummary,
//    onClick: () -> Unit
//) {
//    StudioCard(
//        modifier = Modifier
//            .padding(8.dp)
//            .fillMaxWidth()
//            .clickable(onClick = onClick),
//    ) {
//        Column(modifier = Modifier.padding(16.dp)) {
//            Text(
//                text = maker.name,
//                style = MaterialTheme.typography.headlineSmall,
//                maxLines = 1,
//                overflow = TextOverflow.Ellipsis
//            )
//            Spacer(modifier = Modifier.height(8.dp))
//            Text(
//                text = "Logs: ${maker.logCount}",
//                style = MaterialTheme.typography.bodyMedium
//            )
//            Text(
//                text = "Last: ${formatTimestamp(maker.lastLogTimestamp)}",
//                style = MaterialTheme.typography.bodySmall
//            )
//        }
//    }
//}
//
//@Composable
//private fun ClientTile(
//    client: ClientSummary,
//    onClick: () -> Unit
//) {
//    StudioCard(
//        modifier = Modifier
//            .padding(8.dp)
//            .fillMaxWidth()
//            .clickable(onClick = onClick),
//    ) {
//        Column(modifier = Modifier.padding(16.dp)) {
//            Text(
//                text = client.name,
//                style = MaterialTheme.typography.headlineSmall,
//                maxLines = 1,
//                overflow = TextOverflow.Ellipsis
//            )
//            Spacer(modifier = Modifier.height(8.dp))
//            Text(
//                text = "Logs: ${client.logCount}",
//                style = MaterialTheme.typography.bodyMedium
//            )
//            Text(
//                text = "Last: ${formatTimestamp(client.lastLogTimestamp)}",
//                style = MaterialTheme.typography.bodySmall
//            )
//        }
//    }
//}
//
//@Composable
//private fun MakerLogsView(
//    viewModel: ToolsLogViewModel,
//    padding: PaddingValues
//) {
//    val logs by viewModel.currentLogs.collectAsState()
//    val selectedMaker by viewModel.selectedMaker.collectAsState()
//
//    Column(modifier = Modifier.padding(padding)) {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            IconButton(onClick = { viewModel.backToList() }) {
//                Icon(painterResource(Res.drawable.arrow_back), contentDescription = "Back")
//            }
//            Text(
//                text = selectedMaker ?: "",
//                style = MaterialTheme.typography.headlineSmall,
//                modifier = Modifier.weight(1f)
//            )
//        }
//
//        LogsList(
//            logs = logs,
//            title = { log -> Text(log.toolName) },
//            subtitle = { log -> Text(log.clientName) },
//            onFilter = { query -> viewModel.filterLogs(query, "tool") }
//        )
//    }
//}
//
//@Composable
//private fun ClientLogsView(
//    viewModel: ToolsLogViewModel,
//    padding: PaddingValues
//) {
//    val logs by viewModel.currentLogs.collectAsState()
//    val selectedAgent by viewModel.selectedClient.collectAsState()
//
//    Column(modifier = Modifier.padding(padding)) {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            IconButton(onClick = { viewModel.backToList() }) {
//                Icon(painterResource(Res.drawable.arrow_back), contentDescription = "Back")
//            }
//            Text(
//                text = selectedAgent ?: "",
//                style = MaterialTheme.typography.headlineSmall,
//                modifier = Modifier.weight(1f)
//            )
//        }
//
//        LogsList(
//            logs = logs,
//            title = { log -> Text(log.toolName) },
//            subtitle = { log -> Text(log.makerName) },
//            onFilter = { query -> viewModel.filterLogs(query, "tool") }
//        )
//    }
//}
//
//@Composable
//private fun LogsList(
//    logs: List<ToolLog>,
//    title: @Composable (ToolLog) -> Unit,
//    subtitle: @Composable (ToolLog) -> Unit,
//    onFilter: (String) -> Unit
//) {
//    var filterQuery by remember { mutableStateOf("") }
//
//    Column {
//        SearchView(
//            query = filterQuery,
//            onQueryChange = {
//                filterQuery = it
//                onFilter(it)
//            },
//            placeholder = "Filter by tool name"
//        )
//
//        LazyColumn(modifier = Modifier.fillMaxSize()) {
//            items(logs) { log ->
//                LogCard(log, title, subtitle)
//            }
//        }
//    }
//}
//
//@Composable
//private fun LogCard(
//    log: ToolLog,
//    title: @Composable (ToolLog) -> Unit,
//    subtitle: @Composable (ToolLog) -> Unit
//) {
//    var expanded by remember { mutableStateOf(false) }
//
//    StudioCard(
//        modifier = Modifier
//            .padding(8.dp)
//            .fillMaxWidth()
//            .clickable { expanded = !expanded },
//    ) {
//        Column(modifier = Modifier.padding(16.dp)) {
//            Row(
//                modifier = Modifier.fillMaxWidth(),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                Column(modifier = Modifier.weight(1f)) {
//                    title(log)
//                    subtitle(log)
//                }
//                Text(
//                    text = formatTimestamp(log.timestamp),
//                    style = MaterialTheme.typography.bodySmall
//                )
//            }
//
//            if (expanded) {
//                Spacer(modifier = Modifier.height(8.dp))
//                Text(
//                    text = "Input:",
//                    style = MaterialTheme.typography.titleMedium
//                )
//                Text(
//                    text = log.input.toString(),
//                    style = MaterialTheme.typography.bodyMedium,
//                    modifier = Modifier.padding(bottom = 8.dp)
//                )
//                Text(
//                    text = "Output:",
//                    style = MaterialTheme.typography.titleMedium
//                )
//                Text(
//                    text = log.output,
//                    style = MaterialTheme.typography.bodyMedium
//                )
//            }
//        }
//    }
//}
//
//@Composable
//private fun SearchView(
//    query: String,
//    onQueryChange: (String) -> Unit,
//    placeholder: String
//) {
//    TextField(
//        value = query,
//        onValueChange = onQueryChange,
//        placeholder = { Text(placeholder) },
//        leadingIcon = { Icon(painterResource(Res.drawable.search), contentDescription = "Search") },
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(8.dp),
//        singleLine = true,
////        colors = TextFieldDefaults.colors(
////            backgroundColor = MaterialTheme.colorScheme.surface
////        )
//    )
//}
//
//private fun formatTimestamp(timestamp: Long): String {
//    // Implement timestamp formatting based on your requirements
//    return SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date(timestamp))
//}

@Composable
fun ToolsLogbookScreen(   viewModel: ToolsLogViewModel,
    onBack: () -> Unit) {
//    val logs by viewModel.currentLogs.collectAsState()
    val selectedLog by viewModel.selectedLog

    if (selectedLog == null) {
        MasterLogScreen(viewModel,onLogSelected = { viewModel.selectLog(it) })
    } else {
        DetailLogScreen(
            log = selectedLog!!,
            details = viewModel.logDetails.value,
            onBack = { viewModel.clearSelectedLog() }
        )
    }
}
@Composable
private fun SearchView(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String
) {
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text(placeholder) },
        leadingIcon = { Icon(painterResource(Res.drawable.search), contentDescription = "Search") },
        singleLine = true,
//        colors = TextFieldDefaults.colors(
//            backgroundColor = MaterialTheme.colorScheme.surface
//        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MasterLogScreen(
    viewModel: ToolsLogViewModel,
    onLogSelected: (ToolLogHandler.ToolLog) -> Unit
) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tool Logbook") },
            )

        }
    ) { padding ->
        val logs by viewModel.currentLogs.collectAsState()
        val groups by viewModel.dateGroupNames.collectAsState()
        viewModel.selectDateGroup()
        Row {

            LazyColumn(modifier = Modifier.weight(1f).padding(padding)) {
                items(groups) { date->
                    Row(
                        modifier = Modifier.fillMaxWidth().clickable(
                        enabled = true,
                        onClick = {
                            viewModel.selectDateGroup(date)
                        }
                    )) {
                        Text(
                            text = date,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
//                groupedLogs.forEach { (date, dateLogs) ->
//                    Text(
//                        text = date,
//                        style = MaterialTheme.typography.titleMedium,
//                        modifier = Modifier.padding(16.dp)
//                    )
//                    stickyHeader {
//                        Surface(
//                            color = MaterialTheme.colorScheme.surfaceVariant,
//                            modifier = Modifier.fillMaxWidth()
//                        ) {
//                            Text(
//                                text = date,
//                                style = MaterialTheme.typography.titleMedium,
//                                modifier = Modifier.padding(16.dp)
//                            )
//                        }
//                    }
//
//                    items(dateLogs) { log ->
//                        LogItem(log = log, onClick = { onLogSelected(log) })
//                    }
//                }
            }
            StudioCard(modifier = Modifier.fillMaxSize().weight(3f).padding(padding)) {

                LazyColumn{
                    stickyHeader{
                        val searchQuery by viewModel.searchQuery.collectAsState()
                        Surface(
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            SearchView(
                                query = searchQuery,
                                onQueryChange = { viewModel.updateSearchQuery(it) },
                                placeholder = "Search logs..."
                            )

                        }
                        HorizontalDivider()
                    }

                    items(logs) { log ->
                        LogItem(log = log, onClick = { onLogSelected(log) })
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}

@Composable
private fun LogItem(log: ToolLogHandler.ToolLog, onClick: () -> Unit) {
//    Card(
//        onClick = onClick,
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(horizontal = 16.dp, vertical = 8.dp)
//    ) {
    Row(modifier = Modifier.fillMaxWidth().clickable(enabled = true, onClick = onClick)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = log.toolName,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${log.makerName} • ${log.clientName}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                    .format(Date(log.timestamp)),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
//    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DetailLogScreen(
    log: ToolLogHandler.ToolLog,
    details: ToolLogHandler.ToolLogDetails?,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(log.toolName) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(painterResource(Res.drawable.arrow_back), "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (details == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // Input Column
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Input",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = details.input,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                // Divider
                VerticalDivider()

                // Output Column
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Output",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = details.output,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}