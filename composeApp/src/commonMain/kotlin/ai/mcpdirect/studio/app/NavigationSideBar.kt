package ai.mcpdirect.studio.app

import ai.mcpdirect.studio.app.agent.MyStudioScreen
import ai.mcpdirect.studio.app.auth.PasswordChangeState
import ai.mcpdirect.studio.app.auth.authViewModel
import ai.mcpdirect.studio.app.model.account.AIPortUser
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import mcpdirectstudioapp.composeapp.generated.resources.Res
import mcpdirectstudioapp.composeapp.generated.resources.account_circle
import mcpdirectstudioapp.composeapp.generated.resources.block
import mcpdirectstudioapp.composeapp.generated.resources.check
import mcpdirectstudioapp.composeapp.generated.resources.dark_mode
import mcpdirectstudioapp.composeapp.generated.resources.light_mode
import mcpdirectstudioapp.composeapp.generated.resources.logout
import mcpdirectstudioapp.composeapp.generated.resources.mcpdirect_studio_256
import mcpdirectstudioapp.composeapp.generated.resources.password
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun NavigationSideBar(){
    var showMenu by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showChangePasswordDialog by remember { mutableStateOf(false) }
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    val passwordChangeState = authViewModel.passwordChangeState

    // Register the log handler

    //    var currentScreen = generalViewModel.currentScreen
    @Composable
    fun navigationRailItem(screen: Screen) {
        NavigationRailItem(
//                modifier = Modifier.padding(4.dp),
            icon = {
                Row(Modifier.width(160.dp), verticalAlignment = Alignment.CenterVertically){
                    Icon(painterResource(screen.icon),
                        contentDescription = stringResource(screen.title))
                    Text(stringResource(screen.title),
                        Modifier.padding(start = 16.dp),
                        style = MaterialTheme.typography.labelLarge)
                }},
            selected = generalViewModel.currentScreen == screen,
            onClick = { generalViewModel.currentScreen = screen }
        )
    }
    Row(Modifier.fillMaxSize()) {
        NavigationRail(
            modifier = Modifier.width(196.dp),
            header = {
                Image(
                    painter = painterResource(Res.drawable.mcpdirect_studio_256),
                    contentDescription = "MCPdirect Studio",
                    modifier = Modifier.padding(horizontal = 16.dp).width(180.dp)
                )
            }) {
//            navigationRailItem(Screen.ConnectMCP)
            navigationRailItem(Screen.MCPAccessKey)
//            navigationRailItem(Screen.ToolsLogbook)
            HorizontalDivider()
            navigationRailItem(Screen.VirtualMCP)
            HorizontalDivider()
            navigationRailItem(Screen.MyStudio)
            navigationRailItem(Screen.MCPTeam)
            HorizontalDivider()
            Spacer(modifier = Modifier.weight(1f))
            HorizontalDivider()
            NavigationRailItem(
//                modifier = Modifier.padding(4.dp),
                icon = {
                    Row(Modifier.width(160.dp),verticalAlignment = Alignment.CenterVertically){
                        Icon(painterResource(
                            if(generalViewModel.darkMode) Res.drawable.light_mode else Res.drawable.dark_mode),
                            contentDescription = ""
                        )
                        Text(
                            if(generalViewModel.darkMode) "Light" else "Dark",
                            Modifier.padding(start = 16.dp),
                            style = MaterialTheme.typography.labelLarge,
                            overflow = TextOverflow.Ellipsis)
                    }
                },
                selected = false,
                onClick = { generalViewModel.darkMode = !(generalViewModel.darkMode!!)}
            )
            navigationRailItem(Screen.UserSetting)
            NavigationRailItem(
//                modifier = Modifier.padding(4.dp),
                icon = {
                    Row(Modifier.width(160.dp), verticalAlignment = Alignment.CenterVertically){
                        Icon(painterResource(Res.drawable.account_circle), contentDescription = "Account")
                        Text(
                            authViewModel.user!!.name,
                            Modifier.padding(start = 16.dp),
                            style = MaterialTheme.typography.labelLarge,
                            overflow = TextOverflow.Ellipsis)
                    }
                },
                selected = showMenu,
                onClick = { showMenu = true  }
            )

        }
        VerticalDivider()
        Column {
            when (generalViewModel.currentScreen) {
                Screen.Dashboard -> {}
                Screen.ToolDevelopment -> {}
                Screen.ConnectMCP -> {}
                Screen.MCPAccessKey -> {}
                Screen.ToolsLogbook -> {}
                Screen.UserSetting -> {}
                Screen.ToolPermission -> {}
                Screen.MyStudio -> {
//                    MyStudioScreen(paddingValues = )
                }
                Screen.MCPTeam -> {}
                Screen.MCPTeamToolMaker -> {}
                Screen.VirtualMCP -> {}
                Screen.VirtualMCPToolConfig -> {}
                Screen.ToolDetails -> {}

            }
        }
    }

    DropdownMenu(
        expanded = showMenu,
        onDismissRequest = { showMenu = false }
    ) {
        if(authViewModel.user!!.type==AIPortUser.ANONYMOUS)DropdownMenuItem(
            text = { Text("Change Password") },
            onClick = {
                showMenu = false
                showChangePasswordDialog = true
            },
            leadingIcon = { Icon(painterResource(Res.drawable.password), "Password") }
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
        AlertDialog(
            onDismissRequest = {
                showChangePasswordDialog = false
//                settingsViewModel.resetPasswordChangeState()
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

@Composable
fun PasswordRequirements(
    currentPassword:String,
    password: String,
    confirmPassword:String,
    isErrorState: Boolean = false
) {
    val requirements = listOf(
        "Current password cannot be empty" to currentPassword.isNotBlank(),
        "New password cannot be empty" to password.isNotBlank(),
        "8+ characters" to (password.length >= 8),
        "1+ number" to password.any { it.isDigit() },
        "1+ uppercase" to password.any { it.isUpperCase() },
        "1+ lowercase" to password.any { it.isLowerCase() },
        "New password match confirm password" to (password.isNotBlank()&&password == confirmPassword)
    )

    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            "Password must contain:",
            style = MaterialTheme.typography.labelSmall,
            color = if (isErrorState) MaterialTheme.colorScheme.error
            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Spacer(Modifier.height(4.dp))
        requirements.forEach { (text, met) ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painterResource(if (met) Res.drawable.check else Res.drawable.block),
                    contentDescription = null,
                    tint = if (met) Color.Green
                    else if (isErrorState) MaterialTheme.colorScheme.error
                    else Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}