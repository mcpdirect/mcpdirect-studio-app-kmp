package ai.mcpdirect.studio.app.team.view

import ai.mcpdirect.studio.app.compose.StudioActionBar
import ai.mcpdirect.studio.app.compose.StudioSearchbar
import ai.mcpdirect.studio.app.model.account.AIPortTeam
import ai.mcpdirect.studio.app.model.aitool.AIPortToolMaker
import ai.mcpdirect.studio.app.model.repository.TeamRepository
import ai.mcpdirect.studio.app.model.repository.ToolRepository
import ai.mcpdirect.studio.app.virtualmcp.view.ToolMakerView
import ai.mcpdirect.studio.app.virtualmcp.view.ToolMakerViewModel
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import mcpdirectstudioapp.composeapp.generated.resources.Res
import mcpdirectstudioapp.composeapp.generated.resources.collapse_all
import mcpdirectstudioapp.composeapp.generated.resources.edit
import mcpdirectstudioapp.composeapp.generated.resources.expand_all
import mcpdirectstudioapp.composeapp.generated.resources.reset_settings
import org.jetbrains.compose.resources.painterResource
import kotlin.collections.set

class SharedMCPServerListViewModel: ViewModel() {

    val currentTeam = MutableStateFlow<AIPortTeam?>(null)
    private val _ttms: StateFlow<List<Long>> = combine(
        TeamRepository.teamToolMakers,
        currentTeam
    ) { teamToolMakers, team ->
        if(team!=null) teamToolMakers.values
            .filter { maker -> maker.teamId == team.id }
            .map{it.toolMakerId}
            .toList()
        else emptyList()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    private val _ttmts: StateFlow<List<Long>> = combine(
        TeamRepository.teamToolMakerTemplates,
        currentTeam
    ) { teamToolMakerTemplates, team ->
        if(team!=null) teamToolMakerTemplates.values
            .filter { template -> template.teamId == team.id }
            .map{it.toolMakerTemplateId}
            .toList()
        else emptyList()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    val toolMakerCandidateFilter = MutableStateFlow("")
    val sharedToolMakers: StateFlow<List<AIPortToolMaker>> = combine(
        ToolRepository.toolMakers,
        _ttms,
        _ttmts
    ) { toolMakers, ttms,templates ->
        toolMakers.values.filter { maker -> maker.id in ttms||maker.templateId in templates }.toList()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
}
@Composable
fun SharedMCPServerListView(
    team: AIPortTeam?,
    modifier: Modifier = Modifier,
){
    val viewModel by remember { mutableStateOf(SharedMCPServerListViewModel()) }
    val currentTeam by viewModel.currentTeam.collectAsState()
    val sharedToolMakers  by viewModel.sharedToolMakers.collectAsState()
    var expanded by remember { mutableStateOf(false) }
    LaunchedEffect(team) {
        viewModel.currentTeam.value=team
        if(team!=null)TeamRepository.loadTeamToolMakers(team)
    }
    Card(modifier = modifier) {
        currentTeam?.let { team ->
            StudioActionBar(
                "Shared MCP Servers (${sharedToolMakers.size}) with ${team.name ?: ""}"
            ){
                IconButton(
                    onClick = {},
                    modifier = Modifier.size(32.dp)
                ){
                    Icon(painterResource(Res.drawable.edit),contentDescription = null, Modifier.size(20.dp))
                }
                IconButton(
                    onClick = {expanded=!expanded},
                    modifier = Modifier.size(32.dp)
                ) {
                    val icon = if(expanded) Res.drawable.collapse_all else Res.drawable.expand_all
                    Icon(painterResource(icon),contentDescription = null, Modifier.size(20.dp))
                }
            }
            StudioSearchbar(modifier = Modifier.padding(16.dp)) {
                viewModel.toolMakerCandidateFilter.value = it
            }
            LazyColumn(Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(sharedToolMakers){ toolMaker->
                    SharedMCPServerView(toolMaker,expanded,Modifier.fillMaxWidth())
                }
            }
        }?:StudioActionBar("Shared MCP Servers")
    }
}