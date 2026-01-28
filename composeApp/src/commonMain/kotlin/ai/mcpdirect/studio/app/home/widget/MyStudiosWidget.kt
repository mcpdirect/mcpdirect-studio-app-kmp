package ai.mcpdirect.studio.app.home.widget

import ai.mcpdirect.mcpdirectstudioapp.getPlatform
import ai.mcpdirect.studio.app.Screen
import ai.mcpdirect.studio.app.compose.EditableText
import ai.mcpdirect.studio.app.compose.InlineTextField
import ai.mcpdirect.studio.app.generalViewModel
import ai.mcpdirect.studio.app.home.HomeViewModel
import ai.mcpdirect.studio.app.model.repository.UserRepository
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import mcpdirectstudioapp.composeapp.generated.resources.Res
import mcpdirectstudioapp.composeapp.generated.resources.design_services
import org.jetbrains.compose.resources.painterResource

@Composable
fun MyStudiosWidget(
    viewModel: HomeViewModel,
    modifier: Modifier = Modifier
){
    val toolAgents by viewModel.toolAgents.collectAsState()
    LaunchedEffect(viewModel) {
        viewModel.refreshToolAgents()
    }
//    val interactionSource = remember { MutableInteractionSource() }
//    val isHovered by interactionSource.collectIsHoveredAsState()
//    Column(modifier.padding(start=8.dp).hoverable(interactionSource)) {
    Column(modifier.padding(start=8.dp)) {
        Row(
            modifier = Modifier.height(48.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                painterResource(Res.drawable.design_services),
                ""
            )
            Text("My Studios (${toolAgents.size - 1})", fontWeight = FontWeight.Bold)
//            Spacer(Modifier.weight(1.0f))
//            if(isHovered)IconButton(
//                onClick = {
//                    generalViewModel.currentScreen(
//                        Screen.MyStudio(),
//                        "My Studios",
//                        Screen.Home
//                    )
//                }
//            ) {
//                Icon(
//                    painterResource(Res.drawable.add),
//                    contentDescription = ""
//                )
//            }
        }
//        HorizontalDivider()
        if (toolAgents.size > 1) {
            val localToolAgent by viewModel.localToolAgent.collectAsState()
            LazyColumn{
                items(toolAgents) {
                    if (it.id > 0L && UserRepository.me(it.userId)) {
                        var edited by remember { mutableStateOf(false) }
                        if(!edited)TextButton(
                            enabled = !edited,
                            modifier = Modifier.height(32.dp),
                            contentPadding = PaddingValues(horizontal = if(edited) 0.dp else 16.dp),
                            onClick = {
                                generalViewModel.currentScreen(
                                    Screen.MyStudio(it),
                                    "My Studios",
                                    Screen.Home
                                )
                            },
                        ) {
                            Row(Modifier.fillMaxWidth()) {
                                BadgedBox(
                                    badge = {
                                        if (!edited && it.id == localToolAgent.id) Badge(Modifier.padding(start = 8.dp)) {
                                            Text(
                                                "This device",
                                                style = MaterialTheme.typography.labelSmall,
                                            )
                                        }
                                    }
                                ) {
                                    EditableText(
                                        it.name, softWrap = false,
                                        overflow = TextOverflow.MiddleEllipsis,
                                        onEdit = {edited = it},
                                    )
                                }
                            }
                        } else InlineTextField(
                            it.name,
                            modifier = Modifier.height(32.dp),
                            validator = { it.length<31 }
                        ){ name->
                            edited = false
                            if(name!=null) viewModel.modifyToolAgent(it,name){

                            }
                        }
                    }
                }
            }
        } else if (getPlatform().type == 0) Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val uriHandler = LocalUriHandler.current
            Text("Please download MCPdirect Studio to start")
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    uriHandler.openUri("https://github.com/mcpdirect/mcpdirect-studio-app-kmp/releases")
                }
            ) {
                Text("Download")
            }
        }
    }
}