package ch.epfl.skysync.screens

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ch.epfl.skysync.navigation.Route
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseUser

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    signInLauncher: ActivityResultLauncher<Intent>,
    user: FirebaseUser?,
    navController: NavController
) {

  val providers =
      listOf(
          // Google sign-in
          AuthUI.IdpConfig.GoogleBuilder().build())

  val signInIntent =
      AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers).build()

  Surface(modifier = Modifier.fillMaxSize()) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center) {
          Button(onClick = { signInLauncher.launch(signInIntent) }) {
            Icon(imageVector = Icons.Default.Check, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Sign in with Google")
          }

          if (user != null) {
            // logged in
              navController.navigate(Route.HOME){
                  popUpTo(Route.LOGIN)
                  launchSingleTop = true
              }
          } else {
            // not logged in
            Text(text = "You need to log in")
          }
        }
  }
}
