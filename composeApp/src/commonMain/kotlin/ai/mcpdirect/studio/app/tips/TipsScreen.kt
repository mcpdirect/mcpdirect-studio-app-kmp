package ai.mcpdirect.studio.app.tips

import ai.mcpdirect.studio.app.Screen
import ai.mcpdirect.studio.app.generalViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.font.FontWeight
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
            "Let MCP power your work",
            Modifier.padding(top = 32.dp,bottom = 16.dp),
            style = MaterialTheme.typography.displayLarge)
//        https://storage.googleapis.com/mcpdirect-image/tips/mcpdirect_diagram_simple.png
        Spacer(Modifier.height(16.dp))
        Column(Modifier.width(900.dp),verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("Quick Start",style = MaterialTheme.typography.displayMedium)
//            Text("3 steps, Let any of your agents access any of your private MCP servers",
//                style = MaterialTheme.typography.headlineMedium)
            Row(Modifier.fillMaxWidth()) {
                val steps = listOf(
                    "1. Connect MCP servers to MCPdirect",
                    "2. Generate MCPdirect key for access",
                    "3. Integrate MCPdirect with AI Agents"
                )
                Column(Modifier.padding(end=16.dp).weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
//                Text("3 steps,",style = MaterialTheme.typography.headlineLarge)
//                Text("Any of your agents access",style = MaterialTheme.typography.headlineLarge)
//                Text("Access any of your",style = MaterialTheme.typography.headlineLarge)
//                Text("Access",style = MaterialTheme.typography.headlineLarge)
//                    Text("Any of your MCP servers",style = MaterialTheme.typography.headlineLarge)
                Text("3 steps, let any of your agents access any of your MCP servers",
                    style = MaterialTheme.typography.headlineLarge)
                    repeat(steps.size) { index ->
                        Box(
                            Modifier.background(
                                MaterialTheme.colorScheme.tertiaryContainer,
                                ButtonDefaults.shape,
                            ).height(40.dp).padding(horizontal = 16.dp),
                            contentAlignment = Alignment.Center
                        ){
                            Text(steps[index], color = MaterialTheme.colorScheme.onTertiaryContainer)
                        }
                    }
                }
                OutlinedCard(Modifier.width(500.dp)) {
                    Box(
                        Modifier.clickable {
                            generalViewModel.currentScreen(
                                Screen.QuickStart,
                                "3 steps, let any of your agents access any of your in-house MCP servers",
                                Screen.Home
                            )
                        },
                    ) {
                        Image(
                            painterResource(Res.drawable.mcpdirect_tips_one_url),
                            contentDescription = "MCPdirect: Universal MCP Access Gateway",
                        )
                        Column(Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Spacer(Modifier.weight(1f))
//                                Icon(painterResource(Res.drawable.keyboard_arrow_right), contentDescription = "")
                                Button(onClick = {
                                    generalViewModel.currentScreen(
                                        Screen.QuickStart,
                                        "3 steps, let any of your agents access any of your in-house MCP servers",
                                        Screen.Home
                                    )
                                }){
                                    Text("Let's start")
                                }
                            }
                        }
                    }
                }
            }

        }

//        Spacer(Modifier.height(16.dp))
//        OutlinedCard(Modifier.width(900.dp),) {
//            Row(Modifier.padding(start = 16.dp, end = 4.dp),verticalAlignment = Alignment.CenterVertically) {
////                Text("3 steps to share your MCP servers with your team",
////                    style = MaterialTheme.typography.titleLarge)
//                Text("3 steps, let any of your agents access any of your in-house MCP servers", style = MaterialTheme.typography.titleLarge)
//                Spacer(Modifier.weight(1f))
////                IconButton(onClick = {}) {
////                    Icon(painterResource(Res.drawable.keyboard_arrow_right), contentDescription = "")
////                }
//                TextButton(
//                    onClick = {}
//                ){
//                    Text("Let's start")
//                }
//            }
//            HorizontalDivider()
//            Row(Modifier.padding(16.dp).fillMaxWidth(),
//                horizontalArrangement = Arrangement.SpaceBetween) {
//                                        Text("1. Connect MCP servers to MCPdirect", style = MaterialTheme.typography.bodyLarge)
//                        Text("2. Generate MCPdirect key to access", style = MaterialTheme.typography.bodyLarge)
//                        Text("3. Configure MCPdirect in AI Agents", style = MaterialTheme.typography.bodyLarge)
//
////                TextButton(onClick = {  }) {
////                    Text("1. Create a team for sharing")
////                }
////                TextButton(onClick = {  }) {
////                    Text("2. Invite members to your team")
////                }
////                TextButton(onClick = {  }) {
////                    Text("3. Share MCP servers with the team")
////                }
//            }
//        }
    }
}