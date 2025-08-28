//package ai.mcpdirect.studio.app.auth
//
//import androidx.compose.foundation.layout.*
//import androidx.compose.material.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.focus.FocusDirection
//import androidx.compose.ui.focus.FocusRequester
//import androidx.compose.ui.focus.focusRequester
//import androidx.compose.ui.input.key.Key
//import androidx.compose.ui.input.key.KeyEventType
//import androidx.compose.ui.input.key.key
//import androidx.compose.ui.input.key.onKeyEvent
//import androidx.compose.ui.input.key.type
//import androidx.compose.ui.platform.LocalFocusManager
//import androidx.compose.ui.text.input.PasswordVisualTransformation
//import androidx.compose.ui.text.input.VisualTransformation
//import androidx.compose.ui.unit.dp
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Visibility
//import androidx.compose.material.icons.filled.VisibilityOff
//
//@Composable
//fun RegisterSetPasswordScreen(authViewModel: AuthViewModel) {
//    var password by remember { mutableStateOf("") }
//    var confirmPassword by remember { mutableStateOf("") }
//
//    val focusManager = LocalFocusManager.current
//    val (passwordFocusRequester, confirmPasswordFocusRequester) = FocusRequester.createRefs()
//
//    Column(
//        modifier = Modifier.fillMaxSize(),
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Text("Set your password for ${authViewModel.registrationEmail}")
//        Spacer(modifier = Modifier.height(16.dp))
//        var passwordVisibility by remember { mutableStateOf(false) }
//        OutlinedTextField(
//            value = password,
//            onValueChange = { password = it },
//            label = { Text("Password") },
//            visualTransformation = if (passwordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
//            singleLine = true,
//            trailingIcon = {
//                IconButton(onClick = { passwordVisibility = !passwordVisibility }) {
//                    Icon(
//                        imageVector = if (passwordVisibility) Icons.Default.Visibility else Icons.Default.VisibilityOff,
//                        contentDescription = if (passwordVisibility) "Hide password" else "Show password"
//                    )
//                }
//            },
//            modifier = Modifier
//                .focusRequester(passwordFocusRequester)
//                .onKeyEvent {
//                    if (it.key == Key.Tab && it.type == KeyEventType.KeyDown) {
//                        focusManager.moveFocus(FocusDirection.Next)
//                        true
//                    } else if (it.key == Key.Spacebar && it.type == KeyEventType.KeyDown) {
//                        false
//                    } else {
//                        false
//                    }
//                }
//        )
//        Spacer(modifier = Modifier.height(16.dp))
//        var confirmPasswordVisibility by remember { mutableStateOf(false) }
//        OutlinedTextField(
//            value = confirmPassword,
//            onValueChange = { confirmPassword = it },
//            label = { Text("Confirm Password") },
//            visualTransformation = if (confirmPasswordVisibility) VisualTransformation.None else PasswordVisualTransformation(),
//            singleLine = true,
//            trailingIcon = {
//                IconButton(onClick = { confirmPasswordVisibility = !confirmPasswordVisibility }) {
//                    Icon(
//                        imageVector = if (confirmPasswordVisibility) Icons.Default.Visibility else Icons.Default.VisibilityOff,
//                        contentDescription = if (confirmPasswordVisibility) "Hide password" else "Show password"
//                    )
//                }
//            },
//            modifier = Modifier
//                .focusRequester(confirmPasswordFocusRequester)
//                .onKeyEvent {
//                    if (it.key == Key.Tab && it.type == KeyEventType.KeyDown) {
//                        focusManager.moveFocus(FocusDirection.Next)
//                        true
//                    } else if (it.key == Key.Spacebar && it.type == KeyEventType.KeyDown) {
//                        false
//                    } else {
//                        false
//                    }
//                }
//        )
//        Spacer(modifier = Modifier.height(16.dp))
//        Button(onClick = { authViewModel.register(authViewModel.registrationEmail, password) }) {
//            Text("Set Password and Register")
//        }
//        Spacer(modifier = Modifier.height(16.dp))
//        TextButton(onClick = { authViewModel.navigateTo(AuthScreen.RegisterOtpVerification) }) {
//            Text("Back to OTP Verification")
//        }
//
//        when (val state = authViewModel.uiState) {
//            is UiState.Loading -> {
//                CircularProgressIndicator()
//            }
//            is UiState.Error -> {
//                Text(state.message, color = MaterialTheme.colors.error)
//            }
//            else -> {}
//        }
//    }
//}
