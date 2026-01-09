package ai.mcpdirect.studio.app.mcp.component

import ai.mcpdirect.studio.app.compose.StudioActionBar
import ai.mcpdirect.studio.app.compose.StudioBoard
import ai.mcpdirect.studio.app.compose.StudioListItem
import ai.mcpdirect.studio.app.model.aitool.AIPortToolMaker
import ai.mcpdirect.studio.app.model.repository.ToolRepository
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import mcpdirectstudioapp.composeapp.generated.resources.Res
import mcpdirectstudioapp.composeapp.generated.resources.close
import mcpdirectstudioapp.composeapp.generated.resources.inbox_empty
import mcpdirectstudioapp.composeapp.generated.resources.search
import mcpdirectstudioapp.composeapp.generated.resources.search_off
import org.jetbrains.compose.resources.painterResource

class MCPServersComponentViewModel : ViewModel() {
    val toolMakerFilter = MutableStateFlow("")
    val toolMakers: StateFlow<List<AIPortToolMaker>> = combine(
        ToolRepository.toolMakers,
        toolMakerFilter
    ) { makers, toolMakerFilter ->
        makers.values.filter { maker-> toolMakerFilter.isEmpty()||maker.name.lowercase().contains(toolMakerFilter.lowercase()) }.toList()
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    val selectedToolMakers = mutableStateMapOf<Long, AIPortToolMaker>()
    fun selectToolMaker(toolMaker: AIPortToolMaker,multiSelectable: Boolean = false) {
        if(!multiSelectable) selectedToolMakers.clear()
        selectedToolMakers[toolMaker.id] = toolMaker
    }
    fun unselectToolMaker(toolMaker: AIPortToolMaker) {
        selectedToolMakers.remove(toolMaker.id)
    }
    fun selectedToolMaker(maker: AIPortToolMaker): Boolean{
        return selectedToolMakers.containsKey(maker.id)
    }
}

@Composable
fun MCPServersComponent(
    multiSelectable: Boolean = false,
    viewModel: MCPServersComponentViewModel,
    modifier: Modifier = Modifier
){

    val toolMakers by viewModel.toolMakers.collectAsState()
    Column(modifier = modifier) {
        StudioActionBar(
            title = "MCP Servers",
        )
        HorizontalDivider()
        Box(modifier = Modifier.padding(16.dp)){
            Row(
                Modifier.background(
                    MaterialTheme.colorScheme.surfaceContainerHigh,
                    ButtonDefaults.shape
                ).clip(ButtonDefaults.shape),
                verticalAlignment = Alignment.CenterVertically,
            ){
                var value by remember { mutableStateOf("") }
                Icon(painterResource(Res.drawable.search), contentDescription = null,
                    Modifier.padding(12.dp))
                BasicTextField(
                    modifier=Modifier.weight(1f).padding(end = 4.dp),
                    value = value,
                    onValueChange = {
                        value = it
                        viewModel.toolMakerFilter.value = it
                    },
                )
                if(value.isNotEmpty()) IconButton(onClick = {
                    value=""
                    viewModel.toolMakerFilter.value = ""
                }){
                    Icon(painterResource(Res.drawable.close), contentDescription = null)
                }
            }
        }
        if(toolMakers.isEmpty()) StudioBoard(Modifier.weight(1f)) {
            Icon(
                painterResource(Res.drawable.search_off),
                contentDescription = null,
                modifier = Modifier.size(80.dp).padding(bottom = 16.dp)
            )
            Text("No MCP server found.")
        } else {
            LazyColumn {
                items(toolMakers){ toolMaker ->
                    StudioListItem(
                        selected = viewModel.selectedToolMaker(toolMaker),
                        modifier = Modifier.clickable {
                            viewModel.selectToolMaker(toolMaker,multiSelectable)
                        },
                        headlineContent = { Text(toolMaker.name, style = MaterialTheme.typography.bodyMedium) },
                        trailingContent = {
                            if(multiSelectable)Checkbox(
                                checked = viewModel.selectedToolMaker(toolMaker),
                                onCheckedChange = { checked->
                                    if(checked) viewModel.selectToolMaker(toolMaker,multiSelectable)
                                    else viewModel.unselectToolMaker(toolMaker)
                                },
                            )
                        }
                    )
                }
            }
        }
    }
}