package ch.epfl.skysync.navigation

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import ch.epfl.skysync.screens.LoginScreen
import com.google.firebase.auth.FirebaseUser

/** Graph of the whole navigation of the app */
@Composable
fun MainGraph(
    navHostController: NavHostController,
    signInLauncher: ActivityResultLauncher<Intent>,
    user: FirebaseUser?
) {
  NavHost(
      navController = navHostController,
      startDestination = if (user == null) Route.MAIN else Route.MAIN) {
        homeGraph(navHostController, user)
        composable(Route.LOGIN) { LoginScreen(signInLauncher = signInLauncher) }
      }
}
