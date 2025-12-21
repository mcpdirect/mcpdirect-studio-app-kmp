package ai.mcpdirect.studio.app

import ai.mcpdirect.studio.app.model.AIPortServiceResponse
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

val generalViewModel = GeneralViewModel()
class GeneralViewModel() : ViewModel() {
    var loadingProcess by mutableStateOf<Float?>(1.0f)
    fun loading(process: Float?=null){
        loadingProcess = process
    }

    fun loaded(title:String,code: Int,message:String?){
        if(code== AIPortServiceResponse.SERVICE_SUCCESSFUL) loadingProcess = 1.0f else {
            loadingProcess = -1.0f
            val error = if(message==null) "$title Error" else "$title\n$message"
            showSnackbar(error,"Error",true)
        }
    }

    var darkMode by mutableStateOf(false)
    var lastRefreshed = 0;
    var currentScreen by mutableStateOf<Screen>(Screen.Dashboard)
        private set
    fun currentScreen(currentScreen:Screen,currentScreenTitle:String?=null,previousScreen: Screen?=null){
        this.currentScreen = currentScreen
        this.currentScreenTitle = currentScreenTitle
        this.previousScreen = previousScreen
        this.topBarActions = {}
    }
    var currentScreenTitle by mutableStateOf<String?>(null)
    var previousScreen by mutableStateOf<Screen?>(null)
        private set
    fun previousScreen(){
        if(previousScreen!=null){
            currentScreenTitle = null
            currentScreen = previousScreen!!
            previousScreen = null
        }
    }
    var topBarActions by mutableStateOf<@Composable (() -> Unit)>({})
    val snackbarHostState = SnackbarHostState()

    fun showSnackbar(message: String,actionLabel: String? = null,
                     withDismissAction: Boolean = false,) {
        viewModelScope.launch {
            snackbarHostState.showSnackbar(message,actionLabel,withDismissAction)
        }
    }
}