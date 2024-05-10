package ch.epfl.skysync

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.material3.Scaffold
import androidx.navigation.compose.rememberNavController
import ch.epfl.skysync.components.GlobalSnackbarHost
import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.navigation.MainGraph
import ch.epfl.skysync.ui.theme.SkySyncTheme
import ch.epfl.skysync.viewmodel.LocationViewModel
import ch.epfl.skysync.viewmodel.UserGlobalViewModel
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
  private lateinit var signInLauncher: ActivityResultLauncher<Intent>
  private var userGlobalViewModel: UserGlobalViewModel? = null
  private val db: FirestoreDatabase = FirestoreDatabase()
  private val repository: Repository = Repository(db)

  private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
    val vm = userGlobalViewModel ?: return
    if (result.resultCode == RESULT_OK) {
      val user = FirebaseAuth.getInstance().currentUser!!
      vm.loadUser(user.uid, user.email!!)
    }
  }

  @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // Initialize the signInLauncher here
    // as it needs to be created in MainActivity before the application is started,
    // that is before the setContent method is called
    signInLauncher =
        registerForActivityResult(FirebaseAuthUIActivityResultContract()) { res ->
          this.onSignInResult(res)
        }

    setContent {
      SkySyncTheme {
        val navController = rememberNavController()
        Scaffold(snackbarHost = { GlobalSnackbarHost() }) {
          userGlobalViewModel = UserGlobalViewModel.createViewModel(repository)
          val inFlightViewModel =
              LocationViewModel.createViewModel(
                  repository = repository) // is shared between all screens
          MainGraph(
              repository = repository,
              navHostController = navController,
              signInLauncher = signInLauncher,
              userGlobalViewModel = userGlobalViewModel!!,
              inFlightsViewModel = inFlightViewModel)
        }
      }
    }
  }
}
