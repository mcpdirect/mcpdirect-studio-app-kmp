package ai.mcpdirect.studio.app.auth

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
fun ForgotPasswordScreen(authViewModel: AuthViewModel) {
    var email by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(Res.drawable.mcpdirect_studio_256),
            contentDescription = "MCPdirect Studio",
            modifier = Modifier.width(256.dp)
        )
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            singleLine = true,
            isError = !authViewModel.isForgotPasswordEmailValid
        )
        if (!authViewModel.isForgotPasswordEmailValid) {
            Text("Email cannot be empty", color = MaterialTheme.colorScheme.error)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { authViewModel.sendOtpForForgotPassword(email) }) {
            Text("Send OTP")
        }
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = { authViewModel.navigateTo(AuthScreen.Login) }) {
            Text("Back to Login")
        }

        when (val state = authViewModel.uiState) {
            is UiState.Loading -> {
                CircularProgressIndicator()
            }
            is UiState.SuccessWithData -> {
                LaunchedEffect(state) {
                    authViewModel.navigateTo(AuthScreen.ForgotPasswordOtpVerification)
                }
            }
            is UiState.Error -> {
                Text(state.message, color = MaterialTheme.colorScheme.error)
            }
            else -> {}
        }
    }
}
