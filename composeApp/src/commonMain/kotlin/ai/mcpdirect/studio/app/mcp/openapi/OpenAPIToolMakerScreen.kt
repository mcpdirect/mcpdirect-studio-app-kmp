package ai.mcpdirect.studio.app.mcp.openapi

import ai.mcpdirect.studio.app.compose.StudioCard
import ai.mcpdirect.studio.app.generalViewModel
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun OpenAPIToolMakerScreen(){
    val viewModel = remember { OpenAPIToolMakerViewModel() }
    LaunchedEffect(null){
        generalViewModel.topBarActions = {
            Button(
                onClick = {}
            ){
                Text("Connect")
            }
        }
    }
    DisposableEffect(null){
        onDispose {
            generalViewModel.topBarActions = {}
        }
    }
    Box(
        Modifier.fillMaxSize().padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            Modifier.fillMaxHeight().width(900.dp),
        ) {
            StudioCard(Modifier.weight(2.0f).fillMaxHeight()) {

            }
            Spacer(Modifier.width(8.dp))
            StudioCard(Modifier.weight(1.0f).fillMaxHeight())  {
                Column{
                    TextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = viewModel.name,
                        onValueChange = { viewModel.onValueChange(it) },
                        label = {Text("OpenAPI MCP Server Name")},
                        supportingText = {Text("Name must not be empty and length < 33")}
                    )
                }
            }

        }
    }
}