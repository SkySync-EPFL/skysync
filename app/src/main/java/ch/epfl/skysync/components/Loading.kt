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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.epfl.skysync.ui.theme.lightOrange
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

/**
 * A reusable loading component that displays a progress indicator when content is being loaded. It
 * also supports a pull-to-refresh mechanism to reload content. The progress indicator is displayed
 * over the content as an overlay when data is loading or during a refresh action.
 *
 * @param isLoading Boolean state indicating whether the data is currently loading. The progress
 *   indicator is shown on top of the content when true.
 * @param onRefresh Function to be invoked when a refresh is triggered by the user. This allows
 *   external specification of the reload behavior, enhancing the component's reusability.
 * @param content A composable that defines the content to be displayed when not loading. This
 *   content will be displayed beneath the progress indicator during loading operations.
 */
@Composable
fun LoadingComponent(
    isLoading: Boolean,
    onRefresh: () -> Unit, // Added onRefresh as a parameter to handle reload logic ,
    content: @Composable () -> Unit
) {
  // The SwipeRefresh component is used to wrap the content. It provides pull-to-refresh
  // functionality.
  SwipeRefresh(
      state = rememberSwipeRefreshState(isLoading),
      onRefresh = onRefresh, // Use the passed onRefresh function here
      modifier = Modifier.testTag("SwipeRefreshLayout"),
      indicator = { state, trigger ->
        // This ensures the CircularProgressIndicator is part of the layer but does not obscure
        // content layout
        if (state.isRefreshing) {
          Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                modifier = Modifier.size(50.dp).testTag("ProgressIndicator"), color = lightOrange)
          }
        }
      }) {
        content()
      } // Display the CircularProgressIndicator centered on top of the content when isLoading is
  // true
  if (isLoading) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
      CircularProgressIndicator(
          modifier = Modifier.size(50.dp).testTag("LoadingIndicator"), color = lightOrange)
    }
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
        isLoading = true,
        onRefresh = { /* Define what happens on refresh here, e.g., viewModel.loadData() */},
        content = {
          Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Content goes here", style = MaterialTheme.typography.bodyLarge)
          }
        })
  }
}
