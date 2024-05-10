package ch.epfl.skysync.components.forms

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun <T> SearchBarCustom(
    title: String = "",
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    active: Boolean,
    onActiveChange: (Boolean) -> Unit,
    onElementClick: (T) -> Unit,
    propositions: List<T>,
    showProposition: (T) -> String,
    placeholder: String = "Search"
) {
  val focusRequester = remember { FocusRequester() }
  var height by remember { mutableStateOf(90.dp) }
  val animatedHeight by animateDpAsState(targetValue = height, label = "")
  val keyboardController = LocalSoftwareKeyboardController.current
  LaunchedEffect(active) { height = if (active) 300.dp else 90.dp }
  Column(
      modifier =
          Modifier.fillMaxWidth()
              .height(animatedHeight)
              .padding(bottom = 16.dp, start = 16.dp, end = 16.dp)) {
        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier =
                Modifier.fillMaxWidth()
                    .focusRequester(focusRequester)
                    .onFocusChanged { focusState -> onActiveChange(focusState.isFocused) }
                    .testTag("$title Search Bar Input"),
            singleLine = true,
            placeholder = { Text(text = placeholder) },
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Search),
            keyboardActions =
                KeyboardActions(
                    onSearch = {
                      onSearch(query)
                      keyboardController?.hide()
                      focusRequester.freeFocus()
                    }))
        Spacer(modifier = Modifier.height(16.dp))
        if (active) {
          LazyColumn(modifier = Modifier.fillMaxSize().weight(1f).testTag("Search Propositions")) {
            items(propositions) { proposition ->
              Box(modifier = Modifier.fillMaxWidth().clickable { onElementClick(proposition) }) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = showProposition(proposition),
                    modifier =
                        Modifier.fillMaxWidth().padding(vertical = 16.dp, horizontal = 16.dp))
              }
            }
          }
        }
      }
}

@Preview
@Composable
fun SearchBarCustomPreview() {
  var active by remember { mutableStateOf(false) }
  var query by remember { mutableStateOf("") }

  SearchBarCustom(
      query = query,
      onQueryChange = { query = it },
      onSearch = { active = false },
      active = active,
      onActiveChange = { active = it },
      onElementClick = { query = it },
      propositions =
          listOf(
              "Proposition 1",
              "Proposition 2",
              "Proposition 3",
              "Proposition 4",
              "Proposition 5",
              "Proposition 6",
              "Proposition 7",
              "Proposition 8",
              "Proposition 9",
              "Proposition 10",
              "Proposition 11",
              "Proposition 12",
              "Proposition 13",
              "Proposition 14",
              "Proposition 15"),
      showProposition = { it })
}
