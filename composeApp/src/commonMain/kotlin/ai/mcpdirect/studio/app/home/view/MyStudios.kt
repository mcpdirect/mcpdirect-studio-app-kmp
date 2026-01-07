package ai.mcpdirect.studio.app.home.view

import ai.mcpdirect.mcpdirectstudioapp.getPlatform
import ai.mcpdirect.studio.app.Screen
import ai.mcpdirect.studio.app.compose.Tag
import ai.mcpdirect.studio.app.dashboard.DashboardViewModel
import ai.mcpdirect.studio.app.generalViewModel
import ai.mcpdirect.studio.app.home.HomeViewModel
import ai.mcpdirect.studio.app.model.repository.UserRepository
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import mcpdirectstudioapp.composeapp.generated.resources.Res
import mcpdirectstudioapp.composeapp.generated.resources.add
import mcpdirectstudioapp.composeapp.generated.resources.cloud_off
import mcpdirectstudioapp.composeapp.generated.resources.design_services
import mcpdirectstudioapp.composeapp.generated.resources.plug_connect
import mcpdirectstudioapp.composeapp.generated.resources.refresh
import org.jetbrains.compose.resources.painterResource

@Composable
fun MyStudios(
    viewModel: HomeViewModel,
    modifier: Modifier
){
    val toolAgents by viewModel.toolAgents.collectAsState()
    LaunchedEffect(viewModel) {
        viewModel.refreshToolAgents()
    }
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    Column(modifier.padding(start=16.dp).hoverable(interactionSource)) {
        Row(
            modifier = Modifier.height(48.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                painterResource(Res.drawable.design_services),
                ""
            )
            Text("My Studios (${toolAgents.size - 1})", fontWeight = FontWeight.Bold)
            Spacer(Modifier.weight(1.0f))
            if(isHovered)IconButton(
                onClick = {
                    generalViewModel.currentScreen(
                        Screen.MyStudio(),
                        "My Studios",
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
//        HorizontalDivider()
        if (toolAgents.size > 1) {
            val localToolAgent by viewModel.localToolAgent.collectAsState()
            LazyColumn {
                items(toolAgents) {
                    if (it.id != 0L && UserRepository.me(it.userId)) {
                        TextButton(
                            modifier = Modifier.height(32.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            onClick = {
                                generalViewModel.currentScreen(
                                    Screen.MyStudio(),
                                    "My Studios",
                                    Screen.Home
                                )
                            },
//                            border = BorderStroke(1.dp, ButtonDefaults.textButtonColors().contentColor),
//                            elevation = ButtonDefaults.elevatedButtonElevation(1.dp)
                        ) {
                            Text(
                                it.name, softWrap = false,
                                overflow = TextOverflow.MiddleEllipsis
                            )
                            if (it.id == localToolAgent.id) Badge(Modifier.padding(start=8.dp)) { Text("This device") }
                        }

                    }
//                    if (it.id != 0L && UserRepository.me(it.userId)) ListItem(
////                        modifier = Modifier.clickable(
////                            enabled = it.status == AIPortToolMaker.STATUS_ON
////                        ) {
////                            generalViewModel.currentScreen(Screen.MyStudio(it))
////                        },
//                        headlineContent = {
//                            Text(
//                                it.name, softWrap = false,
//                                overflow = TextOverflow.MiddleEllipsis
//                            )
//                        },
//                        supportingContent = {
//                            if (it.id == localToolAgent.id)
//                                Tag("This device")
//                        },
//                        trailingContent = {
//                            if (it.status == 0) Icon(
//                                painterResource(Res.drawable.cloud_off),
//                                contentDescription = "Offline",
//                                tint = MaterialTheme.colorScheme.error
//                            )
//                        },
//                    )
                }
            }
        } else if (getPlatform().type == 0) Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val uriHandler = LocalUriHandler.current
            Text("Please download MCPdirect Studio to start")
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    uriHandler.openUri("https://github.com/mcpdirect/mcpdirect-studio-app-kmp/releases")
                }
            ) {
                Text("Download")
            }
        }
    }
}