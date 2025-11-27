package ai.mcpdirect.studio.app.template

import ai.mcpdirect.studio.app.auth.authViewModel
import ai.mcpdirect.studio.app.compose.StudioCard
import ai.mcpdirect.studio.app.generalViewModel
import ai.mcpdirect.studio.app.model.aitool.AIPortToolMaker
import ai.mcpdirect.studio.app.model.repository.UserRepository
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import mcpdirectstudioapp.composeapp.generated.resources.Res
import mcpdirectstudioapp.composeapp.generated.resources.keyboard_arrow_right
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MCPTemplateScreen() {
    var showPermissionChangedDialog by remember { mutableStateOf(false) }
    val viewModel = mcpTemplateViewModel
    LaunchedEffect(null){

        generalViewModel.refreshToolMakers()
    }
//    generalViewModel.topBarActions =  {
//        IconButton(onClick = {
//            viewModel.resetAllPermissions()
//        }) {
//            Icon(painterResource(Res.drawable.reset_settings), contentDescription = "Reset To Default")
//        }
//        Button(onClick = {
//            viewModel.savePermissions()
//            generalViewModel.previousScreen()
//        }){
//            Text("Save")
//        }
//    }
    Row(Modifier.fillMaxSize()){
        LazyColumn(Modifier.weight(1.0f)) {
            items(generalViewModel.toolMakers){
                if(it.notVirtual()&& UserRepository.me(it.userId))ListItem(
                    modifier = Modifier.clickable{
                    },
                    headlineContent = { Text(
                        it.name,
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis) },
                    supportingContent = {
                        Text(it.agentName)
                    },
                    trailingContent = {
                        if(viewModel.toolMaker!=null&&viewModel.toolMaker!!.id==it.id)
                            Icon(painterResource(Res.drawable.keyboard_arrow_right),
                                contentDescription = "Current Tool Maker")
                    },
                    colors = ListItemDefaults.colors(
                        containerColor = if(viewModel.toolMaker==it)
                            MaterialTheme.colorScheme.surfaceContainer
                        else Color.Transparent
                    )
                )
            }
        }

        StudioCard(Modifier.padding(8.dp).weight(2.0f)) {

        }
    }
}