package ai.mcpdirect.studio.app.tool

import ai.mcpdirect.studio.app.Screen
import ai.mcpdirect.studio.app.auth.authViewModel
import ai.mcpdirect.studio.app.compose.StudioIcon
import ai.mcpdirect.studio.app.compose.StudioListItem
import ai.mcpdirect.studio.app.compose.TooltipText
import ai.mcpdirect.studio.app.generalViewModel
import ai.mcpdirect.studio.app.model.account.AIPortTeam
import ai.mcpdirect.studio.app.model.aitool.AIPortToolMaker
import ai.mcpdirect.studio.app.model.repository.UserRepository
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import mcpdirectstudioapp.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource


@Composable
fun ToolMakerListView(
    toolMakerListViewModel: ToolMakerListViewModel,
    toolListViewModel: ToolListViewModel,
    modifier: Modifier = Modifier
){
    LaunchedEffect(null){
        toolMakerListViewModel.refreshToolMakers()
        toolMakerListViewModel.refreshTeams()
    }
//    val viewModel = mcpToolsViewModel
    val toolMaker by toolMakerListViewModel.toolMaker.collectAsState()
    val toolMakers by toolMakerListViewModel.toolMakers.collectAsState()
    LazyColumn(modifier=modifier) {
        items(toolMakers){

            val me = UserRepository.me(it.userId)
//            var team by remember { mutableStateOf<AIPortTeam?>(null) }
//            LaunchedEffect(it){
//                if(it.teamId!=0L) toolMakerListViewModel.team(it.teamId){
//                    code, message, data ->
//                    team = data
//                }
//            }
            StudioListItem(
                selected = toolMaker?.id == it.id,
                modifier = Modifier.clickable{
                    toolMakerListViewModel.toolMaker(it)
                    toolListViewModel.toolMaker(it)
                },
                overlineContent = {
                    Row {
                        StudioIcon(
                            Res.drawable.person,
                            "Owner",
                            modifier = Modifier.size(18.dp).padding(end = 4.dp)
                        )
                        if(me) Text("Me")
//                        else team?.let {
//                            TooltipText(
//                                it.ownerName,
//                                contentDescription = it.ownerAccount,
//                                overflow = TextOverflow.Ellipsis
//                            )
//                        }
                    }
                },
                headlineContent = { Text(
                    it.name,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis) },
                supportingContent = {
                    if(me) Row{
                        StudioIcon(
                            Res.drawable.graph_5,
                            "From My Studio",
                            MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp).padding(end = 4.dp)
                        )
                        Text(
                            if(it.virtual())
                                "Virtual MCP"
                            else "",
//                            else it.agentName,
                            color = MaterialTheme.colorScheme.primary,
                            style=MaterialTheme.typography.bodySmall
                        )

                    }
//                    else team?.let {
//                        Row{
//                            StudioIcon(
//                                Res.drawable.groups,
//                                "From Team",
//                                MaterialTheme.colorScheme.secondary,
//                                modifier = Modifier.size(20.dp).padding(end = 4.dp)
//                            )
//                            Text(
//                                it.name,
//                                color = MaterialTheme.colorScheme.secondary,
//                                style=MaterialTheme.typography.bodySmall)
//                        }
//                    }

                },
                trailingContent = {
                    if(toolMaker!=null&&toolMaker!!.id==it.id)
                        Icon(painterResource(Res.drawable.keyboard_arrow_right),
                            contentDescription = "Current Tool Maker")
                }
//                ,
//                colors = ListItemDefaults.colors(
//                    containerColor = if(toolMaker==it)
//                        MaterialTheme.colorScheme.surfaceContainer
//                    else Color.Transparent
//                )
            )
            HorizontalDivider()
        }
    }
}