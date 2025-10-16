package ai.mcpdirect.studio.app.setting
import ai.mcpdirect.studio.MCPDirectStudio
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ai.mcpdirect.backend.dao.entity.account.AIPortAccount
import ai.mcpdirect.backend.dao.entity.account.AIPortUser
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

// ViewModel.kt
class SettingsViewModel : ViewModel() {
    val snackbarHostState = SnackbarHostState()
    fun showSnackbar(message: String) {
        CoroutineScope(Dispatchers.Main).launch {
            snackbarHostState.showSnackbar(message)
        }
    }
    val state = mutableStateOf(SettingsState())
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
        state.value = state.value.copy(deviceName = newName)
        viewModelScope.launch {
//            SettingsRepository.saveDeviceName(newName)
        }
    }

    fun updateNewTagInput(input: String) {
        state.value = state.value.copy(newTagInput = input)
    }

    fun addTag() {
        val newTag = state.value.newTagInput.trim()
        if (newTag.isNotEmpty() && !state.value.tags.contains(newTag)) {
            val updatedTags = state.value.tags + newTag
            state.value = state.value.copy(
                tags = updatedTags,
                newTagInput = ""
            )
            viewModelScope.launch {
//                SettingsRepository.saveTags(updatedTags)
            }
        }
    }

    fun removeTag(tag: String) {
        val updatedTags = state.value.tags - tag
        state.value = state.value.copy(tags = updatedTags)
        viewModelScope.launch {
//            SettingsRepository.saveTags(updatedTags)
        }
    }
    fun logout(){
            viewModelScope.launch {
                MCPDirectStudio.logout();
//            AuthRepository.logout()
            // Navigate to login screen (handle in UI layer)
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
    val passwordChangeState = mutableStateOf<PasswordChangeState>(PasswordChangeState.Idle)
//    val passwordChangeState: State<PasswordChangeState> = _passwordChangeState

    sealed class PasswordChangeState {
        object Idle : PasswordChangeState()
        object Loading : PasswordChangeState()
        data class Success(val message: String) : PasswordChangeState()
        data class Error(val message: String) : PasswordChangeState()
    }

    fun changePassword(
        currentPassword: String,
        confirmPassword: String,
    ) {
        viewModelScope.launch {
            passwordChangeState.value = PasswordChangeState.Loading
            when (MCPDirectStudio.changePassword(currentPassword,confirmPassword)){
                0 -> { passwordChangeState.value = PasswordChangeState.Success("Password changed successfully") }
                AIPortAccount.PASSWORD_INCORRECT -> {passwordChangeState.value = PasswordChangeState.Error("Incorrect Current password")}
                else -> {PasswordChangeState.Error("Password change failed") }
            }
        }
    }

    fun resetPasswordChangeState() {
        passwordChangeState.value = PasswordChangeState.Idle
    }
    fun transferAnonymous(key:String){
        viewModelScope.launch {
            MCPDirectStudio.transferAnonymous(key,null)
        }
    }
    fun getAnonymousKey():String{
        return MCPDirectStudio.getAnonymousKey()
    }
    fun saveDeviceName(name:String){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                MCPDirectStudio.modifyToolAgent(
                    MCPDirectStudio.getLocalToolAgentDetails().toolAgent.id,
                    name,null,null
                ){
                    code, message, data ->
                }
            }
        }
    }
}