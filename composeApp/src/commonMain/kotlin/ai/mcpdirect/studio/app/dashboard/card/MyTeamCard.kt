package ai.mcpdirect.studio.app.dashboard.card

import ai.mcpdirect.mcpdirectstudioapp.getPlatform
import ai.mcpdirect.studio.app.compose.StudioBoard
import ai.mcpdirect.studio.app.dashboard.DashboardViewModel
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.unit.dp
import mcpdirectstudioapp.composeapp.generated.resources.Res
import mcpdirectstudioapp.composeapp.generated.resources.groups
import mcpdirectstudioapp.composeapp.generated.resources.key
import mcpdirectstudioapp.composeapp.generated.resources.refresh
import org.jetbrains.compose.resources.painterResource

@Composable
fun MyTeamCard(
    viewModel: DashboardViewModel,
    modifier: Modifier
){
    LaunchedEffect(viewModel) {
        viewModel.refreshTeams()
    }
    OutlinedCard(modifier) {
        val teams by viewModel.teams.collectAsState()
        Row(
            Modifier.padding(8.dp).padding(start = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                painterResource(Res.drawable.groups),
                ""
            )
            Text("My Teams (${teams.size})")
            Spacer(Modifier.weight(1.0f))
            IconButton(
                onClick = {viewModel.refreshTeams(true)}
            ){
                Icon(
                    painterResource(Res.drawable.refresh),
                    contentDescription = ""
                )
            }
        }
        HorizontalDivider()
        if(teams.isNotEmpty()) {
            LazyColumn {
                items(teams) { team ->
                    ListItem(
                        headlineContent = {Text(team.name) },
                    )
                }
            }
        } else if(getPlatform().type == 0) Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val uriHandler = LocalUriHandler.current
            Text("Please create a team to start")
//            Spacer(modifier = Modifier.height(16.dp))
//            Button(
//                onClick = {
//                    uriHandler.openUri("https://github.com/mcpdirect/mcpdirect-studio-app-kmp/releases")
//                }
//            ){
//                Text("Download")
//            }
        }
    }
}