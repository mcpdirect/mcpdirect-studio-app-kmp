package ai.mcpdirect.studio.app.home.widget

import ai.mcpdirect.mcpdirectstudioapp.getPlatform
import ai.mcpdirect.studio.app.Screen
import ai.mcpdirect.studio.app.generalViewModel
import ai.mcpdirect.studio.app.home.HomeViewModel
import ai.mcpdirect.studio.app.model.aitool.AIPortToolAgent
import ai.mcpdirect.studio.app.model.repository.StudioRepository
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import mcpdirectstudioapp.composeapp.generated.resources.Res
import mcpdirectstudioapp.composeapp.generated.resources.add
import mcpdirectstudioapp.composeapp.generated.resources.plug_connect
import mcpdirectstudioapp.composeapp.generated.resources.setting_config
import mcpdirectstudioapp.composeapp.generated.resources.virtual_machine
import org.jetbrains.compose.resources.painterResource

@Composable
fun VirtualMCPWidget(
    viewModel: HomeViewModel,
    modifier: Modifier
){
    val toolMakers by viewModel.virtualToolMakers.collectAsState()
    LaunchedEffect(viewModel) {
        viewModel.refreshToolMakers(true)
    }
    Column(modifier.padding(start =16.dp,end = 16.dp,bottom = 16.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                painterResource(Res.drawable.virtual_machine),
                ""
            )
            Text("Virtual MCP (${toolMakers.size})", fontWeight = FontWeight.Bold)
            Spacer(Modifier.weight(1.0f))
            IconButton(
                onClick = {
                    generalViewModel.currentScreen(
                        Screen.MyStudio(),
                        "Create Virtual MCP",
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
                    OutlinedCard(Modifier.weight(1f).height(80.dp)) {
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
                            IconButton(
                                onClick = {
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    painterResource(Res.drawable.setting_config),
                                    "Create Virtual MCP",
                                    modifier = Modifier.size(16.dp),
                                )
                            }
                        }

                    }

                }
            }
        } else if (getPlatform().type == 0) Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button({
                generalViewModel.currentScreen(
                    Screen.MyStudio(),
                    "Create Virtual MCP",
                    Screen.Home
                )
            }){
                Text("Create Virtual MCP")
            }
        }
    }
}