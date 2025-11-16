package ai.mcpdirect.studio.app.mcp.openapi

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class OpenAPIToolMakerViewModel: ViewModel() {
    var name by mutableStateOf("")
        private set
    fun onValueChange(value:String){

    }
}