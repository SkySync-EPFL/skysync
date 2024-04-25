package ch.epfl.skysync.components.forms

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp

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
  Text(
      modifier = Modifier.padding(horizontal = defaultPadding),
      text = title,
      style = MaterialTheme.typography.headlineSmall)
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
