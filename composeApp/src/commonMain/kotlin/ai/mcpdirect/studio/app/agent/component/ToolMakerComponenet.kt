package ai.mcpdirect.studio.app.agent.component

import ai.mcpdirect.mcpdirectstudioapp.JSON
import ai.mcpdirect.studio.app.compose.JsonTreeView
import ai.mcpdirect.studio.app.compose.StudioActionBar
import ai.mcpdirect.studio.app.compose.StudioBoard
import ai.mcpdirect.studio.app.compose.TooltipIconButton
import ai.mcpdirect.studio.app.model.aitool.AIPortTool
import ai.mcpdirect.studio.app.model.aitool.AIPortToolMaker
import ai.mcpdirect.studio.app.model.aitool.AIPortToolMaker.Companion.STATUS_WAITING
import ai.mcpdirect.studio.app.model.repository.ToolRepository
import ai.mcpdirect.studio.app.tips.ConnectMCPViewAction
import ai.mcpdirect.studio.app.tips.QuickStartViewModel
import ai.mcpdirect.studio.app.tool.ToolDetails
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import mcpdirectstudioapp.composeapp.generated.resources.Res
import mcpdirectstudioapp.composeapp.generated.resources.close
import mcpdirectstudioapp.composeapp.generated.resources.delete
import mcpdirectstudioapp.composeapp.generated.resources.restart_alt
import mcpdirectstudioapp.composeapp.generated.resources.setting_config
import org.jetbrains.compose.resources.painterResource

@Composable
fun ToolMakerComponent(
    toolMaker: AIPortToolMaker,
    viewModel: ToolAgentComponentViewModel,
    modifier: Modifier = Modifier,
    onActionChange: (action: ConnectMCPViewAction)->Unit
){
    val tools = remember(toolMaker.id, viewModel.tools) {
        derivedStateOf {
            viewModel.tools.filter { it.makerId == toolMaker.id }
        }
    }.value
    if(toolMaker.status == STATUS_WAITING){
        StudioBoard(modifier) {
            CircularProgressIndicator()
            Text("${toolMaker.name} starting")
        }
    }else Column(modifier) {
        Row(
            Modifier.padding(start = 16.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(toolMaker.name, style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.weight(1f))
            TooltipIconButton("Remove", onClick = {
                viewModel.removeToolMaker(toolMaker)
            }){
                Icon(
                    painterResource(Res.drawable.delete), contentDescription = "",
                    Modifier.size(24.dp), tint = MaterialTheme.colorScheme.error
                )
            }
            TooltipIconButton("Restart",onClick = {
                viewModel.modifyToolMakerStatus(
                    toolMaker, 1
                )
            }) {
                Icon(
                    painterResource(Res.drawable.restart_alt), contentDescription = "",
                    Modifier.size(24.dp), tint = MaterialTheme.colorScheme.primary
                )
            }
            TooltipIconButton("Configure",onClick = {
                if (toolMaker.templateId > 0) {
                    if (toolMaker.mcp()) onActionChange(ConnectMCPViewAction.CONFIG_MCP_TEMPLATE)
                } else if (toolMaker.mcp()) onActionChange(ConnectMCPViewAction.CONFIG_MCP)
                else if (toolMaker.openapi()) onActionChange(ConnectMCPViewAction.CONFIG_OPENAPI)
            }) {
                Icon(
                    painterResource(Res.drawable.setting_config), contentDescription = "",
                    Modifier.size(24.dp)
                )
            }
        }
//        HorizontalDivider()
        if(toolMaker.errorCode!=0){
            Text(toolMaker.errorMessage,Modifier.padding(horizontal = 8.dp) , color = MaterialTheme.colorScheme.error)
        } else {
            var currentTool by remember{ mutableStateOf<AIPortTool?>(null) }
            var toolDetails by remember { mutableStateOf(ToolDetails("","{}")) }
            val scrollState = rememberScrollState()
            LaunchedEffect(toolMaker){
                currentTool = null
            }
            LaunchedEffect(currentTool){
                currentTool?.let { tool ->
                    ToolRepository.tool(tool.id) {
                        if (it.successful()) it.data?.let { data->
                            val json = JSON.parseToJsonElement(data.metaData)
                            toolDetails = ToolDetails(
                                json.jsonObject["description"]?.jsonPrimitive?.content ?: "",
                                json.jsonObject["requestSchema"]?.jsonPrimitive?.content ?: "{}"
                            )
                        }
                    }
                }
            }
            currentTool?.let { tool ->
                var tabIndex by remember { mutableStateOf(0) }
                OutlinedCard(modifier = Modifier.fillMaxSize().padding(start=16.dp,end=16.dp, bottom = 16.dp)) {
                    StudioActionBar(tool.name) {
                        SecondaryTabRow(
                            tabIndex,
                            Modifier.width(300.dp),
//                            containerColor = CardDefaults.cardColors().containerColor,
//                            contentColor = CardDefaults.cardColors().contentColor,
                        ){
                            Tab(tabIndex==0, onClick = {tabIndex = 0}, text = {Text("Description")})
                            Tab(tabIndex==1, onClick = {tabIndex = 1}, text = {Text("Input Schema")})
                        }
                        IconButton(onClick = {currentTool=null}){
                            Icon(painterResource(Res.drawable.close), contentDescription = "")
                        }
                    }
                    HorizontalDivider()
                    when(tabIndex){
                        0 -> Text(toolDetails.description, Modifier.padding(16.dp))
                        1 -> JsonTreeView(toolDetails.inputSchema, Modifier.padding(16.dp))
                    }
//                    Text(toolDetails.description, Modifier.padding(16.dp))
                }
            }?: Box(modifier = Modifier.fillMaxSize()) {
                Column (
                    modifier = Modifier
                        .verticalScroll(scrollState)
                        .padding(start = 16.dp,end=16.dp, bottom = 16.dp) // Add padding to prevent content from going under the scrollbar
                )  {
                    FlowRow(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ){
                        var index = 1

                        tools.forEach { tool ->
                            if (tool.makerId == toolMaker.id && tool.status>-1) {
                                TextButton(
                                    shape = OutlinedTextFieldDefaults.shape,
                                    border = BorderStroke(1.dp, ButtonDefaults.textButtonColors().contentColor),
                                    onClick = { currentTool = tool }
                                ) {
                                    Text("${index++}. ${tool.name}")
                                }
                            }

                        }
                    }
                }
                VerticalScrollbar(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    adapter = rememberScrollbarAdapter(scrollState = scrollState)
                )
            }
        }
    }
}