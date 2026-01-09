package ai.mcpdirect.studio.app.key

import ai.mcpdirect.studio.app.key.component.MCPdirectKeysComponent
import ai.mcpdirect.studio.app.key.component.MCPdirectKeysComponentViewModel
import ai.mcpdirect.studio.app.mcp.component.MCPServersComponent
import ai.mcpdirect.studio.app.mcp.component.MCPServersComponentViewModel
import ai.mcpdirect.studio.app.model.aitool.AIPortToolAccessKey
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedCard
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun MCPdirectKeysScreen(
    key: AIPortToolAccessKey?,
    paddingValues: PaddingValues = PaddingValues(),
){
    val mcpServersViewModel by remember { mutableStateOf(MCPServersComponentViewModel()) }
    val keysViewModel by remember {mutableStateOf(MCPdirectKeysComponentViewModel())}
    Row(Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),) {
        OutlinedCard(Modifier.weight(1f).fillMaxHeight()) {
            MCPdirectKeysComponent(
                key = key,
                showKeyGeneration = key==null,
                keysViewModel
            )
        }
        Card(Modifier.weight(2f).fillMaxHeight()) {

        }
        OutlinedCard(Modifier.weight(1f).fillMaxHeight()) {
            MCPServersComponent(true, viewModel = mcpServersViewModel)
        }
    }
}