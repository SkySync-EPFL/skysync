package ch.epfl.skysync.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import ch.epfl.skysync.navigation.BottomBar

@Composable
fun ConnectivityWrapper(
    connectivityStatus: ConnectivityStatus,
    navController: NavHostController,
    content: @Composable () -> Unit
) {
  var showOfflineMessage by remember { mutableStateOf(false) }

  if (connectivityStatus.isOnline()) {
    content()
  } else {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = { BottomBar(navController) },
        floatingActionButton = {},
        floatingActionButtonPosition = FabPosition.End,
    ) { padding ->
      if (showOfflineMessage) {
        Box(modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.Center)) {
          Text(
              text = "Feature not available offline",
              color = Color.Red,
              fontSize = 18.sp,
              modifier = Modifier.padding(16.dp))
        }
      } else {
        AlertDialog(
            onDismissRequest = { /* Do nothing */},
            title = {
              Text(text = "No Internet Connection", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            },
            text = { Text(text = "Feature not available offline", fontSize = 18.sp) },
            confirmButton = { Button(onClick = { showOfflineMessage = true }) { Text("OK") } },
            containerColor = Color.White,
            titleContentColor = Color.Black,
            textContentColor = Color.Black,
        )
      }
    }
  }
}
