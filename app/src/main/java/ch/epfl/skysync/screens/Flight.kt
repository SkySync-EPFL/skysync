package ch.epfl.skysync.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import ch.epfl.skysync.navigation.BottomBar

@Composable
fun FlightScreen(navController : NavHostController) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = { BottomBar(navController) }
    )
    { padding ->
        androidx.compose.material3.Text(
            modifier = Modifier.padding(padding),
            text = "Flight",
            fontSize = androidx.compose.material3.MaterialTheme.typography.displayLarge.fontSize,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}

@Composable
@Preview
fun FlightScreenPreview() {
    val navController = rememberNavController()
    FlightScreen(navController = navController)
}