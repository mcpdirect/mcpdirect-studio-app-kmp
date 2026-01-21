package ai.mcpdirect.studio.app.home.widget

import ai.mcpdirect.studio.app.Screen
import ai.mcpdirect.studio.app.generalViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import mcpdirectstudioapp.composeapp.generated.resources.Res
import mcpdirectstudioapp.composeapp.generated.resources.lightbulb_2
import mcpdirectstudioapp.composeapp.generated.resources.mcpdirect_tips_600
import org.jetbrains.compose.resources.painterResource

@Composable
fun QuickstartWidget(modifier: Modifier = Modifier) {
    Column(modifier) {
        Row(
            Modifier.fillMaxWidth().height(48.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                "Quick start",
                style = MaterialTheme.typography.titleLarge,
            )
        }
        HorizontalDivider()
        Row(
            Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
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
        Spacer(Modifier.height(4.dp))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                generalViewModel.currentScreen(
                    Screen.QuickStart,
                    "3 steps, let any of your agents access any of your MCP servers",
                    Screen.Home
                )
            }){
            Text("Let's start")
        }
    }
}