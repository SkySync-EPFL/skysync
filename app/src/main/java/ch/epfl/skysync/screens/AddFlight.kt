package ch.epfl.skysync.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavHostController
import ch.epfl.skysync.navigation.BottomBar

@Composable
fun AddFlight(navController: NavHostController) {
  Scaffold(modifier = Modifier.fillMaxSize(), bottomBar = { BottomBar(navController) }) { padding ->
    Text(
        modifier = Modifier.padding(padding),
        text = "Add Flight",
        fontSize = MaterialTheme.typography.displayLarge.fontSize,
        fontWeight = FontWeight.Bold,
        color = Color.Black)
  }
}
