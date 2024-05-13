package ch.epfl.skysync.screens.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import ch.epfl.skysync.navigation.AdminBottomBar

@Composable
fun AdminStatsScreen(
    navController: NavHostController
    // , viewModel: StatsViewModel){
) {
  Scaffold(modifier = Modifier.fillMaxSize(), bottomBar = { AdminBottomBar(navController) }) {
      padding ->
    Column(
        modifier = Modifier.fillMaxSize().padding(padding),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
          Text(text = "Feature not available", fontSize = 24.sp)
        }
  }
}
