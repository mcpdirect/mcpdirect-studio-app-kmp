package ai.mcpdirect.studio.app.compose

import ai.mcpdirect.studio.app.Screen
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudioScaffold(
    title: @Composable (() -> Unit),
    navigationIcon: @Composable (() -> Unit) = {},
    actions: @Composable (RowScope.() -> Unit) = {},
    content: @Composable (() -> Unit)
){
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = navigationIcon,
                title = title,
                actions = actions
            )
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)){
            content()
        }
    }
}