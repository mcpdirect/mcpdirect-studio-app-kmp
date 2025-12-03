package ai.mcpdirect.mcpdirectstudioapp

import ai.mcpdirect.studio.app.NavigationTopBar
import ai.mcpdirect.studio.app.Screen
import ai.mcpdirect.studio.app.UIState
import ai.mcpdirect.studio.app.agent.MyStudioScreen
import ai.mcpdirect.studio.app.auth.AuthScreen
import ai.mcpdirect.studio.app.auth.ForgotPasswordOtpVerificationScreen
import ai.mcpdirect.studio.app.auth.ForgotPasswordScreen
import ai.mcpdirect.studio.app.auth.LoginScreen
import ai.mcpdirect.studio.app.auth.RegisterOtpVerificationScreen
import ai.mcpdirect.studio.app.auth.RegisterScreen
import ai.mcpdirect.studio.app.auth.authViewModel
import ai.mcpdirect.studio.app.dashboard.DashboardScreen
import ai.mcpdirect.studio.app.generalViewModel
import ai.mcpdirect.studio.app.mcpkey.MCPAccessKeyScreen
import ai.mcpdirect.studio.app.setting.SettingsScreen
import ai.mcpdirect.studio.app.team.MCPTeamScreen
import ai.mcpdirect.studio.app.team.MCPTeamToolMakerScreen
import ai.mcpdirect.studio.app.team.MCPTeamToolMakerTemplateScreen
import ai.mcpdirect.studio.app.theme.purple.PurpleTheme
import ai.mcpdirect.studio.app.tool.MCPToolsScreen
//import ai.mcpdirect.studio.app.tool.ToolDetailScreen
import ai.mcpdirect.studio.app.tool.ToolPermissionScreen
import ai.mcpdirect.studio.app.virtualmcp.VirtualMakerScreen
import ai.mcpdirect.studio.app.virtualmcp.VirtualMakerToolConfigScreen
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App(
    darkTheme: Boolean = isSystemInDarkTheme(),
    typography: Typography? = null,
) {
//    MaterialTheme {
//        Column(
//            modifier = Modifier
//                .background(MaterialTheme.colorScheme.primaryContainer)
//                .safeContentPadding()
//                .fillMaxSize(),
//            horizontalAlignment = Alignment.CenterHorizontally,
//        ) {
//            Button(onClick = { authViewModel.login("a","b") }) {
//                Text("Click me!字体")
//            }
//            authViewModel.user?.let {
//                Text(it.name)
//            }
//        }
//    }
    PurpleTheme (
        darkTheme = darkTheme,
        typography = typography
    ) {
        Surface{
            if(authViewModel.uiState == UIState.Success){
                NavigationTopBar(
                    screens = listOf(
                        Screen.Dashboard,
                        Screen.MyStudio(),
                        Screen.MCPAccessKey(),
                        Screen.MCPTools,
                        Screen.MCPTeam(),
//                        Screen.ToolDevelopment -> {}
//                        Screen.ToolsLogbook,
//                        Screen.VirtualMCP,
                    )
                ){
                    when (val screen = generalViewModel.currentScreen) {
                        Screen.Dashboard -> {
                            DashboardScreen()
                        }
                        Screen.ToolDevelopment -> {}
                        Screen.ConnectMCP -> {}
                        is Screen.MCPAccessKey -> {
                            MCPAccessKeyScreen(
                                screen.accessKey,
                                screen.dialog
                            )
                        }
                        Screen.ToolsLogbook -> {}
                        Screen.UserSetting -> {
                            SettingsScreen()
                        }
                        is Screen.ToolPermission -> {
                            ToolPermissionScreen(screen.accessKey)
                        }
                        is Screen.MyStudio -> MyStudioScreen(
                            screen.toolAgent,
                            screen.toolMaker,
                            screen.dialog
                        )
                        is Screen.MCPTeam -> {
                            MCPTeamScreen(screen.dialog)
                        }
                        is Screen.MCPTeamToolMaker -> {
                            MCPTeamToolMakerScreen(screen.team)
                        }
                        is Screen.MCPTeamToolMakerTemplate -> {
                            MCPTeamToolMakerTemplateScreen(screen.team)
                        }
                        Screen.VirtualMCP -> {
                            VirtualMakerScreen()
                        }
                        Screen.VirtualMCPToolConfig -> {
                            VirtualMakerToolConfigScreen()
                        }
//                        Screen.ToolDetails -> {
//                            ToolDetailScreen()
//                        }
                        Screen.MCPTools -> {
                            MCPToolsScreen()
                        }
//                        Screen.OpenAPIMCP -> {
//                            OpenAPIToolMakerScreen()
//                        }
                    }
                }
            } else Row(
                Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ){
                when(authViewModel.currentScreen){
                    AuthScreen.Login -> {
                        LoginScreen()
                    }
                    AuthScreen.Register ->{
                        RegisterScreen()
                    }
                    AuthScreen.RegisterOtpVerification -> {
                        RegisterOtpVerificationScreen()
                    }
                    AuthScreen.ForgotPassword -> {
                        ForgotPasswordScreen()
                    }
                    AuthScreen.ForgotPasswordOtpVerification -> {
                        ForgotPasswordOtpVerificationScreen()
                    }
                    else -> {

                    }
                }
            }
        }
    }
}