package ch.epfl.skysync.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import ch.epfl.skysync.navigation.BottomBar

@Composable
fun HomeScreen(navController: NavHostController) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = { BottomBar(navController) }
    )
    { padding ->
        Text(
            modifier = Modifier.padding(padding),
            text = "Home",
            fontSize = MaterialTheme.typography.displayLarge.fontSize,
            fontWeight = FontWeight.Bold,
            color = Color.Black)
    }
}

@Composable
@Preview
fun HomeScreenPreview() {
    val navController = rememberNavController()
    HomeScreen(navController = navController)
}
