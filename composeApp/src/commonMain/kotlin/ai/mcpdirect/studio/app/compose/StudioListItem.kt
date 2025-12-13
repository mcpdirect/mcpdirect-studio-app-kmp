package ai.mcpdirect.studio.app.compose

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

val selectedListItemColors: ListItemColors
    @Composable
    get() = ListItemDefaults.colors(
        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
        leadingIconColor = MaterialTheme.colorScheme.onTertiaryContainer,
        headlineColor = MaterialTheme.colorScheme.onTertiaryContainer,
        supportingColor = MaterialTheme.colorScheme.onTertiaryContainer,
        trailingIconColor = MaterialTheme.colorScheme.onTertiaryContainer,
    )

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StudioListItem(
    selected:Boolean=false,
    headlineContent: @Composable (() -> Unit),
    modifier: Modifier = Modifier,
    overlineContent: @Composable (() -> Unit)? = null,
    supportingContent: @Composable (() -> Unit)? = null,
    leadingContent: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    colors: ListItemColors = ListItemDefaults.colors(),
    tonalElevation: Dp = ListItemDefaults.Elevation,
    shadowElevation: Dp = ListItemDefaults.Elevation
){
    ListItem(
        headlineContent = headlineContent,
        modifier = modifier,
        overlineContent = overlineContent,
        supportingContent = supportingContent,
        leadingContent = leadingContent,
        trailingContent = trailingContent,
//        colors = colors,
        colors = if(selected) selectedListItemColors else colors,
        tonalElevation = tonalElevation,
        shadowElevation = shadowElevation
    )
}