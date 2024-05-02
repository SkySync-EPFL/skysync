package ch.epfl.skysync.components.forms

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> CustomDropDownMenu(
    defaultPadding: Dp,
    title: String,
    value: T,
    onclickMenu: (T) -> Unit,
    items: List<T>,
    showString: (T) -> String = { it.toString() },
    isError: Boolean = false,
    messageError: String = "",
) {

  var expanded by remember { mutableStateOf(false) }
  Column {
    ExposedDropdownMenuBox(
        modifier =
            Modifier.fillMaxWidth()
                .padding(horizontal = defaultPadding)
                .clickable(onClick = { expanded = true })
                .testTag("$title Menu"),
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }) {
          OutlinedTextField(
              value = showString(value),
              modifier = Modifier.fillMaxWidth().menuAnchor().testTag("selected $title dropdown"),
              readOnly = true,
              onValueChange = {},
              trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
              isError = isError,
              supportingText = { if (isError) Text(messageError) })
        }
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
    ) {
      items.withIndex().forEach { (id, item) ->
        DropdownMenuItem(
            modifier =
                Modifier.fillMaxWidth().padding(horizontal = defaultPadding).testTag("$title $id"),
            onClick = {
              onclickMenu(item)
              expanded = false
            },
            text = { Text(showString(item)) },
            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding)
      }
    }
  }
}
