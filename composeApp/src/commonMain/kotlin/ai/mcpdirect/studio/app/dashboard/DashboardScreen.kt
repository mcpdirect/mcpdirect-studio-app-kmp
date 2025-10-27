package ai.mcpdirect.studio.app.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mcpdirectstudioapp.composeapp.generated.resources.Res
import mcpdirectstudioapp.composeapp.generated.resources.mcpdirect_logo_512
import mcpdirectstudioapp.composeapp.generated.resources.mcpdirect_text_logo
import org.jetbrains.compose.resources.painterResource

@Composable
fun DashboardScreen(){
    Column(Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painter = painterResource(Res.drawable.mcpdirect_text_logo),
            contentDescription = "MCPdirect",
            modifier = Modifier.width(512.dp)
        )
        Image(
            painter = painterResource(Res.drawable.mcpdirect_logo_512),
            contentDescription = "MCPdirect",
        )
    }
}