package ai.mcpdirect.studio.app.compose

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mcpdirectstudioapp.composeapp.generated.resources.Res
import mcpdirectstudioapp.composeapp.generated.resources.search
import org.jetbrains.compose.resources.painterResource

val textFieldColors: TextFieldColors
    @Composable
    get() =  OutlinedTextFieldDefaults.colors(
//        focusedBorderColor = MaterialTheme.colorScheme.surface,
//        unfocusedBorderColor = MaterialTheme.colorScheme.onSurface,
        errorBorderColor = MaterialTheme.colorScheme.error
    )

@Composable
fun SearchView(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text(placeholder) },
        leadingIcon = { Icon(painterResource(Res.drawable.search), contentDescription = "Search") },
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = textFieldColors
    )
}