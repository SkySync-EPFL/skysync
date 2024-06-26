package ch.epfl.skysync.screens.reports

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import ch.epfl.skysync.components.CustomTopAppBar
import ch.epfl.skysync.components.LoadingComponent
import ch.epfl.skysync.components.forms.LocationPickerField
import ch.epfl.skysync.components.forms.ModifyVehicleProblem
import ch.epfl.skysync.components.forms.TimePickerField
import ch.epfl.skysync.components.forms.TitledInputTextField
import ch.epfl.skysync.components.forms.baseReportFields
import ch.epfl.skysync.models.flight.Vehicle
import ch.epfl.skysync.models.reports.PilotReport
import ch.epfl.skysync.navigation.Route
import ch.epfl.skysync.ui.theme.lightOrange
import ch.epfl.skysync.util.hasError
import ch.epfl.skysync.util.nbPassengerInputValidation
import ch.epfl.skysync.viewmodel.FinishedFlightsViewModel
import java.time.Instant
import java.util.Date

/**
 * The screen to add a pilot report
 *
 * @param navHostController The navigation controller
 * @param finishedFlightsViewModel The view model
 * @param flightId The flight id
 */
@Composable
fun PilotReportScreen(
    navHostController: NavHostController,
    finishedFlightsViewModel: FinishedFlightsViewModel,
    flightId: String
) {
  val title = "Pilot Report"
  val pilot = finishedFlightsViewModel.currentUser.collectAsStateWithLifecycle()
  val flight = finishedFlightsViewModel.getFlight(flightId).collectAsStateWithLifecycle()
  Scaffold(topBar = { CustomTopAppBar(navController = navHostController, title = title) }) { padding
    ->
    Column(modifier = Modifier.fillMaxSize().padding(padding)) {
      if (pilot.value == null || flight.value == null) {
        LoadingComponent(isLoading = true, onRefresh = { /*TODO*/}) {}
      } else {
        val defaultPadding = 16.dp
        var pax by remember { mutableStateOf(flight.value!!.nPassengers.toString()) }
        var errorPax by remember { mutableStateOf(false) }
        var addProblem by remember { mutableStateOf(false) }
        var newProblem by remember { mutableStateOf("") }
        var newVehicle: Vehicle? by remember { mutableStateOf(null) }
        val vehicleProblem = remember { mutableMapOf<Vehicle, String>() }
        var takeoffTime by remember { mutableStateOf(Date.from(Instant.now())) }
        var takeoffLocation by remember { mutableStateOf(flight.value!!.takeOffLocation) }
        var landingTime by remember { mutableStateOf(Date.from(Instant.now())) }
        var landingLocation by remember { mutableStateOf(flight.value!!.takeOffLocation) }
        var beginTime by remember { mutableStateOf(flight.value!!.takeOffTime) }
        var endTime by remember { mutableStateOf(flight.value!!.takeOffTime) }
        var pauseDuration by remember { mutableLongStateOf(0L) }
        var comments by remember { mutableStateOf("") }

        ModifyVehicleProblem(addProblem = addProblem, vehicle = newVehicle, problem = newProblem) {
            vehicle,
            problem ->
          vehicleProblem[vehicle] = problem
          addProblem = false
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().weight(1f).testTag("Pilot Report LazyColumn")) {
              item {
                TitledInputTextField(
                    title = "Number of passengers",
                    value = pax,
                    onValueChange = { value -> pax = value.filter { it.isDigit() } },
                    padding = defaultPadding,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = errorPax,
                    messageError =
                        if (errorPax) "Please enter a valid number of passengers" else "")
              }

              item {
                val takeoffTimeTitle = "Takeoff time"
                TimePickerField(defaultPadding, takeoffTimeTitle, takeoffTime) { takeoffTime = it }
              }

              item {
                val takeoffLocationTitle = "Takeoff location"
                LocationPickerField(
                    location = takeoffLocation,
                    defaultPadding = defaultPadding,
                    title = takeoffLocationTitle,
                    viewModel = finishedFlightsViewModel) {
                      takeoffLocation = it
                    }
              }

              item {
                val landingTimeTitle = "Landing time"
                TimePickerField(defaultPadding, landingTimeTitle, landingTime) { landingTime = it }
              }

              item {
                val landingLocationTitle = "Landing location"
                LocationPickerField(
                    location = takeoffLocation,
                    defaultPadding = defaultPadding,
                    title = landingLocationTitle,
                    viewModel = finishedFlightsViewModel) {
                      takeoffLocation = it
                    }
              }

              baseReportFields(
                  defaultPadding = defaultPadding,
                  beginTime = beginTime,
                  endTime = endTime,
                  pauseDuration = pauseDuration,
                  flight = flight.value!!,
                  onConfirm = { vehicle, problem ->
                    newVehicle = vehicle
                    newProblem = problem
                    addProblem = true
                  },
                  vehicleProblem = vehicleProblem,
                  comments = comments,
                  onBeginTimeChange = { beginTime = it },
                  onEndTimeChange = { endTime = it },
                  onPauseDurationChange = { pauseDuration = it },
                  onCommentsChange = { comments = it })
            }
        Divider()
        Button(
            modifier = Modifier.fillMaxWidth().padding(defaultPadding).testTag("Submit Button"),
            colors = ButtonDefaults.buttonColors(containerColor = lightOrange),
            onClick = {
              errorPax = !nbPassengerInputValidation(pax)
              if (!hasError(errorPax)) {
                val report =
                    PilotReport(
                        author = pilot.value!!.id,
                        effectivePax = pax.toInt(),
                        takeOffTime = takeoffTime,
                        takeOffLocation = takeoffLocation,
                        landingLocation = landingLocation,
                        landingTime = landingTime,
                        begin = beginTime,
                        end = endTime,
                        pauseDuration = pauseDuration.toInt(),
                        comments = comments,
                    )
                finishedFlightsViewModel.addReport(report, flightId)
                navHostController.navigate(Route.CREW_HOME) {
                  popUpTo(Route.CREW_HOME) { inclusive = true }
                }
              }
            }) {
              Text("Submit")
            }
      }
    }
  }
}
