package ch.epfl.skysync.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.epfl.skysync.ui.theme.lightOrange
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

/**
 * A reusable loading component that displays a progress indicator when content is being loaded. It
 * also supports a pull-to-refresh mechanism to reload content.
 *
 * @param isLoading Boolean state indicating whether the data is currently loading.
 * @param modifier Modifier for styling and positioning the SwipeRefresh component.
 * @param content A composable that defines the content to be displayed when not loading.
 */
@Composable
fun LoadingComponent(
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
  // The SwipeRefresh component is used to wrap the content. It provides pull-to-refresh
  // functionality.
  SwipeRefresh(
      state = rememberSwipeRefreshState(isLoading),
      onRefresh = { /* Define what happens on refresh here, e.g., viewModel.loadData()*/},
      indicator = { state, trigger ->
        // Display a circular progress indicator if a refresh is ongoing.
        if (state.isRefreshing) {
          CircularProgressIndicator(modifier = Modifier.size(50.dp), color = lightOrange)
        }
      },
      modifier = modifier.fillMaxSize()) {
        content() // Display the actual content of the composable
  }
}

/**
 * Preview of the LoadingComponent. This preview function helps visualize how the LoadingComponent
 * will look in the UI.
 */
@Preview(showBackground = true)
@Composable
fun LoadingContentPreview() {
  Surface(modifier = Modifier.fillMaxSize()) {
    // Instantiate the LoadingComponent with a simple Text inside.
    LoadingComponent(
        isLoading = true, // Set to true for preview purposes, showing the loading state.
        content = {
          // Content is placed in a Box to center the text within.
          Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Content goes here", style = MaterialTheme.typography.bodyLarge)
          }
        })
  }
}
