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
import androidx.compose.ui.focus.FocusRequester.Companion.FocusRequesterFactory.component3
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import mcpdirectstudioapp.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource

@Composable
fun AnonymousLoginScreen(authViewModel: AuthViewModel) {
//    var email by remember { mutableStateOf("") }
    var anonymousKey by remember { mutableStateOf("") }

    val focusManager = LocalFocusManager.current
    val (emailFocusRequester, passwordFocusRequester,loginFocusRequester) = FocusRequester.createRefs()
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
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { authViewModel.anonymousLogin() }){
            Text("Login with local anonymous key")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text("OR your have another anonymous key")
        Spacer(modifier = Modifier.height(8.dp))
        var keyVisibility by remember { mutableStateOf(false) }

        OutlinedTextField(
            value = anonymousKey,
            onValueChange = { anonymousKey = it },
            label = { Text("Your anonymous key") },
            singleLine = true,
            visualTransformation = if (keyVisibility) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { keyVisibility = !keyVisibility }) {
                    Icon(
                        painter = painterResource(if (!keyVisibility) Res.drawable.visibility else Res.drawable.visibility_off),
                        contentDescription = if (!keyVisibility) "Hide password" else "Show password"
                    )
                }
            },
            modifier = Modifier
                .focusRequester(passwordFocusRequester)
                .onKeyEvent {
                    if ((it.key == Key.Tab) && it.type == KeyEventType.KeyDown) {
                        focusManager.moveFocus(FocusDirection.Next)
                        true
                    }else if(it.key==Key.Enter && it.type == KeyEventType.KeyDown){
                        authViewModel.anonymousLogin(anonymousKey)
                        true
                    }else if (it.key == Key.Spacebar && it.type == KeyEventType.KeyDown) {
                        false
                    } else {
                        false
                    }
                }
        )
        if (!authViewModel.isLoginPasswordValid) {
            Text("Anonymous key cannot be empty", color = MaterialTheme.colorScheme.error)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { authViewModel.anonymousLogin(anonymousKey) }) {
            Text("Login")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text("OR No key?")
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = {  authViewModel.anonymousRegister()}) {
            Text("Generate an anonymous key and login")
        }
        Spacer(modifier = Modifier.height(8.dp))
        TextButton(onClick = { authViewModel.navigateTo(AuthScreen.AuthOption) }) {
            Text("Back")
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
