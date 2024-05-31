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

/**
 * A composable function that displays a dropdown menu with a title.
 *
 * @param defaultPadding The default padding value to be applied to the dropdown menu.
 * @param title The title of the dropdown menu.
 * @param value The current selected value in the dropdown menu.
 * @param onclickMenu Callback function triggered when a menu item is clicked.
 * @param items The list of items to be displayed in the dropdown menu.
 * @param showString Function to convert an item to a string for display.
 * @param isError Boolean indicating if there is an error.
 * @param messageError The error message to be displayed if there is an error.
 */
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

/**
 * A composable function that displays a dropdown menu with a title and a delete icon.
 *
 * @param defaultPadding The default padding value to be applied to the dropdown menu.
 * @param title The title of the dropdown menu.
 * @param value The current selected value in the dropdown menu.
 * @param onclickMenu Callback function triggered when a menu item is clicked.
 * @param onDeletion Callback function triggered when the delete icon is clicked.
 * @param items The list of items to be displayed in the dropdown menu.
 * @param showString Function to convert an item to a string for display.
 * @param isError Boolean indicating if there is an error.
 * @param messageError The error message to be displayed if there is an error.
 */
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
