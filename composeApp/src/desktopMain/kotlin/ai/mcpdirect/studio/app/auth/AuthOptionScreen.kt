package ai.mcpdirect.studio.app.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import mcpdirectstudioapp.composeapp.generated.resources.Res
import mcpdirectstudioapp.composeapp.generated.resources.account_circle
import mcpdirectstudioapp.composeapp.generated.resources.key
import mcpdirectstudioapp.composeapp.generated.resources.mcpdirect_studio_256
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun AuthOptionScreen(
    viewModel: AuthViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Header
        ActivationHeader()

        Spacer(modifier = Modifier.height(40.dp))

        // Options
        Row(
            modifier = Modifier.fillMaxWidth().weight(1f),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Account Option
            ActivationOptionCard(
                icon = Res.drawable.account_circle,
                title = "Sign in with an Account",
                description = "Never lose your tool configurations.",
                isRecommended = true,
                onClick = {viewModel.navigateTo(screen = AuthScreen.Login)},
                modifier = Modifier.fillMaxSize().weight(1f)
            )
            // Product Key Option
            ActivationOptionCard(
                icon = Res.drawable.key,
                title = "Anonymous sign in",
                description = "No account required. Generate anonymous key yourself - but losing key will losing everything.  \nAnd each device can generate an anonymous Key once",
                isRecommended = false,
                onClick = {viewModel.navigateTo(screen = AuthScreen.AnonymousLogin)},
                modifier = Modifier.fillMaxSize().weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Footer Links
        Row(
//                modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TextButton(onClick = { /* Privacy */ }) {
                Text("Privacy")
            }
            TextButton(onClick = { /* License Terms */ }) {
                Text("License Terms")
            }
            TextButton(onClick = { /* Get Help */ }) {
                Text("Get Help")
            }
        }
    }
}

@Composable
private fun ActivationHeader() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painter = painterResource(Res.drawable.mcpdirect_studio_256),
            contentDescription = "MCPdirect Studio",
            modifier = Modifier.width(256.dp)
        )
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "Connect your home/office MCP servers directly to AI agents worldwide",
            style = MaterialTheme.typography.headlineSmall,
//            fontWeight = FontWeight.Normal
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Choose how you want to activate.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ActivationOptionCard(
    icon: DrawableResource,
    title: String,
    description: String,
    isRecommended: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
//            verticalArrangement = Arrangement.SpaceAround
        ) {
                Row(
                modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(painterResource(icon), "")

                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Medium
                    )
                    if (isRecommended) {
                        Spacer(modifier = Modifier.width(16.dp))
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = RoundedCornerShape(100)
                        ) {
                            Text(
                                text = "Recommended",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(
                                    horizontal = 10.dp,
                                    vertical = 4.dp
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            Spacer(modifier = Modifier.weight(1f))
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = onClick
                ) {
                    Text(
                        text = if (isRecommended) "Sign in" else "Enter anonymously",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}
