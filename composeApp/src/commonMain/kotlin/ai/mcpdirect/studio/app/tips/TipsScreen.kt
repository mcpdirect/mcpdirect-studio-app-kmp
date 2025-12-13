package ai.mcpdirect.studio.app.tips

import ai.mcpdirect.studio.app.Screen
import ai.mcpdirect.studio.app.generalViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mcpdirectstudioapp.composeapp.generated.resources.Res
import mcpdirectstudioapp.composeapp.generated.resources.keyboard_arrow_right
import mcpdirectstudioapp.composeapp.generated.resources.mcpdirect_tips_one_url
import org.jetbrains.compose.resources.painterResource

@Composable
fun TipsScreen() {
    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            "Let MCP power your business",
            Modifier.padding(vertical = 16.dp),
            style = MaterialTheme.typography.displayMedium)
//        https://storage.googleapis.com/mcpdirect-image/tips/mcpdirect_diagram_simple.png
        Spacer(Modifier.height(16.dp))
        OutlinedCard(Modifier.width(900.dp),) {
            Row(Modifier.padding(start = 16.dp, end = 4.dp),verticalAlignment = Alignment.CenterVertically) {
                Text("3 steps, let any of your agents access any of your in-house MCP servers",
                    style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.weight(1f))
                IconButton(onClick = { generalViewModel.currentScreen(
                    Screen.QuickStart,
                    "3 steps, let any of your agents access any of your in-house MCP servers",
                    Screen.Tips
                ) }) {
                    Icon(painterResource(Res.drawable.keyboard_arrow_right), contentDescription = "")
                }
            }

            HorizontalDivider()
            Box{
                Image(
                    painterResource(Res.drawable.mcpdirect_tips_one_url),
                    contentDescription = "MCPdirect: Universal MCP Access Gateway",
//                    Modifier.padding(top = 8.dp, start = 16.dp, end = 16.dp, bottom = 16.dp).fillMaxWidth().clip(CardDefaults.shape),
                )
                Row(Modifier.padding(horizontal = 8.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween) {
                    TextButton(onClick = {  }) {
                        Text("1. Connect MCP servers to MCPdirect")
                    }
                    TextButton(onClick = {  }) {
                        Text("2. Generate MCPdirect key for MCP servers access")
                    }
                    TextButton(onClick = {  }) {
                        Text("3. Configure MCPdirect in AI Agents")
                    }
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        OutlinedCard(Modifier.width(900.dp),) {
            Row(Modifier.padding(start = 16.dp, end = 4.dp),verticalAlignment = Alignment.CenterVertically) {
                Text("3 steps to share your MCP servers with your team",
                    style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.weight(1f))
                IconButton(onClick = {}) {
                    Icon(painterResource(Res.drawable.keyboard_arrow_right), contentDescription = "")
                }
            }
            HorizontalDivider()
            Row(Modifier.padding(16.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween) {
                TextButton(onClick = {  }) {
                    Text("1. Create a team for sharing")
                }
                TextButton(onClick = {  }) {
                    Text("2. Invite members to your team")
                }
                TextButton(onClick = {  }) {
                    Text("3. Share MCP servers with the team")
                }
            }
        }
    }
}