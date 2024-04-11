package ch.epfl.skysync.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import ch.epfl.skysync.navigation.BottomBar
import kotlin.annotation.AnnotationRetention
import kotlin.annotation.Retention

@Retention(AnnotationRetention.RUNTIME) // Set retention policy to runtime
annotation class ExcludeGenerated

@Composable
fun ChatScreen(navController: NavHostController) {
  Scaffold(modifier = Modifier.fillMaxSize().testTag("ChatScreenScaffold"), bottomBar = { BottomBar(navController) }) { padding ->
    Text(
        modifier = Modifier.padding(padding),
        text = "Chat",
        fontSize = androidx.compose.material3.MaterialTheme.typography.displayLarge.fontSize,
        fontWeight = FontWeight.Bold,
        color = Color.Black)
  }
}
@ExcludeGenerated
fun test(): String {
  return "Yes"
}

@Composable
@Preview
@ExcludePreviewFromTestReport
fun Preview() {
  val navController = rememberNavController()
  ChatScreen(navController = navController)
}
