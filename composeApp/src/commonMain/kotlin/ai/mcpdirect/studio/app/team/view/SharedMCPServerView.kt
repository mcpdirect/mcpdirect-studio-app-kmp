package ai.mcpdirect.studio.app.team.view

import ai.mcpdirect.studio.app.compose.StudioActionBar
import ai.mcpdirect.studio.app.model.aitool.AIPortTool
import ai.mcpdirect.studio.app.model.aitool.AIPortToolAgent
import ai.mcpdirect.studio.app.model.aitool.AIPortToolMaker
import ai.mcpdirect.studio.app.model.aitool.AIPortVirtualTool
import ai.mcpdirect.studio.app.model.repository.StudioRepository
import ai.mcpdirect.studio.app.model.repository.ToolRepository
import androidx.compose.foundation.background
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import mcpdirectstudioapp.composeapp.generated.resources.Res
import mcpdirectstudioapp.composeapp.generated.resources.collapse_all
import mcpdirectstudioapp.composeapp.generated.resources.description
import mcpdirectstudioapp.composeapp.generated.resources.expand_all
import mcpdirectstudioapp.composeapp.generated.resources.reset_settings
import org.jetbrains.compose.resources.painterResource

class SharedMCPServerViewModel : ViewModel() {
    var toolAgent by mutableStateOf<AIPortToolAgent?>(null)
    var expanded by mutableStateOf(false)
    val toolMaker = MutableStateFlow<AIPortToolMaker?>(null)
    val tools : StateFlow<List<AIPortTool>> = combine(
        ToolRepository.tools,
        toolMaker
    ){ tools,maker ->
        if(maker!=null) {
            tools.values.filter { it.makerId == maker.id }.toList()
        }
        else emptyList()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun toolMaker(maker: AIPortToolMaker){
        toolMaker.value = maker
        viewModelScope.launch {
            ToolRepository.loadTools(maker.userId, maker)
            StudioRepository.toolAgent(maker.agentId){
                if(it.successful()) it.data?.let { toolAgent = it }
            }
        }
    }
}
@Composable
fun SharedMCPServerView(
    toolMaker: AIPortToolMaker,
    expanded: Boolean = false,
    modifier: Modifier,
){
    val viewModel by remember { mutableStateOf(SharedMCPServerViewModel()) }
    LaunchedEffect(toolMaker){
        viewModel.toolMaker(toolMaker)
    }
    LaunchedEffect(expanded) {
        viewModel.expanded = expanded
    }
    val toolMaker by viewModel.toolMaker.collectAsState()
    val localToolAgent by StudioRepository.localToolAgent.collectAsState()
    val tools by viewModel.tools.collectAsState()
    OutlinedCard(modifier) {
        StudioActionBar("${toolMaker?.name} (${tools.size})") {
            IconButton(onClick = { viewModel.expanded = !viewModel.expanded },modifier = Modifier.size(32.dp)) {
                val icon = if(viewModel.expanded) Res.drawable.collapse_all else Res.drawable.expand_all
                Icon(painterResource(icon),contentDescription = null, Modifier.size(20.dp))
            }
        }
        if(viewModel.expanded)FlowRow(
            Modifier.padding(start = 16.dp,end = 16.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ){
            tools.forEach { tool ->
                val toolId = if(tool is AIPortVirtualTool) tool.toolId else tool.id
                val interactionSource = remember { MutableInteractionSource() }
                val isHovered by interactionSource.collectIsHoveredAsState()
                OutlinedButton(
                    modifier = Modifier.height(28.dp).hoverable(interactionSource),
                    onClick = {
                    },
                    contentPadding = PaddingValues(0.dp),
                ) {
                    Box(contentAlignment = Alignment.CenterEnd) {
                        Text(tool.name, Modifier.padding(horizontal = 8.dp))
                        if (isHovered) {
                            Box(Modifier.size(28.dp).background(
                                color = MaterialTheme.colorScheme.surfaceContainer,
                                shape = MaterialTheme.shapes.large
                            ), contentAlignment = Alignment.Center){
                                Icon(
                                    painterResource(Res.drawable.description),
                                    contentDescription = null,
                                    Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }

            }
        }
        HorizontalDivider()
        Row(Modifier.padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically) {
            viewModel.toolAgent?.let {
                Text(it.name)
                if (it.id == localToolAgent.id) Badge(Modifier.padding(start = 8.dp)) { Text("This device") }
            }
        }
    }
}