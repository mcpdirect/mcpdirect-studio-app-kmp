package ai.mcpdirect.studio.app.dashboard.shortcut

import ai.mcpdirect.studio.app.dashboard.DashboardViewModel
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

interface Shortcut{
    val title:String
    @Composable
    fun wizard(modifier: Modifier)
}