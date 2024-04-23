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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    val locationPermission =
        rememberPermissionState(android.Manifest.permission.ACCESS_FINE_LOCATION)

    // Provides access to the Fused Location Provider API.
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    // State holding the current location.
    var location by remember { mutableStateOf<LatLng?>(null) }

    // Remembers and controls the camera position state for the map.
    val cameraPositionState = rememberCameraPositionState()

    // Manages the state of the map marker.
    val markerState = location?.let { rememberMarkerState(position = it) }

    var speed by remember { mutableStateOf(0f) } // Speed in meters/second

    var altitude by remember { mutableStateOf(0.0) } // Altitude in meters

    var bearing by remember { mutableStateOf(0f) } // Direction in degrees

    var verticalSpeed by remember { mutableStateOf(0.0) } // Vertical speed in meters/second

    var previousAltitude by remember { mutableStateOf<Double?>(null) }
    var previousTime by remember { mutableStateOf<Long?>(null) }

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
                        val newLocation = LatLng(it.latitude, it.longitude)
                        if (location == null) { // Initial setup
                            cameraPositionState.position =
                                CameraPosition.fromLatLngZoom(newLocation, 13f)
                        }
                        location = newLocation
                        if (markerState != null) {
                            markerState.position = newLocation
                        } // Updates marker position on the map
                        speed = it.speed // Update speed
                        bearing = it.bearing // Update Bearing

                        val currentTime = System.currentTimeMillis()
                        previousAltitude?.let { prevAlt ->
                            verticalSpeed = if (previousTime != null) {
                                (it.altitude - prevAlt) / ((currentTime - previousTime!!) / 1000.0)
                            } else {
                                0.0
                            }
                        }
                        altitude = it.altitude
                        previousAltitude = it.altitude
                        previousTime = currentTime
                    }
                }
            }

        // Requests location updates if permission is granted.
        if (locationPermission.status.isGranted) {
            try {
                fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
            } catch (e: SecurityException) {
                // Exception handling if the permission was rejected.
                Log.e("FlightScreen", "Failed to request location updates", e)
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
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 32.dp, bottom = 88.dp),
                    contentAlignment = Alignment.BottomStart
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        FloatingActionButton(
                            onClick = {
                                // Moves the camera to the current location when clicked.
                                location
                                    ?.let { CameraUpdateFactory.newLatLngZoom(it, 13f) }
                                    ?.let { cameraPositionState.move(it) }
                            },
                            containerColor = lightOrange
                        ) {
                            Icon(Icons.Default.LocationOn, contentDescription = "Locate Me")
                        }
                        FloatingActionButton(
                            onClick = {
                                // Here is where you'd navigate to a new screen. For now, just log a
                                // message.
                                Log.d(
                                    "FlightScreen",
                                    "FloatingActionButton clicked. Implement navigation here."
                                )
                                // Example navigation call: navController.navigate("FlightInfos")
                            },
                            containerColor = lightOrange
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Flight infos",
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        },
        bottomBar = { BottomBar(navController) }) { padding ->
        // Renders the Google Map or a permission request message based on the permission status.
        if (locationPermission.status.isGranted && location != null) {
            GoogleMap(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .testTag("Map"),
                cameraPositionState = cameraPositionState
            ) {
                if (markerState != null) {
                    Marker(state = markerState, title = "Your Location", snippet = "You are here")
                }
            }
            Text(
                text =
                "X Speed: ${speed} m/s\nY Speed: ${verticalSpeed} m/s\nAltitude: ${altitude} m\nBearing: ${bearing} °",
                style = MaterialTheme.typography.bodyLarge,
                modifier =
                Modifier
                    .padding(top = 16.dp, start = 12.dp, end = 12.dp)
                    .background(color = Color.White, shape = RoundedCornerShape(8.dp))
                    .padding(6.dp)
            )
        } else {
            // Displays a message if location permission is denied.
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
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
