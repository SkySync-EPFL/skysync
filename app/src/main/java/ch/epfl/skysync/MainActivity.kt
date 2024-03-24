package ch.epfl.skysync

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.mutableStateOf
import androidx.fragment.app.Fragment
import ch.epfl.skysync.databinding.ActivityMainBinding
import ch.epfl.skysync.screens.LoginScreen
import ch.epfl.skysync.ui.theme.SkySyncTheme
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser



class MainActivity : AppCompatActivity() {
  private lateinit var signInLauncher: ActivityResultLauncher<Intent>
  private lateinit var binding : ActivityMainBinding
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
    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)

    binding.bottomNavigationView.setOnItemSelectedListener {
      when(it.itemId){
        R.id.home -> replaceFragment(Home())
        R.id.flight -> replaceFragment(Flight())
        R.id.chat -> replaceFragment(Chat())
        R.id.calendar -> replaceFragment(Calendar())
        else ->{

        }
      }
      true
    }

    // Initialize the signInLauncher
    signInLauncher =
      registerForActivityResult(FirebaseAuthUIActivityResultContract()) { res ->
        this.onSignInResult(res)
      }

    setContent { SkySyncTheme { LoginScreen(signInLauncher = signInLauncher, user = user.value) } }
  }

  private fun replaceFragment(fragment : Fragment){
    val fragmentManager = supportFragmentManager
    val fragmentTransaction = fragmentManager.beginTransaction()
    fragmentTransaction.replace(R.id.frame_layout,fragment)
    fragmentTransaction.commit()
  }
}
