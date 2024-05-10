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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import ch.epfl.skysync.components.CustomTopAppBar
import ch.epfl.skysync.components.forms.PauseField
import ch.epfl.skysync.components.forms.TimePickerField
import ch.epfl.skysync.components.forms.TitledInputTextField
import ch.epfl.skysync.components.forms.VehicleProblemField
import ch.epfl.skysync.components.forms.baseReportFields
import ch.epfl.skysync.models.flight.FinishedFlight
import ch.epfl.skysync.models.flight.Vehicle
import ch.epfl.skysync.models.reports.CrewReport
import ch.epfl.skysync.models.user.Crew
import ch.epfl.skysync.navigation.Route
import ch.epfl.skysync.ui.theme.lightOrange
import ch.epfl.skysync.util.bottleInputValidation
import ch.epfl.skysync.util.inputValidation
import java.time.Instant
import java.util.Date

@Composable
fun CrewReportScreen(navHostController: NavHostController, flight: FinishedFlight, crew: Crew) {
  val title = "Crew Report"
  Scaffold(topBar = { CustomTopAppBar(navController = navHostController, title = title) }) { padding
    ->
    Column(modifier = Modifier.fillMaxSize().padding(padding)) {
      val defaultPadding = 16.dp
      var littleChampagne by remember { mutableStateOf("") }
      var errorLittleChampagne by remember { mutableStateOf(false) }
      var bigChampagne by remember { mutableStateOf("") }
      var errorBigChampagne by remember { mutableStateOf(false) }
      var prestigeChampagne by remember { mutableStateOf("") }
      var errorPrestigeChampagne by remember { mutableStateOf(false) }
      var addProblem by remember { mutableStateOf(false) }
      var newProblem by remember { mutableStateOf("") }
      var newVehicle: Vehicle? by remember { mutableStateOf(null) }
      val vehicleProblem = remember { mutableMapOf<Vehicle, String>() }
      var takeoffTime by remember { mutableStateOf(Date.from(Instant.now())) }
      var landingTime by remember { mutableStateOf(Date.from(Instant.now())) }
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

      LazyColumn(modifier = Modifier.fillMaxSize().weight(1f).testTag("Crew Report LazyColumn")) {
        item {
          TitledInputTextField(
              title = "Number of little champagne bottles",
              value = littleChampagne,
              onValueChange = { value -> littleChampagne = value.filter { it.isDigit() } },
              padding = defaultPadding,
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
              isError = errorLittleChampagne,
              messageError = if (errorLittleChampagne) "Please enter a number" else "")
        }
        item {
          TitledInputTextField(
              title = "Number of big champagne bottles",
              value = bigChampagne,
              onValueChange = { value -> bigChampagne = value.filter { it.isDigit() } },
              padding = defaultPadding,
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
              isError = errorBigChampagne,
              messageError = if (errorBigChampagne) "Please enter a number" else "")
        }
        item {
          TitledInputTextField(
              title = "Number of prestige champagne bottles",
              value = prestigeChampagne,
              onValueChange = { value -> prestigeChampagne = value.filter { it.isDigit() } },
              padding = defaultPadding,
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
              isError = errorPrestigeChampagne,
              messageError = if (errorPrestigeChampagne) "Please enter a number" else "")
        }

        item {
          val startTimeTitle = "Effective time of start"
          TimePickerField(defaultPadding, startTimeTitle, takeoffTime) { takeoffTime = it }
        }

        item {
          val endTimeTitle = "Effective time of end"
          TimePickerField(defaultPadding, endTimeTitle, landingTime) { landingTime = it }
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
        baseReportFields(
            defaultPadding = defaultPadding,
            beginTime = beginTime,
            endTime = endTime,
            pauseDuration = pauseDuration,
            flight = flight,
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
            onComentsChange = { comments = it })
      }
      Divider()
      Button(
          modifier = Modifier.fillMaxWidth().padding(defaultPadding).testTag("Submit Button"),
          colors = ButtonDefaults.buttonColors(containerColor = lightOrange),
          onClick = {
            errorLittleChampagne = !bottleInputValidation(littleChampagne)
            errorBigChampagne = !bottleInputValidation(bigChampagne)
            errorPrestigeChampagne = !bottleInputValidation(prestigeChampagne)
            if (!inputValidation(errorLittleChampagne, errorBigChampagne, errorPrestigeChampagne)) {
              val vehicleProblems = vehicleProblem.toMap()
              CrewReport(
                  author = crew,
                  littleChampagne = littleChampagne.toInt(),
                  bigChampagne = bigChampagne.toInt(),
                  prestigeChampagne = prestigeChampagne.toInt(),
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

/*
@Preview
@Composable
fun PreviewCrewReportScreen() {
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
  val crew =
      Crew(
          "id",
          "firstname",
          "lastname",
          "email",
          AvailabilityCalendar(),
          FlightGroupCalendar(),
          setOf(RoleType.PILOT))
  CrewReportScreen(navHostController = navController, flight = flight, crew = crew)
}*/
