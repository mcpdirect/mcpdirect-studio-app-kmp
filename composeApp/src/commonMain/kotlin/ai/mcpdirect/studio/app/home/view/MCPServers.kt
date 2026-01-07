package ai.mcpdirect.studio.app.home.view

import ai.mcpdirect.mcpdirectstudioapp.getPlatform
import ai.mcpdirect.studio.app.Screen
import ai.mcpdirect.studio.app.compose.Tag
import ai.mcpdirect.studio.app.generalViewModel
import ai.mcpdirect.studio.app.home.HomeViewModel
import ai.mcpdirect.studio.app.model.aitool.AIPortToolAgent
import ai.mcpdirect.studio.app.model.aitool.AIPortToolMaker.Companion.STATUS_ON
import ai.mcpdirect.studio.app.model.repository.StudioRepository
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import mcpdirectstudioapp.composeapp.generated.resources.Res
import mcpdirectstudioapp.composeapp.generated.resources.add
import mcpdirectstudioapp.composeapp.generated.resources.check
import mcpdirectstudioapp.composeapp.generated.resources.edit
import mcpdirectstudioapp.composeapp.generated.resources.key
import mcpdirectstudioapp.composeapp.generated.resources.plug_connect
import mcpdirectstudioapp.composeapp.generated.resources.refresh
import mcpdirectstudioapp.composeapp.generated.resources.setting_config
import mcpdirectstudioapp.composeapp.generated.resources.settings
import mcpdirectstudioapp.composeapp.generated.resources.toggle_off
import mcpdirectstudioapp.composeapp.generated.resources.toggle_on
import org.jetbrains.compose.resources.painterResource

@Composable
fun MCPServers(
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
                        Screen.QuickStart,
                        "3 steps, let any of your agents access any of your MCP servers",
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
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(toolMakers) { toolMaker ->
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
                    OutlinedCard(Modifier.weight(1f).height(100.dp)) {
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
                                        Screen.MyStudio(),
                                        "My Studios",
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