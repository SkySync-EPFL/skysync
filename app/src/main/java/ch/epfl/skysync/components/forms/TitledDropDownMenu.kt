package ch.epfl.skysync.components.forms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import ch.epfl.skysync.components.MediumTitle

@Composable
fun <T> TitledDropDownMenu(
    defaultPadding: Dp,
    title: String,
    value: T,
    onclickMenu: (T) -> Unit,
    items: List<T>,
    showString: (T) -> String = { it.toString() },
    isError: Boolean = false,
    messageError: String = ""
) {
  MediumTitle(modifier = Modifier.fillMaxWidth(), title = title, padding = defaultPadding)
  CustomDropDownMenu(
      defaultPadding = defaultPadding,
      title = title,
      value = value,
      onclickMenu = onclickMenu,
      items = items,
      showString = showString,
      isError = isError,
      messageError = messageError)
}

@Composable
fun <T> TitledIconDropDownMenu(
    defaultPadding: Dp,
    title: String,
    value: T,
    onclickMenu: (T) -> Unit,
    onDeletion: () -> Unit,
    items: List<T>,
    showString: (T) -> String = { it.toString() },
    isError: Boolean = false,
    messageError: String = ""
) {
  MediumTitle(modifier = Modifier.fillMaxWidth(), title = title, padding = defaultPadding)
  Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically) {
        CustomDropDownMenu(
            modifier = Modifier.weight(1f),
            defaultPadding = defaultPadding,
            title = title,
            value = value,
            onclickMenu = onclickMenu,
            items = items,
            showString = showString,
            isError = isError,
            messageError = messageError)
        IconButton(
            modifier = Modifier.testTag("Delete Balloon Button"),
            onClick = { onDeletion() },
        ) {
          Icon(Icons.Default.Delete, contentDescription = "Delete Balloon")
        }
      }
}
