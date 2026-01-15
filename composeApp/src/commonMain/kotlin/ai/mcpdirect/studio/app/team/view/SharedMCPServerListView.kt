package ai.mcpdirect.studio.app.team.view

import ai.mcpdirect.studio.app.compose.StudioActionBar
import ai.mcpdirect.studio.app.compose.StudioSearchbar
import ai.mcpdirect.studio.app.model.account.AIPortTeam
import ai.mcpdirect.studio.app.model.aitool.AIPortToolMaker
import ai.mcpdirect.studio.app.model.repository.TeamRepository
import ai.mcpdirect.studio.app.model.repository.ToolRepository
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import mcpdirectstudioapp.composeapp.generated.resources.Res
import mcpdirectstudioapp.composeapp.generated.resources.collapse_all
import mcpdirectstudioapp.composeapp.generated.resources.edit
import mcpdirectstudioapp.composeapp.generated.resources.expand_all
import org.jetbrains.compose.resources.painterResource

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
    private val _sharedToolMakerCandidateIds = MutableStateFlow(mutableSetOf<Long>())
    val sharedToolMakerCandidateIds: StateFlow<Set<Long>> = _sharedToolMakerCandidateIds
    val sharedToolMakers: StateFlow<List<AIPortToolMaker>> = combine(
        ToolRepository.toolMakers,
        _ttms,
        _ttmts
    ) { toolMakers, ttms,templates ->
        toolMakers.values.filter { maker ->
            val yes = maker.id in ttms||maker.templateId in templates
            if(yes) _sharedToolMakerCandidateIds.update { set->
                set.toMutableSet().apply {
                    add(maker.id)
                }
            }
            yes
        }.toList()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    val toolMakerFilter = MutableStateFlow("")
    val toolMakers: StateFlow<List<AIPortToolMaker>> = combine(
        ToolRepository.toolMakers,
        toolMakerFilter
    ) { makers, toolMakerFilter -> makers.values.filter { maker->
        toolMakerFilter.isEmpty()||maker.name.lowercase().contains(toolMakerFilter.lowercase())
    }.toList() }.stateIn(
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
    val ids by viewModel.sharedToolMakerCandidateIds.collectAsState()
    val toolMakers by viewModel.toolMakers.collectAsState()
    var expanded by remember { mutableStateOf(false) }
    var editable by remember { mutableStateOf(false) }
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
                    onClick = { editable = !editable },
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
                viewModel.toolMakerFilter.value = it
            }
            LazyColumn(Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if(editable) items(toolMakers){ toolMaker->
                    SharedMCPServerView(toolMaker,expanded,Modifier.fillMaxWidth())
                } else items(sharedToolMakers){ toolMaker->
                    SharedMCPServerView(toolMaker,expanded,Modifier.fillMaxWidth())
                }
            }
        }?:StudioActionBar("Shared MCP Servers")
    }
}