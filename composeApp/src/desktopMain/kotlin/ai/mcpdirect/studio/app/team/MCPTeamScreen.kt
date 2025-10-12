package ai.mcpdirect.studio.app.team

import ai.mcpdirect.backend.dao.entity.account.AIPortTeam
import ai.mcpdirect.studio.app.Screen
import ai.mcpdirect.studio.app.UIState
import ai.mcpdirect.studio.app.compose.SearchView
import ai.mcpdirect.studio.app.compose.StudioCard
import ai.mcpdirect.studio.app.compose.TooltipIconButton
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.IconButton
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import mcpdirectstudioapp.composeapp.generated.resources.Res
import mcpdirectstudioapp.composeapp.generated.resources.arrow_back
import mcpdirectstudioapp.composeapp.generated.resources.badge
import mcpdirectstudioapp.composeapp.generated.resources.block
import mcpdirectstudioapp.composeapp.generated.resources.check
import mcpdirectstudioapp.composeapp.generated.resources.delete
import mcpdirectstudioapp.composeapp.generated.resources.keyboard_arrow_right
import mcpdirectstudioapp.composeapp.generated.resources.person_add
import mcpdirectstudioapp.composeapp.generated.resources.play_circle
import mcpdirectstudioapp.composeapp.generated.resources.restart_alt
import mcpdirectstudioapp.composeapp.generated.resources.sell
import mcpdirectstudioapp.composeapp.generated.resources.settings
import mcpdirectstudioapp.composeapp.generated.resources.share
import mcpdirectstudioapp.composeapp.generated.resources.stop_circle
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
                title = { Text(stringResource(Screen.MyTeam.title) ) },
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
    }

    LaunchedEffect(Unit) {
        viewModel.queryMCPTeams()
    }
}

@OptIn(ExperimentalMaterialApi::class)
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
                        dialog = MCPTeamDialog.None
                    }else viewModel.modifyMCPTeam(viewModel.mcpTeamName,null){
                        dialog = MCPTeamDialog.None
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
                            IconButton(onClick = {  }) {
                                Icon(painterResource(Res.drawable.arrow_back), contentDescription = "Back")
                            }
                            TooltipIconButton(
                                Res.drawable.person_add,
                                tooltipText = "Invite Member",
                                onClick = { }
                            )
                            TooltipIconButton(
                                Res.drawable.share,
                                tooltipText = "Share MCP Server",
                                onClick = { }
                            )
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


@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun TeamItem(
    team: AIPortTeam,
    onClick: () -> Unit
) {
    val viewModel = mcpTeamViewModel
    val selectedColor = ListItemDefaults.colors(
        containerColor = MaterialTheme.colorScheme.primary,
        headlineColor = MaterialTheme.colorScheme.onPrimary,
        supportingColor = MaterialTheme.colorScheme.onPrimary,
        trailingIconColor = MaterialTheme.colorScheme.onPrimary,
    )
    ListItem(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        headlineContent = {
            Text(team.name)
        },
        supportingContent = {
//            Text("Tags: ${maker.tags}")
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun TeamMemberList() {
    val viewModel = mcpTeamViewModel
    LazyColumn {
        items(viewModel.mcpTeamMembers){
            ListItem(
                headlineContent = {
                    Text(it.name)
                },
                overlineContent = {
                    Text(it.account)
                },
                supportingContent = {
                    if (it.expirationDate < -1L) Text(
                        "waiting for user acceptance",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(2.dp)
                    )else if (it.status == 0) Text(
                        "inactive",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(2.dp)
                    ) else Text(
                        "active",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(2.dp)
                    )
                }
            )
        }
    }
}