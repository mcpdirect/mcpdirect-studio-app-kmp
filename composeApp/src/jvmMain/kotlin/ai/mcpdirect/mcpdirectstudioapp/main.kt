package ai.mcpdirect.mcpdirectstudioapp

import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import mcpdirectstudioapp.composeapp.generated.resources.Res
import mcpdirectstudioapp.composeapp.generated.resources.mcpdirect_logo_48
import org.jetbrains.compose.resources.painterResource

fun main(args: Array<String>) = application {
    var mcpdirect = false;
    var mcpdirectKey:String? = null
    for (arg in args) {
        if(mcpdirect) {
            mcpdirectKey = arg
            break;
        }
        if(arg=="--mcpdirect") mcpdirect=true
    }
    if(mcpdirectKey!=null){
        //TODO launch mcpdirect proxy
    }else {
        val windowState = rememberWindowState(
            size = DpSize(1200.dp, 900.dp),
            position = WindowPosition.Aligned(Alignment.Center),
        )
        val version = AppInfo.APP_VERSION
        Window(
//            undecorated = true,
            onCloseRequest = ::exitApplication,
            title = "MCPdirect Studio $version",
            state = windowState,
            icon = painterResource(Res.drawable.mcpdirect_logo_48)
        ) {
            App()
        }
    }
}