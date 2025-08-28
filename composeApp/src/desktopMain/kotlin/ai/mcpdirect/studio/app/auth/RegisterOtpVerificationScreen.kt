package ai.mcpdirect.studio.app.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.FocusRequester.Companion.FocusRequesterFactory.component1
import androidx.compose.ui.focus.FocusRequester.Companion.FocusRequesterFactory.component2
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
fun RegisterOtpVerificationScreen(authViewModel: AuthViewModel) {
    var otp by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    val focusManager = LocalFocusManager.current
    val (otpRequester,nameRequester,passwordFocusRequester, confirmPasswordFocusRequester) = FocusRequester.createRefs()
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
        Text("Enter OTP sent to ${authViewModel.registrationEmail}")
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = otp,
            onValueChange = { otp = it },
            label = { Text("OTP") }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("Set your name and password")
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Your Name") },
            singleLine = true,
            modifier = Modifier
                .focusRequester(passwordFocusRequester)
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
        var passwordVisibility by remember { mutableStateOf(false) }
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
            singleLine = true,
            trailingIcon = {
                IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
                    Icon(
                        painter = painterResource(if (!passwordVisibility) Res.drawable.visibility else Res.drawable.visibility_off),
                        contentDescription = if (passwordVisibility) "Hide password" else "Show password"
                    )
                }
            },
            modifier = Modifier
                .focusRequester(passwordFocusRequester)
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
        var confirmPasswordVisibility by remember { mutableStateOf(false) }
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            visualTransformation = if (confirmPasswordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
            singleLine = true,
            trailingIcon = {
                IconButton(onClick = { confirmPasswordVisibility = !confirmPasswordVisibility }) {
                    Icon(
                        painter = painterResource(if (!passwordVisibility) Res.drawable.visibility else Res.drawable.visibility_off),
                        contentDescription = if (confirmPasswordVisibility) "Hide password" else "Show password"
                    )
                }
            },
            modifier = Modifier
                .focusRequester(confirmPasswordFocusRequester)
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
        Button(onClick = { authViewModel.register(name, otp, password, confirmPassword) }) {
            Text("Verify OTP and Register")
        }
        Spacer(modifier = Modifier.height(16.dp))
        TextButton(onClick = { authViewModel.navigateTo(AuthScreen.Register) }) {
            Text("Back to Registration")
        }

        when (val state = authViewModel.uiState) {
            is UiState.Loading -> {
                CircularProgressIndicator()
            }

            is UiState.SuccessWithAccount -> {
                // Navigation handled by AuthViewModel
            }

            is UiState.Error -> {
                Text(state.message, color = MaterialTheme.colorScheme.error)
            }

            else -> {}
        }
    }
}
