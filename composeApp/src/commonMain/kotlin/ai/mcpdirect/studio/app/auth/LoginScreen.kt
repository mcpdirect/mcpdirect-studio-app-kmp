package ai.mcpdirect.studio.app.auth

import ai.mcpdirect.mcpdirectstudioapp.getPlatform
import ai.mcpdirect.studio.app.UIState
import ai.mcpdirect.studio.app.model.AIPortServiceResponse
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import mcpdirectstudioapp.composeapp.generated.resources.Res
import mcpdirectstudioapp.composeapp.generated.resources.mcpdirect_platform_logo
import mcpdirectstudioapp.composeapp.generated.resources.mcpdirect_studio_logo
import mcpdirectstudioapp.composeapp.generated.resources.mcpdirect_text_logo
import mcpdirectstudioapp.composeapp.generated.resources.visibility
import mcpdirectstudioapp.composeapp.generated.resources.visibility_off
import org.jetbrains.compose.resources.painterResource

@Composable
fun LoginScreen() {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val focusManager = LocalFocusManager.current
    val (emailFocusRequester, passwordFocusRequester,loginFocusRequester) = FocusRequester.createRefs()
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(Res.drawable.mcpdirect_text_logo),
            contentDescription = "MCPdirect Studio",
            modifier = Modifier.width(256.dp)
        )
        if(getPlatform().type==0) Image(
            painter = painterResource(Res.drawable.mcpdirect_platform_logo),
            contentDescription = "MCPdirect Studio",
            modifier = Modifier.width(256.dp)
        ) else Image(
            painter = painterResource(Res.drawable.mcpdirect_studio_logo),
            contentDescription = "MCPdirect Studio",
            modifier = Modifier.width(256.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            modifier = Modifier.width(256.dp),
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            singleLine = true,
            isError = !authViewModel.isLoginEmailValid,
            supportingText = {
                if (!authViewModel.isLoginEmailValid) {
                    Text("Email cannot be empty", color = MaterialTheme.colorScheme.error)
                }
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        var passwordVisibility by remember { mutableStateOf(false) }

        OutlinedTextField(
            modifier = Modifier.width(256.dp),
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            singleLine = true,
            isError = !authViewModel.isLoginPasswordValid,
            visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                    Icon(
                        painter = painterResource(if (!passwordVisibility) Res.drawable.visibility else Res.drawable.visibility_off),
                        contentDescription = if (!passwordVisibility) "Hide password" else "Show password"
                    )
                }
            },
            supportingText = {
                if (!authViewModel.isLoginPasswordValid) {
                    Text("Password cannot be empty", color = MaterialTheme.colorScheme.error)
                }
            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        if(authViewModel.uiState== UIState.Loading)
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = MaterialTheme.colorScheme.primary
            )
        else Button(onClick = { authViewModel.login(email, password) }) {
            Text("Login")
        }

        Spacer(modifier = Modifier.height(16.dp))
        Row {
            TextButton(onClick = { authViewModel.navigateTo(AuthScreen.Register) }) {
                Text("Register")
            }
            Spacer(modifier = Modifier.width(16.dp))
            TextButton(onClick = { authViewModel.navigateTo(AuthScreen.ForgotPassword) }) {
                Text("Forgot Password?")
            }
        }
//        Spacer(modifier = Modifier.height(16.dp))
//        TextButton(onClick = { authViewModel.navigateTo(AuthScreen.AuthOption) }) {
//            Text("Back")
//        }
//        Spacer(modifier = Modifier.height(32.dp))
//        Row {
//            Button(onClick = { authViewModel.loginWithGoogle() }) {
//                Text("Google")
//            }
//            Spacer(modifier = Modifier.width(16.dp))
//            Button(onClick = { authViewModel.loginWithGitHub() }) {
//                Text("GitHub")
//            }
//            Spacer(modifier = Modifier.width(16.dp))
//            Button(onClick = { authViewModel.loginWithApple() }) {
//                Text("Apple")
//            }
//        }

        when (val state = authViewModel.uiState) {
            is UIState.Loading -> {}

            is UIState.Error -> {
                Text (
                    when (state.code) {
                        AIPortServiceResponse.ACCOUNT_NOT_EXIST -> "account not exist"
                        else -> "sign in failed"
                    },
                    color = MaterialTheme.colorScheme.error
                )
            }

            else -> {}
        }
    }
}
