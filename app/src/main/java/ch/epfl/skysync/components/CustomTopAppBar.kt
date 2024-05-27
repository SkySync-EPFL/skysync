package ch.epfl.skysync.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.navigation.NavController
import ch.epfl.skysync.navigation.Route

/**
 * Custom top app bar with back button and title
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopAppBar(navController: NavController, title: String) {
  TopAppBar(
      title = { Text(text = title, style = MaterialTheme.typography.headlineMedium) },
      navigationIcon = {
        IconButton(
            onClick = {
              if (!navController.popBackStack()) {
                navController.navigate(Route.MAIN)
              }
            },
            modifier = Modifier.testTag("BackButton")) {
              Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
      })
}
