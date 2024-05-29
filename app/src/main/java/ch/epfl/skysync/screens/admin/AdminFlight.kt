package ch.epfl.skysync.screens.admin

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import ch.epfl.skysync.models.location.Location
import ch.epfl.skysync.models.location.LocationPoint
import ch.epfl.skysync.models.user.User
import ch.epfl.skysync.navigation.AdminBottomBar
import ch.epfl.skysync.ui.theme.lightOrange
import ch.epfl.skysync.viewmodel.InFlightViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun UserLocationMarker(location: Location, user: User) {
  return Marker(state = MarkerState(position = location.point.latlng()), title = user.name())
}

/**
 * This composable function renders a screen with a map that shows the user's current location. It
 * requests and checks location permissions, updates location in real-time, and handles permissions
 * denial.
 *
 * @param navController Navigation controller for navigating between composables.
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun AdminFlightScreen(
    navController: NavHostController,
    inFlightViewModel: InFlightViewModel,
) {

  val currentLocations by inFlightViewModel.currentLocations.collectAsStateWithLifecycle()
  val flightLocations by inFlightViewModel.flightLocations.collectAsStateWithLifecycle()
  val locationPermission = rememberPermissionState(android.Manifest.permission.ACCESS_FINE_LOCATION)

  // State holding the current location initialized to Lausanne as default value.
  val defaultLocation = LocationPoint(0, 46.516, 6.63282)

  val cameraPositionState = rememberCameraPositionState {
    position = CameraPosition.fromLatLngZoom(defaultLocation.latlng(), 13f)
  }

  if (!locationPermission.status.isGranted) {
    Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Access to location is required to use this feature.")
        Text("Please enable location permissions in settings.")
      }
    }
  } else {
    Scaffold(modifier = Modifier.fillMaxSize(), bottomBar = { AdminBottomBar(navController) }) {
        padding ->
      GoogleMap(
          modifier = Modifier.fillMaxSize().padding(padding).testTag("Map"),
          cameraPositionState = cameraPositionState) {
            Polyline(
                points = flightLocations.map { it.point.latlng() }, color = Color.Red, width = 5f)
            currentLocations.values.forEach { (user, location) ->
              UserLocationMarker(location, user)
            }
          }
      Box(modifier = Modifier.fillMaxSize().padding(padding)) {
        Button(
            modifier =
                Modifier.align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(bottom = 20.dp, start = 60.dp, end = 60.dp),
            onClick = {
              inFlightViewModel.quitDisplayFlightTrace()
              navController.popBackStack()
            },
            colors = ButtonDefaults.buttonColors(containerColor = lightOrange)) {
              Text(text = "Quit display")
            }
      }
    }
  }
}
