package ai.mcpdirect.studio.app.virtual

import ai.mcpdirect.studio.app.Screen
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import mcpdirectstudioapp.composeapp.generated.resources.Res
import mcpdirectstudioapp.composeapp.generated.resources.add
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VirtualMCPScreen(
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Screen.VirtualMCP.title) ) },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(
                            painterResource(Res.drawable.add),
                            contentDescription = "Add Virtual MCP Server"
                        )
                    }

                }
            )
        }
    ) { padding ->
        when {
        }
    }
}