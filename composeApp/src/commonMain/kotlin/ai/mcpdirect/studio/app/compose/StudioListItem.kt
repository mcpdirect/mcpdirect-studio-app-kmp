package ai.mcpdirect.studio.app.compose

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

val selectedListItemColors: ListItemColors
    @Composable
    get() = ListItemDefaults.colors(
        containerColor = NavigationRailItemDefaults.colors().selectedIndicatorColor,
        leadingIconColor = NavigationRailItemDefaults.colors().selectedIconColor,
        headlineColor = NavigationRailItemDefaults.colors().selectedIconColor,
        supportingColor = NavigationRailItemDefaults.colors().selectedIconColor,
        trailingIconColor = NavigationRailItemDefaults.colors().selectedIconColor,
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
        colors = colors,
//        colors = if(selected) selectedListItemColors else colors,
        tonalElevation = if(selected) 8.dp else tonalElevation,
        shadowElevation = shadowElevation
    )
}