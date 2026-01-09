package ai.mcpdirect.studio.app.agent

import ai.mcpdirect.studio.app.generalViewModel
import ai.mcpdirect.studio.app.home.BlankDialog
import ai.mcpdirect.studio.app.model.aitool.AIPortToolAgent
import ai.mcpdirect.studio.app.model.aitool.AIPortToolMaker
import ai.mcpdirect.studio.app.model.repository.StudioRepository
import ai.mcpdirect.studio.app.tips.ConnectMCPView
import ai.mcpdirect.studio.app.tips.GenerateMCPdirectKeyView
import ai.mcpdirect.studio.app.tips.QuickStartViewModel
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ToolAgentScreen(
    toolAgent: AIPortToolAgent?,
    toolMaker: AIPortToolMaker?,
    paddingValues: PaddingValues = PaddingValues()
){
    val viewModel by remember { mutableStateOf(QuickStartViewModel()) }
    var showMCPdirectKeysDialog by remember { mutableStateOf(false) }
    LaunchedEffect(null) {
        toolAgent?.let { agent ->
            viewModel.currentToolAgent(agent)
        }
        toolMaker?.let { maker ->
            StudioRepository.toolAgent(maker.agentId){
                if(it.successful()) it.data?.let{ agent ->
                    viewModel.currentToolAgent(agent)
                    viewModel.currentToolMaker(maker)
                }
            }
        }
        generalViewModel.topBarActions = {
            if(viewModel.selectedToolMakers.isNotEmpty()){
                Column {
                    Spacer(Modifier.height(24.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TextButton(
                            modifier = Modifier.height(32.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp),
                            onClick = {
                                showMCPdirectKeysDialog = true
                            }) {
                            Text("Grant to MCPdirect Keys", fontWeight = FontWeight.Bold)
                        }
                        TextButton(
                            modifier = Modifier.height(32.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp),
                            onClick = {
                            }) {
                            Text("Share to MCP Teams", fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Spacer(Modifier.width(16.dp))
            }
        }
    }
    DisposableEffect(null){
        onDispose {
            generalViewModel.topBarActions = {}
        }
    }
    ConnectMCPView(
        Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
        viewModel,toolAgent==null&&toolMaker==null
    )
    if(showMCPdirectKeysDialog){
        BlankDialog(
            "Grant to MCPdirect key",
            {showMCPdirectKeysDialog = false},
        ) { paddingValues ->
            Column(Modifier.fillMaxSize().padding(paddingValues).padding(start=16.dp, end = 16.dp,bottom=16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)) {
                GenerateMCPdirectKeyView(Modifier.weight(1f),viewModel = viewModel)
                Row{
                    Spacer(Modifier.weight(1f))
                    Button(
                        onClick = {}
                    ){
                        Text("Grant");
                    }
                }
            }
        }
    }
}