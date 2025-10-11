package ai.mcpdirect.studio.app.tool

import ai.mcpdirect.studio.app.Screen
import ai.mcpdirect.studio.app.generalViewModel
import ai.mcpdirect.studio.app.toolDetailViewModel
import ai.mcpdirect.studio.app.virtualmcp.VirtualMakerViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.IconButton
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mcpdirectstudioapp.composeapp.generated.resources.Res
import mcpdirectstudioapp.composeapp.generated.resources.arrow_back
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolDetailScreen() {

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tool details of #${toolDetailViewModel.toolName}" ) },

                navigationIcon = {
                    IconButton(onClick = {
                        generalViewModel.currentScreen = generalViewModel.backToScreen!!
                    }) {
                        Icon(painterResource(Res.drawable.arrow_back), contentDescription = "Back")
                    }
                },
            )
        }
    ) { padding ->
        ToolDetailView(padding)
    }

    LaunchedEffect(Unit) {
        toolDetailViewModel.queryToolMetadata()
    }
}

@Composable
private fun ToolDetailView(
    padding: PaddingValues
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier.fillMaxSize().padding(padding),
    ) {
        HorizontalDivider()
        Column(Modifier.padding(8.dp).fillMaxSize().verticalScroll(scrollState)) {
            toolDetailViewModel.toolMetadata?.let {
                Text(
                    text = it.description,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}
