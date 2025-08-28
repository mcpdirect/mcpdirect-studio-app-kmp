package ai.mcpdirect.studio.app.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import mcpdirectstudioapp.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource

@Composable
fun ForgotPasswordOtpVerificationScreen(authViewModel: AuthViewModel) {
    var otp by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmNewPassword by remember { mutableStateOf("") }

    val focusManager = LocalFocusManager.current
    val (otpFocusRequester, newPasswordFocusRequester, confirmNewPasswordFocusRequester) = FocusRequester.createRefs()

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
        Text("Set new password for ${authViewModel.forgotPasswordEmail}")
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = otp,
            onValueChange = { otp = it },
            label = { Text("OTP") },
            singleLine = true,
            modifier = Modifier
                .focusRequester(otpFocusRequester)
                .onKeyEvent {
                    if (it.key == Key.Tab && it.type == KeyEventType.KeyDown) {
                        focusManager.moveFocus(FocusDirection.Next)
                        true
                    } else if (it.key == Key.Spacebar && it.type == KeyEventType.KeyDown) {
                        false
                    } else {
                        false
                    }
                }
        )
        Spacer(modifier = Modifier.height(16.dp))
        var newPasswordVisibility by remember { mutableStateOf(false) }
        OutlinedTextField(
            value = newPassword,
            onValueChange = { newPassword = it },
            label = { Text("New Password") },
            visualTransformation = if (newPasswordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
            singleLine = true,
            trailingIcon = {
                IconButton(onClick = { newPasswordVisibility = !newPasswordVisibility }) {
                    Icon(
                        painter = painterResource(if (newPasswordVisibility) Res.drawable.visibility else Res.drawable.visibility_off),
                        contentDescription = if (newPasswordVisibility) "Hide password" else "Show password"
                    )
                }
            },
            modifier = Modifier
                .focusRequester(newPasswordFocusRequester)
                .onKeyEvent {
                    if (it.key == Key.Tab && it.type == KeyEventType.KeyDown) {
                        focusManager.moveFocus(FocusDirection.Next)
                        true
                    } else if (it.key == Key.Spacebar && it.type == KeyEventType.KeyDown) {
                        false
                    } else {
                        false
                    }
                }
        )
        Spacer(modifier = Modifier.height(16.dp))
        var confirmNewPasswordVisibility by remember { mutableStateOf(false) }
        OutlinedTextField(
            value = confirmNewPassword,
            onValueChange = { confirmNewPassword = it },
            label = { Text("Confirm New Password") },
            visualTransformation = if (confirmNewPasswordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
            singleLine = true,
            trailingIcon = {
                IconButton(onClick = { confirmNewPasswordVisibility = !confirmNewPasswordVisibility }) {
                    Icon(
                        painter = painterResource(if (confirmNewPasswordVisibility) Res.drawable.visibility else Res.drawable.visibility_off),
                        contentDescription = if (confirmNewPasswordVisibility) "Hide password" else "Show password"
                    )
                }
            },
            modifier = Modifier
                .focusRequester(confirmNewPasswordFocusRequester)
                .onKeyEvent {
                    if (it.key == Key.Tab && it.type == KeyEventType.KeyDown) {
                        focusManager.moveFocus(FocusDirection.Next)
                        true
                    } else if (it.key == Key.Spacebar && it.type == KeyEventType.KeyDown) {
                        false
                    } else {
                        false
                    }
                }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { authViewModel.setNewPassword(authViewModel.forgotPasswordEmail, otp, newPassword) }) {
            Text("Set New Password")
        }
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = { authViewModel.navigateTo(AuthScreen.ForgotPassword) }) {
            Text("Back to Forgot Password")
        }

        when (val state = authViewModel.uiState) {
            is UiState.Loading -> {
                CircularProgressIndicator()
            }
            is UiState.Error -> {
                Text(state.message, color = MaterialTheme.colorScheme.error)
            }
            else -> {}
        }
    }
}
