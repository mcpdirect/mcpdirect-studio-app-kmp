package ai.mcpdirect.studio.app.home.widget

import ai.mcpdirect.mcpdirectstudioapp.getPlatform
import ai.mcpdirect.studio.app.Screen
import ai.mcpdirect.studio.app.compose.StudioSearchbar
import ai.mcpdirect.studio.app.generalViewModel
import ai.mcpdirect.studio.app.home.HomeViewModel
import ai.mcpdirect.studio.app.model.account.AIPortUser
import ai.mcpdirect.studio.app.model.aitool.AIPortToolAgent
import ai.mcpdirect.studio.app.model.repository.StudioRepository
import ai.mcpdirect.studio.app.model.repository.UserRepository
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import mcpdirectstudioapp.composeapp.generated.resources.Res
import mcpdirectstudioapp.composeapp.generated.resources.add
import mcpdirectstudioapp.composeapp.generated.resources.design_services
import mcpdirectstudioapp.composeapp.generated.resources.group
import mcpdirectstudioapp.composeapp.generated.resources.person
import mcpdirectstudioapp.composeapp.generated.resources.plug_connect
import mcpdirectstudioapp.composeapp.generated.resources.setting_config
import org.jetbrains.compose.resources.painterResource

@Composable
fun MCPServersWidget(
    viewModel: HomeViewModel,
    modifier: Modifier
){
    val localToolAgent by viewModel.localToolAgent.collectAsState()
    val toolMakers by viewModel.toolMakers.collectAsState()
    val scrollState = rememberScrollState()
    LaunchedEffect(viewModel) {
        viewModel.refreshToolMakers(true)
    }
    Column(modifier.padding(start =16.dp,end = 5.dp,bottom = 16.dp)) {
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
        StudioSearchbar(modifier=Modifier.padding(end=11.dp)) {
            viewModel.toolMakerFilter.value = it
        }
        if (toolMakers.isNotEmpty()) {
            Box(Modifier.weight(1f).padding(top = 8.dp)) {
                FlowRow(
                    Modifier.verticalScroll(scrollState).padding( end = 11.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    maxItemsInEachRow = 2
                ) {
                    toolMakers.forEach { toolMaker ->
                        var toolAgent by remember { mutableStateOf(AIPortToolAgent()) }
                        var user by remember { mutableStateOf<AIPortUser?>(null) }
                        LaunchedEffect(toolMaker) {
                            StudioRepository.toolAgent(toolMaker.agentId) {
                                if (it.successful()) it.data?.let { data ->
                                    toolAgent = data
                                }
                            }
                            UserRepository.user(toolMaker.userId){
                                if (it.successful()) it.data?.let { data ->
                                    user = data
                                }
                            }
                        }
                        OutlinedCard(Modifier.weight(1f).height(120.dp)) {
                            Row(
                                Modifier.padding(horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    toolMaker.name,
                                    modifier = Modifier.padding(8.dp),
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Spacer(Modifier.weight(1.0f))
                                if(UserRepository.me(toolMaker.userId)) {
                                    IconButton(
                                        onClick = {
                                            generalViewModel.currentScreen(
                                                Screen.MyStudio(toolMaker = toolMaker),
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
                                }else{
                                    IconButton(
                                        onClick = {
                                            generalViewModel.currentScreen(
                                                Screen.MCPTeam(),
                                                "My Teams",
                                                Screen.Home
                                            )
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            painterResource(Res.drawable.group),
                                            "Team",
                                            modifier = Modifier.size(16.dp),
                                        )
                                    }
                                }
                            }

                            Spacer(Modifier.weight(1.0f))
                            HorizontalDivider()
                            Row(
                                Modifier.padding(start=8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                user?.let { user ->
                                    if(UserRepository.me(user)) {
                                        Icon(painterResource(Res.drawable.design_services), contentDescription = null, Modifier.size(20.dp))
                                        Text(
                                            toolAgent.name,
                                            modifier = Modifier.padding(vertical = 8.dp),
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                        if (toolAgent.id == localToolAgent.id) Badge { Text("This device") }
                                    }else {
                                        Icon(painterResource(Res.drawable.person), contentDescription = null, Modifier.size(20.dp))
                                        Text(user.name,
                                            modifier = Modifier.padding(vertical = 8.dp),
                                            style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }
                        }
                    }
                }
                VerticalScrollbar(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    adapter = rememberScrollbarAdapter(scrollState = scrollState)
                )
            }

        } else if (getPlatform().type == 0) Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button({
                generalViewModel.currentScreen(
                    Screen.MyStudio(),
                    "Install MCP server",
                    Screen.Home
                )
            }){
                Text("Install MCP server")
            }
        }
    }
}