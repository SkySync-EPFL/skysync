package ch.epfl.skysync.screens.crewpilot

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import ch.epfl.skysync.components.FlightCard
import ch.epfl.skysync.components.LoadingComponent
import ch.epfl.skysync.components.Timer
import ch.epfl.skysync.models.flight.Flight
import ch.epfl.skysync.models.location.Location
import ch.epfl.skysync.models.location.LocationPoint
import ch.epfl.skysync.models.location.UserMetrics
import ch.epfl.skysync.models.user.User
import ch.epfl.skysync.navigation.BottomBar
import ch.epfl.skysync.ui.theme.lightOrange
import ch.epfl.skysync.ui.theme.lightViolet
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
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

@Composable
fun UserLocationMarker(location: Location, user: User) {
  return Marker(
      state = rememberMarkerState(position = location.point.latlng()), title = user.name())
}

@Composable
fun ShowFlightToStart(
    navController: NavHostController,
    flight: Flight?,
    onClick: (String) -> Unit
) {
  Scaffold(modifier = Modifier.fillMaxSize(), bottomBar = { BottomBar(navController) }) { padding ->
    // Renders the Google Map or a permission request message based on the permission status.
    Column(modifier = Modifier.fillMaxSize().padding(padding).testTag("FlightLaunch")) {
      Text(
          text = "Flight to be launched",
          style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
          modifier =
              Modifier.background(
                      color = lightViolet,
                      shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                  .fillMaxWidth()
                  .padding(
                      top = padding.calculateTopPadding() + 16.dp,
                      start = 16.dp,
                      end = 16.dp,
                      bottom = 16.dp),
          color = Color.White,
          textAlign = TextAlign.Center)

      Spacer(modifier = Modifier.height(16.dp))

      if (flight == null) {
        Text("no flight to be launched for the moment")
      } else {
        FlightCard(flight = flight) { onClick(it) }
      }
    }
  }
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
  val rawTime by inFlightViewModel.rawCounter.collectAsStateWithLifecycle()
  val currentTime by inFlightViewModel.counter.collectAsStateWithLifecycle()
  val flightIsStarted by inFlightViewModel.inFlight.collectAsStateWithLifecycle()
  val personalFlights by inFlightViewModel.confirmedFlights.collectAsStateWithLifecycle()
  val currentFlightId by inFlightViewModel.flightId.collectAsStateWithLifecycle()

  val currentLocations = inFlightViewModel.currentLocations.collectAsState().value

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

            inFlightViewModel.addLocation(Location(userId = uid, point = newLocation))

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
  if (!locationPermission.status.isGranted) {
    Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Access to location is required to use this feature.")
        Text("Please enable location permissions in settings.")
      }
    }
  } else if (personalFlights == null) {
    LoadingComponent(isLoading = true, onRefresh = {}) {}
  } else if (personalFlights!!.isEmpty()) {
    ShowFlightToStart(navController = navController, flight = null) {}
  } else if (currentFlightId == null) {
    ShowFlightToStart(navController, personalFlights!!.first()) {
      inFlightViewModel.setFlightId(it)
    }
  } else {
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
                      onStart = { inFlightViewModel.startFlight() },
                      onStop = { inFlightViewModel.stopFlight() },
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
        }
  }
}
