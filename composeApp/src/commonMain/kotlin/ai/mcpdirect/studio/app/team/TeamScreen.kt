package ai.mcpdirect.studio.app.team

import ai.mcpdirect.studio.app.model.account.AIPortTeam
import ai.mcpdirect.studio.app.model.aitool.AIPortToolMaker
import ai.mcpdirect.studio.app.model.repository.TeamRepository
import ai.mcpdirect.studio.app.model.repository.ToolRepository
import ai.mcpdirect.studio.app.team.view.SharedMCPServerListView
import ai.mcpdirect.studio.app.team.view.SharedMCPServerView
import ai.mcpdirect.studio.app.team.view.TeamListView
import ai.mcpdirect.studio.app.team.view.TeamMemberView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel

class TeamScreenViewModel : ViewModel() {
    var currentTeam by mutableStateOf<AIPortTeam?>(null)
        private set
    fun currentTeam(team: AIPortTeam?){
        currentTeam = team
    }
}
@Composable
fun TeamScreen(
    team: AIPortTeam?=null,
    toolMakers: List<AIPortToolMaker>?=null,
    editable: Boolean = false,
    paddingValues: PaddingValues
){
    val viewModel by remember { mutableStateOf(TeamScreenViewModel()) }
    LaunchedEffect(viewModel) {
        TeamRepository.loadTeams()
        ToolRepository.loadToolMakers()
        ToolRepository.loadToolMakerTemplates()
    }
    Row(Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),) {
        OutlinedCard(Modifier.weight(1f).fillMaxHeight()) {
            TeamListView(team,team==null,modifier = Modifier.fillMaxHeight()) {
                viewModel.currentTeam(it)
            }
        }
        Column(Modifier.weight(2f)) {
            SharedMCPServerListView(
                viewModel.currentTeam,
                toolMakers,
                editable,
                Modifier.weight(2f).fillMaxWidth())
        }
        TeamMemberView(viewModel.currentTeam,Modifier.weight(1f).fillMaxHeight())
    }
}