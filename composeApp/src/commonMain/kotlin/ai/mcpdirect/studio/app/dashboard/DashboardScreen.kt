package ai.mcpdirect.studio.app.dashboard

import ai.mcpdirect.studio.app.dashboard.card.MCPDirectKeyCard
import ai.mcpdirect.studio.app.dashboard.card.MyStudiosCard
import ai.mcpdirect.studio.app.dashboard.card.MyTeamCard
import ai.mcpdirect.studio.app.dashboard.shortcut.ConnectMCPShortcut
import ai.mcpdirect.studio.app.dashboard.shortcut.ConnectOpenAPIShortcut
import ai.mcpdirect.studio.app.dashboard.shortcut.CreateMCPKeyShortcut
import ai.mcpdirect.studio.app.dashboard.shortcut.CreateMCPTeamShortcut
import ai.mcpdirect.studio.app.dashboard.shortcut.Shortcut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DashboardScreen(){
    val viewModel = remember { DashboardViewModel() }
    val shortcuts = listOf(
        ConnectMCPShortcut(),
        ConnectOpenAPIShortcut(),
        CreateMCPKeyShortcut(),
        CreateMCPTeamShortcut()
    )
    Column(
        Modifier.width(1200.dp).fillMaxHeight().padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Row(
            Modifier.fillMaxWidth().height(200.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ){
            MyStudiosCard(viewModel,Modifier.weight(1.0f).fillMaxHeight())
            MCPDirectKeyCard(viewModel,Modifier.weight(1.0f).fillMaxHeight())
//            OutlinedCard(Modifier.weight(1.0f).fillMaxHeight()) {
//                Text("My Studios", modifier = Modifier.padding(16.dp))
//                HorizontalDivider()
//
//            }
            OutlinedCard(Modifier.weight(1.0f).fillMaxHeight()) {
                Text("MCP Tools", modifier = Modifier.padding(16.dp))
                HorizontalDivider()
            }
            MyTeamCard(viewModel,Modifier.weight(1.0f).fillMaxHeight())
        }
//        HorizontalDivider(Modifier.padding(vertical = 8.dp))
        Row(
            Modifier.fillMaxHeight().weight(1.0f)
        ){
            var shortcut by remember { mutableStateOf<Shortcut?>(null) }
            Column(Modifier.weight(1.0f)){
                Text(
                    "Shortcuts",
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
            shortcut?.wizard(Modifier.weight(3.0f))
        }
    }
}