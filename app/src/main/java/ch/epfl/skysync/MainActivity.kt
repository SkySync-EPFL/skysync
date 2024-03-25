package ch.epfl.skysync

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.runtime.mutableStateOf
import ch.epfl.skysync.screens.LoginScreen
import ch.epfl.skysync.ui.theme.SkySyncTheme
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MainActivity : ComponentActivity() {
  private lateinit var signInLauncher: ActivityResultLauncher<Intent>
  private val user = mutableStateOf<FirebaseUser?>(null)

  private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
    // callback after service
    if (result.resultCode == RESULT_OK) {
      // Successfully signed in
      user.value = FirebaseAuth.getInstance().currentUser
    } else {
      print("The authentication failed") // make this a pop-up ?
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // Initialize the signInLauncher
    signInLauncher =
      registerForActivityResult(FirebaseAuthUIActivityResultContract()) { res ->
        this.onSignInResult(res)
      }

    setContent { SkySyncTheme { LoginScreen(signInLauncher = signInLauncher, user = user.value) } }
  }

}
