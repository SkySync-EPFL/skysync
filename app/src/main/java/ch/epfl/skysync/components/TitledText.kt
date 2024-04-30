package ch.epfl.skysync.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun TitledText(
    padding: Dp,
    title: String,
    value: String,
) {

    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = padding),
        text = title,
        style = MaterialTheme.typography.headlineSmall,
        textDecoration = TextDecoration.None,
        color = Color.Black
    )
    Spacer(modifier = Modifier.padding(4.dp))
    OutlinedTextField(
        value = value,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = padding)
            .testTag(title),
        onValueChange = { },
        enabled = false,
        colors = TextFieldDefaults.colors(
            disabledTextColor = Color.Black,
            disabledContainerColor = Color.White,
        )
    )
    Spacer(modifier = Modifier.padding(12.dp))
}