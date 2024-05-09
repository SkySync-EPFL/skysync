package ch.epfl.skysync.screens.reports

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import ch.epfl.skysync.components.CustomTopAppBar
import ch.epfl.skysync.components.forms.LocationPickerField
import ch.epfl.skysync.components.forms.PauseField
import ch.epfl.skysync.components.forms.TimePickerField
import ch.epfl.skysync.components.forms.TitledInputTextField
import ch.epfl.skysync.components.forms.VehicleProblemField
import ch.epfl.skysync.models.UNSET_ID
import ch.epfl.skysync.models.calendar.AvailabilityCalendar
import ch.epfl.skysync.models.calendar.FlightGroupCalendar
import ch.epfl.skysync.models.calendar.TimeSlot
import ch.epfl.skysync.models.flight.BASE_ROLES
import ch.epfl.skysync.models.flight.Balloon
import ch.epfl.skysync.models.flight.BalloonQualification
import ch.epfl.skysync.models.flight.Basket
import ch.epfl.skysync.models.flight.FinishedFlight
import ch.epfl.skysync.models.flight.FlightType
import ch.epfl.skysync.models.flight.Role
import ch.epfl.skysync.models.flight.RoleType
import ch.epfl.skysync.models.flight.Team
import ch.epfl.skysync.models.flight.Vehicle
import ch.epfl.skysync.models.location.LocationPoint
import ch.epfl.skysync.models.reports.PilotReport
import ch.epfl.skysync.models.user.Pilot
import ch.epfl.skysync.navigation.Route
import ch.epfl.skysync.ui.theme.lightOrange
import ch.epfl.skysync.util.inputValidation
import ch.epfl.skysync.util.nbPassengerInputValidation
import java.time.Instant
import java.time.LocalDate
import java.util.Date

@Composable
fun PilotReportScreen(flight: FinishedFlight, navHostController: NavHostController, pilot: Pilot) {
  val title = "Pilot Report"
  Scaffold(topBar = { CustomTopAppBar(navController = navHostController, title = title) }) { padding
    ->
    Column(modifier = Modifier.fillMaxSize().padding(padding)) {
      val defaultPadding = 16.dp
      var pax by remember { mutableStateOf(flight.nPassengers.toString()) }
      var errorPax by remember { mutableStateOf(false) }
      var addProblem by remember { mutableStateOf(false) }
      var newProblem by remember { mutableStateOf("") }
      var newVehicle: Vehicle? by remember { mutableStateOf(null) }
      val vehicleProblem = remember { mutableMapOf<Vehicle, String>() }
      var takeoffTime by remember { mutableStateOf(Date.from(Instant.now())) }
      var takeoffLocation by remember { mutableStateOf(flight.takeOffLocation) }
      var landingTime by remember { mutableStateOf(Date.from(Instant.now())) }
      var landingLocation by remember { mutableStateOf(flight.takeOffLocation) }
      var beginTime by remember { mutableStateOf(flight.takeOffTime) }
      var endTime by remember { mutableStateOf(flight.takeOffTime) }
      var pauseDuration by remember { mutableLongStateOf(0L) }
      var comments by remember { mutableStateOf("") }

      LaunchedEffect(addProblem) {
        if (addProblem) {
          vehicleProblem[newVehicle!!] = newProblem
          addProblem = false
        }
      }

      LazyColumn(modifier = Modifier.fillMaxSize().weight(1f).testTag("Pilot Report LazyColumn")) {
        item {
          TitledInputTextField(
              title = "Number of passengers",
              value = pax,
              onValueChange = { value -> pax = value.filter { it.isDigit() } },
              padding = defaultPadding,
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
              isError = errorPax,
              messageError = if (errorPax) "Please enter a valid number of passengers" else "")
        }

        item {
          val takeoffTimeTitle = "Takeoff time"
          TimePickerField(defaultPadding, takeoffTimeTitle, takeoffTime) { takeoffTime = it }
        }

        item {
          val takeoffLocationTitle = "Takeoff location"
          LocationPickerField(takeoffLocation, defaultPadding, takeoffLocationTitle) {
            takeoffLocation = it
          }
        }

        item {
          val landingTimeTitle = "Landing time"
          TimePickerField(defaultPadding, landingTimeTitle, landingTime) { landingTime = it }
        }

        item {
          val landingLocationTitle = "Landing location"
          LocationPickerField(landingLocation, defaultPadding, landingLocationTitle) {
            landingLocation = it
          }
        }

        item {
          val beginTimeTitle = "Effective time of start"
          TimePickerField(defaultPadding, beginTimeTitle, beginTime) { beginTime = it }
        }

        item {
          val endTimeTitle = "Effective time of end"
          TimePickerField(defaultPadding, endTimeTitle, endTime) { endTime = it }
        }

        item { PauseField(defaultPadding, pauseDuration) { pauseDuration = it } }

        item {
          VehicleProblemField(
              defaultPadding,
              flight.vehicles,
              onConfirm = { vehicle, problem ->
                newVehicle = vehicle
                newProblem = problem
                addProblem = true
              })
        }
        items(vehicleProblem.keys.toList()) { vehicle ->
          Text(
              modifier = Modifier.fillMaxWidth().padding(horizontal = defaultPadding),
              text = vehicle.name,
              style = MaterialTheme.typography.headlineSmall)
          OutlinedTextField(
              value = vehicleProblem[vehicle] ?: "",
              onValueChange = { vehicleProblem[vehicle] = it },
              modifier = Modifier.fillMaxWidth().padding(horizontal = defaultPadding),
          )
        }

        item {
          TitledInputTextField(
              title = "Comments",
              value = comments,
              onValueChange = { comments = it },
              padding = defaultPadding)
        }
      }
      Divider()
      Button(
          modifier = Modifier.fillMaxWidth().padding(defaultPadding).testTag("Submit Button"),
          colors = ButtonDefaults.buttonColors(containerColor = lightOrange),
          onClick = {
            errorPax = !nbPassengerInputValidation(pax)
            if (!inputValidation(errorPax)) {
              val vehicleProblems = vehicleProblem.toMap()
              PilotReport(
                  author = pilot,
                  effectivePax = pax.toInt(),
                  takeOffTime = takeoffTime,
                  takeOffLocation = takeoffLocation,
                  landingLocation = landingLocation,
                  landingTime = landingTime,
                  begin = beginTime,
                  end = endTime,
                  pauseDuration = pauseDuration,
                  comments = comments,
                  vehicleProblems = vehicleProblems)
              // TODO save report
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

@Preview
@Composable
fun PilotReportScreenPreview() {
  val navController = rememberNavController()
  val flight =
      FinishedFlight(
          id = UNSET_ID,
          nPassengers = 1,
          team = Team(Role.initRoles(BASE_ROLES)),
          flightType = FlightType.HIGH_ALTITUDE,
          balloon = Balloon("name", BalloonQualification.MEDIUM),
          basket = Basket("name", true),
          date = LocalDate.now(),
          timeSlot = TimeSlot.AM,
          vehicles = listOf(Vehicle("vehicle1"), Vehicle("vehicle2")),
          takeOffTime = Date.from(Instant.now()),
          takeOffLocation =
              LocationPoint(time = 0, latitude = 0.0, longitude = 0.0, name = "test1"),
          landingTime = Date.from(Instant.now()),
          landingLocation =
              LocationPoint(time = 0, latitude = 1.0, longitude = 1.0, name = "test2"),
          flightTime = 10L)
  val pilot =
      Pilot(
          "id",
          "firstname",
          "lastname",
          "email",
          AvailabilityCalendar(),
          FlightGroupCalendar(),
          setOf(RoleType.PILOT),
          BalloonQualification.MEDIUM)
  PilotReportScreen(flight, navHostController = navController, pilot)
}
