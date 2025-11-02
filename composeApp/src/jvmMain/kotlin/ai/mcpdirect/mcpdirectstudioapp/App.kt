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
import ai.mcpdirect.studio.app.mcp.ConnectMCPScreen
import ai.mcpdirect.studio.app.mcpkey.MCPAccessKeyScreen
import ai.mcpdirect.studio.app.setting.SettingsScreen
import ai.mcpdirect.studio.app.team.MCPTeamScreen
import ai.mcpdirect.studio.app.team.MCPTeamToolMakerScreen
import ai.mcpdirect.studio.app.theme.purple.PurpleTheme
import ai.mcpdirect.studio.app.tool.MCPToolsScreen
import ai.mcpdirect.studio.app.tool.ToolDetailScreen
import ai.mcpdirect.studio.app.tool.ToolPermissionScreen
import ai.mcpdirect.studio.app.virtualmcp.VirtualMakerScreen
import ai.mcpdirect.studio.app.virtualmcp.VirtualMakerToolConfigScreen
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.ExperimentalResourceApi


@OptIn(ExperimentalResourceApi::class)
@Composable
fun App(){
//    generalViewModel.darkMode = isSystemInDarkTheme()
    PurpleTheme(
        darkTheme = generalViewModel.darkMode
    ) {
        Surface{
            if(authViewModel.uiState == UIState.Success){
                NavigationTopBar(
                    screens = listOf(
//                        Screen.ToolDevelopment -> {}
                        Screen.Dashboard,
                        Screen.ConnectMCP,
                        Screen.MCPTools,
                        Screen.MCPAccessKey,
//                        Screen.ToolsLogbook,
                        Screen.MyStudio,
                        Screen.MCPTeam,
                        Screen.VirtualMCP
                    )
                ){
                    when (generalViewModel.currentScreen) {
                        Screen.Dashboard -> {
                            DashboardScreen()
                        }
                        Screen.ToolDevelopment -> {}
                        Screen.ConnectMCP -> {
                            ConnectMCPScreen()
                        }
                        Screen.MCPAccessKey -> {
                            MCPAccessKeyScreen()
                        }
                        Screen.ToolsLogbook -> {}
                        Screen.UserSetting -> {
                            SettingsScreen()
                        }
                        Screen.ToolPermission -> {
                            ToolPermissionScreen()
                        }
                        Screen.MyStudio -> {
                            MyStudioScreen()
                        }
                        Screen.MCPTeam -> {
                            MCPTeamScreen()
                        }
                        Screen.MCPTeamToolMaker -> {
                            MCPTeamToolMakerScreen()
                        }
                        Screen.VirtualMCP -> {
                            VirtualMakerScreen()
                        }
                        Screen.VirtualMCPToolConfig -> {
                            VirtualMakerToolConfigScreen()
                        }
                        Screen.ToolDetails -> {
                            ToolDetailScreen()
                        }
                        Screen.MCPTools -> MCPToolsScreen()

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