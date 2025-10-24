package ai.mcpdirect.studio.app.auth

import ai.mcpdirect.mcpdirectstudioapp.getPlatform
import ai.mcpdirect.studio.app.UIState
import ai.mcpdirect.studio.app.model.AIPortServiceResponse
import ai.mcpdirect.studio.app.model.account.AIPortOtp
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
    var currentScreen by mutableStateOf<AuthScreen>(AuthScreen.Login)
        private set
//
    var uiState by mutableStateOf<UIState>(UIState.Idle)
////        private set
//
    var registrationEmail by mutableStateOf("")
        private set

    var forgotPasswordEmail by mutableStateOf("")
        private set

    var isLoginEmailValid by mutableStateOf(true)
    var isLoginPasswordValid by mutableStateOf(true)
    var isRegisterEmailValid by mutableStateOf(true)
    var isRegisterPasswordValid by mutableStateOf(true)
    var isForgotPasswordEmailValid by mutableStateOf(true)
    var isSetNewPasswordValid by mutableStateOf(true)
    var isConfirmNewPasswordValid by mutableStateOf(true)
    var isRegisterSetPasswordValid by mutableStateOf(true)
    var isRegisterConfirmPasswordValid by mutableStateOf(true)
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
                account = email
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
    private var otp = AIPortOtp()
    fun sendOtpForRegistration(email: String) {
        isRegisterEmailValid = email.isNotBlank()

        if (!isRegisterEmailValid) {
            uiState = UIState.Error(AIPortServiceResponse.ACCOUNT_INCORRECT)
            return
        }

        uiState = UIState.Loading
        viewModelScope.launch {
            getPlatform().register(email){
                if(it.code==0){
                    it.data?.let {
                        otp = it
                        uiState = UIState.Success
                    }
                } else uiState = UIState.Error(it.code)
            }
        }
    }

    fun register(name:String,otp: String, password: String,confirmedPassword:String) {
        if(password != confirmedPassword){
            uiState = UIState.Error(message = "Password not confirmed.")
            return
        }
        if(name.isBlank()){
            uiState = UIState.Error(message = "Name cannot be empty.")
            return
        }
        isRegisterPasswordValid = password.isNotBlank()
        isRegisterSetPasswordValid = password.isNotBlank()
        isRegisterConfirmPasswordValid = password.isNotBlank()

        if (!isRegisterPasswordValid) {
            uiState = UIState.Error(message = "Password cannot be empty.")
            return
        }

        uiState = UIState.Loading
        viewModelScope.launch {
            getPlatform().register(registrationEmail,name,this@AuthViewModel.otp.id,otp){
                if(it.code==0){
                    getPlatform().forgotPassword(registrationEmail,
                        this@AuthViewModel.otp.id,otp,password){
                        if(it.code==0){
                            currentScreen = AuthScreen.Login
                            uiState = UIState.Success
                        }
                    }
                }else uiState = UIState.Error(it.code)
            }
        }
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
        isForgotPasswordEmailValid = email.isNotBlank()

        if (!isForgotPasswordEmailValid) {
            uiState = UIState.Error(message = "Email cannot be empty.")
            return
        }

        uiState = UIState.Loading
        viewModelScope.launch {
            getPlatform().forgotPassword(email){
                if(it.code==0){
                    it.data?.let { otp = it }
                    uiState = UIState.Success
                } else uiState = UIState.Error(it.code)
            }
        }
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
        isSetNewPasswordValid = newPassword.isNotBlank()
        isConfirmNewPasswordValid = newPassword.isNotBlank()

        if (!isSetNewPasswordValid) {
            uiState = UIState.Error(message ="New password cannot be empty.")
            return
        }

        uiState = UIState.Loading
        viewModelScope.launch {
            getPlatform().forgotPassword(email,
                this@AuthViewModel.otp.id,otp,newPassword){
                if(it.code==0){
                    it.data?.let {
                        currentScreen = AuthScreen.Login
                        uiState = UIState.Success
                    }
                }else uiState = UIState.Error(it.code)
            }
        }
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
