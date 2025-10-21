package ai.mcpdirect.studio.app

import ai.mcpdirect.studio.app.auth.authViewModel
import ai.mcpdirect.studio.app.compose.StudioListItem
import ai.mcpdirect.studio.app.model.account.AIPortUser
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import mcpdirectstudioapp.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationTopBar(
    screens:List<Screen>,
    content: @Composable ((PaddingValues) -> Unit)
){
    var showMenu by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showChangePasswordDialog by remember { mutableStateOf(false) }
    Scaffold (
        topBar = {
            TopAppBar(
                navigationIcon = {
                    Row(verticalAlignment = Alignment.CenterVertically){
                        screens.forEach { screen ->
                            if (screen == generalViewModel.currentScreen) {

                                StudioListItem(
                                    modifier = Modifier.width(140.dp),
                                    selected = true,
//                                    leadingContent = { },
                                    headlineContent = {Row(
                                        modifier =  Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Center){
                                        Icon(
                                        painterResource(screen.icon),
                                        contentDescription = stringResource(screen.title))
                                    }},
                                    supportingContent = {Row(
                                        modifier =  Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Center){
                                        Text(stringResource(screen.title))
                                    }}
                                )
                            }else{
                                IconButton(
                                    onClick = {
                                        generalViewModel.topBarActions = {}
                                        generalViewModel.currentScreen = screen
                                    }
                                ) {
                                    Icon(
                                        painterResource(screen.icon),
                                        contentDescription = stringResource(screen.title)
                                    )
                                }
                            }
                        }
                        IconButton(
                            onClick = {
                                generalViewModel.darkMode = !(generalViewModel.darkMode)
                            }
                        ){
                            Icon(painterResource(
                                if(generalViewModel.darkMode) Res.drawable.light_mode else Res.drawable.dark_mode),
                                contentDescription = ""
                            )
                        }
                    }
                },
                title = {  },
                actions = {
                    generalViewModel.topBarActions()
                    Text(
                        authViewModel.user.name,
                        Modifier.padding(start = 16.dp),
                        style = MaterialTheme.typography.labelLarge,
                        overflow = TextOverflow.Ellipsis)
                    IconButton(
                        onClick = { showMenu = true  }
                    ){
                        Icon(painterResource(Res.drawable.account_circle), contentDescription = "Account")
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        Text(authViewModel.account)
                        if(authViewModel.user.type!=AIPortUser.ANONYMOUS)DropdownMenuItem(
                            text = { Text("Change Password") },
                            onClick = {
                                showMenu = false
                                showChangePasswordDialog = true
                            },
                            leadingIcon = { Icon(painterResource(Res.drawable.password), "Password") }
                        )

                        DropdownMenuItem(
                            text = { Text("Setting") },
                            onClick = {
                                showMenu = false
                                generalViewModel.currentScreen = Screen.UserSetting
                            },
                            leadingIcon = {
                                Icon(painterResource(Res.drawable.settings), "Setting")
                            }
                        )
                        HorizontalDivider()
                        DropdownMenuItem(
                            text = { Text("Logout") },
                            onClick = {
                                showMenu = false
                                showLogoutDialog = true
                            },
                            leadingIcon = {
                                Icon(painterResource(Res.drawable.logout), "Logout")
                            }
                        )
                    }
                }
            )
        }
    ){
        paddingValues ->
        // Logout Confirmation Dialog
        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                title = { Text("Confirm Logout") },
                text = { Text("Are you sure you want to logout?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showLogoutDialog = false
                            authViewModel.logout()
                        }
                    ) {
                        Text("Logout")
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showLogoutDialog = false }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }
        content(paddingValues)
    }
}