package ch.epfl.skysync.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import ch.epfl.skysync.components.Timer
import ch.epfl.skysync.models.location.Location
import ch.epfl.skysync.models.location.LocationPoint
import ch.epfl.skysync.models.user.User
import ch.epfl.skysync.navigation.BottomBar
import ch.epfl.skysync.ui.theme.lightOrange
import ch.epfl.skysync.viewmodel.LocationViewModel
import ch.epfl.skysync.viewmodel.TimerViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

data class UserMetrics(
    val speed: Float,
    val altitude: Double,
    val bearing: Float,
    val verticalSpeed: Double,
    val location: LocationPoint,
) {
  fun withUpdate(
      speed: Float,
      altitude: Double,
      bearing: Float,
      location: LocationPoint,
  ): UserMetrics {
    var verticalSpeed = (altitude - this.altitude) / (location.time - this.location.time)
    if (!verticalSpeed.isFinite()) {
      verticalSpeed = 0.0
    }
    return UserMetrics(speed, altitude, bearing, verticalSpeed, location)
  }

  override fun toString(): String {
    return "Horizontal Speed: %.2f m/s\nVertical Speed: %.2f m/s\nAltitude: %.0f m\nBearing: %.2f °"
        .format(speed, verticalSpeed, altitude, bearing)
  }
}

@Composable
fun UserLocationMarker(location: Location, user: User) {
  return Marker(state = rememberMarkerState(position = location.data.latlng()), title = user.name())
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
fun FlightScreen(
    navController: NavHostController,
    timer: TimerViewModel,
    locationViewModel: LocationViewModel,
    uid: String
) {
  val rawTime by timer.rawCounter.collectAsStateWithLifecycle()
  val currentTime by timer.counter.collectAsStateWithLifecycle()
  val flightIsStarted by timer.isRunning.collectAsStateWithLifecycle()

  val currentLocations = locationViewModel.currentLocations.collectAsState().value

  val locationPermission = rememberPermissionState(android.Manifest.permission.ACCESS_FINE_LOCATION)
  val fusedLocationClient = LocationServices.getFusedLocationProviderClient(LocalContext.current)

  // State holding the current location initialized to Lausanne as default value.
  val defaultLocation = LocationPoint(0, 46.516, 6.63282)

  val cameraPositionState = rememberCameraPositionState {
    position = CameraPosition.fromLatLngZoom(defaultLocation.latlng(), 13f)
  }
  val markerState = rememberMarkerState(position = defaultLocation.latlng())

  var metrics by remember { mutableStateOf(UserMetrics(0.0f, 0.0, 0.0f, 0.0, defaultLocation)) }

  val locationCallback =
      object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
          locationResult.lastLocation?.let {
            val newLocation =
                LocationPoint(
                    time = (rawTime / 1000).toInt(),
                    latitude = it.latitude,
                    longitude = it.longitude,
                )

            locationViewModel.addLocation(Location(userId = uid, data = newLocation))

            markerState.position = newLocation.latlng()
            metrics = metrics.withUpdate(it.speed, it.altitude, it.bearing, newLocation)
          }
        }
      }

  DisposableEffect(locationPermission) {
    // Defines the location request parameters.
    val locationRequest =
        LocationRequest.create().apply {
          interval = 2000
          fastestInterval = 2000
        }

    // Requests location updates if permission is granted.
    if (locationPermission.status.isGranted) {
      try {
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
      } catch (e: SecurityException) {
        Log.e("FlightScreen", "Failed to request location updates", e)
      }
    } else {
      locationPermission.launchPermissionRequest() // Request permission if not granted
    }

    // Cleanup function to stop receiving location updates when the composable is disposed.
    onDispose { fusedLocationClient.removeLocationUpdates(locationCallback) }
  }

  Scaffold(
      modifier = Modifier.fillMaxSize(),
      floatingActionButton = {
        if (locationPermission.status.isGranted) {
          Box(
              modifier =
                  Modifier.fillMaxSize().padding(start = 32.dp, bottom = 88.dp, top = 100.dp),
              contentAlignment = Alignment.BottomStart) {
                Timer(
                    Modifier.align(Alignment.TopEnd).testTag("Timer"),
                    currentTimer = currentTime,
                    isRunning = flightIsStarted,
                    onStart = { timer.start() },
                    onStop = { timer.stop() },
                )

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()) {
                      FloatingActionButton(
                          onClick = {
                            // Moves the camera to the current location when clicked.
                            metrics
                                .let {
                                  CameraUpdateFactory.newLatLngZoom(it.location.latlng(), 13f)
                                }
                                .let { cameraPositionState.move(it) }
                          },
                          containerColor = lightOrange) {
                            Icon(Icons.Default.LocationOn, contentDescription = "Locate Me")
                          }
                      FloatingActionButton(
                          onClick = {
                            // Here is where you'd navigate to a new screen. For now, just log a
                            // message.
                            Log.d(
                                "FlightScreen",
                                "FloatingActionButton clicked. Implement navigation here.")
                            // Example navigation call: navController.navigate("FlightInfos")
                          },
                          containerColor = lightOrange) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Flight infos",
                                tint = Color.White)
                          }
                    }
              }
        }
      },
      bottomBar = { BottomBar(navController) }) { padding ->
        // Renders the Google Map or a permission request message based on the permission status.
        if (locationPermission.status.isGranted) {
          GoogleMap(
              modifier = Modifier.fillMaxSize().padding(padding).testTag("Map"),
              cameraPositionState = cameraPositionState) {
                Marker(state = markerState, title = "Your Location", snippet = "You are here")

                currentLocations.values.forEach { (user, location) ->
                  UserLocationMarker(location, user)
                }
              }
          Text(
              text = "$metrics",
              style = MaterialTheme.typography.bodyLarge,
              modifier =
                  Modifier.padding(top = 16.dp, start = 12.dp, end = 12.dp)
                      .background(color = Color.White, shape = RoundedCornerShape(8.dp))
                      .padding(6.dp))
        }
        if (!locationPermission.status.isGranted) {
          Box(
              modifier = Modifier.fillMaxSize().padding(16.dp),
              contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                  Text("Access to location is required to use this feature.")
                  Text("Please enable location permissions in settings.")
                }
              }
        }
      }
}
/*
@Composable
@Preview
fun FlightScreenPreview() {
  val navController = rememberNavController()
    val db: FirestoreDatabase = FirestoreDatabase()
    val repository: Repository = Repository(db)
    val locationViewModel =
        LocationViewModel.createViewModel(repository)
  FlightScreen(navController = navController, timer = TimerViewModel(), locationViewModel = locationViewModel, uid)
}
*/
