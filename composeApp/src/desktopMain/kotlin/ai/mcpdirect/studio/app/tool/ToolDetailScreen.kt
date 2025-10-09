package ai.mcpdirect.studio.app.tool

import ai.mcpdirect.studio.app.Screen
import ai.mcpdirect.studio.app.virtual.VirtualMakerViewModel
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
fun ToolDetailScreen(
    viewModel: VirtualMakerViewModel,
    onBack: () -> Unit
) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("${stringResource(Screen.VirtualMCP.title)} Tool Config for #${viewModel.selectedVirtualMaker!!.name}" ) },

                navigationIcon = {
                    IconButton(onClick = {
                        onBack()
                    }) {
                        Icon(painterResource(Res.drawable.arrow_back), contentDescription = "Back")
                    }
                },
            )
        }
    ) { padding ->
        ToolDetailView(viewModel, padding)
    }

    LaunchedEffect(Unit) {
        if(viewModel.selectedMakerTool!=null)
            viewModel.queryToolMetadata(viewModel.selectedMakerTool!!.id)
    }
}

@Composable
private fun ToolDetailView(
    viewModel: VirtualMakerViewModel,
    padding: PaddingValues
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier.fillMaxSize().padding(padding),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            androidx.compose.material3.IconButton(onClick = {
                viewModel.selectedMakerTool = null
                viewModel.selectedMakerToolMetadata = null
            }) {
                Icon(painterResource(Res.drawable.arrow_back), contentDescription = "Back")
            }
            Text(
                text = viewModel.selectedMakerTool?.name ?: "",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f)
            )
        }
        HorizontalDivider()
        Column(Modifier.padding(8.dp).fillMaxSize().verticalScroll(scrollState)) {
                viewModel.selectedMakerToolMetadata?.let {
                    Text(
                        text = it,
                        modifier = Modifier.padding(8.dp)
                    )
                }
        }
    }

}
