package ai.mcpdirect.studio.app.team.view

import ai.mcpdirect.studio.app.compose.StudioActionBar
import ai.mcpdirect.studio.app.compose.StudioListItem
import ai.mcpdirect.studio.app.model.AIPortServiceResponse
import ai.mcpdirect.studio.app.model.account.AIPortTeam
import ai.mcpdirect.studio.app.model.repository.TeamRepository
import ai.mcpdirect.studio.app.model.repository.UserRepository
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class TeamListViewModel : ViewModel() {
    val teamFilter = MutableStateFlow("")
    val teams: StateFlow<List<AIPortTeam>> = combine(
        TeamRepository.teams,
        teamFilter
    ) { teams,filter-> teams.values.filter {
        filter.isEmpty()||it.name.contains(filter,ignoreCase = true)
    }.toList() }.stateIn(
        scope = viewModelScope,      // 或 CoroutineScope(Dispatchers.Main.immediate)
        started = SharingStarted.WhileSubscribed(5000), // 按需启动
        initialValue = emptyList()
    )

    var currentTeam by mutableStateOf<AIPortTeam?>(null)
        private set
    fun currentTeam(team: AIPortTeam?){
        currentTeam = team
    }
    fun selectedTeam(team: AIPortTeam): Boolean{
        return currentTeam?.id == team.id
    }
    fun createTeam(
        name:String,
        onResponse: (resp: AIPortServiceResponse<AIPortTeam>) -> Unit
    ){
        viewModelScope.launch {
            TeamRepository.createTeam(name){
                if(it.successful()) it.data?.let {
                    currentTeam = it
                }
                onResponse(it)
            }
        }
    }
}
@Composable
fun TeamListView(
    team: AIPortTeam?=null,
    showKeyGeneration: Boolean = false,
    modifier: Modifier = Modifier,
    onTeamChange:(team:AIPortTeam)->Unit
){
    val viewModel by remember {mutableStateOf(TeamListViewModel())}
    val teams by viewModel.teams.collectAsState()
    var showGenerateKeyView by remember { mutableStateOf(showKeyGeneration) }
    LaunchedEffect(team){
        if(team!=null){
            viewModel.currentTeam(team)
            onTeamChange(team)
        }
    }
    Column(modifier) {
        if(!showGenerateKeyView){
            StudioActionBar("Teams"){
                TextButton(
                    modifier = Modifier.height(32.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp),
                    onClick = { showGenerateKeyView = true }
                ) {
                    Text(
                        "Create",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
            HorizontalDivider()
            LazyColumn {
                items(teams) { team ->
                    val selected = viewModel.selectedTeam(team)
                    StudioListItem(
                        modifier = Modifier.clickable {
                            viewModel.currentTeam(team)
                            onTeamChange(team)
                        },
                        selected = selected,
                        headlineContent = { Text(team.name) },
                        supportingContent = {
                            if(UserRepository.me(team.ownerId)) Text("Me")
                            else if(team.ownerName.lowercase() == "anonymous")Text(team.ownerName)
                            else Text("${team.ownerName} (${team.ownerAccount})")
                        },
                    )
                }
            }
        } else {
            StudioActionBar("Create"){
                if(teams.isNotEmpty())TextButton(
                    modifier = Modifier.height(32.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp),
                    onClick = { showGenerateKeyView = false }
                ) {
                    Text(
                        "Teams",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
            HorizontalDivider()
            var name by remember { mutableStateOf("") }
            var nameError by remember { mutableStateOf(true) }
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                value = name,
                onValueChange = { text ->
                    nameError = text.isBlank() || text.length>20
                    name = text.ifBlank { "" }
                },
                label = { Text("Virtual MCP Name") },
                isError = nameError,
                supportingText = {
                    Text("Name must not be empty and should have at most 20 characters")
                },
            )
            Button(
                enabled = !nameError,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                onClick = {
                    viewModel.createTeam(name){
                        if(it.successful()) it.data?.let {
                            viewModel.currentTeam(it)
                            onTeamChange(it)
                        }
                    }
                },
            ){
                Text("Create")
            }
        }
    }
}