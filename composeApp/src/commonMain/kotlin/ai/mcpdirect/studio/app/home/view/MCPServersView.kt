package ai.mcpdirect.studio.app.home.view

import ai.mcpdirect.mcpdirectstudioapp.getPlatform
import ai.mcpdirect.studio.app.Screen
import ai.mcpdirect.studio.app.generalViewModel
import ai.mcpdirect.studio.app.home.HomeViewModel
import ai.mcpdirect.studio.app.model.aitool.AIPortToolAgent
import ai.mcpdirect.studio.app.model.aitool.AIPortToolMaker.Companion.STATUS_ON
import ai.mcpdirect.studio.app.model.repository.StudioRepository
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import mcpdirectstudioapp.composeapp.generated.resources.Res
import mcpdirectstudioapp.composeapp.generated.resources.add
import mcpdirectstudioapp.composeapp.generated.resources.plug_connect
import mcpdirectstudioapp.composeapp.generated.resources.setting_config
import org.jetbrains.compose.resources.painterResource

@Composable
fun MCPServersView(
    viewModel: HomeViewModel,
    modifier: Modifier
){
    val localToolAgent by viewModel.localToolAgent.collectAsState()
    val toolMakers by viewModel.toolMakers.collectAsState()
    LaunchedEffect(viewModel) {
        viewModel.refreshToolMakers(true)
    }
    Column(modifier.padding(start =16.dp,end = 16.dp,bottom = 16.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                painterResource(Res.drawable.plug_connect),
                ""
            )
            Text("MCP Servers (${toolMakers.size})", fontWeight = FontWeight.Bold)
            Spacer(Modifier.weight(1.0f))
            IconButton(
                onClick = {
                    generalViewModel.currentScreen(
                        Screen.MyStudio(),
                        "Install MCP server",
                        Screen.Home
                    )
                }
            ) {
                Icon(
                    painterResource(Res.drawable.add),
                    contentDescription = ""
                )
            }
        }
        if (toolMakers.isNotEmpty()) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                maxItemsInEachRow = 2
            ) {
                toolMakers.forEach { toolMaker ->
                    var toolAgent by remember { mutableStateOf(AIPortToolAgent()) }
                    LaunchedEffect(toolMaker){
                        StudioRepository.toolAgent(toolMaker.agentId){
                            if(it.successful()) it.data?.let { data->
                                toolAgent = data
                            }
                        }
                    }
//                    val interactionSource = remember { MutableInteractionSource() }
//                    val isHovered by interactionSource.collectIsHoveredAsState()
                    OutlinedCard(Modifier.weight(1f).height(150.dp)) {
                        Row(
                            Modifier.padding(horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ){
                            Text(
                                toolMaker.name,
                                modifier = Modifier.padding(8.dp),
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(Modifier.weight(1.0f))
                            var checked by remember { mutableStateOf(toolMaker.status==STATUS_ON) }
                            // 2. Wrap in a Box to control the actual layout size
                            Box(
                                modifier = Modifier
                                    .size(width = 36.dp, height = 22.dp) // Manually adjusted size
                                    .wrapContentSize(Alignment.Center)
                            ) {
                                Switch(
                                    checked = checked,
                                    onCheckedChange = {checked=it},
                                    modifier = Modifier
                                        .scale(0.6f)
                                    // Remove default touch padding if it interferes with your layout
                                    // (Optional, use with caution for accessibility)
                                )
                            }
                            IconButton(
                                onClick = {
                                    generalViewModel.currentScreen(
                                        Screen.MyStudio(toolMaker=toolMaker),
                                        "MCP Servers",
                                        Screen.Home
                                    )
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    painterResource(Res.drawable.setting_config), null,
                                    modifier = Modifier.size(16.dp),
                                )
                            }
                        }

                        Spacer(Modifier.weight(1.0f))
                        HorizontalDivider()
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                if (toolAgent.id == localToolAgent.id) "This Device" else toolAgent.name,
                                modifier = Modifier.padding(8.dp),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        } else if (getPlatform().type == 0) Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Please install a MCP server to start")
        }
    }
}