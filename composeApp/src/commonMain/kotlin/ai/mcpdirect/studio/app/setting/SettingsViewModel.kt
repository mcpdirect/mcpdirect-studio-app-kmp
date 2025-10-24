package ai.mcpdirect.studio.app.setting
import ai.mcpdirect.mcpdirectstudioapp.getPlatform
import ai.mcpdirect.studio.app.auth.PasswordChangeState
import ai.mcpdirect.studio.app.model.AIPortServiceResponse
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Model.kt
data class SettingsState(
    val deviceName: String = "",
    val tags: List<String> = emptyList(),
    val newTagInput: String = ""
)
//sealed class PasswordChangeState {
//    object Idle : PasswordChangeState()
//    object Loading : PasswordChangeState()
//    data class Success(val message: String) : PasswordChangeState()
//    data class Error(val message: String) : PasswordChangeState()
//}
val settingsViewModel = SettingsViewModel()
class SettingsViewModel : ViewModel() {
    var state by mutableStateOf(SettingsState())
//    val state: State<SettingsState> = _state

    init {
        loadSettings()
    }

    private fun loadSettings() {
        // Load from preferences/database
        viewModelScope.launch {
//            val savedName = SettingsRepository.getDeviceName()
//            val savedTags = SettingsRepository.getTags()
//            _state.value = _state.value.copy(
//                deviceName = savedName,
//                tags = savedTags
//            )
        }
    }

    fun updateDeviceName(newName: String) {
        state = state.copy(deviceName = newName)
        viewModelScope.launch {
//            SettingsRepository.saveDeviceName(newName)
        }
    }

    fun updateNewTagInput(input: String) {
        state = state.copy(newTagInput = input)
    }

    fun addTag() {
        val newTag = state.newTagInput.trim()
        if (newTag.isNotEmpty() && !state.tags.contains(newTag)) {
            val updatedTags = state.tags + newTag
            state = state.copy(
                tags = updatedTags,
                newTagInput = ""
            )
            viewModelScope.launch {
//                SettingsRepository.saveTags(updatedTags)
            }
        }
    }

    fun removeTag(tag: String) {
        val updatedTags = state.tags - tag
        state = state.copy(tags = updatedTags)
        viewModelScope.launch {
//            SettingsRepository.saveTags(updatedTags)
        }
    }

    fun resetPassword() {
        viewModelScope.launch {
            try {
//                AuthRepository.sendPasswordResetEmail()
                // Show success message (handle in UI layer)
            } catch (e: Exception) {
                // Show error message (handle in UI layer)
            }
        }
    }

        // Password change state
    var passwordChangeState by mutableStateOf<PasswordChangeState>(PasswordChangeState.Idle)
//    val passwordChangeState: State<PasswordChangeState> = _passwordChangeState

    fun changePassword(
        currentPassword: String,
        confirmPassword: String,
    ) {
        viewModelScope.launch {
            passwordChangeState = PasswordChangeState.Loading
            getPlatform().changePassword(currentPassword,confirmPassword){
                when (it.code){
                    0 -> { passwordChangeState = PasswordChangeState.Success("Password changed successfully") }
                    AIPortServiceResponse.PASSWORD_INCORRECT -> {passwordChangeState = PasswordChangeState.Error("Incorrect Current password")}
                    else -> {PasswordChangeState.Error("Password change failed") }
                }
            }

        }
    }

    fun resetPasswordChangeState() {
        passwordChangeState = PasswordChangeState.Idle
    }
//    fun transferAnonymous(key:String){
//        viewModelScope.launch {
//            MCPDirectStudio.transferAnonymous(key,null)
//        }
//    }
//    fun getAnonymousKey():String{
//        return MCPDirectStudio.getAnonymousKey()
//    }
    fun saveDeviceName(name:String){
        viewModelScope.launch {
            getPlatform().modifyToolAgent(getPlatform().toolAgentId, name){
            }
        }
    }
}