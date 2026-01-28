package ai.mcpdirect.studio.app.team.view

import ai.mcpdirect.studio.app.compose.ListButton
import ai.mcpdirect.studio.app.compose.StudioActionBar
import ai.mcpdirect.studio.app.compose.TooltipIconButton
import ai.mcpdirect.studio.app.compose.TooltipText
import ai.mcpdirect.studio.app.model.AIPortServiceResponse
import ai.mcpdirect.studio.app.model.account.AIPortTeam
import ai.mcpdirect.studio.app.model.repository.TeamRepository
import ai.mcpdirect.studio.app.model.repository.UserRepository
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import mcpdirectstudioapp.composeapp.generated.resources.Res
import mcpdirectstudioapp.composeapp.generated.resources.add
import mcpdirectstudioapp.composeapp.generated.resources.edit
import mcpdirectstudioapp.composeapp.generated.resources.person
import org.jetbrains.compose.resources.painterResource

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

//    var currentTeam by mutableStateOf<AIPortTeam?>(null)
//        private set
//    fun currentTeam(team: AIPortTeam?){
//        currentTeam = team
//    }
//    fun selectedTeam(team: AIPortTeam): Boolean{
//        return currentTeam?.id == team.id
//    }
    fun createTeam(
        name:String,
        onResponse: (resp: AIPortServiceResponse<AIPortTeam>) -> Unit
    ){
        viewModelScope.launch {
            TeamRepository.createTeam(name){
//                if(it.successful()) it.data?.let {
//                    currentTeam = it
//                }
                onResponse(it)
            }
        }
    }
    fun modifyTeam(
        team:AIPortTeam,name:String?=null,status:Int?=null,
        onResponse: ((resp: AIPortServiceResponse<AIPortTeam>) -> Unit)?=null
        ){
        viewModelScope.launch {
            TeamRepository.modifyTeam(team,name,status,onResponse)
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
    var currentTeam by remember { mutableStateOf<AIPortTeam?>(null) }
    var showCreateTeamView by remember { mutableStateOf(showKeyGeneration) }
    var editableTeam by remember { mutableStateOf<AIPortTeam?>(null) }
    LaunchedEffect(team){
        if(team!=null&&team.id>0){
//            viewModel.currentTeam(team)
            currentTeam = team
            onTeamChange(team)
        }
    }
    Box(
        modifier,
        contentAlignment = Alignment.BottomEnd
    ){
        Column(modifier) {
            if(!showCreateTeamView){
                StudioActionBar("Teams"){
//                    val enabled = currentTeam!=null&& UserRepository.me(currentTeam!!.ownerId)
//                    TooltipIconButton(
//                        if(enabled) "Edit team name" else "Only for team owner",
//                        enabled = currentTeam!=null&& UserRepository.me(currentTeam!!.ownerId),
//                        onClick = {
//                            editableTeam = true
//                            showCreateTeamView = true
//                        },
//                        modifier = Modifier.size(32.dp)
//                    ){
//                        Icon(painterResource(Res.drawable.edit),contentDescription = null, Modifier.size(20.dp))
//                    }
//                    IconButton(
//                        enabled = currentTeam!=null&& UserRepository.me(currentTeam!!.ownerId),
//                        onClick = {
//                            editableTeam = true
//                            showCreateTeamView = true
//                        },
//                        modifier = Modifier.size(32.dp)
//                    ){
//                        Icon(painterResource(Res.drawable.edit),contentDescription = null, Modifier.size(20.dp))
//                    }
//                    TextButton(
//                        modifier = Modifier.height(32.dp),
//                        contentPadding = PaddingValues(horizontal = 8.dp),
//                        onClick = { showCreateTeamView = true }
//                    ) {
//                        Text(
//                            "Create",
//                            style = MaterialTheme.typography.bodySmall,
//                        )
//                    }
                }
//                HorizontalDivider()
                LazyColumn(
                    Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(teams) { team ->
                        val selected = currentTeam?.id == team.id
                        ListButton(
                            selected = selected,
                            headlineContent = {
                                Row {
                                    Text(team.name)
                                    val enabled = UserRepository.me(team.ownerId)
                                    TooltipIconButton(
                                        if (enabled) "Edit team name" else "Only for team owner",
                                        enabled = enabled,
                                        onClick = {
                                            editableTeam = team
                                            showCreateTeamView = true
                                        },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            painterResource(Res.drawable.edit),
                                            contentDescription = null,
                                            Modifier.size(16.dp)
                                        )
                                    }
                                }
                            },
                            onClick = {
                                currentTeam = team
                                onTeamChange(team)
                            },
                            supportingContent = {
                                Badge(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                )  {
                                    Icon(
                                        painterResource(Res.drawable.person),
                                        contentDescription = null,
                                        Modifier.size(16.dp)
                                    )
                                    if (UserRepository.me(team.ownerId))
                                        Text("Me")
                                    else if (team.ownerName.lowercase() == "anonymous")
                                        Text(team.ownerName)
                                    else TooltipText(
                                        team.ownerName,
                                        team.ownerAccount
                                    )
                                }
                            },
                        )
//                        StudioListItem(
//                            modifier = Modifier.clickable {
//                                currentTeam = team
//                                onTeamChange(team)
//                            },
//                            selected = selected,
//                            headlineContent = { Text(team.name) },
//                            supportingContent = {
//                                Row {
//                                    Icon(
//                                        painterResource(Res.drawable.person),
//                                        contentDescription = null,
//                                        Modifier.size(16.dp)
//                                    )
//                                    if (UserRepository.me(team.ownerId))
//                                        Text("Me", style = MaterialTheme.typography.bodySmall)
//                                    else if (team.ownerName.lowercase() == "anonymous")
//                                        Text(team.ownerName, style = MaterialTheme.typography.bodySmall)
//                                    else TooltipText(
//                                        team.ownerName,
//                                        team.ownerAccount,
//                                        style = MaterialTheme.typography.bodySmall
//                                    )
//                                }
//                            },
//                        )
                    }
                }
            } else {
                var name by remember { mutableStateOf(editableTeam?.name?:"") }
                var nameError by remember { mutableStateOf(true) }

                StudioActionBar(title = editableTeam?.name?:"Create Team")
//                {
//                    if(teams.isNotEmpty())TextButton(
//                        modifier = Modifier.height(32.dp),
//                        contentPadding = PaddingValues(horizontal = 8.dp),
//                        onClick = { showCreateTeamView = false }
//                    )
//                    {
//                        Text(
//                            "Teams",
//                            style = MaterialTheme.typography.bodySmall,
//                        )
//                    }
//                }
//                HorizontalDivider()
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    value = name,
                    onValueChange = { text ->
                        nameError = text.isBlank() || text.length > 20
                        name = text.ifBlank { "" }
                    },
                    label = { Text("Team Name") },
                    isError = nameError,
                    supportingText = {
                        Text("Name must not be empty and should have at most 20 characters")
                    },
                )
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        onClick = {
                            editableTeam = null
                            showCreateTeamView = false
                        }
                    ) {
                        Text("Cancel")
                    }
                    Button(
                        enabled = !nameError,
                        modifier = Modifier.padding(horizontal = 16.dp),
                        onClick = {
                            if (editableTeam!=null)
                                viewModel.modifyTeam(currentTeam!!, name) {
                                    if(it.successful()) it.data?.let{ data ->
                                        showCreateTeamView = false
                                        editableTeam = null
                                        if(currentTeam?.id==data.id){
                                            currentTeam = data
                                            onTeamChange(data)
                                        }
                                    }
                                }
                            else viewModel.createTeam(name) {
                                editableTeam = null
                                showCreateTeamView = !it.successful()
                                if (it.successful()) it.data?.let {
                                    currentTeam = it
                                    onTeamChange(it)
                                }
                            }
                        },
                    ) {
                        Text(if(editableTeam!=null) "Save" else "Create")
                    }
                }

            }
        }
        if(!showCreateTeamView){
            FloatingActionButton(
                onClick = { showCreateTeamView = true },
                modifier = Modifier.padding(16.dp),
                shape = CircleShape,
            ){
                Icon(painterResource(Res.drawable.add),contentDescription = null)
            }
        }
    }
}