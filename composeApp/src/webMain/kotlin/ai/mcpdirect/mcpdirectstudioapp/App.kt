package ai.mcpdirect.mcpdirectstudioapp

import ai.mcpdirect.studio.app.NavigationSideBar
import ai.mcpdirect.studio.app.NavigationTopBar
import ai.mcpdirect.studio.app.Screen
import ai.mcpdirect.studio.app.UIState
import ai.mcpdirect.studio.app.agent.MyStudioScreen
import ai.mcpdirect.studio.app.auth.AuthScreen
import ai.mcpdirect.studio.app.auth.LoginScreen
import ai.mcpdirect.studio.app.auth.authViewModel
import ai.mcpdirect.studio.app.generalViewModel
import ai.mcpdirect.studio.app.theme.purple.AppTypography
import ai.mcpdirect.studio.app.theme.purple.PurpleTheme
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Typography
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.toFontFamily
import mcpdirectstudioapp.composeapp.generated.resources.Res
import mcpdirectstudioapp.composeapp.generated.resources.noto_sans_sc_regular
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.preloadFont


@OptIn(ExperimentalResourceApi::class)
@Composable
fun App(){
//    if(generalViewModel.darkMode==null) {
//        generalViewModel.darkMode = isSystemInDarkTheme()
//    }
    val notoSansSCFont by preloadFont(Res.font.noto_sans_sc_regular)
    var typography by remember { mutableStateOf<Typography?>(null) }
    LaunchedEffect(notoSansSCFont) {
        notoSansSCFont?.let {
            typography = Typography(
                displayLarge = AppTypography.displayLarge.copy(fontFamily = it.toFontFamily()),
                displayMedium = AppTypography.displayMedium.copy(fontFamily = it.toFontFamily()),
                displaySmall = AppTypography.displaySmall.copy(fontFamily = it.toFontFamily()),
                headlineLarge = AppTypography.headlineLarge.copy(fontFamily = it.toFontFamily()),
                headlineMedium = AppTypography.headlineMedium.copy(fontFamily = it.toFontFamily()),
                headlineSmall = AppTypography.headlineSmall.copy(fontFamily = it.toFontFamily()),
                titleLarge = AppTypography.titleLarge.copy(fontFamily = it.toFontFamily()),
                titleMedium = AppTypography.titleMedium.copy(fontFamily = it.toFontFamily()),
                titleSmall = AppTypography.titleSmall.copy(fontFamily = it.toFontFamily()),
                bodyLarge = AppTypography.bodyLarge.copy(fontFamily = it.toFontFamily()),
                bodyMedium = AppTypography.bodyMedium.copy(fontFamily = it.toFontFamily()),
                bodySmall = AppTypography.bodySmall.copy(fontFamily = it.toFontFamily()),
                labelLarge = AppTypography.labelLarge.copy(fontFamily = it.toFontFamily()),
                labelMedium = AppTypography.labelMedium.copy(fontFamily = it.toFontFamily()),
                labelSmall = AppTypography.labelSmall.copy(fontFamily = it.toFontFamily()),
            )
        }
    }
    if(typography==null){
        Column(Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
        }
    }else PurpleTheme (typography = typography!!) {
        Surface{
//            if(authViewModel.uiState == UIState.Success){
//                NavigationSideBar()
//            } else Row(
//                Modifier.fillMaxSize(),
//                horizontalArrangement = Arrangement.Center,
//                verticalAlignment = Alignment.CenterVertically
//            ){
//                Column(Modifier.weight(1.0f)){
//
//                }
//                when(authViewModel.currentScreen){
//                    AuthScreen.Login -> {
//                        LoginScreen()
//                    }
//                    else -> {
//
//                    }
//                }
//            }

            if(authViewModel.uiState == UIState.Success){
                NavigationTopBar(
                    screens = listOf(
                        Screen.Dashboard,
                        Screen.MyStudio,
                        Screen.MCPTeam,
//                        Screen.ToolDevelopment -> {}
                        Screen.MCPAccessKey,
//                        Screen.ToolsLogbook,
                        Screen.VirtualMCP
                    )
                ){  paddingValues ->
                    when (generalViewModel.currentScreen) {
                        Screen.Dashboard -> {}
                        Screen.ToolDevelopment -> {}
                        Screen.ConnectMCP -> {}
                        Screen.MCPAccessKey -> {}
                        Screen.ToolsLogbook -> {}
                        Screen.UserSetting -> {}
                        Screen.ToolPermission -> {}
                        Screen.MyStudio -> {
                            MyStudioScreen(paddingValues = paddingValues)
                        }
                        Screen.MCPTeam -> {}
                        Screen.MCPTeamToolMaker -> {}
                        Screen.VirtualMCP -> {}
                        Screen.VirtualMCPToolConfig -> {}
                        Screen.ToolDetails -> {}
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