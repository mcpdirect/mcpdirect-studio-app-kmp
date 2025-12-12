package ai.mcpdirect.studio.app.tips

import ai.mcpdirect.studio.app.compose.StudioBoard
import ai.mcpdirect.studio.app.compose.StudioListItem
import ai.mcpdirect.studio.app.compose.Tag
import ai.mcpdirect.studio.app.mcp.ConfigMCPServerDialog
import ai.mcpdirect.studio.app.mcp.ConfigMCPServerFromTemplatesDialog
import ai.mcpdirect.studio.app.mcp.ConnectMCPServerDialog
import ai.mcpdirect.studio.app.mcp.openapi.ConnectOpenAPIServerDialog
import ai.mcpdirect.studio.app.model.MCPServer
import ai.mcpdirect.studio.app.model.OpenAPIServer
import ai.mcpdirect.studio.app.model.aitool.AIPortToolMaker.Companion.ERROR
import ai.mcpdirect.studio.app.model.aitool.AIPortToolMaker.Companion.STATUS_OFF
import ai.mcpdirect.studio.app.model.aitool.AIPortToolMaker.Companion.STATUS_WAITING
import ai.mcpdirect.studio.app.model.repository.StudioRepository
import ai.mcpdirect.studio.app.model.repository.ToolRepository.toolMaker
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mcpdirectstudioapp.composeapp.generated.resources.Res
import mcpdirectstudioapp.composeapp.generated.resources.add
import mcpdirectstudioapp.composeapp.generated.resources.error
import mcpdirectstudioapp.composeapp.generated.resources.inbox_empty
import mcpdirectstudioapp.composeapp.generated.resources.mobiledata_off
import mcpdirectstudioapp.composeapp.generated.resources.restart_alt
import mcpdirectstudioapp.composeapp.generated.resources.save
import mcpdirectstudioapp.composeapp.generated.resources.setting_config
import org.jetbrains.compose.resources.painterResource

@Composable

fun QuickStartScreen() {
    val viewModel by remember { mutableStateOf(QuickStartViewModel()) }
    var stepIndex by remember { mutableStateOf(0) }
    val steps = listOf(
        "1. Connect MCP servers to MCPdirect",
        "2. Generate MCPdirect key for MCP servers access",
        "3. Configure MCPdirect in AI Agents"
    )
    Column(Modifier.fillMaxSize().padding(8.dp)) {
        Row(
            Modifier.fillMaxWidth().padding(top = 8.dp,bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceAround,
        ) {
            repeat(steps.size) { index ->
                Box(
                    Modifier.background(
                        if(stepIndex==index) MaterialTheme.colorScheme.tertiaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                        ButtonDefaults.shape,
                    ).height(40.dp).padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ){
                    Text(steps[index], color = if(stepIndex==index) MaterialTheme.colorScheme.onTertiaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,)
                }
            }
        }
        when(stepIndex){
            0 -> ConnectMCPView(Modifier.weight(1f),viewModel)
            1 -> GenerateMCPdirectKeyView(Modifier.weight(1f),viewModel)
        }
        Row(
            Modifier.fillMaxWidth().padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End,
        ){
            if(stepIndex>0){
                TextButton(
                    modifier = Modifier.padding(start = 8.dp),
                    onClick = {stepIndex--},
                ) {
                    Text("Previous")
                }
                Spacer(Modifier.weight(1f))
            }
            when(stepIndex){
                0 -> {
                    Text(
                        "Select MCP servers for MCPdirect key and go to ",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Button(
                        modifier = Modifier.padding(start = 8.dp),
                        enabled = !viewModel.selectedToolMakers.isEmpty(),
                        onClick = {stepIndex++}
                    ){
                        Text("Next")
                    }
                }
            }
        }

    }
}

enum class ConnectMCPDialog {
    None,
    MCP,
    MCP_TEMPLATE,
    OpenAPI,
    JSON
}

@Composable
fun ConnectMCPView(
    modifier: Modifier,
    viewModel: QuickStartViewModel
){ OutlinedCard(modifier) {
    val toolMakers by viewModel.toolMakers.collectAsState()
    var dialog by remember { mutableStateOf(ConnectMCPDialog.None) }
    var showMenu by remember { mutableStateOf(false ) }
    Row(Modifier.fillMaxSize()){
        Column(Modifier.weight(1f)) {
            Row(Modifier.padding(start = 16.dp, end = 4.dp),verticalAlignment = Alignment.CenterVertically) {
                Text("Connect MCP servers", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.weight(1f))
                IconButton(onClick = {
                    showMenu = true
                }) {
                    Icon(painterResource(Res.drawable.add), contentDescription = "")
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = {showMenu=false}
                    ){
                        DropdownMenuItem(
                            { Text("Import MCP servers from JSON") },
                            onClick = {
                                dialog = ConnectMCPDialog.JSON
                                showMenu = false
                            }
                        )
                        DropdownMenuItem(
                            { Text("Add MCP server") },
                            onClick = {
                                showMenu = false
                            }
                        )
                        DropdownMenuItem(
                            { Text("Add OpenAPI Server") },
                            onClick = {
                                showMenu = false
                            }
                        )
                    }
                }
            }
            HorizontalDivider()
            if(toolMakers.isEmpty()) StudioBoard {
                Icon(
                    painterResource(Res.drawable.inbox_empty),
                    contentDescription = null,
                    modifier = Modifier.size(80.dp).padding(bottom = 16.dp)
                )
                Text("No MCP servers available")
            } else LazyColumn {
                items(toolMakers) { toolMaker ->
                    StudioListItem(
                        selected = viewModel.currentToolMaker?.id==toolMaker.id,
                        modifier = Modifier.clickable {
                            viewModel.currentToolMaker(toolMaker)
                        },
                        headlineContent = { Text(toolMaker.name) },
                        leadingContent = {
                            if (toolMaker.status == STATUS_OFF) Icon(
                                painterResource(Res.drawable.mobiledata_off),
                                contentDescription = "Disconnect",
                                Modifier.size(48.dp).padding( 12.dp),
                                tint = MaterialTheme.colorScheme.error
                            ) else if(toolMaker.status== STATUS_WAITING){
                                CircularProgressIndicator(
                                    Modifier.size(48.dp).padding( 8.dp),
                                )
                            } else if (toolMaker.errorCode == ERROR) Icon(
                                painterResource(Res.drawable.error),
                                contentDescription = "Error",
                                Modifier.size(48.dp).padding( 12.dp),
                                tint = MaterialTheme.colorScheme.error
                            )else Checkbox(
                                checked = viewModel.selectedToolMaker(toolMaker),
                                onCheckedChange = {
                                    viewModel.selectToolMaker(it,toolMaker)
                                },
                            )
                        },
                        supportingContent = { Row(horizontalArrangement = Arrangement.spacedBy(8.dp),) {
                            when (toolMaker) {
                                is MCPServer -> {
                                    if (toolMaker.transport == 0) {
                                        Tag("STDIO")
                                    } else if (toolMaker.transport == 1) {
                                        Tag("SSE")
                                    } else if (toolMaker.transport == 2) {
                                        Tag("Streamable HTTP")
                                    }
                                }
                                is OpenAPIServer -> {
                                    Tag("OpenAPI")
                                }
                            }
                        } }
                    )
                }
            }
        }
        viewModel.currentToolMaker?.let {
            VerticalDivider()
            Column(Modifier.weight(2f)) {
                Row(Modifier.padding(start = 16.dp, end = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(it.name, style = MaterialTheme.typography.titleSmall)
                    Spacer(Modifier.weight(1f))
                    IconButton(onClick = {
                        viewModel.modifyToolMakerStatus(
                            StudioRepository.localToolAgent.value,
                            it,1
                        )
                    }){
                        Icon(
                            painterResource(Res.drawable.restart_alt), contentDescription = "",
                            Modifier.size(24.dp)
                        )
                    }
                    IconButton(onClick = {
                        if(it.templateId>0){
                            if(it.mcp()) dialog = ConnectMCPDialog.MCP_TEMPLATE
                        }else if(it.mcp()) dialog = ConnectMCPDialog.MCP
                        else if(it.openapi()) dialog = ConnectMCPDialog.OpenAPI
                    }){
                        Icon(
                            painterResource(Res.drawable.setting_config), contentDescription = "",
                            Modifier.size(24.dp)
                        )
                    }
                }
                HorizontalDivider()
                if(it.errorCode!=0){
                    Text(it.errorMessage,Modifier.padding(horizontal = 8.dp) , color = MaterialTheme.colorScheme.error)
                }else LazyColumn {
                    items(viewModel.tools) { tool ->
                        if(tool.makerId == it.id) ListItem(
                            modifier = Modifier.clickable {},
                            headlineContent = { Text(tool.name) },
                        )
                    }
                }
            }
        }
    }
    when(dialog){
        ConnectMCPDialog.None -> {}
        ConnectMCPDialog.MCP -> if(viewModel.currentToolMaker is MCPServer){
            ConfigMCPServerDialog(
                viewModel.currentToolMaker as MCPServer,
                onDismissRequest = { dialog = ConnectMCPDialog.None },
                onConfirmRequest = { mcpServer,config ->
                    viewModel.modifyMCPServerConfig(mcpServer, config)
                }
            )
        }
        ConnectMCPDialog.MCP_TEMPLATE -> ConfigMCPServerFromTemplatesDialog(
            StudioRepository.localToolAgent.value,
            viewModel.currentToolMaker!!,
            onConfirmRequest = { toolMaker,inputs->
//                myStudioViewModel.modifyMCPServerConfig(
//                    toolAgent!!,toolMaker,inputs
//                )
            },
            onDismissRequest = {
                dialog = ConnectMCPDialog.None
            }
        )
        ConnectMCPDialog.OpenAPI -> ConnectOpenAPIServerDialog(
            StudioRepository.localToolAgent.value,
            onConfirmRequest = {
                    config ->
            },
            onDismissRequest = {
                dialog = ConnectMCPDialog.None
            }
        )
        ConnectMCPDialog.JSON -> ConnectMCPServerDialog(
            "This Studio",
            onConfirmRequest = { configs ->
//                viewModel.addMCPConfigs(configs)
            },
            onDismissRequest = {
                dialog = ConnectMCPDialog.None
            }
        )
    }
} }

@Composable
fun GenerateMCPdirectKeyView(
    modifier: Modifier,
    viewModel: QuickStartViewModel
){
    Row(modifier.fillMaxSize()){
        OutlinedCard(Modifier.weight(1f).fillMaxHeight()) {
            Row(Modifier.padding(start = 16.dp, end = 4.dp),verticalAlignment = Alignment.CenterVertically) {
                Text("Generate MCPdirect Key", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.weight(1f))
                IconButton(onClick = {
                }) {
                    Icon(painterResource(Res.drawable.add), contentDescription = "")
                }
            }
            HorizontalDivider()
        }
        Spacer(Modifier.width(8.dp))
        OutlinedCard(Modifier.weight(2f).fillMaxHeight()) {
            Row(Modifier.padding(start = 16.dp, end = 4.dp),verticalAlignment = Alignment.CenterVertically) {
                Text("Select the tools that MCPdirect key can access", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.weight(1f))
                IconButton(onClick = {
                }) {
                    Icon(painterResource(Res.drawable.save), contentDescription = "")
                }
            }
            HorizontalDivider()
            Row(Modifier.fillMaxSize()) {
                LazyColumn(Modifier.padding(8.dp)) {
                    items(viewModel.selectedToolMakers) { toolMaker ->
                        val toolCount = viewModel.tools.count { it.makerId == toolMaker.id }
                        val tools = viewModel.tools.filter { it.makerId == toolMaker.id }.toList()

                        ElevatedCard {
                            ListItem(
                                headlineContent = { Text(toolMaker.name) },
                                trailingContent = {
                                    Text("$toolCount/$toolCount")
                                },
                                supportingContent = {
                                    FlowRow(
                                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                                        verticalArrangement = Arrangement.spacedBy(4.dp),
                                    ){
                                        tools.forEach { tool ->
                                            Tag(tool.name)
                                        }
                                    }
                                }
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                    }
                }
                VerticalDivider()
                LazyColumn {
                    items(viewModel.tools) { tool ->

                    }
                }
            }
        }
    }
}