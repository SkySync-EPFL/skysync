package ch.epfl.skysync.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times

/** A composable made to select a time duration of a maximum of 24 hours. */
@Composable
fun RangeTimePicker(
    padding: Dp,
    title: String,
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (Long) -> Unit
) {
  if (!showDialog) {
    return
  }
  var timeDuration by remember { mutableLongStateOf(0L) }
  val listState1 = rememberLazyListState(Int.MAX_VALUE / 2)
  val listState2 = rememberLazyListState(Int.MAX_VALUE / 2)
  val visibleItemCount = 3
  val itemHeight = 80.dp
  AlertDialog(
      modifier = Modifier.padding(padding).fillMaxWidth(),
      onDismissRequest = { onDismiss() },
      title = { Text(text = title) },
      text = {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
          CircularList(
              items = (0..23).map { "%02d".format(it) },
              modifier = Modifier.fillMaxWidth().weight(1f),
              listState1,
              itemHeight = itemHeight,
              visibleItemCount = visibleItemCount,
              testTag = "HourCircularList")
          CircularList(
              items = (0..59).map { "%02d".format(it) },
              modifier = Modifier.fillMaxWidth().weight(1f),
              listState2,
              itemHeight = itemHeight,
              visibleItemCount = visibleItemCount,
              testTag = "MinuteCircularList")
        }
      },
      confirmButton = {
        TextButton(
            onClick = {
              val offset = visibleItemCount / 2
              val hour = (listState1.firstVisibleItemIndex + offset) % 24
              val minute = (listState2.firstVisibleItemIndex + offset) % 60
              timeDuration = ((hour * 60 + minute) * 60_000).toLong() // convert to milliseconds
              onConfirm(timeDuration)
            },
            modifier = Modifier.testTag("AlertDialogConfirm")) {
              Text("Confirm")
            }
      },
      dismissButton = {
        TextButton(onClick = { onDismiss() }, modifier = Modifier.testTag("AlertDialogDismiss")) {
          Text("Dismiss")
        }
      })
}

/** CircularList is a composable to mimic a vertical carousel list */
@Composable
fun CircularList(
    items: List<String>,
    modifier: Modifier = Modifier,
    listState: LazyListState,
    isEndless: Boolean = true,
    itemHeight: Dp = 60.dp,
    visibleItemCount: Int = 3,
    testTag: String
) {
  val verticalPadding = 10.dp
  val listHeight = (itemHeight + 2 * verticalPadding) * visibleItemCount
  LazyColumn(state = listState, modifier = modifier.height(listHeight).testTag(testTag)) {
    items(
        count = if (isEndless) Int.MAX_VALUE else items.size,
        itemContent = { index ->
          val firstVisibleIndex = remember { derivedStateOf { listState.firstVisibleItemIndex } }
          val layoutInfo = remember { derivedStateOf { listState.layoutInfo } }
          val scrollOffset = remember { derivedStateOf { listState.firstVisibleItemScrollOffset } }
          val idx = index % items.size
          val middleIndex = firstVisibleIndex.value + layoutInfo.value.visibleItemsInfo.size / 2
          val isEnabled = index == middleIndex
          val animatedColor by
              animateColorAsState(
                  if (isEnabled) MaterialTheme.colorScheme.onSurface else Color.Gray,
                  label = "color")
          // LaunchedEffect to keep items aligned
          LaunchedEffect(listState.isScrollInProgress) {
            if (!listState.isScrollInProgress) {
              if (scrollOffset.value > itemHeight.value / 2) {
                listState.scrollToItem(firstVisibleIndex.value + 1)
              } else {
                listState.scrollToItem(firstVisibleIndex.value)
              }
            }
          }
          Text(
              modifier =
                  Modifier.padding(horizontal = 16.dp, vertical = verticalPadding)
                      .height(itemHeight),
              text = items[idx],
              style = MaterialTheme.typography.displayLarge, // item composable
              color = animatedColor)
        })
  }
}

@Preview
@Composable
fun RangeTimePickerPreview() {
  var time by remember { mutableLongStateOf(0L) }
  RangeTimePicker(
      padding = 16.dp,
      title = "Select a time duration",
      showDialog = true,
      onDismiss = {},
      onConfirm = { time = it })
}
