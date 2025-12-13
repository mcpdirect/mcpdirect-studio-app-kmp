package ai.mcpdirect.studio.app.team

import ai.mcpdirect.studio.app.Screen
import ai.mcpdirect.studio.app.compose.*
import ai.mcpdirect.studio.app.generalViewModel
import ai.mcpdirect.studio.app.isValidEmail
import ai.mcpdirect.studio.app.model.AIPortServiceResponse
import ai.mcpdirect.studio.app.model.account.AIPortTeam
import ai.mcpdirect.studio.app.model.aitool.AIPortToolMaker
import ai.mcpdirect.studio.app.model.repository.TeamRepository
import ai.mcpdirect.studio.app.model.repository.ToolRepository
import ai.mcpdirect.studio.app.model.repository.UserRepository
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import mcpdirectstudioapp.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MCPTeamScreen(
    dialog: MCPTeamDialog
) {
    val mcpTeamViewModel by remember { mutableStateOf(MCPTeamViewModel()) }
    val teams by mcpTeamViewModel.teams.collectAsState()
    LaunchedEffect(mcpTeamViewModel) {
        mcpTeamViewModel.dialog = dialog
//        mcpTeamViewModel.uiState = UIState.Loading
        TeamRepository.loadTeams()
        ToolRepository.loadToolMakers()
        ToolRepository.loadToolMakerTemplates()
//        generalViewModel.refreshTeams{
//                code, message ->
//            if(code==0) {
//                generalViewModel.refreshToolMakers{
//                        code, message ->
//                    mcpTeamViewModel.uiState = UIState.Success
//                }
//                generalViewModel.refreshToolMakerTemplates()
//            }
//        }
    }

//    when(mcpTeamViewModel.uiState){
//        is UIState.Error -> {}
//        UIState.Idle -> {}
//        UIState.Loading -> {
//            Column(
//                Modifier.fillMaxSize(),
//                verticalArrangement = Arrangement.Center,
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                CircularProgressIndicator(
//                    modifier = Modifier.size(48.dp),
//                    color = MaterialTheme.colorScheme.primary
//                )
//            }
//        }
//        UIState.Success -> {
//            if(generalViewModel.teams.isEmpty())
//                Column(
//                    Modifier.fillMaxSize(),
//                    verticalArrangement = Arrangement.Center,
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//                    Button(onClick = {
//                        mcpTeamViewModel.dialog = MCPTeamDialog.CreateTeam
//                    }){
//                        Text("Create your first MCP Team")
//                    }
//                }
//            else TeamListView(mcpTeamViewModel)
//        }
//    }

    if(teams.isEmpty()) Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            mcpTeamViewModel.dialog = MCPTeamDialog.CreateTeam
        }){
            Text("Create your first MCP Team")
        }
    } else TeamListView(mcpTeamViewModel)
    when(mcpTeamViewModel.dialog){
        MCPTeamDialog.None -> {}
        MCPTeamDialog.CreateTeam -> {
            CreateTeamDialog(null,mcpTeamViewModel){
                mcpTeamViewModel.dialog = MCPTeamDialog.None
            }
        }
        MCPTeamDialog.EditTeamName -> {
            CreateTeamDialog(mcpTeamViewModel.mcpTeam.value,mcpTeamViewModel){
                mcpTeamViewModel.dialog = MCPTeamDialog.None
            }
        }
        MCPTeamDialog.InviteTeamMember -> InviteTeamDialog(mcpTeamViewModel)
    }
}

@Composable
fun CreateTeamDialog(
    team: AIPortTeam?,
    viewModel: MCPTeamViewModel,
    onDismissRequest: () -> Unit,
) {
//    val viewModel = mcpTeamViewModel
//    val team = viewModel.mcpTeam
    val nameFocusRequester = remember { FocusRequester() }
    val addFocusRequester = remember { FocusRequester() }
    val formScrollState = rememberScrollState()

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(if(team==null) "Create MCP Team" else "Edit MCP Team name of ${team.name}") },
        text = {
            Column(Modifier.verticalScroll(formScrollState)) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth().focusRequester(nameFocusRequester),
                    value = viewModel.mcpTeamName,
                    onValueChange = { viewModel.onMCPTeamNameChange(it) },
                    label = { Text("Team Name") },
                    singleLine = true,
                    isError = viewModel.mcpTeamNameErrors,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Send
                    ),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            viewModel.onMCPTeamNameChange(viewModel.mcpTeamName)
                            addFocusRequester.requestFocus(FocusDirection.Next)
                        }
                    ),
                    supportingText = {
                        Text("Team name cannot be empty and the max length is 32")                    }
                )
            }
            LaunchedEffect(Unit) {
                nameFocusRequester.requestFocus()
            }
        },
        confirmButton = {
            Button(
                enabled = viewModel.mcpTeamName.isNotBlank(),
                modifier = Modifier.focusRequester(addFocusRequester),
                onClick = {
                    if(team==null) viewModel.createMCPTeam {
                        code, message,data ->
                        if(code==0) onDismissRequest()
                    }else viewModel.modifyMCPTeam(viewModel.mcpTeamName,null){
                        code, message,data ->
                        if(code==0) onDismissRequest()
                    }
                }
            ) {
                Text(if(team==null) "Create" else "Save")
            }
        },
        dismissButton = {
            Button(
                colors = ButtonDefaults.textButtonColors(),
                onClick = onDismissRequest) {
                Text("Cancel")
            }
        }
    )
}


@Composable
private fun TeamListView(
    mcpTeamViewModel: MCPTeamViewModel
) {
    val mcpTeam by mcpTeamViewModel.mcpTeam.collectAsState()
    val teams by mcpTeamViewModel.teams.collectAsState()
    LaunchedEffect(null){
        generalViewModel.topBarActions = {
            TextButton(onClick = { mcpTeamViewModel.dialog = MCPTeamDialog.CreateTeam }) {
                Text("Create MCP Team")
            }
        }
    }
    DisposableEffect(null){
        onDispose {
            generalViewModel.topBarActions = {}
        }
    }
//    val viewModel = mcpTeamViewModel
    Column {
//        SearchView(
//            query = viewModel.searchQuery,
//            onQueryChange = { viewModel.updateSearchQuery(it) },
//            placeholder = "Search MCP teams..."
//        )
        StudioCard(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxSize()
        ){
            Row(modifier = Modifier.fillMaxWidth()) {
                LazyColumn(modifier = Modifier.weight(3.0f)) {
                    items(teams) {
                        TeamItem(it,mcpTeamViewModel) {
                            mcpTeamViewModel.setMCPTeam(it)
                        }
                    }
                }
                if(mcpTeam.id>Int.MAX_VALUE) mcpTeam.let {
                    VerticalDivider()
                    var currentTabIndex by remember { mutableStateOf(0) }
                    val tabs = listOf("Team Members", "Shared MCP Servers","Shared MCP Templates")

                    Column(Modifier.weight(5.0f)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            IconButton(onClick = { mcpTeamViewModel.setMCPTeam(AIPortTeam())}) {
                                Icon(painterResource(Res.drawable.arrow_back), contentDescription = "Back")
                            }
                            if(UserRepository.me(it.ownerId)) {
                                Spacer(Modifier.weight(1.0f))
                                TooltipIconButton(
                                    Res.drawable.person_add,
                                    contentDescription = "Invite Member",
                                    onClick = { mcpTeamViewModel.dialog = MCPTeamDialog.InviteTeamMember }
                                )
                            }
                            when(currentTabIndex){
                                1 -> TooltipIconButton(
                                    Res.drawable.share,
                                    contentDescription = "Share MCP Server",
                                    onClick = {
                                        generalViewModel.currentScreen(Screen.MCPTeamToolMaker(mcpTeam),
                                            "Share MCP Server to Team ${it.name}",
                                            Screen.MCPTeam())
                                    }
                                )
                                2 -> TooltipIconButton(
                                    Res.drawable.share,
                                    contentDescription = "Share MCP Template",
                                    onClick = {
                                        generalViewModel.currentScreen(Screen.MCPTeamToolMakerTemplate(mcpTeam),
                                            "Share MCP Template to Team ${it.name}",
                                            Screen.MCPTeam())
                                    }
                                )
                            }

                        }
                        HorizontalDivider()
                        SecondaryTabRow(selectedTabIndex = currentTabIndex) {
                            tabs.forEachIndexed { index, title ->
                                Tab(
                                    selected = currentTabIndex == index,
                                    onClick = { currentTabIndex = index },
                                    text = { Text(title) }
                                )
                            }
                        }

                        // Content based on selected tab
                        when (currentTabIndex) {
                            0 -> TeamMemberList(mcpTeamViewModel)
                            1 -> TeamToolMakerList(mcpTeamViewModel)
                            2 -> TeamToolMakerTemplateList(mcpTeamViewModel)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TeamItem(
    team: AIPortTeam,
    viewModel: MCPTeamViewModel,
    onClick: () -> Unit
) {
//    val viewModel = mcpTeamViewModel
    val currentTeam by viewModel.mcpTeam.collectAsState()
    val selectedColor = ListItemDefaults.colors(
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        headlineColor = MaterialTheme.colorScheme.onSurface,
        supportingColor = MaterialTheme.colorScheme.onSurface,
        trailingIconColor = MaterialTheme.colorScheme.onSurface,
    )
    ListItem(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        overlineContent = {
            if(UserRepository.me(team.ownerId)) Text("Me")
            else if(team.ownerName.lowercase() == "anonymous")Text(team.ownerName)
            else Text("${team.ownerName} (${team.ownerAccount})")
        },
        headlineContent = {
            Text(team.name)
        },
        supportingContent = {
            when(team.status){
                0-> Tag(
                    "inactive",
                    toggleColor = MaterialTheme.colorScheme.error,
                )
                1-> Tag("active",)
            }
        },
        trailingContent = {
            if(currentTeam.id==team.id)
                Icon(painterResource(Res.drawable.keyboard_arrow_right),
                    contentDescription = "Current Tool Maker")
        },
        colors = if(currentTeam.id==team.id)
            selectedColor
        else ListItemDefaults.colors()
    )
}

@Composable
private fun TeamMemberList(
    viewModel: MCPTeamViewModel
) {
//    val viewModel = mcpTeamViewModel
    val myId = UserRepository.me.value.id
    val team by viewModel.mcpTeam.collectAsState()
    val teamOwner = UserRepository.me(team.ownerId)
    LazyColumn {
        items(viewModel.mcpTeamMembers){
            val me = UserRepository.me(it.memberId)
            ListItem(
                headlineContent = {
                    Text(it.name)
                },
                overlineContent = {
                    if(me) Text("Me", color = MaterialTheme.colorScheme.primary)
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
                        )else if (it.status == 0) Tag(
                            "inactive",
                            toggleColor = MaterialTheme.colorScheme.error,
                        ) else Tag(
                            "active",
                        )
                    }
                },
                trailingContent = {
                    if(teamOwner){

                    }else if(me&&it.expirationDate<0){
                        Button(
                            onClick = {viewModel.acceptMCPTeamMember(team,myId)}
                        ){
                            Text("Accept")
                        }
                    }
                }
            )
            HorizontalDivider()
        }
    }
}

@Composable
fun InviteTeamDialog(
    viewModel: MCPTeamViewModel
) {
//    val viewModel = mcpTeamViewModel
    val mcpTeam by viewModel.mcpTeam.collectAsState()
    var userNotExist by remember { mutableStateOf(false) }
    OutlinedTextFieldDialog(
        title = {Text("Invite member for ${mcpTeam.name}")},
        label = {Text("Member account")},
        supportingText = { value, isValid ->
            if(userNotExist) Text("Member not exists", color = MaterialTheme.colorScheme.error)
        },
        onValueChange = { value,onValueChanged->
            onValueChanged(value,isValidEmail(value))
        },
        confirmButton = { value,isValid->
            Button(
                enabled = value.isNotEmpty()&&isValid,
                onClick = {viewModel.inviteMCPTeamMember(value){
                    code, message,data ->
                    when(code){
                        0-> viewModel.dialog = MCPTeamDialog.None
                        AIPortServiceResponse.ACCOUNT_NOT_EXIST -> {userNotExist=true}
                    }
                }}
            ){
                Text("Invite")
            }
        },
        onDismissRequest = { value, isValid ->
            viewModel.dialog = MCPTeamDialog.None
        }
    )
}

@Composable
private fun TeamToolMakerList(
    viewModel: MCPTeamViewModel
) {
//    val viewModel = mcpTeamViewModel
//    val myId = UserRepository.me.value.id
    val team by viewModel.mcpTeam.collectAsState()
    val toolMakers by viewModel.toolMakersFromTeam.collectAsState()

    LazyColumn {
        items(toolMakers){
//            val teamToolMaker = viewModel.teamToolMaker(it.id)
//            val teamToolMaker = generalViewModel.teamToolMaker(team.id,it.id)
            val teamToolMaker = TeamRepository.teamToolMaker(team.id,it.id)
            if(teamToolMaker!=null&&teamToolMaker.status>0) {
                val me = UserRepository.me(it.userId)
                val member = viewModel.teamMember(it.userId)
                ListItem(
                    headlineContent = {
                        Text(it.name)
                    },
                    overlineContent = {
                        if (me) Text("Me", color = MaterialTheme.colorScheme.primary)
                        else member?.let { Text("${it.name} (${it.account})") }
                    },
                    supportingContent = {
                        Box(
                            Modifier.border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                                shape = RoundedCornerShape(8.dp)
                            )
                        ) {
                            if (it.status == AIPortToolMaker.STATUS_OFF) Tag(
                                "inactive",
                                toggleColor = MaterialTheme.colorScheme.error,
                            ) else Tag(
                                "active",
                            )
                        }
                    },
                    trailingContent = {

                    }
                )
                HorizontalDivider()
            }
        }
    }
}

@Composable
private fun TeamToolMakerTemplateList(
    viewModel: MCPTeamViewModel
) {
//    val myId = authViewModel.user.id
    val team by viewModel.mcpTeam.collectAsState()
//    val teamToolMakerTemplates = mcpTeamViewModel.mcpTeamToolMakerTemplates
//    val teamToolMakerTemplates = generalViewModel.teamToolMakerTemplates(team.id)
    val teamToolMakerTemplates by viewModel.teamToolMakerTemplates.collectAsState()
        LazyColumn {
        items(teamToolMakerTemplates){
//            val toolMakerTemplate = generalViewModel.toolMakerTemplate(it.toolMakerTemplateId)
            val toolMakerTemplate = ToolRepository.toolMakerTemplate(it.toolMakerTemplateId)
            if(toolMakerTemplate!=null&&toolMakerTemplate.status>0) {
                val me = UserRepository.me(toolMakerTemplate.userId)
                val member = viewModel.teamMember(toolMakerTemplate.userId)
                ListItem(
                    headlineContent = {
                        Text(toolMakerTemplate.name)
                    },
                    overlineContent = {
                        if (me) Text("Me", color = MaterialTheme.colorScheme.primary)
                        else member?.let { Text("${it.name} (${it.account})") }
                    },
                    supportingContent = {
                        Box(
                            Modifier.border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                                shape = RoundedCornerShape(8.dp)
                            )
                        ) {
                            if (it.status == 0) Tag(
                                "inactive",
                                toggleColor = MaterialTheme.colorScheme.error,
                            ) else Tag(
                                "active",
                            )
                        }
                    },
                    trailingContent = {

                    }
                )
                HorizontalDivider()
            }
        }
    }
}