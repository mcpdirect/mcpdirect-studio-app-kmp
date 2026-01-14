package ai.mcpdirect.mcpdirectstudioapp

//import ai.mcpdirect.studio.app.tool.ToolDetailScreen
import ai.mcpdirect.studio.app.NavigationTopBar
import ai.mcpdirect.studio.app.Screen
import ai.mcpdirect.studio.app.UIState
import ai.mcpdirect.studio.app.agent.MyStudioScreen
import ai.mcpdirect.studio.app.auth.*
import ai.mcpdirect.studio.app.compose.Carousel
import ai.mcpdirect.studio.app.compose.CarouselSlide
import ai.mcpdirect.studio.app.dashboard.DashboardScreen
import ai.mcpdirect.studio.app.generalViewModel
import ai.mcpdirect.studio.app.home.HomeScreen
import ai.mcpdirect.studio.app.mcpkey.MCPAccessKeyScreen
import ai.mcpdirect.studio.app.setting.SettingsScreen
import ai.mcpdirect.studio.app.team.MCPTeamScreen
import ai.mcpdirect.studio.app.team.MCPTeamToolMakerScreen
import ai.mcpdirect.studio.app.team.MCPTeamToolMakerTemplateScreen
import ai.mcpdirect.studio.app.theme.AppTheme
import ai.mcpdirect.studio.app.tips.QuickStartScreen
import ai.mcpdirect.studio.app.tips.TipsScreen
import ai.mcpdirect.studio.app.tool.MCPToolsScreen
import ai.mcpdirect.studio.app.tool.ToolPermissionScreen
import ai.mcpdirect.studio.app.virtualmcp.VirtualMCPScreen
import ai.mcpdirect.studio.app.virtualmcp.VirtualMakerScreen
import ai.mcpdirect.studio.app.virtualmcp.VirtualMakerToolConfigScreen
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Surface
import androidx.compose.material3.Typography
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mcpdirectstudioapp.composeapp.generated.resources.Res
import mcpdirectstudioapp.composeapp.generated.resources.mcpdirect_tips_one_url
import mcpdirectstudioapp.composeapp.generated.resources.mcpdirect_tips_openapi
import mcpdirectstudioapp.composeapp.generated.resources.mcpdirect_tips_share_tools

@Composable
fun App(
    darkTheme: Boolean = isSystemInDarkTheme(),
    typography: Typography? = null,
) {
    AppTheme (
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
                        Screen.Tips
//                        Screen.ToolDevelopment -> {}
//                        Screen.ToolsLogbook,
//                        Screen.VirtualMCP,
                    )
                ){
                    when (val screen = generalViewModel.currentScreen) {
                        Screen.Home -> {
                            HomeScreen()
                        }
                        Screen.Tips -> {
                            TipsScreen()
                        }
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
                        is Screen.VirtualMCP -> {
//                            VirtualMakerScreen()
                            VirtualMCPScreen(screen.toolMaker)
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
                        Screen.QuickStart ->{
                            QuickStartScreen(PaddingValues())
                        }
                    }
                }
            } else Row(
                Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ){
                val slides = listOf(
                    CarouselSlide(
                        imageResource = Res.drawable.mcpdirect_tips_one_url, // Platform-specific
                        description = "One URL access your any in-house MCP Servers",
                        title = ""
                    ),
                    CarouselSlide(
                        imageResource = Res.drawable.mcpdirect_tips_openapi,
                        description = "Zero code to connect OpenAPI as MCP Tools",
                        title = ""
                    ),
                    CarouselSlide(
                        imageResource = Res.drawable.mcpdirect_tips_share_tools,
                        description = "Share MCP tools with your team",
                        title = ""
                    )
                )
                Carousel(
                    "Let MCP power your business",
                    slides,
                    Modifier.width(800.dp),
                )
                VerticalDivider()
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