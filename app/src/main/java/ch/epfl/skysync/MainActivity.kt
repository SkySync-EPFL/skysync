package ch.epfl.skysync

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.material3.Scaffold
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import ch.epfl.skysync.components.GlobalSnackbarHost
import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.navigation.MainGraph
import ch.epfl.skysync.ui.theme.SkySyncTheme
import ch.epfl.skysync.viewmodel.InFlightViewModel
import ch.epfl.skysync.viewmodel.NotificationViewModel
import ch.epfl.skysync.viewmodel.UserGlobalViewModel
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import android.provider.Settings

class MainActivity : ComponentActivity() {
    private lateinit var signInLauncher: ActivityResultLauncher<Intent>
    private val db: FirestoreDatabase = FirestoreDatabase()
    private val repository: Repository = Repository(db)
    private var userGlobalViewModel: UserGlobalViewModel? = null

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val vm = userGlobalViewModel ?: return
        if (result.resultCode == RESULT_OK) {
            val user = FirebaseAuth.getInstance().currentUser!!
            vm.loadUser(user.uid, user.email!!)
        }
    }

    private fun requestNotificationPermission() {
        // Check if the POST_NOTIFICATIONS permission is granted
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED) {

            // Request the permission
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1001)
        }
    }

    private fun checkNotificationEnabled() {
        val notificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (!notificationManager.areNotificationsEnabled()) {
            // Prompt user to enable notifications
            val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
            startActivity(intent)
        }
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the signInLauncher here
        signInLauncher = registerForActivityResult(FirebaseAuthUIActivityResultContract()) { res ->
            onSignInResult(res)
        }

        // Request notification permission
        requestNotificationPermission()

        // Check if notifications are enabled
        checkNotificationEnabled()

        setContent {
            SkySyncTheme {
                val navController = rememberNavController()
                Scaffold(snackbarHost = { GlobalSnackbarHost() }) {
                    val notificationViewModel: NotificationViewModel = viewModel()
                    userGlobalViewModel = UserGlobalViewModel.createViewModel(repository, notificationViewModel)
                    val inFlightViewModel = InFlightViewModel.createViewModel(repository)

                    MainGraph(
                        repository = repository,
                        navHostController = navController,
                        signInLauncher = signInLauncher,
                        userGlobalViewModel = userGlobalViewModel!!,
                        inFlightsViewModel = inFlightViewModel
                    )
                }
            }
        }
    }

    // Handle the result of the permission request
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1001) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // Permission granted, you can send notifications
            } else {
                // Permission denied, inform the user about the importance of the permission
            }
        }
    }
}
