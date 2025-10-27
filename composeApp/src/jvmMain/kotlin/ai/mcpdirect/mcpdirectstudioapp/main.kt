package ai.mcpdirect.mcpdirectstudioapp

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import mcpdirectstudioapp.composeapp.generated.resources.Res
import mcpdirectstudioapp.composeapp.generated.resources.mcpdirect_logo_48
import mcpdirectstudioapp.composeapp.generated.resources.mcpdirect_studio_icon_transparent_48
import org.jetbrains.compose.resources.painterResource

fun main() = application {
    val windowState = rememberWindowState(size = DpSize(1024.dp, 768.dp))
    val version = System.getProperty("ai.mcpdirect.studio.app.version")
    println(System.getProperty("ai.mcpdirect.studio.app.version"))
    println(System.getProperty("ai.mcpdirect.hstp.webport"))
    println(System.getProperty("ai.mcpdirect.hstp.service.gateway"))
    Window(
        onCloseRequest = ::exitApplication,
        title = "MCPdirect Studio v$version",
        state = windowState,
        icon = painterResource(Res.drawable.mcpdirect_logo_48)
    ) {
        App()
    }
}