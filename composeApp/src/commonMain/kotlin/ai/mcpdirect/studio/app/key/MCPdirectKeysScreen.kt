package ai.mcpdirect.studio.app.key

import ai.mcpdirect.studio.app.compose.StudioActionBar
import ai.mcpdirect.studio.app.key.component.MCPdirectKeysComponent
import ai.mcpdirect.studio.app.key.component.MCPdirectKeysComponentViewModel
import ai.mcpdirect.studio.app.key.view.MCPdirectKeyToolPermissionView
import ai.mcpdirect.studio.app.key.view.MCPdirectKeyToolPermissionViewModel
import ai.mcpdirect.studio.app.mcp.component.MCPServersComponent
import ai.mcpdirect.studio.app.mcp.component.MCPServersComponentViewModel
import ai.mcpdirect.studio.app.model.aitool.AIPortToolAccessKey
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mcpdirectstudioapp.composeapp.generated.resources.Res
import mcpdirectstudioapp.composeapp.generated.resources.reset_settings
import org.jetbrains.compose.resources.painterResource

@Composable
fun MCPdirectKeysScreen(
    accessKey: AIPortToolAccessKey?,
    paddingValues: PaddingValues = PaddingValues(),
){
//    val accessKeysViewModel by remember {mutableStateOf(MCPdirectKeysComponentViewModel())}
//    val toolPermissionsViewModel by remember {mutableStateOf(MCPdirectKeyToolPermissionViewModel())}
    val mcpServersViewModel by remember { mutableStateOf(MCPServersComponentViewModel()) }
    var currentAccessKey by remember { mutableStateOf<AIPortToolAccessKey?>(null) }
//    LaunchedEffect(currentAccessKey){
//        toolPermissionsViewModel.accessKey(currentAccessKey)
//    }

    Row(Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),) {
        OutlinedCard(Modifier.weight(1f).fillMaxHeight()) {
            MCPdirectKeysComponent(
                accessKey = accessKey,
                showKeyGeneration = accessKey==null,
//                accessKeysViewModel
            ){
                currentAccessKey = it
            }
        }
        Card(Modifier.weight(2f).fillMaxHeight()) {
            currentAccessKey?.let { key ->
                StudioActionBar(
                    "Tools Permission"
                ){
                    IconButton(onClick = {}){
                        Icon(painterResource(Res.drawable.reset_settings),contentDescription = null)
                    }
                }
                MCPdirectKeyToolPermissionView(key)
            }
        }
        OutlinedCard(Modifier.weight(1f).fillMaxHeight()) {
            MCPServersComponent(true, viewModel = mcpServersViewModel)
        }
    }
}