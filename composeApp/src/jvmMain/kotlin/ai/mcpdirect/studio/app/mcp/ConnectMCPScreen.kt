/**
 * SPDX-License-Identifier: AGPL-3.0-or-later
 * Copyright (C) 2025–present
 * All rights reserved.
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package ai.mcpdirect.studio.app.mcp

import ai.mcpdirect.backend.dao.entity.aitool.AIPortTool
import ai.mcpdirect.studio.app.compose.OutlinedTagFieldDialog
import ai.mcpdirect.studio.app.compose.OutlinedTextFieldDialog
import ai.mcpdirect.studio.app.compose.SearchView
import ai.mcpdirect.studio.app.compose.StudioCard
import ai.mcpdirect.studio.app.compose.Tag
import ai.mcpdirect.studio.app.compose.TooltipIconButton
import ai.mcpdirect.studio.app.generalViewModel
import ai.mcpdirect.studio.dao.entity.MCPServer
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import mcpdirectstudioapp.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource

private var dialog by mutableStateOf<ConnectMCPDialog>(ConnectMCPDialog.None)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConnectMCPScreen(
    padding: PaddingValues
) {
    val viewModel = connectMCPViewModel
//    val selectedMaker by viewModel.selectedMaker
    val selectedTool by viewModel.selectedTool
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("Connect MCP") },
//                actions = {
//                    Button(onClick = { viewModel.showAddServerDialog = true }) {
//                        Text("Add MCP Server")
//                    }
//                }
//            )
//        }
//    ) { padding ->
//        when {
//            selectedTool != null -> ToolDetailView(viewModel,padding)
////            selectedMaker != null -> MakerToolView(viewModel, padding)
//            else -> MakerListView(viewModel, padding)
//        }
//    }

    generalViewModel.topBarActions = {
        Button(onClick = { viewModel.showAddServerDialog = true }) {
            Text("Add MCP Server")
        }
    }

    when {
        selectedTool != null -> ToolDetailView(viewModel,padding)
//            selectedMaker != null -> MakerToolView(viewModel, padding)
        else -> MakerListView(viewModel,padding)
    }
    when(dialog){
        ConnectMCPDialog.None->{}
        ConnectMCPDialog.AddServer->{ AddServerDialog(viewModel)}
        ConnectMCPDialog.EditServerName->{EditServerNameDialog()}
        ConnectMCPDialog.EditServerTags->{EditServerTagsDialog()}
    }
    // Error Dialog
    if (viewModel.errorMessage != null) {
        AlertDialog(
            onDismissRequest = { viewModel.errorMessage = null },
            title = { Text("Error") },
            text = { Text(viewModel.errorMessage!!) },
            confirmButton = {
                Button(onClick = { viewModel.errorMessage = null }) {
                    Text("OK")
                }
            }
        )
    }
}

@Composable
private fun MakerListView(
    viewModel: ConnectMCPViewModel,
    padding: PaddingValues
) {
    val selectedMaker = viewModel.selectedMaker
//    val makerSummaries by viewModel.makerSummaries.collectAsState()
//    val localMakerSummaries by viewModel.localMakerSummaries.collectAsState()
//    val searchQuery by viewModel.searchQuery

    Column(modifier = Modifier.padding(padding)
    ) {
        SearchView(
            query = viewModel.searchQuery,
            onQueryChange = { viewModel.updateSearchQuery(it) },
            placeholder = "Search makers..."
        )
        StudioCard(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxSize()
        ){
            Row(modifier = Modifier.fillMaxWidth()) {
//                Column(modifier = Modifier.weight(1.0f)) {
//                    val summaries = mutableListOf<MCPServer>()
//                    summaries.addAll(makerSummaries)
//                    summaries.addAll(localMakerSummaries)
//                    LazyColumn() {
//                        items(makerSummaries) { maker ->
//                            MakerItem(maker) {
//                                viewModel.selectMaker(maker)
//                            }
//                        }
//                    }
//                    LazyColumn() {
//                        items(localMakerSummaries) { maker ->
//                            MakerItem(maker) {
//                                viewModel.selectMaker(maker)
//                            }
//                        }
//                    }
//                }
//                val summaries = mutableListOf<MCPServer>()
//                summaries.addAll(makerSummaries)
//                summaries.addAll(localMakerSummaries)
                var currentTabIndex by remember { mutableStateOf(0) }
                Column(modifier = Modifier.weight(3.0f)) {
//                    LazyColumn(modifier = Modifier.weight(3.0f)) {
//                        items(mcpServerIntegrationViewModel.test){
//                            Text(it)
//                        }
//                    }
                    val tabs = listOf("MCPdirect", "Local")
                    TabRow(selectedTabIndex = currentTabIndex) {
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                selected = currentTabIndex == index,
                                onClick = { currentTabIndex = index },
                                text = { Text(title) }
                            )
                        }
                    }
                    val makers =  if(currentTabIndex==0) viewModel.makers else viewModel.localMakers
                    LazyColumn(modifier = Modifier.weight(3.0f)) {
//                        when(currentTabIndex){
//                            0 -> {
                                items(makers) { maker ->
                                    MakerItem(maker) {
                                        viewModel.selectMaker(maker)
                                    }
                                }
//                            }
//                            1 -> {
//                                items(mcpServerIntegrationViewModel.localMakers) { maker ->
//                                    MakerItem(maker) {
//                                        viewModel.selectMaker(maker)
//                                    }
//                                }
//
//                            }
//                        }
                    }
                }

                if (selectedMaker != null) {
                    VerticalDivider()
                    Column(modifier = Modifier.weight(5.0f)) {
                        MakerToolView(viewModel)
                    }
                }

//                if (makers.isEmpty()) {
//                    Column(modifier = Modifier.fillMaxSize(),
//                        verticalArrangement = Arrangement.SpaceAround,
//                        horizontalAlignment = Alignment.CenterHorizontally) {
//                        Icon(painterResource(Res.drawable.draft),
//                            contentDescription = "Empty",
//                            modifier = Modifier.size(64.dp)
//                        )
//                    }
//                }else {
//                    LazyColumn(modifier = Modifier.weight(3.0f)) {
//                        items(makers) { maker ->
//                            MakerItem(maker) {
//                                viewModel.selectMaker(maker)
//                            }
//                        }
//                    }
//                    if (selectedMaker != null) {
//                        VerticalDivider()
//                        Column(modifier = Modifier.weight(5.0f)) {
//                            MakerToolView(viewModel)
//                        }
//                    }
//                }
            }
        }
//        LazyVerticalGrid(
//            columns = GridCells.Adaptive(minSize = 400.dp),
//            modifier = Modifier.fillMaxSize()
//        ) {
//            items(makerSummaries) { maker ->
//                MakerTile(maker) {
//                    viewModel.selectMaker(maker)
//                }
//            }
//        }
    }
}

@Composable
private fun MakerItem(
    maker: MCPServer,
    onClick: () -> Unit
) {
    ListItem(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        headlineContent = {
            Text(
                text = maker.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        supportingContent = {
            maker.tags?.let {
                Text("Tags :$it")
            }
        },
//        trailingContent = {
//            if(maker.id==0L){
//                Tag("Local")
//            } else{
//                Tag("MCPdirect", color = MaterialTheme.colorScheme.primary)
//            }
//        }
    )
//    Column (modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),){
//
//        BadgedBox(
//            modifier = Modifier.padding(16.dp),
//            badge = {
//                if(maker.id==0L) Badge(
//                    containerColor = MaterialTheme.colorScheme.surfaceVariant
//                ) {
//                    Text("Local")
//                } else Badge(
//                    containerColor = MaterialTheme.colorScheme.primary
//                ) {
//                    Text("MCPdirect")
//                }
//            }
//        ){
//            Text(
////                text = "${maker.name}(${maker.tools.size})",
//                text = maker.name,
//                style = MaterialTheme.typography.titleLarge,
//                maxLines = 1,
//                overflow = TextOverflow.Ellipsis
//            )
//        }
//    }


//    }
}

@Composable
private fun MakerToolView(
    viewModel: ConnectMCPViewModel,
) {
    val tools by viewModel.currentTools.collectAsState()
    val selectedMaker = viewModel.selectedMaker
    
//    Column(modifier = Modifier.padding(padding)) {
    Column{
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(onClick = { viewModel.backToList() }) {
                Icon(painterResource(Res.drawable.arrow_back), contentDescription = "Back")
            }
            Text(
                text = selectedMaker?.name ?: "",
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
//            Spacer(modifier = Modifier.weight(1.0f))
            IconButton(onClick = { viewModel.reloadMCPServer() }) {
                Icon(
                    painterResource(Res.drawable.restart_alt),
                    contentDescription = "Reload MCP Server"
                )}
            when(selectedMaker!!.id.toInt()){
                0 -> {
                    IconButton(onClick = { viewModel.removeLocalMCPServer() }) {
                        Icon(
                            painterResource(Res.drawable.delete),
                            contentDescription = "Remove Local MCP Server"
                        )}
                    if(selectedMaker.tools!!.isNotEmpty()) {
                        IconButton(onClick = { viewModel.publishMCPServer() }) {
                            Icon(
                                painterResource(Res.drawable.cloud_upload),
                                contentDescription = "Publish MCP Server"
                            )
                        }
                    }
                }
                else -> {
                    TooltipIconButton(
                        Res.drawable.sell,
                        contentDescription = "Edit tags",
                        onClick = {
                            dialog = ConnectMCPDialog.EditServerTags
                        })
                    TooltipIconButton(
                        Res.drawable.badge,
                        contentDescription = "Edit name",
                        onClick = {
                            dialog = ConnectMCPDialog.EditServerName
                        })
                    TooltipIconButton(
                        Res.drawable.cloud_off,
                        contentDescription = "Abandon MCP Server",
                        onClick = { viewModel.unpublishMCPServer() })
                    TooltipIconButton(
                        Res.drawable.sync,
                        contentDescription = "Update MCP Server",
                        onClick = { viewModel.publishMCPServer() })
                }
            }

        }
        
        ToolList(
            viewModel,
            tools = tools,
            onFilter = { query -> viewModel.filterTools(query, "tool") }
        )
    }
}

@Composable
private fun ToolList(
    viewModel: ConnectMCPViewModel,
    tools: List<AIPortTool>,
    onFilter: (String) -> Unit
) {
    var filterQuery by remember { mutableStateOf("") }
    
    Column {
        SearchView(
            query = filterQuery,
            onQueryChange = {
                filterQuery = it
                onFilter(it)
            },
            placeholder = "Filter by tool name"
        )
//        StudioCard(
//            modifier = Modifier.padding(8.dp).fillMaxWidth(),
//        ) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(tools) { tool ->
                    ToolItem(viewModel,tool)
                    HorizontalDivider()
                }
            }
//        }
    }
}

@Composable
private fun ToolItem(
    viewModel: ConnectMCPViewModel,
    tool: AIPortTool,
) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable {
            viewModel.selectedTool.value=tool
        },
        verticalAlignment = Alignment.CenterVertically
    ) {
        ListItem(
            headlineContent = { Text(tool.name) },
            supportingContent = {Text(tool.metaData, maxLines = 1, overflow = TextOverflow.Ellipsis)},
            trailingContent = {
                when(tool.lastUpdated.toInt()){
                    -1 -> {
                        Tag(
                            "Abandoned",
                            color= MaterialTheme.colorScheme.error
                        )
                    }
                    0 -> {}
                    1 -> {
                        Tag("New")
//                        Badge(
//                            containerColor = MaterialTheme.colorScheme.primary
//                        ) {
//                            println(tool.name)
//                            Text("New")
//                        }
                    }
                    else -> {
                        Tag("Updates")
//                        Badge(
//                            containerColor = MaterialTheme.colorScheme.tertiary
//                        ) {
//                            Text("Updates")
//                        }
                    }
                }
            }
        )
//        Column(modifier = Modifier.weight(1f).padding(16.dp)) {
//            BadgedBox(
//                badge = {
//                    when(tool.lastUpdated.toInt()){
//                        -1 -> {
//                            Badge(
//                                containerColor = MaterialTheme.colorScheme.error
//                            ) {
//                                Text("Abandoned")
//                            }
//                        }
//                        0 -> {}
//                        1 -> {
//                            Badge(
//                                containerColor = MaterialTheme.colorScheme.primary
//                            ) {
//                                println(tool.name)
//                                Text("New")
//                            }
//                        }
//                        else -> {
//                            Badge(
//                                containerColor = MaterialTheme.colorScheme.tertiary
//                            ) {
//                                Text("Updates")
//                            }
//                        }
//                    }
//                }
//            ){
//
//            }
//            Text(
//                tool.metaData,
//                style = MaterialTheme.typography.bodySmall,
//                overflow = TextOverflow.Ellipsis,
//                maxLines = 1
//            )
//        }

    }
}

@Composable
private fun ToolDetailView(
    viewModel: ConnectMCPViewModel,
    padding: PaddingValues
) {
    val selectedTool by viewModel.selectedTool
//    val highlights = remember {
//        mutableStateOf(
//            Highlights
//                .Builder(code = selectedTool!!.metaData)
//                .build()
//        )
//    }
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier.fillMaxSize().padding(padding),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.selectedTool.value=null }) {
                Icon(painterResource(Res.drawable.arrow_back), contentDescription = "Back")
            }
            Text(
                text = selectedTool?.name ?: "",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f)
            )
        }
        HorizontalDivider()
        Column(Modifier.padding(8.dp).fillMaxSize().verticalScroll(scrollState)) {
            Text(
                text = selectedTool!!.metaData,
                modifier = Modifier.padding(8.dp)
            )
//            CodeTextView(highlights = highlights.value)
        }
    }
}

@Composable
fun AddServerDialog(viewModel: ConnectMCPViewModel) {
    val jsonScrollState = rememberScrollState()
    val formScrollState = rememberScrollState()
    AlertDialog(
        onDismissRequest = { viewModel.dismissAddServerDialog() },
        title = { Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            Text("Add New MCP Server")
            Button({
                viewModel.pasteJsonFromClipboard()
            }){
                Text("Paste from clipboard")
            }
        } },
        text = {
            Column (modifier = Modifier.height(500.dp),){
//                TabRow(selectedTabIndex = if (viewModel.showJsonView) 1 else 0) {
//                    Tab(
//                        selected = !viewModel.showJsonView,
//                        onClick = { viewModel.showJsonView = false; viewModel.convertFormToJson() },
//                        text = { Text("Form") },
//                    )
//                    Tab(
//                        selected = viewModel.showJsonView,
//                        onClick = { viewModel.showJsonView = true },
//                        text = { Text("JSON") },
//                    )
//                }
//                if (!viewModel.showJsonView) {
                    Column(Modifier.verticalScroll(formScrollState)) {
                        OutlinedTextField(
                            value = viewModel.newServerName,
                            onValueChange = { viewModel.onNewServerNameChange(it) },
                            label = { Text("Server Name") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            isError = !viewModel.isServerNameValid && viewModel.showValidationError
                        )
                        if (!viewModel.isServerNameValid && viewModel.showValidationError) {
                            Text("Server Name cannot be empty and the max length is 32", color = MaterialTheme.colorScheme.error)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Server Type:")
                            Spacer(modifier = Modifier.width(8.dp))
                            RadioButton(selected = viewModel.newServerTransport == 0, onClick = { viewModel.onNewServerTypeChange(0) })
                            Text("Stdio")
                            Spacer(modifier = Modifier.width(16.dp))
                            RadioButton(selected = viewModel.newServerTransport == 1, onClick = { viewModel.onNewServerTypeChange(1) })
                            Text("SSE")
                            RadioButton(selected = viewModel.newServerTransport == 2, onClick = { viewModel.onNewServerTypeChange(2) })
                            Text("Streamable Http")
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        if (viewModel.newServerTransport == 0) {
                            OutlinedTextField(
                                value = viewModel.newServerCommand,
                                onValueChange = { viewModel.onNewServerCommandChange(it) },
                                label = { Text("Command") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                isError = !viewModel.isCommandValid && viewModel.showValidationError
                            )
                            if (!viewModel.isCommandValid && viewModel.showValidationError) {
                                Text("Command cannot be empty for Stdio type", color = MaterialTheme.colorScheme.error)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Arguments:")
                            Column {
                                viewModel.newServerArgs.forEachIndexed { index, arg ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        OutlinedTextField(
                                            value = arg,
                                            onValueChange = { viewModel.newServerArgs[index] = it },
                                            modifier = Modifier.weight(0.45f),
                                            singleLine = true
                                        )
                                        IconButton(onClick = { viewModel.newServerArgs.removeAt(index) }) {
                                            Icon(painterResource(Res.drawable.delete), contentDescription = "Remove")
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                }
                                Button(onClick = { viewModel.newServerArgs.add("") }) {
                                    Text("Add Argument")
                                }
                            }
                        } else {
                            OutlinedTextField(
                                value = viewModel.newServerUrl,
                                onValueChange = { viewModel.onNewServerUrlChange(it) },
                                label = { Text("URL") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                isError = !viewModel.isUrlValid && viewModel.showValidationError
                            )
                            if (!viewModel.isUrlValid && viewModel.showValidationError) {
                                Text("URL cannot be empty for SSE type", color = MaterialTheme.colorScheme.error)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Environment Variables:")
                        Column {
                            viewModel.newServerEnv.forEachIndexed { index, pair ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedTextField(
                                        value = pair.first,
                                        onValueChange = { viewModel.newServerEnv[index] = it to pair.second },
                                        label = { Text("Key") },
                                        modifier = Modifier.weight(0.45f),
                                        singleLine = true
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    OutlinedTextField(
                                        value = pair.second,
                                        onValueChange = { viewModel.newServerEnv[index] = pair.first to it },
                                        label = { Text("Value") },
                                        modifier = Modifier.weight(0.45f),
                                        singleLine = true
                                    )
                                    IconButton(onClick = { viewModel.newServerEnv.removeAt(index) }) {
                                        Icon(painterResource(Res.drawable.delete), contentDescription = "Remove")
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                            }
                            Button(onClick = { viewModel.newServerEnv.add("" to "") }) {
                                Text("Add Environment Variable")
                            }
                        }
                    }
//                } else {
//                    Column (Modifier.verticalScroll(jsonScrollState)){
//                        OutlinedTextField(
//                            value = viewModel.serverJsonString,
//                            onValueChange = { viewModel.serverJsonString = it },
//                            label = { Text("Server JSON") },
//                            modifier = Modifier.fillMaxWidth().height(350.dp)
//                        )
//                        Spacer(modifier = Modifier.height(8.dp))
//                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
//                            Button(onClick = { viewModel.pasteJsonFromClipboard() }) {
//                                Icon(painterResource(Res.drawable.content_paste), contentDescription = "Paste from Clipboard")
//                                Text("Paste from Clipboard")
//                            }
////                            Button(onClick = { viewModel.convertFormToJson() }) {
////                                Text("Generate JSON")
////                            }
//                        }
//                    }
//                }
            }
        },
        confirmButton = {
            Button(onClick = { viewModel.addServer() }) {
                Text("Add")
            }
        },
        dismissButton = {
            Button(onClick = { viewModel.dismissAddServerDialog() }) {
                Text("Cancel")
            }
        }
    )
}
@Composable
fun EditServerNameDialog() {
    val viewModel = connectMCPViewModel
    var userNotExist by remember { mutableStateOf(false) }
    OutlinedTextFieldDialog(
        title = {Text("Edit MCP server name for ${viewModel.selectedMaker!!.name}")},
        label = {Text("MCP Server Name")},
        supportingText = { value, isValid ->
            if(!isValid) Text("MCP server name length must less than 33")
        },
        onValueChange = { value,onValueChanged->
            onValueChanged(value.replace(" ","_"),value.length<33)
        },
        confirmButton = { value,isValid->
            Button(
                enabled = value.isNotEmpty()&&isValid,
                onClick = {viewModel.updateServerName(value){ code, message ->
                    when(code){
                        0-> dialog = ConnectMCPDialog.None
                        else -> {}
                    }
                }}
            ){
                Text("Save")
            }
        },
        onDismissRequest = { value, isValid ->
            dialog = ConnectMCPDialog.None
        }
    )
}

@Composable
fun EditServerTagsDialog() {
    val viewModel = connectMCPViewModel
    OutlinedTagFieldDialog(
        title = {Text("Edit MCP server tags for ${viewModel.selectedMaker!!.name}")},
        label = {Text("MCP Server Tag")},
        tags = viewModel.selectedMaker?.tags,
        onDismissRequest = { submit, tags, isValid ->
            if(submit) viewModel.updateServerTags(tags){ code, message ->
                when(code){
                    0-> dialog = ConnectMCPDialog.None
                    else -> {}
                }
            }else dialog = ConnectMCPDialog.None
        }
    )
}