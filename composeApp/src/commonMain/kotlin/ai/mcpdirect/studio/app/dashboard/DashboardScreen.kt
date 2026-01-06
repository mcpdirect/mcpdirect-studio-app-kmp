package ai.mcpdirect.studio.app.dashboard

import ai.mcpdirect.studio.app.Screen
import ai.mcpdirect.studio.app.dashboard.card.MCPDirectKeyCard
import ai.mcpdirect.studio.app.dashboard.card.MyStudiosCard
import ai.mcpdirect.studio.app.dashboard.card.MyTeamCard
import ai.mcpdirect.studio.app.dashboard.shortcut.ConnectMCPShortcut
import ai.mcpdirect.studio.app.dashboard.shortcut.ConnectOpenAPIShortcut
import ai.mcpdirect.studio.app.dashboard.shortcut.CreateMCPKeyShortcut
import ai.mcpdirect.studio.app.dashboard.shortcut.CreateMCPTeamShortcut
import ai.mcpdirect.studio.app.dashboard.shortcut.Shortcut
import ai.mcpdirect.studio.app.generalViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mcpdirectstudioapp.composeapp.generated.resources.Res
import mcpdirectstudioapp.composeapp.generated.resources.close
import mcpdirectstudioapp.composeapp.generated.resources.mcpdirect_tips_600
import mcpdirectstudioapp.composeapp.generated.resources.mcpdirect_tips_one_url
import org.jetbrains.compose.resources.painterResource

@Composable
fun DashboardScreen(){
    val viewModel = remember { DashboardViewModel() }
    val shortcuts = listOf(
        ConnectMCPShortcut(),
        ConnectOpenAPIShortcut(),
        CreateMCPKeyShortcut(),
        CreateMCPTeamShortcut()
    )
    Row(
        Modifier.width(1200.dp).fillMaxHeight().padding(8.dp)
    ){
        var shortcut by remember { mutableStateOf<Shortcut?>(null) }
        Column(Modifier.weight(1.0f).padding(horizontal = 8.dp)) {
            Text(
                "Quick start",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(8.dp)
            )
            HorizontalDivider()
            Row(
                Modifier.padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("3",style = MaterialTheme.typography.displayMedium)
                Text(
                    "steps, let any of your agents access any of your MCP servers",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            OutlinedCard{
                Image(
                    painterResource(Res.drawable.mcpdirect_tips_600),
                    contentDescription = "MCPdirect: Universal MCP Access Gateway",
                )
            }
//            val steps = listOf(
//                "1. Connect MCP servers to MCPdirect",
//                "2. Generate MCPdirect key for access",
//                "3. Integrate MCPdirect with AI Agents"
//            )
//            repeat(steps.size) { index ->
//                Spacer(Modifier.height(4.dp))
//                Box(
//                    Modifier.fillMaxWidth().background(
//                        MaterialTheme.colorScheme.tertiaryContainer,
//                        ButtonDefaults.shape,
//                    ).height(32.dp).padding(horizontal = 12.dp),
//                    contentAlignment = Alignment.Center
//                ){
//                    Text(steps[index],
//                        style = MaterialTheme.typography.bodyMedium,
//                        color = MaterialTheme.colorScheme.onTertiaryContainer)
//                }
//            }
            Spacer(Modifier.height(4.dp))
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                generalViewModel.currentScreen(
                    Screen.QuickStart,
                    "3 steps, let any of your agents access any of your MCP servers",
                    Screen.Dashboard
                )
            }){
                Text("Let's start")
            }
            Spacer(Modifier.height(8.dp))
            Text(
                "Actions",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(8.dp)
            )
            HorizontalDivider()
            shortcuts.forEach {
                ListItem(
                    modifier = Modifier.clickable(
                        enabled = true,
                        onClick = {shortcut=it}
                    ),
                    headlineContent = {Text(it.title)}
                )
            }
        }
        if(shortcut!=null) OutlinedCard(
            Modifier.weight(3.0f),
        ){
            Row(
                verticalAlignment = Alignment.CenterVertically
            ){
                IconButton(
                    onClick = {shortcut=null}
                ){
                    Icon(painterResource(Res.drawable.close), contentDescription = "")
                }
                Text(shortcut!!.title, style = MaterialTheme.typography.titleLarge)
            }
            HorizontalDivider()
            shortcut!!.wizard()
        } else {
//            VerticalDivider()
            Column(
                Modifier.weight(3.0f).padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    Modifier.fillMaxWidth().weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ){
                    MyStudiosCard(viewModel,Modifier.weight(1.0f).fillMaxHeight())
                    MCPDirectKeyCard(viewModel,Modifier.weight(1.0f).fillMaxHeight())
                    MyTeamCard(viewModel,Modifier.weight(1.0f).fillMaxHeight())
                }
                OutlinedCard(Modifier.weight(2f)) {
                    Text("MCP Tools", modifier = Modifier.padding(16.dp))
                    HorizontalDivider()
                }
            }
        }
    }
}