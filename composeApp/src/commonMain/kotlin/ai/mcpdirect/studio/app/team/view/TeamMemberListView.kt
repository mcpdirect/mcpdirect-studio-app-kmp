package ai.mcpdirect.studio.app.team.view

import ai.mcpdirect.studio.app.compose.StudioActionBar
import ai.mcpdirect.studio.app.compose.Tag
import ai.mcpdirect.studio.app.compose.TooltipIconButton
import ai.mcpdirect.studio.app.model.AIPortServiceResponse
import ai.mcpdirect.studio.app.model.account.AIPortTeam
import ai.mcpdirect.studio.app.model.account.AIPortTeamMember
import ai.mcpdirect.studio.app.model.repository.TeamRepository
import ai.mcpdirect.studio.app.model.repository.UserRepository
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import mcpdirectstudioapp.composeapp.generated.resources.Res
import mcpdirectstudioapp.composeapp.generated.resources.person_add
import org.jetbrains.compose.resources.painterResource

class TeamMemberViewModel: ViewModel() {
    val currentTeam = MutableStateFlow<AIPortTeam?>(null)
    private val _teamMembers = mutableStateMapOf<Long, AIPortTeamMember>()
    val teamMembers by derivedStateOf {
        _teamMembers.values.toList()
    }
    fun loadTeamMembers(team:AIPortTeam?){
        currentTeam.value=team
        if(team!=null){
            _teamMembers.clear()
            viewModelScope.launch {
                TeamRepository.loadTeamMembers(team){
                    if(it.successful()) it.data?.let { data->
                        data.forEach {
                            _teamMembers[it.memberId]=it
                        }
                    }
                }
            }
        }
    }
    fun inviteMCPTeamMember(account:String?,
                            onResponse: (AIPortServiceResponse<AIPortTeamMember>) -> Unit){
        currentTeam.value?.let { team->
            viewModelScope.launch {
                TeamRepository.inviteTeamMember(team,account){
                    if(it.successful()) it.data?.let { data->
                        _teamMembers[data.memberId]=data
                    }
                    onResponse(it)
                }
            }
        }
    }
    fun acceptMCPTeamMember(team: AIPortTeam, memberId:Long){
        viewModelScope.launch {
            TeamRepository.acceptTeamMember(team,memberId){
                    code, message, data ->
                if(code==0&&data!=null){
                    _teamMembers[data.memberId]=data
                }
            }
        }
    }
}
@Composable
fun TeamMemberView(
    team: AIPortTeam?,
    modifier: Modifier = Modifier,
){
    val viewModel by remember { mutableStateOf(TeamMemberViewModel()) }
    val currentTeam by viewModel.currentTeam.collectAsState()
    var invite by remember { mutableStateOf(false)}
    LaunchedEffect(team) {
        viewModel.loadTeamMembers(team)
    }
    OutlinedCard(modifier) {
        currentTeam?.let { team ->
            val myId = UserRepository.me.value.id
            val teamOwner = UserRepository.me(team.ownerId)
            if(invite){
                StudioActionBar("Invite Member"){
                    TextButton(
                        modifier = Modifier.height(32.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp),
                        onClick = { invite = false }
                    ) {
                        Text(
                            "Team Members",
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
                HorizontalDivider()
                var userNotExist by remember { mutableStateOf(false) }
                var name by remember { mutableStateOf("") }
                var nameError by remember { mutableStateOf(true) }
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    value = name,
                    onValueChange = { text ->
                        userNotExist = false
                        nameError = text.isBlank() || text.length>60
                        name = text.ifBlank { "" }
                    },
                    label = { Text("Member account") },
                    isError = nameError||userNotExist,
                    supportingText = {
                        if(userNotExist) Text("User Not Exist")
                        else Text("Name must not be empty and should have at most 60 characters")
                    },
                )
                Button(
                    enabled = !nameError,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    onClick = {
                        viewModel.inviteMCPTeamMember(name){
                            when(it.code){
                                0-> invite=false
                                AIPortServiceResponse.ACCOUNT_NOT_EXIST -> {userNotExist=true}
                            }
                        }
                    },
                ){
                    Text("Invite")
                }
            }else {
                StudioActionBar("Team Members (${viewModel.teamMembers.size})") {
                    TooltipIconButton(
                       tooltip = if(teamOwner)"Invite" else "Only for team owner",
                        onClick = {invite = true},
                        enabled = teamOwner,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            painterResource(
                                Res.drawable.person_add
                            ), contentDescription = null, Modifier.size(20.dp)
                        )
                    }
                }
                LazyColumn {
                    items(viewModel.teamMembers) {
                        val me = UserRepository.me(it.memberId)
                        ListItem(
                            headlineContent = {
                                Text(it.name)
                            },
                            overlineContent = {
                                if (me) Text("Me", color = MaterialTheme.colorScheme.primary)
                                else Text(it.account)
                            },
                            supportingContent = {
                                Box(
                                    Modifier.border(
                                        width = 1.dp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                ) {
                                    if (it.expirationDate < 0L) Tag(
                                        "waiting for acceptance",
                                        toggleColor = MaterialTheme.colorScheme.error,
                                    ) else if (it.status == 0) Tag(
                                        "inactive",
                                        toggleColor = MaterialTheme.colorScheme.error,
                                    ) else Tag(
                                        "active",
                                    )
                                }
                            },
                            trailingContent = {
                                if (teamOwner) {

                                } else if (me && it.expirationDate < 0) {
                                    Button(
                                        onClick = { viewModel.acceptMCPTeamMember(team, myId) }
                                    ) {
                                        Text("Accept")
                                    }
                                }
                            }
                        )
                        HorizontalDivider()
                    }
                }
            }
        }?:StudioActionBar("Team Members")
    }
}