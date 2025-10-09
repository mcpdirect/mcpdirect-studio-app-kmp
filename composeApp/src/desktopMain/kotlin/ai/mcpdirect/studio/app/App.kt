package ai.mcpdirect.studio.app

import ai.mcpdirect.studio.MCPDirectStudio
import ai.mcpdirect.studio.app.auth.*
import ai.mcpdirect.studio.app.key.AccessKeyNotificationHandlerImplement
import ai.mcpdirect.studio.app.key.AccessKeyPermissionScreen
import ai.mcpdirect.studio.app.key.AccessKeyScreen
import ai.mcpdirect.studio.app.key.AccessKeyToolPermissionScreen
import ai.mcpdirect.studio.app.key.AccessKeyToolPermissionViewModel
import ai.mcpdirect.studio.app.key.AccessKeyViewModel
import ai.mcpdirect.studio.app.logbook.ToolsLogHandlerImplement
import ai.mcpdirect.studio.app.logbook.ToolsLogViewModel
import ai.mcpdirect.studio.app.logbook.ToolsLogbookScreen
import ai.mcpdirect.studio.app.mcp.MCPServerIntegrationScreen
import ai.mcpdirect.studio.app.mcp.MCPServerIntegrationViewModel
import ai.mcpdirect.studio.app.mcp.MCPServerNotificationHandlerImplement
//import ai.mcpdirect.studio.app.setting.PasswordRequirements
import ai.mcpdirect.studio.app.setting.SettingsScreen
import ai.mcpdirect.studio.app.setting.SettingsViewModel
import ai.mcpdirect.studio.app.theme.purple.PurpleTheme
import ai.mcpdirect.studio.app.virtual.VirtualMakerScreen
import ai.mcpdirect.studio.app.virtual.VirtualMakerToolConfigScreen
import ai.mcpdirect.studio.app.virtual.VirtualMakerViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
//import androidx.compose.material.NavigationRail
//import androidx.compose.material.NavigationRailItem
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import mcpdirectstudioapp.composeapp.generated.resources.*
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

sealed class Screen(val title: StringResource, val icon: DrawableResource) {
    object ToolDevelopment : Screen(Res.string.tool_development,
        Res.drawable.handyman)
    object MCPServerIntegration : Screen(Res.string.mcp_server_integration,
        Res.drawable.usb)
    object ToolsLogbook : Screen(Res.string.tools_logbook,
        Res.drawable.data_info_alert)
    object AgentInteraction : Screen(Res.string.agent_interaction,
        Res.drawable.key)
    object UserSetting : Screen(Res.string.user_setting,
        Res.drawable.settings)
    object ToolPermission : Screen(Res.string.tool_permission,
        Res.drawable.shield_toggle)
    object MyStudio : Screen(Res.string.my_studio,
        Res.drawable.design_services)
    object MyTeam : Screen(Res.string.my_team,
        Res.drawable.diversity_3)
    object VirtualMCP : Screen(
        Res.string.virtual_mcp,
        Res.drawable.graph_2)
    object VirtualMCPToolConfig : Screen(
        Res.string.virtual_mcp,
        Res.drawable.graph_2)
}

val authViewModel = AuthViewModel()

//val repository = ToolsLogRepositoryImpl() // Your implementation
val toolLogsViewModel = ToolsLogViewModel()
val mcpServerIntegrationViewModel = MCPServerIntegrationViewModel()

val accessKeyViewModel = AccessKeyViewModel()
val accessKeyToolPermissionViewModel = AccessKeyToolPermissionViewModel()

val settingsViewModel = SettingsViewModel()
val virtualMakerViewModel = VirtualMakerViewModel()

val darkMode = mutableStateOf<Boolean?>(null)
@Composable
fun App() {
    MCPDirectStudio.setUserInfoNotificationHandler(UserInfoNotificationHandlerImplement(authViewModel))
    if(darkMode.value==null) {
        darkMode.value = isSystemInDarkTheme()
    }
    PurpleTheme(darkTheme = darkMode.value == true) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.surface
        ) {
            when (authViewModel.uiState) {
                is UiState.SuccessWithAccount,
                is UiState.SuccessWithAnonymous -> {
                    MCPDirectStudio.setToolLogHandler(ToolsLogHandlerImplement(toolLogsViewModel))
                    MCPDirectStudio.setMcpServerNotificationHandler(
                        MCPServerNotificationHandlerImplement(
                            mcpServerIntegrationViewModel
                        )
                    )
                    MCPDirectStudio.setAccessKeyNotificationHandler(
                        AccessKeyNotificationHandlerImplement(
                            accessKeyViewModel
                        )
                    )
                    MCPDirectStudio.setToolAgentsDetailsNotificationHandler(
                        AccessKeyNotificationHandlerImplement(
                            accessKeyViewModel
                        )
                    )
                    MainAppContent()
                }

                else -> {
                    AuthContent(authViewModel)
                }
            }
        }
    }
}

@Composable
fun AuthContent(authViewModel: AuthViewModel) {
    when (authViewModel.currentScreen) {
        is AuthScreen.AuthOption -> AuthOptionScreen(authViewModel)
        is AuthScreen.Login -> LoginScreen(authViewModel)
        is AuthScreen.Register -> RegisterScreen(authViewModel)
        is AuthScreen.RegisterOtpVerification -> RegisterOtpVerificationScreen(authViewModel)
//        is AuthScreen.RegisterSetPassword -> RegisterSetPasswordScreen(authViewModel)
        is AuthScreen.ForgotPassword -> ForgotPasswordScreen(authViewModel)
        is AuthScreen.ForgotPasswordOtpVerification -> ForgotPasswordOtpVerificationScreen(authViewModel)
        is AuthScreen.AnonymousLogin -> AnonymousLoginScreen(authViewModel)
    }
}


@Composable
fun MainAppContent() {
    var showMenu by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showChangePasswordDialog by remember { mutableStateOf(false) }
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    val passwordChangeState = authViewModel.passwordChangeState.value

        // Register the log handler

    var currentScreen by remember { mutableStateOf<Screen>(Screen.MCPServerIntegration) }
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
            selected = currentScreen == screen,
            onClick = { currentScreen = screen }
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

//            NavigationRailItem(
////                modifier = Modifier.padding(4.dp),
//                icon = {
//                    Row(Modifier.width(160.dp), verticalAlignment = Alignment.CenterVertically){
//                        Icon(painterResource(Screen.MCPServerIntegration.icon),
//                            contentDescription = stringResource(Screen.MCPServerIntegration.title))
//                        Text(stringResource(Screen.MCPServerIntegration.title),
//                            Modifier.padding(start = 16.dp),
//                            style = MaterialTheme.typography.labelLarge)
//                    }},
//                selected = currentScreen == Screen.MCPServerIntegration,
//                onClick = { currentScreen = Screen.MCPServerIntegration }
//            )
            navigationRailItem(Screen.MCPServerIntegration)
//            NavigationRailItem(
////                modifier = Modifier.padding(4.dp),
//                icon = {
//                    Row(Modifier.width(160.dp), verticalAlignment = Alignment.CenterVertically){
//                        Icon(
//                            painterResource(Screen.AgentInteraction.icon),
//                            contentDescription = stringResource(Res.string.agent_interaction)
//                        )
//                        Text(stringResource(Res.string.agent_interaction),
//                            Modifier.padding(start = 16.dp),
//                            style = MaterialTheme.typography.labelLarge)
//                    }
//                },
//                selected = currentScreen == Screen.AgentInteraction,
//                onClick = { currentScreen = Screen.AgentInteraction }
//            )
            navigationRailItem(Screen.AgentInteraction)
//            NavigationRailItem(
////                modifier = Modifier.padding(4.dp),
//                icon = {
//                    Row(Modifier.width(160.dp), verticalAlignment = Alignment.CenterVertically){
//                        Icon(painterResource(Screen.ToolsLogbook.icon),
//                            contentDescription = stringResource(Res.string.tools_logbook))
//                        Text(stringResource(Res.string.tools_logbook),
//                            Modifier.padding(start = 16.dp),
//                            style = MaterialTheme.typography.labelLarge)
//                    }},
//                selected = currentScreen == Screen.ToolsLogbook,
//                onClick = { currentScreen = Screen.ToolsLogbook }
//            )
            navigationRailItem(Screen.ToolsLogbook)
            HorizontalDivider()
//            NavigationRailItem(
////                modifier = Modifier.padding(4.dp),
//                icon = {
//                    Row(Modifier.width(160.dp),verticalAlignment = Alignment.CenterVertically){
//                        Icon(painterResource(Screen.ToolDevelopment.icon), contentDescription = stringResource(Res.string.tool_development))
//                        Text(
//                            stringResource(Res.string.tool_development),
//                            Modifier.padding(start = 16.dp),
//                            style = MaterialTheme.typography.labelLarge
//                        )
//                    }
//                },
//                alwaysShowLabel = false,
//                selected = currentScreen == Screen.ToolDevelopment,
//                onClick = { currentScreen = Screen.ToolDevelopment }
//            )
            navigationRailItem(Screen.ToolDevelopment)
            HorizontalDivider()
//            NavigationRailItem(
////                modifier = Modifier.padding(4.dp),
//                icon = {
//                    Row(Modifier.width(160.dp), verticalAlignment = Alignment.CenterVertically){
//                        Icon(painterResource(Screen.MyStudio.icon),
//                            contentDescription = stringResource(Res.string.my_studio))
//                        Text(stringResource(Res.string.my_studio),
//                            Modifier.padding(start = 16.dp),
//                            style = MaterialTheme.typography.labelLarge)
//                    }},
//                selected = currentScreen == Screen.MyStudio,
//                onClick = { currentScreen = Screen.MyStudio }
//            )
            navigationRailItem(Screen.MyStudio)
//            NavigationRailItem(
////                modifier = Modifier.padding(4.dp),
//                icon = {
//                    Row(Modifier.width(160.dp),verticalAlignment = Alignment.CenterVertically){
//                        Icon(painterResource(Screen.MyTeam.icon),
//                            contentDescription = stringResource(Screen.MyTeam.title))
//                        Text(
//                            stringResource(Screen.MyTeam.title),
//                            Modifier.padding(start = 16.dp),
//                            style = MaterialTheme.typography.labelLarge
//                        )
//                    }
//                },
//                alwaysShowLabel = false,
//                selected = currentScreen == Screen.MyTeam,
//                onClick = { currentScreen = Screen.MyTeam }
//            )
            navigationRailItem(Screen.MyTeam)
//            NavigationRailItem(
////                modifier = Modifier.padding(4.dp),
//                icon = {
//                    Row(Modifier.width(160.dp),verticalAlignment = Alignment.CenterVertically){
//                        Icon(painterResource(Screen.VirtualMCP.icon), contentDescription = stringResource(Res.string.tool_development))
//                        Text(
//                            stringResource(Screen.VirtualMCP.title),
//                            Modifier.padding(start = 16.dp),
//                            style = MaterialTheme.typography.labelLarge
//                        )
//                    }
//                },
//                alwaysShowLabel = false,
//                selected = currentScreen == Screen.VirtualMCP,
//                onClick = { currentScreen = Screen.VirtualMCP }
//            )
            navigationRailItem(Screen.VirtualMCP)
            Spacer(modifier = Modifier.weight(1f))
            HorizontalDivider()
            NavigationRailItem(
//                modifier = Modifier.padding(4.dp),
                icon = {
                    Row(Modifier.width(160.dp),verticalAlignment = Alignment.CenterVertically){
                        Icon(painterResource(
                            if(darkMode.value == true) Res.drawable.light_mode else Res.drawable.dark_mode),
                            contentDescription = ""
                        )
                        Text(
                            if(darkMode.value == true) "Light" else "Dark",
                            Modifier.padding(start = 16.dp),
                            style = MaterialTheme.typography.labelLarge,
                            overflow = TextOverflow.Ellipsis)
                    }
                },
                selected = false,
                onClick = { darkMode.value=!darkMode.value!! }
            )
//            NavigationRailItem(
////                modifier = Modifier.padding(4.dp),
//                icon = {
//                    Row(Modifier.width(160.dp), verticalAlignment = Alignment.CenterVertically){
//                        Icon(painterResource(Screen.UserSetting.icon), contentDescription = stringResource(Res.string.user_setting))
//                        Text(stringResource(Res.string.user_setting),
//                            Modifier.padding(start = 16.dp),
//                            style = MaterialTheme.typography.labelLarge)
//                    }
//                },
//                selected = currentScreen == Screen.UserSetting,
//                onClick = { currentScreen = Screen.UserSetting }
//            )
            navigationRailItem(Screen.UserSetting)
            NavigationRailItem(
//                modifier = Modifier.padding(4.dp),
                icon = {
                    Row(Modifier.width(160.dp), verticalAlignment = Alignment.CenterVertically){
                        Icon(painterResource(Res.drawable.account_circle), contentDescription = "Account")
                        Text(
                            authViewModel.userInfo.value!!.name,
                            Modifier.padding(start = 16.dp),
                            style = MaterialTheme.typography.labelLarge,
                            overflow = TextOverflow.Ellipsis)
                    }
                },
                selected = showMenu,
                onClick = { showMenu = true  }
            )

        }
        Column {
            when (currentScreen) {
                Screen.ToolDevelopment -> ToolDevelopmentScreen()
                Screen.MCPServerIntegration -> MCPServerIntegrationScreen(mcpServerIntegrationViewModel){}
                Screen.AgentInteraction -> AccessKeyScreen(accessKeyViewModel){
                    accessKeyViewModel.apiKey.value = it
                    accessKeyToolPermissionViewModel.accessKey = it
                    currentScreen = Screen.ToolPermission
                }
                Screen.ToolsLogbook -> ToolsLogbookScreen(toolLogsViewModel) {

                }
                Screen.UserSetting -> SettingsScreen(authViewModel.userInfo,settingsViewModel){
                    authViewModel.uiState = UiState.Idle
                }
//                Screen.ToolPermission -> AccessKeyPermissionScreen(accessKeyViewModel){
//                    currentScreen = Screen.AgentInteraction
//                }
                Screen.ToolPermission -> AccessKeyToolPermissionScreen{
                    currentScreen = Screen.AgentInteraction
                }
                Screen.MyStudio -> MyStudioScreen()
                Screen.MyTeam -> Card {  }
                Screen.VirtualMCP -> VirtualMakerScreen(virtualMakerViewModel){
                    currentScreen = Screen.VirtualMCPToolConfig
                }
                Screen.VirtualMCPToolConfig -> VirtualMakerToolConfigScreen(virtualMakerViewModel){
                    currentScreen = Screen.VirtualMCP
                }
            }
        }
    }

    DropdownMenu(
        expanded = showMenu,
        onDismissRequest = { showMenu = false }
    ) {
        when (authViewModel.uiState) {
            is UiState.SuccessWithAccount -> {
                DropdownMenuItem(
                    text = { Text("Change Password") },
                    onClick = {
                        showMenu = false
                        showChangePasswordDialog = true
                    },
                    leadingIcon = { Icon(painterResource(Res.drawable.password), "Password") }
                )
                HorizontalDivider()
            }
            else -> {}
        }

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
                        authViewModel.uiState = UiState.Idle
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
                        isError = passwordChangeState is AuthViewModel.PasswordChangeState.Error
                                && currentPassword.isNotEmpty(),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(16.dp))

                    OutlinedTextField(
                        value = newPassword,
                        onValueChange = { newPassword = it },
                        label = { Text("New Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        isError = passwordChangeState is AuthViewModel.PasswordChangeState.Error
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
                        isError = passwordChangeState is AuthViewModel.PasswordChangeState.Error
                                && confirmPassword.isNotEmpty(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(16.dp))
                    // Success/Error message display
                    when (passwordChangeState) {
                        is AuthViewModel.PasswordChangeState.Success -> {
                            showChangePasswordDialog = false
                        }
                        is AuthViewModel.PasswordChangeState.Error -> {
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
                if (passwordChangeState is AuthViewModel.PasswordChangeState.Loading) {
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
@Composable
fun ToolDevelopmentScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Coming Soon!", style = MaterialTheme.typography.headlineMedium)
    }
}
@Composable
fun MyStudioScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Coming Soon!", style = MaterialTheme.typography.headlineMedium)
    }
}
