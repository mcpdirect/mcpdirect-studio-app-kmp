package ai.mcpdirect.studio.app.auth

import ai.mcpdirect.mcpdirectstudioapp.getPlatform
import ai.mcpdirect.studio.app.UIState
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mcpdirectstudioapp.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource

@Composable
fun RegisterScreen() {
    var email by remember { mutableStateOf("") }
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
            isError = !authViewModel.isRegisterEmailValid
        )
        if (!authViewModel.isRegisterEmailValid) {
            Text("Email cannot be empty", color = MaterialTheme.colorScheme.error)
        }
        Spacer(modifier = Modifier.height(16.dp))
        if(authViewModel.uiState is UIState.Loading){
            CircularProgressIndicator()
        } else Button(onClick = { authViewModel.sendOtpForRegistration(email) }) {
            Text("Send OTP")
        }
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = { authViewModel.navigateTo(AuthScreen.Login) }) {
            Text("Back to Login")
        }

        when (val state = authViewModel.uiState) {

            is UIState.Success -> {
                LaunchedEffect(state) {
                    authViewModel.navigateTo(AuthScreen.RegisterOtpVerification)
                }
            }

            is UIState.Error -> {
                state.message?.let {
                    Text(it, color = MaterialTheme.colorScheme.error)
                }
            }

            else -> {}
        }
    }
}
