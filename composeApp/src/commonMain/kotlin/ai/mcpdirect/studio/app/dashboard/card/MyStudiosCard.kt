package ai.mcpdirect.studio.app.dashboard.card

import ai.mcpdirect.mcpdirectstudioapp.getPlatform
import ai.mcpdirect.studio.app.Screen
import ai.mcpdirect.studio.app.UIState
import ai.mcpdirect.studio.app.auth.authViewModel
import ai.mcpdirect.studio.app.compose.StudioListItem
import ai.mcpdirect.studio.app.compose.Tag
import ai.mcpdirect.studio.app.dashboard.DashboardViewModel
import ai.mcpdirect.studio.app.generalViewModel
import ai.mcpdirect.studio.app.model.aitool.AIPortToolMaker
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import mcpdirectstudioapp.composeapp.generated.resources.Res
import mcpdirectstudioapp.composeapp.generated.resources.cloud_off
import mcpdirectstudioapp.composeapp.generated.resources.plug_connect
import org.jetbrains.compose.resources.painterResource

@Composable
fun MyStudiosCard(
    viewModel: DashboardViewModel,
    modifier: Modifier
){
    LaunchedEffect(viewModel) {
        viewModel.refreshToolAgents()
    }
    OutlinedCard(modifier) {
        val toolAgents by viewModel.toolAgents.collectAsState()
        Row(
            Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                painterResource(Res.drawable.plug_connect),
                ""
            )
            Text("My Studios (${toolAgents.size})")
        }
        HorizontalDivider()
        if(toolAgents.size>1) {
            val localToolAgent by viewModel.localToolAgent.collectAsState()
            LazyColumn {
                items(toolAgents) {
                    println("${it.id},${it.name}")
                    if (it.id != 0L && it.userId == authViewModel.user.id) ListItem(
                        modifier = Modifier.clickable(
                            enabled = it.status == AIPortToolMaker.STATUS_ON
                        ) {
                            generalViewModel.currentScreen(Screen.MyStudio(it))
                        },
                        headlineContent = { Text(it.name, softWrap = false, overflow = TextOverflow.MiddleEllipsis) },
                        supportingContent = {
                            if (it.id == localToolAgent.id)
                                Tag("This device")
                        },
                        trailingContent = {
                            if (it.status == 0) Icon(
                                painterResource(Res.drawable.cloud_off),
                                contentDescription = "Offline",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    )
                }
            }
        } else if(getPlatform().type == 0) Column(
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
            ){
                Text("Download")
            }
        }
    }
}