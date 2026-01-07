package ai.mcpdirect.studio.app.home.view

import ai.mcpdirect.mcpdirectstudioapp.getPlatform
import ai.mcpdirect.studio.app.Screen
import ai.mcpdirect.studio.app.dashboard.DashboardViewModel
import ai.mcpdirect.studio.app.generalViewModel
import ai.mcpdirect.studio.app.home.HomeViewModel
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import mcpdirectstudioapp.composeapp.generated.resources.Res
import mcpdirectstudioapp.composeapp.generated.resources.add
import mcpdirectstudioapp.composeapp.generated.resources.group
import mcpdirectstudioapp.composeapp.generated.resources.groups
import mcpdirectstudioapp.composeapp.generated.resources.refresh
import org.jetbrains.compose.resources.painterResource

@Composable
fun MyTeams(
    viewModel: HomeViewModel,
    modifier: Modifier
){
    val teams by viewModel.teams.collectAsState()
    LaunchedEffect(viewModel) {
        viewModel.refreshTeams()
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
                painterResource(Res.drawable.group),
                ""
            )
            Text("My Teams (${teams.size})", fontWeight = FontWeight.Bold)
            Spacer(Modifier.weight(1.0f))
            if(isHovered)IconButton(
                onClick = {
                    generalViewModel.currentScreen(
                        Screen.MCPTeam(),
                        "My Team",
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
        if (teams.isNotEmpty()) {
            LazyColumn {
                items(teams) { team ->
                    TextButton(
                        modifier = Modifier.height(32.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        onClick = {
                            generalViewModel.currentScreen(
                                Screen.MCPTeam(),
                                "My Team",
                                Screen.Home
                            )
                        },
//                        border = BorderStroke(1.dp, ButtonDefaults.textButtonColors().contentColor)
                    ) {Text(team.name)}
                }
            }
        } else if (getPlatform().type == 0) Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Please create a team to start")
        }
    }
}