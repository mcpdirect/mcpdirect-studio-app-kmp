package ai.mcpdirect.studio.app.home

import ai.mcpdirect.mcpdirectstudioapp.getPlatform
import ai.mcpdirect.studio.app.PasswordRequirements
import ai.mcpdirect.studio.app.Screen
import ai.mcpdirect.studio.app.auth.PasswordChangeState
import ai.mcpdirect.studio.app.auth.authViewModel
import ai.mcpdirect.studio.app.generalViewModel
import ai.mcpdirect.studio.app.home.widget.MCPDirectKeysWidget
import ai.mcpdirect.studio.app.home.widget.MCPServersWidget
import ai.mcpdirect.studio.app.home.widget.MyStudiosWidget
import ai.mcpdirect.studio.app.home.widget.MyTeamsView
import ai.mcpdirect.studio.app.home.widget.QuickstartWidget
import ai.mcpdirect.studio.app.home.widget.VirtualMCPWidget
import ai.mcpdirect.studio.app.model.account.AIPortUser
import ai.mcpdirect.studio.app.model.repository.UserRepository
import ai.mcpdirect.studio.app.setting.settingsViewModel
import ai.mcpdirect.studio.app.tips.TipsScreen
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import mcpdirectstudioapp.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource

@Composable
fun HomeScreen(){
    val viewModel = remember { HomeViewModel() }
    val me = UserRepository.me.value
    var showMenu by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showChangePasswordDialog by remember { mutableStateOf(false) }
    var showTipsDialog by remember { mutableStateOf(false) }
    Row{
        Column(Modifier.width(300.dp).padding(top = 32.dp, bottom = 16.dp, start = 16.dp)){
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
            QuickstartWidget(Modifier.weight(1f))
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
                    onClick = { showTipsDialog = true }
                ) {
                    Icon(
                        painterResource(Res.drawable.lightbulb_2),
                        contentDescription = ""
                    )
                }
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
        Column(Modifier.weight(1f)) {
            Card (Modifier.weight(2f).fillMaxHeight().padding(start = 16.dp,top = 16.dp,end = 16.dp)) {
                MCPServersWidget(viewModel,Modifier.weight(2f))
            }
            Spacer(Modifier.height(8.dp))
            Card (Modifier.weight(1f).fillMaxHeight().padding(start = 16.dp, bottom = 16.dp,end = 16.dp)){
                VirtualMCPWidget(viewModel,Modifier.weight(1f))
            }
        }
        Column(Modifier.width(300.dp).padding(top = 16.dp, bottom = 16.dp, end = 16.dp)){
            MyStudiosWidget(viewModel)
            Spacer(Modifier.height(16.dp))
            MCPDirectKeysWidget(viewModel)
            Spacer(Modifier.height(16.dp))
            MyTeamsView(viewModel)
            Spacer(Modifier.height(16.dp))
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

    if(showTipsDialog||viewModel.showTips?:false) BlankDialog (
        onDismiss = {
            showTipsDialog = false
            viewModel.showTips = false
        }
    ){
        TipsScreen()
    }
    if(generalViewModel.previousScreen!=null){
        showTipsDialog = false
        viewModel.showTips = false
    }
}

@Composable
fun BlankDialog(
    title:String?=null,
    onDismiss: () -> Unit,
    content: @Composable ((PaddingValues) -> Unit)
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            // 1. Allow custom width/height (CRITICAL for custom sizes)
            usePlatformDefaultWidth = false,
            // 2. Allow clicking outside to dismiss?
            dismissOnClickOutside = true,
            // 3. Allow back button to dismiss? (Android only)
            dismissOnBackPress = true
        )
    ) {
        // Now you have full control over the size.
        // Example: A nearly full-screen dialog
        Surface(
            modifier = Modifier
                .fillMaxSize(0.9f) // Take up 90% of the screen
                .wrapContentHeight(),
            shape = RoundedCornerShape(24.dp),
            color = Color.White
        ) {
            Box(Modifier.fillMaxSize()) {
                content(PaddingValues(top=48.dp))
                Row(
                    Modifier.height(48.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    title?.let {
                        Text(title, Modifier.padding(start=16.dp), style = MaterialTheme.typography.titleLarge)
                    }
                    Spacer(Modifier.weight(1f))
                    IconButton(onClick = onDismiss){
                        Icon(painterResource(Res.drawable.close),contentDescription = null)
                    }
                }
            }
        }
    }
}