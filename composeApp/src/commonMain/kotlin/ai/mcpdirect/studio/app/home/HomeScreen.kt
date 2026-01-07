package ai.mcpdirect.studio.app.home

import ai.mcpdirect.mcpdirectstudioapp.getPlatform
import ai.mcpdirect.studio.app.PasswordRequirements
import ai.mcpdirect.studio.app.Screen
import ai.mcpdirect.studio.app.auth.PasswordChangeState
import ai.mcpdirect.studio.app.auth.authViewModel
import ai.mcpdirect.studio.app.generalViewModel
import ai.mcpdirect.studio.app.home.view.MCPDirectKeys
import ai.mcpdirect.studio.app.home.view.MCPServers
import ai.mcpdirect.studio.app.home.view.MyStudios
import ai.mcpdirect.studio.app.home.view.MyTeams
import ai.mcpdirect.studio.app.model.account.AIPortUser
import ai.mcpdirect.studio.app.model.repository.UserRepository
import ai.mcpdirect.studio.app.setting.settingsViewModel
import androidx.compose.foundation.Image
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

@Composable
fun HomeScreen(){
    val viewModel = remember { HomeViewModel() }
    val me = UserRepository.me.value
    var showMenu by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showChangePasswordDialog by remember { mutableStateOf(false) }
    Row{
        Column(Modifier.width(300.dp).padding(top = 16.dp, bottom = 16.dp, start = 16.dp)){
            Row{
                Image(
                    painter = painterResource(Res.drawable.mcpdirect_logo_48),
                    contentDescription = "MCPdirect Studio",
                    modifier = Modifier.size(64.dp)
                )
                Column {
                    Image(
                        painter = painterResource(Res.drawable.mcpdirect_text_logo_150),
                        contentDescription = "MCPdirect Studio",
                        modifier = Modifier.width(150.dp)
                    )
                    if(getPlatform().type==0) Image(
                        painter = painterResource(Res.drawable.mcpdirect_platform_logo),
                        contentDescription = "MCPdirect Studio",
                        modifier = Modifier.width(150.dp)
                    ) else Image(
                        painter = painterResource(Res.drawable.mcpdirect_studio_logo),
                        contentDescription = "MCPdirect Studio",
                        modifier = Modifier.width(150.dp)
                    )
                }
            }

            Spacer(Modifier.height(32.dp))
            MyStudios(viewModel, Modifier)
            Spacer(Modifier.height(32.dp))
            MCPDirectKeys(viewModel,Modifier)
            Spacer(Modifier.height(32.dp))
            MyTeams(viewModel,Modifier)
            Spacer(Modifier.weight(1f))
            Row(
                Modifier.padding(top = 32.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = { showMenu = true  }
                ){
                    Icon(painterResource(Res.drawable.account_circle), contentDescription = "Account")
                    Text(
                        me.name,
                        Modifier.padding(start = 8.dp),
                        style = MaterialTheme.typography.labelLarge,
                        overflow = TextOverflow.Ellipsis)
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        Text(me.account,Modifier.padding(
                            start = 16.dp, end = 16.dp, bottom = 16.dp))
                        HorizontalDivider()
                        if(me.type!=AIPortUser.ANONYMOUS)DropdownMenuItem(
                            text = { Text("Change Password") },
                            onClick = {
                                showMenu = false
                                showChangePasswordDialog = true
                            },
                            leadingIcon = { Icon(painterResource(Res.drawable.password), "Password") }
                        )
                        if(getPlatform().type!=0)
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
                Spacer(Modifier.weight(1f))
                IconButton(
                    onClick = {
                        viewModel.refreshToolAgents(true)
                        viewModel.refreshToolMakers(true)
                        viewModel.refreshAccessKeys(true)
                        viewModel.refreshTeams(true)
                    }
                ) {
                    Icon(
                        painterResource(Res.drawable.refresh),
                        contentDescription = ""
                    )
                }
            }
        }
        ElevatedCard (Modifier.weight(1f).fillMaxHeight().padding(16.dp)){
            MCPServers(viewModel,Modifier.weight(1f))
        }
        Column(Modifier.width(300.dp).padding(top = 16.dp, bottom = 16.dp, end = 16.dp)){
            Text(
                "Quick start",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(8.dp)
            )
            HorizontalDivider()
            Row(
                Modifier.padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("3",style = MaterialTheme.typography.displayMedium)
                Text(
                    "steps, let any of your agents access any of your MCP servers",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            OutlinedCard{
                Image(
                    painterResource(Res.drawable.mcpdirect_tips_600),
                    contentDescription = "MCPdirect: Universal MCP Access Gateway",
                )
            }
            Spacer(Modifier.height(4.dp))
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    generalViewModel.currentScreen(
                        Screen.QuickStart,
                        "3 steps, let any of your agents access any of your MCP servers",
                        Screen.Home
                    )
                }){
                Text("Let's start")
            }
        }
    }
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

}