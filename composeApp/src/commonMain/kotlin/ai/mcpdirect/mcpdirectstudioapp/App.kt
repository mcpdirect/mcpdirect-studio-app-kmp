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
import ai.mcpdirect.studio.app.mcpkey.MCPAccessKeyScreen
import ai.mcpdirect.studio.app.setting.SettingsScreen
import ai.mcpdirect.studio.app.team.MCPTeamScreen
import ai.mcpdirect.studio.app.team.MCPTeamToolMakerScreen
import ai.mcpdirect.studio.app.team.MCPTeamToolMakerTemplateScreen
import ai.mcpdirect.studio.app.theme.blue.BlueTheme
import ai.mcpdirect.studio.app.theme.purple.PurpleTheme
import ai.mcpdirect.studio.app.tips.QuickStartScreen
import ai.mcpdirect.studio.app.tips.TipsScreen
import ai.mcpdirect.studio.app.tool.MCPToolsScreen
import ai.mcpdirect.studio.app.tool.ToolPermissionScreen
import ai.mcpdirect.studio.app.virtualmcp.VirtualMakerScreen
import ai.mcpdirect.studio.app.virtualmcp.VirtualMakerToolConfigScreen
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
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
import mcpdirectstudioapp.composeapp.generated.resources.one_key_all_mcps
import mcpdirectstudioapp.composeapp.generated.resources.openapi_mcp_tools
import mcpdirectstudioapp.composeapp.generated.resources.share_mcp_tools
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
    BlueTheme (
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
                        Screen.QuickStart ->{
                            QuickStartScreen()
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
                        imageResource = Res.drawable.one_key_all_mcps, // Platform-specific
                        description = "One API Key for your any MCP Servers in house",
                        title = "Let MCP power your business"
                    ),
                    CarouselSlide(
                        imageResource = Res.drawable.openapi_mcp_tools,
                        description = "Zero code to connect OpenAPI as MCP Tools",
                        title = "Let MCP power your business"
                    ),
                    CarouselSlide(
                        imageResource = Res.drawable.share_mcp_tools,
                        description = "Share MCP tools with your team",
                        title = "Let MCP power your business"
                    )
                )
                Carousel(
                    slides,
                    Modifier.width(800.dp),
                )
//                Column(
//                    Modifier.width(800.dp),
//                    verticalArrangement = Arrangement.Center,
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//                    Text(
//                        "One API Key for your any MCP Servers in house",
//                        style = MaterialTheme.typography.headlineMedium,
//                    )
//                    Image(
//                        painterResource(Res.drawable.one_key_all_mcps),
//                        contentDescription = null,
//                        modifier = Modifier.width(600.dp)
//                    )
//                }
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