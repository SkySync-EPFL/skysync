package ch.epfl.skysync

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
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
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {
  private lateinit var signInLauncher: ActivityResultLauncher<Intent>
  private val userId = mutableStateOf<String?>(null)
  private val db: FirestoreDatabase = FirestoreDatabase()
  private val repository: Repository = Repository(db)

  // [START ask_post_notifications]
  // Declare the launcher at the top of your Activity/Fragment:
  private val requestPermissionLauncher =
      registerForActivityResult(
          ActivityResultContracts.RequestPermission(),
      ) { isGranted: Boolean ->
        if (!isGranted) {
          Toast.makeText(
                  this,
                  "Notifications are disabled for this app. You won't receive alerts about assigned flight be careful",
                  Toast.LENGTH_LONG)
              .show()
        }
      }

  private fun askNotificationPermission() {
    // This is only necessary for API level >= 33 (TIRAMISU)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
          PackageManager.PERMISSION_GRANTED) {
        // FCM SDK (and your app) can post notifications.
      } else {
        // Display an educational UI explaining to the user the features that will be enabled
        // by them granting the POST_NOTIFICATION permission.
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder
            .setTitle("Notification Permission")
            .setMessage(
                "Granting notification permission will allow you to receive important alerts about assigned flights")
            .setPositiveButton("OK") { _, _ ->
              // If the user selects "OK," directly request the permission.
              requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
            .setNegativeButton("No thanks") { _, _ ->
              // If the user selects "No thanks," allow the user to continue without notifications.
              // You can handle this case accordingly.
            }
            .show()
      }
    }
  }
  // [END ask_post_notifications]
  private fun onError(e: Exception) {
    SnackbarManager.showMessage(e.message ?: "An unknown error occurred")
  }

  private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
    if (result.resultCode == RESULT_OK) {
      val incomingUser = FirebaseAuth.getInstance().currentUser!!
      val email = incomingUser.email!!

      runBlocking {
        val userExists = repository.userTable.get(incomingUser.uid)
        if (userExists != null) {
          userId.value = incomingUser.uid

          return@runBlocking
        }

        val tempUser = repository.tempUserTable.get(email)
        if (tempUser != null) {
          repository.userTable.set(
              incomingUser.uid, tempUser.toUserSchema(incomingUser.uid).toModel())
          repository.tempUserTable.delete(email)
          userId.value = incomingUser.uid
        } else {
          userId.value = "default-user"
        }
      }
    }

    val snackBarText =
        if (userId.value == "default-user") "Authentication with default Admin user"
        else "Authentication Successful"
    SnackbarManager.showMessage(snackBarText)
  }

  @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    // Initialize the signInLauncher
    signInLauncher =
        registerForActivityResult(FirebaseAuthUIActivityResultContract()) { res ->
          this.onSignInResult(res)
        }
    askNotificationPermission()
    setContent {
      SkySyncTheme {
        val navController = rememberNavController()
        Scaffold(snackbarHost = { GlobalSnackbarHost() }) {
          val timerVm = TimerViewModel.createViewModel() // is shared between all screens
          MainGraph(
              repository = repository,
              navHostController = navController,
              signInLauncher = signInLauncher,
              uid = userId.value,
              timer = timerVm,
          )
        }
      }
    }
  }
}
