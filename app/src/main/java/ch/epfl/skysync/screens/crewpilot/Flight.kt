package ch.epfl.skysync.screens.crewpilot

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
import ch.epfl.skysync.models.location.UserMetrics
import ch.epfl.skysync.models.user.User
import ch.epfl.skysync.navigation.BottomBar
import ch.epfl.skysync.navigation.Route
import ch.epfl.skysync.ui.theme.lightOrange
import ch.epfl.skysync.viewmodel.InFlightViewModel
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
fun FlightScreen(
    navController: NavHostController,
    inFlightViewModel: InFlightViewModel,
    uid: String
) {
  val loading by inFlightViewModel.loading.collectAsStateWithLifecycle()
  val rawTime by inFlightViewModel.rawCounter.collectAsStateWithLifecycle()
  val currentTime by inFlightViewModel.counter.collectAsStateWithLifecycle()
  val flightStage by inFlightViewModel.flightStage.collectAsStateWithLifecycle()
  val startableFlight by inFlightViewModel.startableFlight.collectAsStateWithLifecycle()
  val currentFlight by inFlightViewModel.currentFlight.collectAsStateWithLifecycle()

  val currentLocations by inFlightViewModel.currentLocations.collectAsStateWithLifecycle()
  val flightLocations by inFlightViewModel.flightLocations.collectAsStateWithLifecycle()
  val locationPermission = rememberPermissionState(android.Manifest.permission.ACCESS_FINE_LOCATION)
  val fusedLocationClient = LocationServices.getFusedLocationProviderClient(LocalContext.current)

  // State holding the current location initialized to Lausanne as default value.
  val defaultLocation = LocationPoint(0, 46.516, 6.63282)

  val cameraPositionState = rememberCameraPositionState {
    position = CameraPosition.fromLatLngZoom(defaultLocation.latlng(), 13f)
  }

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

            inFlightViewModel.addLocation(Location(userId = uid, point = newLocation))
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

  if (!locationPermission.status.isGranted) {
    Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Access to location is required to use this feature.")
        Text("Please enable location permissions in settings.")
      }
    }
  } else {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
          // only display the action buttons for ongoing flight not for flight trace display
          if (locationPermission.status.isGranted) {
            Box(
                modifier =
                    Modifier.fillMaxSize().padding(start = 32.dp, bottom = 88.dp, top = 100.dp),
                contentAlignment = Alignment.BottomStart) {
                  Timer(
                      Modifier.align(Alignment.TopEnd).testTag("Timer"),
                      currentTimer = currentTime,
                      flightStage = flightStage,
                      isPilot = inFlightViewModel.isPilot(),
                      onStart = { inFlightViewModel.startFlight() },
                      onStop = { inFlightViewModel.stopFlight() },
                      onClear = {
                        inFlightViewModel.clearFlight()
                        navController.navigate(Route.CREW_HOME)
                      },
                      onQuitDisplay = {
                        inFlightViewModel.quitDisplayFlightTrace()
                        navController.popBackStack()
                      })

                  Row(
                      horizontalArrangement = Arrangement.SpaceBetween,
                      modifier = Modifier.fillMaxWidth()) {
                        // actions disabled on flight trace display
                        if (inFlightViewModel.isDisplayTrace()) return@Row
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
          if (locationPermission.status.isGranted) {
            GoogleMap(
                modifier = Modifier.fillMaxSize().padding(padding).testTag("Map"),
                cameraPositionState = cameraPositionState) {
                  Polyline(
                      points = flightLocations.map { it.point.latlng() },
                      color = Color.Red,
                      width = 5f)
                  currentLocations.values.forEach { (user, location) ->
                    UserLocationMarker(location, user)
                  }
                }
            if (!inFlightViewModel.isDisplayTrace()) {
              Text(
                  text = "$metrics",
                  style = MaterialTheme.typography.bodyLarge,
                  modifier =
                      Modifier.padding(top = 16.dp, start = 12.dp, end = 12.dp)
                          .background(color = Color.White, shape = RoundedCornerShape(8.dp))
                          .padding(6.dp))
            }
          }
        }
  }
}
