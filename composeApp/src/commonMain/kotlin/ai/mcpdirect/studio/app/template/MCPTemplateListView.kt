package ai.mcpdirect.studio.app.template

import ai.mcpdirect.studio.app.auth.authViewModel
import ai.mcpdirect.studio.app.compose.StudioIcon
import ai.mcpdirect.studio.app.compose.StudioListItem
import ai.mcpdirect.studio.app.compose.TooltipText
import ai.mcpdirect.studio.app.generalViewModel
import ai.mcpdirect.studio.app.model.account.AIPortUser
import ai.mcpdirect.studio.app.model.aitool.AIPortToolAgent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import mcpdirectstudioapp.composeapp.generated.resources.Res
import mcpdirectstudioapp.composeapp.generated.resources.graph_5
import mcpdirectstudioapp.composeapp.generated.resources.keyboard_arrow_right
import mcpdirectstudioapp.composeapp.generated.resources.person
import org.jetbrains.compose.resources.painterResource

@Composable
fun MCPTemplateListView(){
    val viewModel = mcpTemplateListViewModel
    LaunchedEffect(viewModel){
        viewModel.queryToolMakerTemplates()
    }
    LazyColumn {
        items(viewModel.toolMakerTemplates){ template ->
            val me = template.userId== authViewModel.user.id
            var toolAgent by remember { mutableStateOf<AIPortToolAgent?>(null) }
            var user by remember { mutableStateOf<AIPortUser?>(null)}
            LaunchedEffect(null){
                generalViewModel.toolAgent(template.agentId){
                        code, message, data ->
                    toolAgent = data
                }
                generalViewModel.user(template.userId){
                        code, message, data ->
                    user = data;
                }
            }
            toolAgent?.let { agent ->
                StudioListItem(
                    selected = viewModel.toolMakerTemplate?.id == template.id,
                    modifier = Modifier.clickable{
                        viewModel.toolMakerTemplate(template)
                    },
                    overlineContent = {
                        Row {
                            StudioIcon(
                                Res.drawable.person,
                                "Owner",
                                modifier = Modifier.size(18.dp).padding(end = 4.dp)
                            )
                            if(me) Text("Me")
                            else user?.let {
                                TooltipText(
                                    it.name,
                                    contentDescription = it.account,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    },
                    headlineContent = { Text(
                        template.name,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis) },
                    supportingContent = {
                        Row{
                            StudioIcon(
                                Res.drawable.graph_5,
                                "From My Studio",
                                MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp).padding(end = 4.dp)
                            )
                            Text(
                                agent.name,
                                color = MaterialTheme.colorScheme.primary,
                                style=MaterialTheme.typography.bodySmall
                            )

                        }

                    },
                    trailingContent = {
                        if(viewModel.toolMakerTemplate!=null&&viewModel.toolMakerTemplate!!.id==template.id)
                            Icon(painterResource(Res.drawable.keyboard_arrow_right),
                                contentDescription = "")
                    },
                    colors = ListItemDefaults.colors(
                        containerColor = if(viewModel.toolMakerTemplate==template)
                            MaterialTheme.colorScheme.surfaceContainer
                        else Color.Transparent
                    )
                )
                HorizontalDivider()
            }
        }
    }
}