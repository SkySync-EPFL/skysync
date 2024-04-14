package ch.epfl.skysync.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import ch.epfl.skysync.navigation.BottomBar
import ch.epfl.skysync.ui.theme.lightOrange
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

/**
 * This composable function renders a screen with a map that shows the user's current location. It
 * requests and checks location permissions, updates location in real-time, and handles permissions
 * denial.
 *
 * @param navController Navigation controller for navigating between composables.
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun FlightScreen(navController: NavHostController) {
  // Access to the application context.
  val context = LocalContext.current

  // Manages the state of location permissions.
  val locationPermission = rememberPermissionState(android.Manifest.permission.ACCESS_FINE_LOCATION)

  // Provides access to the Fused Location Provider API.
  val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

  // State holding the current location initialized to a default value.
  var location by remember { mutableStateOf(LatLng(-33.852, 151.211)) }

  // Remembers and controls the camera position state for the map.
  val cameraPositionState = rememberCameraPositionState {
    position = CameraPosition.fromLatLngZoom(location, 12f)
  }

  // Manages the state of the map marker.
  val markerState = rememberMarkerState(position = location)

  // DisposableEffect to handle location updates and permissions.
  DisposableEffect(locationPermission) {
    // Defines the location request parameters.
    val locationRequest =
        LocationRequest.create().apply {
          interval = 5000 // Interval for location updates.
          fastestInterval = 2000 // Fastest interval for location updates.
        }

    // Callback to receive location updates.
    val locationCallback =
        object : LocationCallback() {
          override fun onLocationResult(locationResult: LocationResult) {
            locationResult.lastLocation?.let {
              location = LatLng(it.latitude, it.longitude)
              markerState.position = location // Updates marker position on the map.
            }
          }
        }

    // Requests location updates if permission is granted.
    if (locationPermission.status.isGranted) {
      try {
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
      } catch (e: SecurityException) {
        // Exception handling if the permission was rejected.
      }
    }

    // Cleanup function to stop receiving location updates when the composable is disposed.
    onDispose { fusedLocationClient.removeLocationUpdates(locationCallback) }
  }

  Scaffold(
      modifier = Modifier.fillMaxSize(),
      floatingActionButton = {
        // Floating action button to center the map on the current location.
        if (locationPermission.status.isGranted) {
          Box(
              modifier = Modifier.fillMaxSize().padding(start = 32.dp, bottom = 16.dp),
              contentAlignment = Alignment.BottomStart) {
                FloatingActionButton(
                    onClick = {
                      // Moves the camera to the current location when clicked.
                      cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(location, 12f))
                    },
                    containerColor = lightOrange) {
                      Icon(Icons.Default.LocationOn, contentDescription = "Locate Me")
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
              }
        } else {
          // Displays a message if location permission is denied.
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

@Composable
@Preview
fun FlightScreenPreview() {
  val navController = rememberNavController()
  FlightScreen(navController = navController)
}
