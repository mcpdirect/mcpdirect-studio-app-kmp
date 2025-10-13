package ai.mcpdirect.studio.app.team

import ai.mcpdirect.backend.dao.entity.account.AIPortTeam
import ai.mcpdirect.backend.service.AccountServiceErrors
import ai.mcpdirect.studio.app.Screen
import ai.mcpdirect.studio.app.UIState
import ai.mcpdirect.studio.app.authViewModel
import ai.mcpdirect.studio.app.compose.*
import ai.mcpdirect.studio.app.generalViewModel
import ai.mcpdirect.studio.app.isValidEmail
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
import androidx.compose.material.IconButton
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
import org.jetbrains.compose.resources.stringResource

private var dialog by mutableStateOf<MCPTeamDialog>(MCPTeamDialog.None)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MCPTeamScreen() {
    val viewModel = mcpTeamViewModel;
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Screen.MCPTeam.title) ) },
                actions = {
                    if(viewModel.mcpTeams.isNotEmpty())
                        Button(onClick = { dialog = MCPTeamDialog.CreateTeam }) {
                            Text("Create MCP Team")
                        }
                }
            )
        }
    ) { padding ->
        when(viewModel.uiState){
            is UIState.Error -> {}
            UIState.Idle -> {}
            UIState.Loading -> {
                Column(
                    Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(48.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            UIState.Success -> {
                if(viewModel.mcpTeams.isEmpty())
                    Column(
                        Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(onClick = {
                            dialog = MCPTeamDialog.CreateTeam
                        }){
                            Text("Create your first MCP Team")
                        }
                    }
                else TeamListView(padding)
            }
        }
    }
    when(dialog){
        MCPTeamDialog.None -> {}
        MCPTeamDialog.CreateTeam -> {
            CreateTeamDialog(null)
        }
        MCPTeamDialog.EditTeamName -> {
            CreateTeamDialog(viewModel.mcpTeam)
        }
        MCPTeamDialog.InviteTeamMember -> InviteTeamDialog()
    }

    LaunchedEffect(Unit) {
        viewModel.queryMCPTeams()
    }
}

@Composable
fun CreateTeamDialog(
    team: AIPortTeam?
) {
    val viewModel = mcpTeamViewModel
//    val team = viewModel.mcpTeam
    val nameFocusRequester = remember { FocusRequester() }
    val addFocusRequester = remember { FocusRequester() }
    val formScrollState = rememberScrollState()

    AlertDialog(
        onDismissRequest = { dialog = MCPTeamDialog.None },
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
                        code, message ->
                        if(code==0)dialog = MCPTeamDialog.None
                    }else viewModel.modifyMCPTeam(viewModel.mcpTeamName,null){
                        code, message ->
                        if(code==0)dialog = MCPTeamDialog.None
                    }
                }
            ) {
                Text(if(team==null) "Create" else "Save")
            }
        },
        dismissButton = {
            Button(
                colors = ButtonDefaults.textButtonColors(),
                onClick = { dialog = MCPTeamDialog.None }) {
                Text("Cancel")
            }
        }
    )
}


@Composable
private fun TeamListView(
    padding: PaddingValues
) {
    val viewModel = mcpTeamViewModel
    Column(modifier = Modifier.padding(padding)) {
        SearchView(
            query = viewModel.searchQuery,
            onQueryChange = { viewModel.updateSearchQuery(it) },
            placeholder = "Search MCP teams..."
        )
        StudioCard(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxSize()
        ){
            Row(modifier = Modifier.fillMaxWidth()) {
                LazyColumn(modifier = Modifier.weight(3.0f)) {
                    items(viewModel.mcpTeams) {
                        TeamItem(it) {
                            viewModel.setMCPTeam(it)
                        }
                    }
                }
                if (viewModel.mcpTeam != null) {
                    VerticalDivider()
                    var currentTabIndex by remember { mutableStateOf(0) }
                    val tabs = listOf("Team Members", "Shared MCP Server")

                    Column(Modifier.weight(5.0f)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            IconButton(onClick = { viewModel.setMCPTeam(null)}) {
                                Icon(painterResource(Res.drawable.arrow_back), contentDescription = "Back")
                            }
                            if(viewModel.mcpTeam!!.ownerId==authViewModel.userInfo.value!!.id) {
                                Spacer(Modifier.weight(1.0f))
                                TooltipIconButton(
                                    Res.drawable.person_add,
                                    tooltipText = "Invite Member",
                                    onClick = { dialog = MCPTeamDialog.InviteTeamMember }
                                )
                                TooltipIconButton(
                                    Res.drawable.share,
                                    tooltipText = "Share MCP Server",
                                    onClick = {
                                        generalViewModel.currentScreen = Screen.MCPTeamToolMaker
                                        generalViewModel.backToScreen = Screen.MCPTeam
                                    }
                                )
                            }
                        }
                        HorizontalDivider()
                        TabRow(selectedTabIndex = currentTabIndex) {
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
                            0 -> TeamMemberList()
                            1 -> Text("Search Content")
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
    onClick: () -> Unit
) {
    val viewModel = mcpTeamViewModel
    val selectedColor = ListItemDefaults.colors(
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        headlineColor = MaterialTheme.colorScheme.onSurface,
        supportingColor = MaterialTheme.colorScheme.onSurface,
        trailingIconColor = MaterialTheme.colorScheme.onSurface,
    )
    ListItem(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        overlineContent = {
            if(team.ownerId== authViewModel.userInfo.value?.id) Text("Me")
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
                    color = MaterialTheme.colorScheme.error,
                )
                1-> Tag("active",)
            }
        },
        trailingContent = {
            if(viewModel.mcpTeam!=null&&viewModel.mcpTeam!!.id==team.id)
                Icon(painterResource(Res.drawable.keyboard_arrow_right),
                    contentDescription = "Current Tool Maker")
        },
        colors = if(viewModel.mcpTeam!=null&&viewModel.mcpTeam!!.id==team.id)
            selectedColor
        else ListItemDefaults.colors()
    )
}

@Composable
private fun TeamMemberList() {
    val viewModel = mcpTeamViewModel
    val myId = authViewModel.userInfo.value?.id
    val team = viewModel.mcpTeam!!
    val teamOwner = team.ownerId==authViewModel.userInfo.value!!.id
    LazyColumn {
        items(viewModel.mcpTeamMembers){
            val me = it.memberId==myId
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
                        if (it.expirationDate < 0) Tag(
                            "waiting for acceptance",
                            color = MaterialTheme.colorScheme.error,
                        )else if (it.status == 0) Tag(
                            "inactive",
                            color = MaterialTheme.colorScheme.error,
                        ) else Tag(
                            "active",
                        )
                    }
                },
                trailingContent = {
                    if(teamOwner){

                    }else if(me&&it.expirationDate<0){
                        Button(
                            onClick = {viewModel.acceptMCPTeamMember(team.id,myId)}
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
fun InviteTeamDialog() {
    val viewModel = mcpTeamViewModel
    var userNotExist by remember { mutableStateOf(false) }
    OutlinedTextFieldDialog(
        title = {Text("Invite member for ${viewModel.mcpTeam!!.name}")},
        label = {Text("Member account")},
        supportingText = {
            value, isValid ->
            if(userNotExist) Text("Member not exists", color = MaterialTheme.colorScheme.error)
        },
        onValueChange = {
            value,onValueChange->
            onValueChange(value,isValidEmail(value))
        },
        confirmButton = {
            value,isValid->
            Button(
                enabled = value.isNotEmpty()&&isValid,
                onClick = {viewModel.inviteMCPTeamMember(value){
                    code, message ->
                    when(code){
                        0-> dialog = MCPTeamDialog.None
                        AccountServiceErrors.USER_NOT_EXIST -> {userNotExist=true}
                    }
                }}
            ){
                Text("Invite")
            }
        },
        onDismissRequest = {
            value, isValid ->
            dialog = MCPTeamDialog.None
        }
    )
}