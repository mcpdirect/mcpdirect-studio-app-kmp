package ai.mcpdirect.studio.app

import ai.mcpdirect.studio.app.auth.AuthViewModel
import ai.mcpdirect.studio.app.auth.PasswordChangeState
import ai.mcpdirect.studio.app.auth.authViewModel
import ai.mcpdirect.studio.app.compose.StudioListItem
import ai.mcpdirect.studio.app.model.account.AIPortUser
import ai.mcpdirect.studio.app.setting.SettingsViewModel
import ai.mcpdirect.studio.app.setting.settingsViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import mcpdirectstudioapp.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavigationTopBar(
    screens:List<Screen>,
    content: @Composable (BoxScope.() -> Unit)
){
    var showMenu by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showChangePasswordDialog by remember { mutableStateOf(false) }
    Scaffold (
        topBar = {
            TopAppBar(
                navigationIcon = {
                    Row(verticalAlignment = Alignment.CenterVertically){
                        if(generalViewModel.previousScreen!=null)IconButton(
                            onClick = {
                                generalViewModel.topBarActions = {}
                                generalViewModel.currentScreen(generalViewModel.previousScreen!!)
                            }
                        ) {
                            Icon(
                                painterResource(Res.drawable.arrow_back),
                                contentDescription = ""
                            )
                        } else screens.forEach { screen ->
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
                                        generalViewModel.currentScreen(screen)
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
                title = { generalViewModel.currentScreenTitle?.let { Text(it) } },
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
                        Text(authViewModel.account,Modifier.padding(
                            start = 16.dp, end = 16.dp, bottom = 16.dp))
                        HorizontalDivider()
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
                                generalViewModel.currentScreen(Screen.UserSetting)
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
        if (showChangePasswordDialog) {
            var currentPassword by remember { mutableStateOf("") }
            var newPassword by remember { mutableStateOf("") }
            var confirmPassword by remember { mutableStateOf("") }
            val passwordChangeState = authViewModel.passwordChangeState
            AlertDialog(
                onDismissRequest = {
                    showChangePasswordDialog = false
                    settingsViewModel.resetPasswordChangeState()
                },
                title = { Text("Change Password") },
                // In SettingsScreen.kt - Update the dialog's text section
                text = {
                    Column {
                        // Input fields
                        OutlinedTextField(
                            value = currentPassword,
                            onValueChange = { currentPassword = it },
                            label = { Text("Current Password") },
                            visualTransformation = PasswordVisualTransformation(),
                            isError = passwordChangeState is PasswordChangeState.Error
                                    && currentPassword.isNotEmpty(),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(Modifier.height(16.dp))

                        OutlinedTextField(
                            value = newPassword,
                            onValueChange = { newPassword = it },
                            label = { Text("New Password") },
                            visualTransformation = PasswordVisualTransformation(),
                            isError = passwordChangeState is PasswordChangeState.Error
                                    && newPassword.isNotEmpty(),
                            modifier = Modifier.fillMaxWidth()
                        )

                        PasswordRequirements(currentPassword,newPassword,confirmPassword) // Your existing component

                        Spacer(Modifier.height(16.dp))

                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            label = { Text("Confirm Password") },
                            visualTransformation = PasswordVisualTransformation(),
                            isError = passwordChangeState is PasswordChangeState.Error
                                    && confirmPassword.isNotEmpty(),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(16.dp))
                        // Success/Error message display
                        when (passwordChangeState) {
                            is PasswordChangeState.Success -> {
                                showChangePasswordDialog = false
                            }
                            is PasswordChangeState.Error -> {
                                Text(
                                    text = passwordChangeState.message,
                                    color = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                            }
                            else -> {}
                        }
                    }
                },
                confirmButton = {
                    if (passwordChangeState is PasswordChangeState.Loading) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    } else {
                        TextButton(
                            onClick = {
                                authViewModel.changePassword(
                                    currentPassword,
                                    confirmPassword
                                )
                            },
                            enabled = currentPassword.isNotBlank()&&
                                    newPassword.isNotBlank()&&
                                    newPassword.length >= 8&&
                                    newPassword.any { it.isDigit() }&&
                                    newPassword.any { it.isUpperCase() }&&
                                    newPassword.any { it.isLowerCase() }&&
                                    newPassword == confirmPassword
                        ) {
                            Text("Change Password")
                        }
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showChangePasswordDialog = false
                            authViewModel.resetPasswordChangeState()
                        }
                    ) {
                        Text("Cancel")
                    }
                }
            )
        }
        Box(
            Modifier.fillMaxSize().padding(paddingValues),
            content = content
        )

    }
}