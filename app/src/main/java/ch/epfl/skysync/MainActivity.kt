package ch.epfl.skysync

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.mutableStateOf
import androidx.navigation.compose.rememberNavController
import ch.epfl.skysync.components.GlobalSnackbarHost
import ch.epfl.skysync.components.SnackbarManager
import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.navigation.MainGraph
import ch.epfl.skysync.ui.theme.SkySyncTheme
import ch.epfl.skysync.viewmodel.TimerViewModel
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MainActivity : ComponentActivity() {
  private lateinit var signInLauncher: ActivityResultLauncher<Intent>
  private val user = mutableStateOf<FirebaseUser?>(null)
  private val db: FirestoreDatabase = FirestoreDatabase()
  private val repository: Repository = Repository(db)

  private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
    if (result.resultCode == RESULT_OK) {
      user.value = FirebaseAuth.getInstance().currentUser
      SnackbarManager.showMessage("Successfully signed in")
    } else {
      SnackbarManager.showMessage("Authentication failed")
    }
  }

  @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // Initialize the signInLauncher
    signInLauncher =
        registerForActivityResult(FirebaseAuthUIActivityResultContract()) { res ->
          this.onSignInResult(res)
        }

    setContent {
      SkySyncTheme {
        val navController = rememberNavController()
        Scaffold(snackbarHost = { GlobalSnackbarHost() }) {
          val timerVm = TimerViewModel.createViewModel()
          MainGraph(
              repository = repository,
              navHostController = navController,
              signInLauncher = signInLauncher,
              uid = user.value?.uid,
              timer=timerVm,
            )
        }
      }
    }
  }
}
