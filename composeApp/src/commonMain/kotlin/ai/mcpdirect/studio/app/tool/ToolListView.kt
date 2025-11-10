package ai.mcpdirect.studio.app.tool

import ai.mcpdirect.studio.app.compose.StudioIcon
import ai.mcpdirect.studio.app.model.aitool.AIPortTool
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import mcpdirectstudioapp.composeapp.generated.resources.Res
import mcpdirectstudioapp.composeapp.generated.resources.add
import mcpdirectstudioapp.composeapp.generated.resources.check_indeterminate_small
import mcpdirectstudioapp.composeapp.generated.resources.info
import mcpdirectstudioapp.composeapp.generated.resources.sync
import org.jetbrains.compose.resources.painterResource

@Composable
fun ToolListView(
    tools:List<AIPortTool>,
    actionBar: (@Composable (() -> Unit))?=null,
    modifier: Modifier
){
    Column(modifier) {
        actionBar?.let {
            it
            HorizontalDivider()
        }
        LazyColumn(modifier) {
            items(tools){
                ListItem(
                    modifier = Modifier.clickable{

                    },
                    leadingContent = {
                        if(it.lastUpdated==-1L)StudioIcon(
                            Res.drawable.check_indeterminate_small,
                            "Abandoned"
                        ) else if(it.lastUpdated==1L)StudioIcon(
                            Res.drawable.add,
                            "New tool"
                        ) else if(it.lastUpdated>1)StudioIcon(
                            Res.drawable.sync,
                            "Tool updated"
                        )
                    },
                    headlineContent = {Text(it.name)},
                    trailingContent = {Icon(painterResource(Res.drawable.info),
                        contentDescription = "Details")}
                )
            }
        }
    }
}