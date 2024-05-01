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
import ch.epfl.skysync.database.UserRole
import ch.epfl.skysync.models.user.TempUser
import ch.epfl.skysync.navigation.MainGraph
import ch.epfl.skysync.ui.theme.SkySyncTheme
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {
  private lateinit var signInLauncher: ActivityResultLauncher<Intent>
  private val user = mutableStateOf<FirebaseUser?>(null)
  private val db: FirestoreDatabase = FirestoreDatabase()
  private val repository: Repository = Repository(db)

  private fun onError(e: Exception) {
    println("HELLLLLOOO")
  }

  private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
    if (result.resultCode == RESULT_OK) {
      val incomingUser = FirebaseAuth.getInstance().currentUser!!
      val email = incomingUser.email!!

      runBlocking {
        val userExists = repository.userTable.get(incomingUser.uid)
        if (userExists != null) {
          user.value = incomingUser
          return@runBlocking
        }

        val tempUser = repository.tempUserTable.get(email)
        if (tempUser != null) {
          repository.userTable.set(
              incomingUser.uid, tempUser.toUserSchema(incomingUser.uid).toModel())
          repository.tempUserTable.delete(email)
          user.value = incomingUser
        }else {
          val u = TempUser(
                  email = email,
          userRole = UserRole.CREW,
          firstname = "Jean",
          lastname = "François",
          balloonQualification = null
          )
          repository.tempUserTable.set(email, u)
        }
      }
    }
    val snackBarText = if(user.value == null) "Authentication failed" else "Authentication Successful"
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

    setContent {
      SkySyncTheme {
        val navController = rememberNavController()
        Scaffold(snackbarHost = { GlobalSnackbarHost() }) {
          MainGraph(
              repository = repository,
              navHostController = navController,
              signInLauncher = signInLauncher,
              uid = user.value?.uid)
        }
      }
    }
  }
}
