package ai.mcpdirect.mcpdirectstudioapp

import ai.mcpdirect.studio.MCPDirectStudio
import ai.mcpdirect.studio.app.NavigationSideBar
import ai.mcpdirect.studio.app.NavigationTopBar
import ai.mcpdirect.studio.app.Screen
import ai.mcpdirect.studio.app.UIState
import ai.mcpdirect.studio.app.agent.MyStudioScreen
import ai.mcpdirect.studio.app.auth.AuthScreen
import ai.mcpdirect.studio.app.auth.LoginScreen
import ai.mcpdirect.studio.app.auth.authViewModel
import ai.mcpdirect.studio.app.generalViewModel
import ai.mcpdirect.studio.app.mcp.ConnectMCPScreen
import ai.mcpdirect.studio.app.mcp.connectMCPViewModel
import ai.mcpdirect.studio.app.mcpkey.MCPAccessKeyScreen
import ai.mcpdirect.studio.app.team.MCPTeamScreen
import ai.mcpdirect.studio.app.theme.purple.PurpleTheme
import ai.mcpdirect.studio.app.tool.ToolDetailScreen
import ai.mcpdirect.studio.app.tool.ToolPermissionScreen
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
                        Screen.MCPAccessKey,
                        Screen.ToolsLogbook,
                        Screen.MyStudio,
                        Screen.MCPTeam,
                        Screen.VirtualMCP
                    )
                ){
                    when (generalViewModel.currentScreen) {
                        Screen.Dashboard -> {}
                        Screen.ToolDevelopment -> {}
                        Screen.ConnectMCP -> {
                            ConnectMCPScreen()
                        }
                        Screen.MCPAccessKey -> {
                            MCPAccessKeyScreen()
                        }
                        Screen.ToolsLogbook -> {}
                        Screen.UserSetting -> {}
                        Screen.ToolPermission -> {
                            ToolPermissionScreen()
                        }
                        Screen.MyStudio -> {
                            MyStudioScreen()
                        }
                        Screen.MCPTeam -> {
                            MCPTeamScreen()
                        }
                        Screen.MCPTeamToolMaker -> {}
                        Screen.VirtualMCP -> {}
                        Screen.VirtualMCPToolConfig -> {}
                        Screen.ToolDetails -> {
                            ToolDetailScreen()
                        }

                    }
                }
            } else Row(
                Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ){
                Column(Modifier.weight(1.0f)){

                }
                when(authViewModel.currentScreen){
                    AuthScreen.Login -> {
                        LoginScreen()
                    }
                    else -> {

                    }
                }
            }
        }
    }
}