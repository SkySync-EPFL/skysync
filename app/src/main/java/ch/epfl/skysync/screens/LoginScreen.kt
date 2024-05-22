package ch.epfl.skysync.screens

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ch.epfl.skysync.R
import ch.epfl.skysync.components.LoadingComponent
import ch.epfl.skysync.ui.theme.deepOrange
import ch.epfl.skysync.viewmodel.UserGlobalViewModel
import com.firebase.ui.auth.AuthUI

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    userGlobalViewModel: UserGlobalViewModel,
    signInLauncher: ActivityResultLauncher<Intent>
) {
  val isLoading by userGlobalViewModel.isLoading.collectAsStateWithLifecycle()

  val providers =
      listOf(
          // Google sign-in
          AuthUI.IdpConfig.GoogleBuilder().build())

  val signInIntent =
      AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers).build()

  if (isLoading) {
    LoadingComponent(isLoading = true, onRefresh = {}) {}
  } else {
    Surface(modifier = Modifier.fillMaxSize()) {
      Column(
          modifier = Modifier.fillMaxSize().padding(16.dp).testTag("LoginScreen"),
          horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(Modifier.fillMaxSize(0.09f))
            Image(
                painter = painterResource(id = R.drawable.logo_signin),
                contentDescription = "",
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxWidth().fillMaxHeight(0.31f))
            Spacer(Modifier.fillMaxSize(0.12f))
            Text(
                text = "Welcome", style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 64.sp))
            Spacer(Modifier.fillMaxSize(0.09f))
            val onClick = { signInLauncher.launch(signInIntent) }
            Surface(
                color = Color.White,
                border = BorderStroke(2.dp, deepOrange),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.padding(16.dp)) {
                  Button(
                      onClick = onClick,
                      modifier =
                          Modifier.background(color = Color.White)
                              .fillMaxWidth(0.7f)
                              .testTag("LoginButton"),
                      colors = ButtonDefaults.buttonColors(Color.White)) {
                        Text(
                            text = "Authenticate with Google",
                            color = deepOrange,
                            fontSize = 15.sp,
                            style = TextStyle(fontWeight = FontWeight.SemiBold),
                            textAlign = TextAlign.Center,
                        )
                      }
                }
            Text(text = "You need to log in")
          }
    }
  }
}
