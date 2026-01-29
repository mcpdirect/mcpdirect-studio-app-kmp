package ai.mcpdirect.studio.app.home.widget

import ai.mcpdirect.mcpdirectstudioapp.getPlatform
import ai.mcpdirect.studio.app.Screen
import ai.mcpdirect.studio.app.compose.EditableText
import ai.mcpdirect.studio.app.compose.InlineTextField
import ai.mcpdirect.studio.app.compose.ValidatorBuilder
import ai.mcpdirect.studio.app.generalViewModel
import ai.mcpdirect.studio.app.home.HomeViewModel
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import mcpdirectstudioapp.composeapp.generated.resources.Res
import mcpdirectstudioapp.composeapp.generated.resources.add
import mcpdirectstudioapp.composeapp.generated.resources.key
import mcpdirectstudioapp.composeapp.generated.resources.sdk
import org.jetbrains.compose.resources.painterResource

@Composable
fun MCPDirectKeysWidget(
    viewModel: HomeViewModel,
    modifier: Modifier = Modifier
){
    val accessKeys by viewModel.accessKeys.collectAsState()
    LaunchedEffect(viewModel) {
        viewModel.refreshAccessKeys()
    }
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    Column(modifier.padding(start=8.dp).hoverable(interactionSource)) {
        Row(
            modifier = Modifier.height(48.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                painterResource(Res.drawable.key),
                ""
            )
            Text("MCPdirect Keys (${accessKeys.size})", fontWeight = FontWeight.Bold)
            Spacer(Modifier.weight(1.0f))
            if(isHovered) IconButton(
                onClick = {
                    generalViewModel.currentScreen(
                        Screen.MCPAccessKey(),
                        "MCPdirect Keys",
                        Screen.Home
                    )
                }
            ) {
                Icon(
                    painterResource(Res.drawable.add),
                    contentDescription = ""
                )
            }
        }
        if (accessKeys.isNotEmpty()) {
            LazyColumn {
                items(accessKeys) { accessKey ->
                    var edited by remember { mutableStateOf(false) }
                    if(!edited)TextButton(
                        modifier = Modifier.height(32.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        onClick = {
                            generalViewModel.currentScreen(
                                Screen.MCPAccessKey(accessKey),
                                "MCPdirect Keys",
                                Screen.Home
                            )
                        },
//                        border = BorderStroke(1.dp, ButtonDefaults.textButtonColors().contentColor)
                    ) {
                        Row(Modifier.fillMaxWidth()) {
                            EditableText(
                                accessKey.name,
                                overflow = TextOverflow.MiddleEllipsis,
                                onEdit = {edited = it}
                            )
                        }
                    } else InlineTextField(
                        accessKey.name,
                        modifier = Modifier.height(32.dp),
                        validator = ValidatorBuilder().required().maxLength(20).build(),
                    ){ name->
                        edited = false
                        if(name!=null) viewModel.modifyMCPdirectKey(accessKey,name){

                        }
                    }
                }
            }
        } else if (getPlatform().type == 0) Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Please create a MCPdirect access key to start")
        }
    }
}