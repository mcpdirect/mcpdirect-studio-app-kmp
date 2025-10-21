package ai.mcpdirect.studio.app.auth

import ai.mcpdirect.mcpdirectstudioapp.getPlatform
import ai.mcpdirect.studio.app.UIState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

import ai.mcpdirect.studio.app.model.account.AIPortUser
//import ai.mcpdirect.studio.app.setting.SettingsViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

//import java.util.Locale
sealed class PasswordChangeState {
    object Idle : PasswordChangeState()
    object Loading : PasswordChangeState()
    data class Success(val message: String) : PasswordChangeState()
    data class Error(val message: String) : PasswordChangeState()
}
val authViewModel = AuthViewModel()
class AuthViewModel() : ViewModel(){
    var user by mutableStateOf(AIPortUser(id=-1))
        private set
    var account by mutableStateOf("")
    var passwordChangeState by mutableStateOf<PasswordChangeState>(PasswordChangeState.Idle)
        private set

    fun changePassword(
        currentPassword: String,
        confirmPassword: String,
    ) {
//        passwordChangeState = PasswordChangeState.Loading
//        viewModelScope.launch {
//            withContext(Dispatchers.IO)
//
//        }
//            when (MCPDirectStudio.changePassword(currentPassword,confirmPassword)){
//                0 -> { passwordChangeState.value = PasswordChangeState.Success("Password changed successfully") }
//                AIPortAccount.PASSWORD_INCORRECT -> {passwordChangeState.value = PasswordChangeState.Error("Incorrect Current password")}
//                else -> {
//                    SettingsViewModel.PasswordChangeState.Error("Password change failed") }
//            }
//        }
    }

    fun resetPasswordChangeState() {
        passwordChangeState = PasswordChangeState.Idle
    }
    var currentScreen by mutableStateOf<AuthScreen>(AuthScreen.Login)
        private set
//
    var uiState by mutableStateOf<UIState>(UIState.Idle)
////        private set
//
//    var registrationEmail by mutableStateOf("")
//        private set
//
//    var forgotPasswordEmail by mutableStateOf("")
//        private set
//
    var isLoginEmailValid by mutableStateOf(true)
    var isLoginPasswordValid by mutableStateOf(true)
//    var isRegisterEmailValid by mutableStateOf(true)
//    var isRegisterPasswordValid by mutableStateOf(true)
//    var isForgotPasswordEmailValid by mutableStateOf(true)
//    var isSetNewPasswordValid by mutableStateOf(true)
//    var isConfirmNewPasswordValid by mutableStateOf(true)
//    var isRegisterSetPasswordValid by mutableStateOf(true)
//    var isRegisterConfirmPasswordValid by mutableStateOf(true)
//
    fun login(email: String, password: String) {
        isLoginEmailValid = email.isNotBlank()
        isLoginPasswordValid = password.isNotBlank()
        uiState = UIState.Loading
        getPlatform().login(email,password){
            if(it.successful()) {
                it.data?.let {
                    user = it
                }
                uiState = UIState.Success
            }else{
                uiState = UIState.Error(it.code)
            }
        }

//        if (!isLoginEmailValid || !isLoginPasswordValid) {
//            uiState = UiState.Error("Email and password cannot be empty.")
//            return
//        }
//        uiState = UiState.Loading
//        CoroutineScope(Dispatchers.Main).launch {
//            val result = MCPDirectStudio.login(email, password)
//            uiState = if (result) {
//                UiState.SuccessWithAccount(MCPDirectStudio.getUserInfo())
//            } else {
//                UiState.Error("Login Failed.")
//            }
//        }
    }
//    fun anonymousLogin(anonymousKey: String) {
//        uiState = UiState.Loading
//        CoroutineScope(Dispatchers.Main).launch {
//            val result = MCPDirectStudio.anonymousLogin(anonymousKey)
//            uiState = if (result) {
//                UiState.SuccessWithAnonymous(MCPDirectStudio.getUserInfo())
//            } else {
//                UiState.Error("Anonymous login failed.")
//            }
//        }
//    }
//
//    fun anonymousLogin() {
//        uiState = UiState.Loading
//        viewModelScope.launch {
//            withContext(Dispatchers.IO){
//                val anonymousKey = MCPDirectStudio.getAnonymousKey()
//                if(anonymousKey==null){
//                    uiState = UiState.Error("Anonymous key not found in local.")
//                }else {
//                    val result = MCPDirectStudio.anonymousLogin(anonymousKey)
//                    uiState = if (result) {
//                        UiState.SuccessWithAnonymous(MCPDirectStudio.getUserInfo())
//                    } else {
//                        UiState.Error("Anonymous login failed.")
//                    }
//                }
//            }
//        }
////        CoroutineScope(Dispatchers.Main).launch {
////            val anonymousKey = MCPDirectStudio.getAnonymousKey()
////            if(anonymousKey==null){
////                uiState = UiState.Error("Anonymous key not found in local.")
////            }else {
////                val result = MCPDirectStudio.anonymousLogin(anonymousKey)
////                uiState = if (result) {
////                    UiState.SuccessWithAnonymous(MCPDirectStudio.getUserInfo())
////                } else {
////                    UiState.Error("Anonymous login failed.")
////                }
////            }
////        }
//    }
//
//    fun anonymousRegister() {
//        uiState = UiState.Loading
//        CoroutineScope(Dispatchers.Main).launch {
//            val anonymousKey = MCPDirectStudio.anonymousRegister(null);
//            if(anonymousKey==null){
//                uiState = UiState.Error("Anonymous not found in local.")
//            }else {
//                val result = MCPDirectStudio.anonymousLogin(anonymousKey)
//                uiState = if (result) {
//                    UiState.SuccessWithAnonymous(MCPDirectStudio.getUserInfo())
//                } else {
//                    UiState.Error("Anonymous login failed.")
//                }
//            }
//        }
//    }
//
    fun sendOtpForRegistration(email: String) {
//        isRegisterEmailValid = email.isNotBlank()
//
//        if (!isRegisterEmailValid) {
//            uiState = UiState.Error("Email cannot be empty.")
//            return
//        }
//
//        uiState = UiState.Loading
//        CoroutineScope(Dispatchers.Main).launch {
////            val result = userRepository.sendOtpForRegistration(email)
//            val locale:Locale? = null
//            val result = MCPDirectStudio.register(email,locale)
//            uiState = if (result==0) {
//                registrationEmail = email
//                UiState.SuccessWithData(email)
//            } else {
//                val message = when(result){
//                    AIPortAccount.ACCOUNT_EXISTED -> "Account Exists"
//                    -1 -> "OTP Expired"
//                    else -> {"Register Failed"}
//                }
//                UiState.Error(message)
//            }
//        }
    }

    fun register(name:String,otp: String, password: String,confirmedPassword:String) {
//        if(password != confirmedPassword){
//            uiState = UiState.Error("Password not confirmed.")
//            return
//        }
//        if(name.isBlank()){
//            uiState = UiState.Error("Name cannot be empty.")
//            return
//        }
//        isRegisterPasswordValid = password.isNotBlank()
//        isRegisterSetPasswordValid = password.isNotBlank()
//        isRegisterConfirmPasswordValid = password.isNotBlank()
//
//        if (!isRegisterPasswordValid) {
//            uiState = UiState.Error("Password cannot be empty.")
//            return
//        }
//
//        uiState = UiState.Loading
//        CoroutineScope(Dispatchers.Main).launch {
////            val result = userRepository.register(email, password)
//            val result = MCPDirectStudio.register(registrationEmail,name,Locale.getDefault(),otp,password)
//            uiState = if (result) {
//                currentScreen = AuthScreen.Login
//                UiState.Idle
//            } else {
//                UiState.Error("Register Failed")
//            }
//        }
    }
//
////    fun verifyOtpForRegistration(email: String, otp: String,password:String) {
////        uiState = UiState.Loading
////        CoroutineScope(Dispatchers.Main).launch {
//////            val result = userRepository.verifyOtpForRegistration(email, otp)
////            val result = MCPwingsWorkshop.register(otp,password)
////            uiState = if (result) {
////                currentScreen = AuthScreen.RegisterSetPassword
////                UiState.Idle // Reset UI state after navigation
////            } else {
////                UiState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
////            }
////        }
////    }
//
    fun sendOtpForForgotPassword(email: String) {
//        isForgotPasswordEmailValid = email.isNotBlank()
//
//        if (!isForgotPasswordEmailValid) {
//            uiState = UiState.Error("Email cannot be empty.")
//            return
//        }
//
//        uiState = UiState.Loading
//        CoroutineScope(Dispatchers.Main).launch {
////            val result = userRepository.sendOtpForForgotPassword(email)
//            val result = MCPDirectStudio.forgotPassword(email,Locale.getDefault())
//            uiState = if (result==0) {
//                forgotPasswordEmail = email
//                UiState.SuccessWithData(email)
//            } else {
//                val message = when(result){
//                    AIPortAccount.ACCOUNT_NOT_EXIST -> "Account not Exists"
//                    else -> {"Send OTP Failed"}
//                }
//                UiState.Error(message)
//            }
//        }
    }
//
////    fun verifyOtpForForgotPassword(email: String, otp: String) {
////        uiState = UiState.Loading
////        CoroutineScope(Dispatchers.Main).launch {
////            val result = userRepository.verifyOtpForForgotPassword(email, otp)
////            uiState = if (result.isSuccess) {
////                currentScreen = AuthScreen.SetNewPassword
////                UiState.Idle // Reset UI state after navigation
////            } else {
////                UiState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
////            }
////        }
////    }
//
    fun setNewPassword(email: String, otp: String, newPassword: String) {
//        isSetNewPasswordValid = newPassword.isNotBlank()
//        isConfirmNewPasswordValid = newPassword.isNotBlank()
//
//        if (!isSetNewPasswordValid) {
//            uiState = UiState.Error("New password cannot be empty.")
//            return
//        }
//
//        uiState = UiState.Loading
//        CoroutineScope(Dispatchers.Main).launch {
//            MCPDirectStudio.forgotPassword(email,otp,newPassword)
//            val result = userRepository.setNewPassword(email, otp, newPassword)
//            uiState = if (result.isSuccess) {
//                currentScreen = AuthScreen.Login
//                UiState.Idle
//            } else {
//                UiState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
//            }
//        }
    }
//
//    fun loginWithGoogle() {
////        uiState = UiState.Loading
////        CoroutineScope(Dispatchers.Main).launch {
////            val result = userRepository.loginWithGoogle()
////            uiState = if (result.isSuccess) {
////                UiState.Success
////            } else {
////                UiState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
////            }
////        }
//    }
//
//    fun loginWithGitHub() {
////        uiState = UiState.Loading
////        CoroutineScope(Dispatchers.Main).launch {
////            val result = userRepository.loginWithGitHub()
////            uiState = if (result.isSuccess) {
////                UiState.Success
////            } else {
////                UiState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
////            }
////        }
//    }
//
//    fun loginWithApple() {
////        uiState = UiState.Loading
////        CoroutineScope(Dispatchers.Main).launch {
////            val result = userRepository.loginWithApple()
////            uiState = if (result.isSuccess) {
////                UiState.Success
////            } else {
////                UiState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
////            }
////        }
//    }
//
    fun navigateTo(screen: AuthScreen) {
        currentScreen = screen
        uiState = UIState.Idle // Reset UI state on navigation
    }
//
    fun logout(){
        viewModelScope.launch {
            getPlatform().logout {
                uiState = UIState.Idle
            }
        }
    }
}

sealed class AuthScreen {
    object AuthOption: AuthScreen()
    object Login : AuthScreen()
    object Register : AuthScreen()
    object RegisterOtpVerification : AuthScreen()
//    object RegisterSetPassword : AuthScreen()
    object ForgotPassword : AuthScreen()
    object ForgotPasswordOtpVerification : AuthScreen()
    object AnonymousLogin : AuthScreen()
}
