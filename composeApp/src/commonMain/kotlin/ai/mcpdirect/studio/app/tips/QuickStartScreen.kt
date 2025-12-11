package ai.mcpdirect.studio.app.tips

import ai.mcpdirect.studio.app.compose.StudioBoard
import ai.mcpdirect.studio.app.compose.Tag
import ai.mcpdirect.studio.app.mcp.ConnectMCPServerDialog
import ai.mcpdirect.studio.app.mcp.openapi.ConnectOpenAPIServerDialog
import ai.mcpdirect.studio.app.model.MCPServerConfig
import ai.mcpdirect.studio.app.model.OpenAPIServerConfig
import ai.mcpdirect.studio.app.model.repository.StudioRepository
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import mcpdirectstudioapp.composeapp.generated.resources.Res
import mcpdirectstudioapp.composeapp.generated.resources.add
import mcpdirectstudioapp.composeapp.generated.resources.delete
import mcpdirectstudioapp.composeapp.generated.resources.error
import mcpdirectstudioapp.composeapp.generated.resources.inbox_empty
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
            Modifier.fillMaxWidth().padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
        ) {
            repeat(steps.size) { index ->
                Box(
                    Modifier.background(
                        if(stepIndex==index) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                        ButtonDefaults.shape,
                    ).height(40.dp).padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ){
                    Text(steps[index])
                }
            }
        }
        OutlinedCard(Modifier.weight(1f)) {
            when(stepIndex){
                0 -> ConnectMCPView(viewModel)
            }
        }

    }
}

enum class ConnectMCPDialog {
    None,
    MCP,
    OpenAPI,
    JSON
}

@Composable
fun ConnectMCPView(
    viewModel: QuickStartViewModel
){
    var dialog by remember { mutableStateOf<ConnectMCPDialog>(ConnectMCPDialog.None) }
    var showMenu by remember { mutableStateOf(false ) }
    Row(Modifier.padding(start = 16.dp, end = 4.dp),verticalAlignment = Alignment.CenterVertically) {
        Text("Connect MCP servers",
            style = MaterialTheme.typography.titleLarge)
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
                    { Text("Import from JSON") },
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
    if(viewModel.mcpConfigs.isEmpty()) StudioBoard {
        Icon(
            painterResource(Res.drawable.inbox_empty),
            contentDescription = null,
            modifier = Modifier.size(80.dp).padding(bottom = 16.dp)
        )
        Text("No MCP servers available")
    } else LazyColumn {
        items(viewModel.mcpConfigs) { mcpConfig ->
            var checked by remember { mutableStateOf(mcpConfig.status==1) }
            ListItem(
                modifier = Modifier.clickable {},
                headlineContent = { Text(mcpConfig.name) },
                trailingContent = {
                    Row{
                        if(mcpConfig.status > -1) Switch(
                            checked,
                            modifier = Modifier.scale(0.65f),
                            onCheckedChange = {
                                checked = it
                                mcpConfig.status = if (checked) 1 else 0

                            }
                        )

                        IconButton(
                            onClick = {
                                viewModel.removeMCPConfig(mcpConfig)
                            }
                        ){
                            Icon(
                                painterResource(Res.drawable.delete),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                },
                leadingContent = {
                    if(mcpConfig.status < 0) Box(
                        Modifier.size(48.dp),
                        contentAlignment = Alignment.Center
                    ){
                        Icon(
                            painterResource(Res.drawable.error),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                        )
                    }else Checkbox(
                        checked = false,
                        onCheckedChange = {})
                },
                supportingContent = {
                    when (mcpConfig.status) {
                        -1 -> Tag("Invalid MCP server config", color = MaterialTheme.colorScheme.error)
                        -2 -> Tag("MCP server type is required, color = MaterialTheme.colorScheme.error")
                        else -> when(mcpConfig) {
                            is MCPServerConfig -> {
                                if(mcpConfig.transport==0){
                                    Tag("STDIO")
                                } else if(mcpConfig.transport==1){
                                    Tag("SSE")
                                } else if(mcpConfig.transport==2){
                                    Tag("Streamable HTTP")
                                }
                            }
                            is OpenAPIServerConfig ->{
                                Tag("OpenAPI")
                            }
                        }
                    }

                }
            )
        }
    }

    when(dialog){
        ConnectMCPDialog.None -> {}
        ConnectMCPDialog.MCP -> {}
        ConnectMCPDialog.OpenAPI -> ConnectOpenAPIServerDialog(
            StudioRepository.localToolAgent.value,
            onConfirmRequest = {
                    name,config ->
            },
            onDismissRequest = {
                dialog = ConnectMCPDialog.None
            }
        )
        ConnectMCPDialog.JSON -> ConnectMCPServerDialog(
            "This Studio",
            onConfirmRequest = { configs ->
                viewModel.addMCPConfigs(configs)
            },
            onDismissRequest = {
                dialog = ConnectMCPDialog.None
            }
        )
    }
}