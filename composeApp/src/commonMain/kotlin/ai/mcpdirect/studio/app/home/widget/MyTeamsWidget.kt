package ai.mcpdirect.studio.app.home.widget

import ai.mcpdirect.mcpdirectstudioapp.getPlatform
import ai.mcpdirect.studio.app.Screen
import ai.mcpdirect.studio.app.compose.EditableText
import ai.mcpdirect.studio.app.compose.InlineTextField
import ai.mcpdirect.studio.app.compose.TooltipBox
import ai.mcpdirect.studio.app.compose.TooltipIcon
import ai.mcpdirect.studio.app.compose.ValidatorBuilder
import ai.mcpdirect.studio.app.generalViewModel
import ai.mcpdirect.studio.app.home.HomeViewModel
import ai.mcpdirect.studio.app.model.account.AIPortUser
import ai.mcpdirect.studio.app.model.repository.UserRepository
import ai.mcpdirect.studio.app.theme.AppColors
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import mcpdirectstudioapp.composeapp.generated.resources.Res
import mcpdirectstudioapp.composeapp.generated.resources.add
import mcpdirectstudioapp.composeapp.generated.resources.group
import mcpdirectstudioapp.composeapp.generated.resources.person
import org.jetbrains.compose.resources.painterResource

@Composable
fun MyTeamsView(
    viewModel: HomeViewModel,
    modifier: Modifier = Modifier
){
    val teams by viewModel.teams.collectAsState()
//    LaunchedEffect(viewModel) {
//        viewModel.refreshTeams()
//    }
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    Column(modifier.hoverable(interactionSource)) {
        Row(
            modifier = Modifier.height(48.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                painterResource(Res.drawable.group),
                ""
            )
            Text("My Teams (${teams.size})", fontWeight = FontWeight.Bold)
            Spacer(Modifier.weight(1.0f))
            if(isHovered)IconButton(
                onClick = {
                    generalViewModel.currentScreen(
                        Screen.MCPTeam(),
                        "My Teams",
                        Screen.Home
                    )
                }
            ) {
                Icon(
                    painterResource(Res.drawable.add),
                    contentDescription = ""
                )
            }
        }
        if (teams.isNotEmpty()) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                items(teams) { team ->
                    val me = UserRepository.me(team.ownerId)
                    var edited by remember { mutableStateOf(false) }
                    if(!edited) Row(Modifier.padding(start = 16.dp)) {
                        TextButton(
                            modifier = Modifier.height(32.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            onClick = {
                                generalViewModel.currentScreen(
                                    Screen.MCPTeam(team),
                                    "My Teams",
                                    Screen.Home
                                )
                            },
                        ) {
                            if(me) EditableText(
                                team.name,
                                overflow = TextOverflow.MiddleEllipsis,
                                onEdit = { edited = it }
                            ) else {
                                var user by remember { mutableStateOf(AIPortUser()) }
                                LaunchedEffect(Unit){
                                    UserRepository.user(team.ownerId){
                                        if(it.successful()) it.data?.let { data->
                                            user = data
                                        }
                                    }
                                }
                                Text(
                                    team.name, softWrap = false,
                                    overflow = TextOverflow.MiddleEllipsis,
                                )
                                Spacer(Modifier.width(4.dp))
                                TooltipBox(
                                    user.name
                                ) {
                                    Icon(
                                        painterResource(Res.drawable.person),
                                        null,
                                        Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    } else InlineTextField(
                        team.name,
                        modifier = Modifier.height(32.dp),
                        validator = ValidatorBuilder().required().maxLength(30).build(),
                    ){ name->
                        edited = false
                        if(name!=null) viewModel.modifyTeam(team,name){

                        }
                    }

                }
            }
        } else if (getPlatform().type == 0) Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Please create a team to start")
        }
    }
}