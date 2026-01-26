package ai.mcpdirect.studio.app.team.view

import ai.mcpdirect.studio.app.compose.StudioActionBar
import ai.mcpdirect.studio.app.compose.StudioSearchbar
import ai.mcpdirect.studio.app.generalViewModel
import ai.mcpdirect.studio.app.model.account.AIPortTeam
import ai.mcpdirect.studio.app.model.aitool.AIPortTeamToolMaker
import ai.mcpdirect.studio.app.model.aitool.AIPortToolMaker
import ai.mcpdirect.studio.app.model.repository.TeamRepository
import ai.mcpdirect.studio.app.model.repository.ToolRepository
import ai.mcpdirect.studio.app.model.repository.UserRepository
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import mcpdirectstudioapp.composeapp.generated.resources.Res
import mcpdirectstudioapp.composeapp.generated.resources.close
import mcpdirectstudioapp.composeapp.generated.resources.collapse_all
import mcpdirectstudioapp.composeapp.generated.resources.edit
import mcpdirectstudioapp.composeapp.generated.resources.expand_all
import mcpdirectstudioapp.composeapp.generated.resources.forms_add_on
import mcpdirectstudioapp.composeapp.generated.resources.shield_toggle
import org.jetbrains.compose.resources.painterResource
import kotlin.collections.set
import kotlin.collections.toList

class SharedMCPServerListViewModel: ViewModel() {
    val currentTeam = MutableStateFlow<AIPortTeam?>(null)
    val teamToolMakerCandidates = mutableStateMapOf<Long, AIPortTeamToolMaker>()
    val sharedToolMakers: StateFlow<List<Long>> = combine(
        TeamRepository.teamToolMakers,
        currentTeam
    ) { teamToolMakers, team ->
        if(team!=null) teamToolMakers.values
            .filter { maker ->
                maker.status>0&&maker.teamId == team.id
            }
            .map{it.toolMakerId}
            .toList()
        else emptyList()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
//    private val _ttmts: StateFlow<List<Long>> = combine(
//        TeamRepository.teamToolMakerTemplates,
//        currentTeam
//    ) { teamToolMakerTemplates, team ->
//        if(team!=null) teamToolMakerTemplates.values
//            .filter { template -> template.status>0&&template.teamId == team.id }
//            .map{it.toolMakerTemplateId}
//            .toList()
//        else emptyList()
//    }.stateIn(
//        scope = viewModelScope,
//        started = SharingStarted.WhileSubscribed(5000),
//        initialValue = emptyList()
//    )
//    val teamToolMakers = mutableMapOf<Long, AIPortTeamToolMaker>()
//    val sharedToolMakers: StateFlow<List<Long>> = combine(
//        ToolRepository.toolMakers,
//        _ttms,
////        _ttmts
//    ) { toolMakers, ttms /*,templates*/ ->
//        toolMakers.values.filter { maker ->
//            maker.id in ttms //||maker.templateId in templates
//        }.map { it.id }.toList()
//    }.stateIn(
//        scope = viewModelScope,
//        started = SharingStarted.WhileSubscribed(5000),
//        initialValue = emptyList()
//    )
    val grantable = MutableStateFlow(false)
    val toolMakerFilter = MutableStateFlow("")
    val toolMakers: StateFlow<List<AIPortToolMaker>> = combine(
        ToolRepository.toolMakers,
        sharedToolMakers,
        toolMakerFilter,
        grantable
    ) { makers,sharedToolMakers, toolMakerFilter,editable -> makers.values.filter { maker->
        if(editable)
            UserRepository.me(maker.userId)&&(toolMakerFilter.isEmpty()||maker.name.contains(toolMakerFilter,ignoreCase = true))
        else
            maker.id in sharedToolMakers && (toolMakerFilter.isEmpty()||maker.name.contains(toolMakerFilter,ignoreCase = true))
    }.sortedBy { it.name } }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    fun selectToolMaker(selected: Boolean, toolMaker: AIPortToolMaker){
        teamToolMakerCandidates.remove(toolMaker.id)
        if(toolMaker.id in sharedToolMakers.value){
            if(!selected) {
                teamToolMakerCandidates[toolMaker.id] =
                    AIPortTeamToolMaker()
                    .toolMakerId(toolMaker.id)
                    .status(0)
            }
        } else if(selected) {
            teamToolMakerCandidates[toolMaker.id] =
                AIPortTeamToolMaker()
                    .toolMakerId(toolMaker.id)
                    .status(Short.MAX_VALUE.toInt())
        }
    }

    fun saveTeamToolMakers(){
        currentTeam.value?.let{ team->
            if(teamToolMakerCandidates.isNotEmpty()) {
                viewModelScope.launch {
                    TeamRepository.modifyTeamToolMakers(
                        team, teamToolMakerCandidates.values.toList()
                    ){
                        if(it.successful()) it.data?.let {
                            grantable.value = false
                            teamToolMakerCandidates.clear()
                            generalViewModel.showSnackbar("${team.name} update successfully")
                        } else {
                            generalViewModel.showSnackbar("${team.name} update failed","Error",true)
                        }
                    }
                }
            } else generalViewModel.showSnackbar("No change in ${team.name}")
        }
    }
}
@Composable
fun SharedMCPServerListView(
    team: AIPortTeam?,
    selectedToolMakers: List<AIPortToolMaker>?,
    editable: Boolean,
    modifier: Modifier = Modifier,
){
    val viewModel by remember { mutableStateOf(SharedMCPServerListViewModel()) }
    LaunchedEffect(team) {
        viewModel.currentTeam.value=team
        viewModel.teamToolMakerCandidates.clear()
        if(team!=null) {
            TeamRepository.loadTeamToolMakers(team)
//            if(UserRepository.me(team.ownerId)){
//                viewModel.editable.value = true
//            }
        }
//        else viewModel.editable.value = true
        viewModel.grantable.value = editable
//        if(viewModel.editable.value)selectedToolMakers?.forEach {
//            viewModel.selectToolMaker(true,it)
//        }
    }
    val currentTeam by viewModel.currentTeam.collectAsState()
    val sharedToolMakers  by viewModel.sharedToolMakers.collectAsState()
    val toolMakers by viewModel.toolMakers.collectAsState()
    val grantable by viewModel.grantable.collectAsState()
    var expanded by remember { mutableStateOf(false) }

    Card(modifier = modifier) {
        currentTeam?.let { team ->
            StudioActionBar(
                if(grantable)
                    "Share MCP Servers with ${team.name ?: ""}"
                else
                    "Shared MCP Servers (${sharedToolMakers.size}) with ${team.name ?: ""}"
            ){
                if(!grantable) IconButton(
                    onClick = { viewModel.grantable.value = !grantable },
                    modifier = Modifier.size(32.dp)
                ){
                    Icon(painterResource(
                        Res.drawable.forms_add_on
                    ),contentDescription = null, Modifier.size(20.dp))
                }
                IconButton(
                    onClick = {expanded=!expanded},
                    modifier = Modifier.size(32.dp)
                ) {
                    val icon = if(expanded) Res.drawable.collapse_all else Res.drawable.expand_all
                    Icon(painterResource(icon),contentDescription = null, Modifier.size(20.dp))
                }
            }
            Row(
                Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                StudioSearchbar(modifier = Modifier.weight(1f)) {
                    viewModel.toolMakerFilter.value = it
                }
//                Spacer(Modifier.size(8.dp))
                if(grantable) {
                    TextButton(
                        modifier = Modifier.height(40.dp),
                        onClick = { viewModel.grantable.value = false }) {
                        Text("Cancel")
                    }
                    Button(
                        modifier = Modifier.height(40.dp),
                        onClick = { viewModel.saveTeamToolMakers() }) {
                        Text("Save")
                    }
                }
            }
            LazyColumn(Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(toolMakers){ t1->
                    val selected = selectedToolMakers?.let{ selectedToolMakers->
                        selectedToolMakers.find { maker -> maker.id == t1.id }!=null
//                            t1.id==toolMaker.id
                    }?:false
                    SharedMCPServerView(
                        t1,
                        expanded||selected,
                        if(grantable) selected||t1.id in sharedToolMakers else null,
                        Modifier.fillMaxWidth()
                    ){ selected->
                        viewModel.selectToolMaker(selected,t1)
                    }
                }
            }
        }?:StudioActionBar("Shared MCP Servers")
    }
}