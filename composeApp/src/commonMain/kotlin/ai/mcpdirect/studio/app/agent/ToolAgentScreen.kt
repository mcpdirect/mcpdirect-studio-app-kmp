package ai.mcpdirect.studio.app.agent

import ai.mcpdirect.studio.app.agent.component.ToolAgentComponent
import ai.mcpdirect.studio.app.agent.component.ToolAgentComponentViewModel
import ai.mcpdirect.studio.app.generalViewModel
import ai.mcpdirect.studio.app.home.BlankDialog
import ai.mcpdirect.studio.app.model.account.AIPortTeam
import ai.mcpdirect.studio.app.model.aitool.AIPortTool
import ai.mcpdirect.studio.app.model.aitool.AIPortToolAccessKey
import ai.mcpdirect.studio.app.model.aitool.AIPortToolAgent
import ai.mcpdirect.studio.app.model.aitool.AIPortToolMaker
import ai.mcpdirect.studio.app.model.repository.StudioRepository
import ai.mcpdirect.studio.app.model.repository.ToolRepository.tools
import ai.mcpdirect.studio.app.team.TeamScreen
import ai.mcpdirect.studio.app.tips.ConnectMCPView
import ai.mcpdirect.studio.app.tips.GenerateMCPdirectKeyView
import ai.mcpdirect.studio.app.tips.QuickStartViewModel
import ai.mcpdirect.studio.app.tips.component.MCPdirectKeyQuickstartComponent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
enum class ToolAgentScreenDialog{
    None,
    GrantToMCPdirectKeys,
    ShareToMCPTeams
}
@Composable
fun ToolAgentScreen(
    toolAgent: AIPortToolAgent?,
    toolMaker: AIPortToolMaker?,
    paddingValues: PaddingValues = PaddingValues()
){
    val viewModel by remember { mutableStateOf(ToolAgentComponentViewModel()) }
    var dialog by remember { mutableStateOf(ToolAgentScreenDialog.None) }
    val showCatalog =
        if(toolAgent==null&&toolMaker==null) 0
        else if(toolMaker!=null&&toolMaker.id<Int.MAX_VALUE) toolMaker.id
        else -1L
    LaunchedEffect(null) {
        toolAgent?.let { agent ->
            viewModel.currentToolAgent(agent)
        }
        toolMaker?.let { maker ->
            if(maker.id>Int.MAX_VALUE) StudioRepository.toolAgent(maker.agentId){
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
                                dialog = ToolAgentScreenDialog.GrantToMCPdirectKeys
                            }) {
                            Text("Grant to MCPdirect Keys", fontWeight = FontWeight.Bold)
                        }
                        TextButton(
                            modifier = Modifier.height(32.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp),
                            onClick = {
                                dialog = ToolAgentScreenDialog.ShareToMCPTeams
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
    ToolAgentComponent(
        Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
        viewModel,
        showCatalog
    )
    when(dialog){
        ToolAgentScreenDialog.None->{}
        ToolAgentScreenDialog.GrantToMCPdirectKeys ->BlankDialog(
            "Grant to MCPdirect key",
            {dialog = ToolAgentScreenDialog.None},
        ) { paddingValues ->
            var enableGrant by remember { mutableStateOf(false)}
            var selectedAccessKey by remember { mutableStateOf<AIPortToolAccessKey?>(null)}
            var selectedTools by remember { mutableStateOf<List<AIPortTool>>(emptyList())}
            Column(Modifier.fillMaxSize().padding(paddingValues).padding(start=16.dp, end = 16.dp,bottom=16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)) {
                MCPdirectKeyQuickstartComponent(
                    viewModel.selectedToolMakers,
                    viewModel.selectedTools,
                    Modifier.weight(1f)
                ){ accessKey, tools ->
                    selectedAccessKey = accessKey
                    selectedTools = tools
                    enableGrant = accessKey!=null && tools.isNotEmpty()
                }
                Row{
                    Spacer(Modifier.weight(1f))
                    Button(
                        enabled = enableGrant,
                        onClick = {
                            viewModel.grantToolPermissions(selectedAccessKey!!,selectedTools)
                            dialog = ToolAgentScreenDialog.None
                        }
                    ){
                        Text("Grant");
                    }
                }
            }
        }
        ToolAgentScreenDialog.ShareToMCPTeams ->BlankDialog(
            "Share to MCP Teams",
            {dialog = ToolAgentScreenDialog.None},
        ) { paddingValues ->
            TeamScreen(
                team = AIPortTeam(),
                toolMakers = viewModel.selectedToolMakers,
                editable = true, paddingValues=paddingValues
            )
        }
    }
}