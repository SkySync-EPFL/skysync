package ch.epfl.skysync.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import ch.epfl.skysync.models.calendar.TimeSlot
import ch.epfl.skysync.models.flight.Balloon
import ch.epfl.skysync.models.flight.BalloonQualification
import ch.epfl.skysync.models.flight.Basket
import ch.epfl.skysync.models.flight.FlightType
import ch.epfl.skysync.models.flight.PlannedFlight
import ch.epfl.skysync.models.flight.Vehicle
import ch.epfl.skysync.navigation.Route
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFlightScreen(navController: NavHostController, flights: MutableList<PlannedFlight>) {
  Scaffold(
      modifier = Modifier.fillMaxSize(),
      topBar = {
        TopAppBar(
            title = { Text("Add Flight") },
            navigationIcon = {
              IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
              }
            })
      }) { padding ->
        Column {
          val defaultPadding = 16.dp

          var nbPassenger by remember { mutableStateOf("") }

          var openDatePicker by remember { mutableStateOf(false) }
          var date by remember { mutableStateOf(LocalDate.now()) }

          var flightType: FlightType? by remember { mutableStateOf(null) }
          var expandedFlightTypeMenu by remember { mutableStateOf(false) }

          var vehicle: Vehicle? by remember { mutableStateOf(null) }
          var expandedVehicleMenu by remember { mutableStateOf(false) }
          // List of all vehicles to be removed when connected to the database
          val allVehicles = listOf<Vehicle>(Vehicle("Car"), Vehicle("Bus"), Vehicle("Bike"))

          var timeSlot: TimeSlot by remember { mutableStateOf(TimeSlot.AM) }
          var expandedTimeSlot by remember { mutableStateOf(false) }

          var balloon: Balloon? by remember { mutableStateOf(null) }
          // List of all balloons to be removed when connected to the database
          val balloons: List<Balloon> =
              listOf(
                  Balloon("Balloon1", BalloonQualification.LARGE),
                  Balloon("Balloon2", BalloonQualification.MEDIUM),
                  Balloon("Balloon3", BalloonQualification.SMALL))
          var expandedBalloonMenu by remember { mutableStateOf(false) }

          var basket: Basket? by remember { mutableStateOf(null) }
          // List of all baskets to be removed when connected to the database
          val baskets: List<Basket> =
              listOf(
                  Basket("Basket1", hasDoor = false),
                  Basket("Basket2", hasDoor = true),
                  Basket("Basket3", hasDoor = true))
          var expandedBasketMenu by remember { mutableStateOf(false) }

          var fondueRole: String? by remember { mutableStateOf(null) }

          Column(
              modifier =
                  Modifier.padding(padding)
                      .weight(1f)
                      .testTag("Flight Lazy Column")
                      .verticalScroll(rememberScrollState()),
              verticalArrangement = Arrangement.SpaceBetween) {
                // Field getting the number of passengers. Only number can be entered

                OutlinedTextField(
                    value = nbPassenger,
                    onValueChange = { value -> nbPassenger = value.filter { it.isDigit() } },
                    placeholder = { Text("Number of passengers") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier =
                        Modifier.fillMaxWidth().padding(defaultPadding).testTag("nb Passenger"))

                // The date field is a clickable field that opens a date picker
                if (openDatePicker) {
                  val datePickerState =
                      rememberDatePickerState(
                          initialSelectedDateMillis = java.util.Calendar.getInstance().timeInMillis)

                  DatePickerDialog(
                      onDismissRequest = {},
                      confirmButton = {
                        TextButton(
                            onClick = {
                              openDatePicker = false
                              java.util.Calendar.getInstance().apply {
                                timeInMillis = datePickerState.selectedDateMillis!!
                                date =
                                    Instant.ofEpochMilli(timeInMillis)
                                        .atZone(ZoneId.of("GMT"))
                                        .toLocalDate()
                              }
                            }) {
                              Text("OK")
                            }
                      },
                      dismissButton = {
                        TextButton(onClick = { openDatePicker = false }) { Text("Cancel") }
                      }) {
                        DatePicker(state = datePickerState)
                      }
                }

                OutlinedTextField(
                    modifier =
                        Modifier.fillMaxWidth()
                            .padding(defaultPadding)
                            .clickable { openDatePicker = true }
                            .testTag("Date Field"),
                    enabled = false,
                    value =
                        String.format(
                            "%02d/%02d/%04d", date.dayOfMonth, date.monthValue, date.year),
                    onValueChange = {})

                // Drop down menu for the flight type
                ExposedDropdownMenuBox(
                    modifier =
                        Modifier.fillMaxWidth()
                            .padding(defaultPadding)
                            .clickable { expandedFlightTypeMenu = true }
                            .testTag("Flight Type Menu"),
                    expanded = expandedFlightTypeMenu,
                    onExpandedChange = { expandedFlightTypeMenu = !expandedFlightTypeMenu }) {
                      OutlinedTextField(
                          value = flightType?.name ?: "Flight Type",
                          modifier = Modifier.menuAnchor().fillMaxWidth(),
                          readOnly = true,
                          onValueChange = {},
                          trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = expandedFlightTypeMenu)
                          })

                      DropdownMenu(
                          expanded = expandedFlightTypeMenu,
                          onDismissRequest = { expandedFlightTypeMenu = false }) {
                            FlightType.all_flights.withIndex().forEach { (id, item) ->
                              DropdownMenuItem(
                                  modifier =
                                      Modifier.fillMaxWidth()
                                          .padding(defaultPadding)
                                          .testTag("Flight Type $id"),
                                  onClick = {
                                    flightType = item
                                    expandedFlightTypeMenu = false
                                  },
                                  text = { Text(item.name) })
                            }
                          }
                    }
                // Drop down menu for the vehicle
                ExposedDropdownMenuBox(
                    modifier =
                        Modifier.fillMaxWidth()
                            .padding(defaultPadding)
                            .clickable { expandedVehicleMenu = true }
                            .testTag("Vehicle Menu"),
                    expanded = expandedVehicleMenu,
                    onExpandedChange = { expandedVehicleMenu = !expandedVehicleMenu }) {
                      OutlinedTextField(
                          value = vehicle?.name ?: "Vehicle",
                          modifier = Modifier.menuAnchor().fillMaxWidth(),
                          readOnly = true,
                          onValueChange = {},
                          trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedVehicleMenu)
                          })

                      DropdownMenu(
                          expanded = expandedVehicleMenu,
                          onDismissRequest = { expandedVehicleMenu = false }) {
                            allVehicles.withIndex().forEach { (id, item) ->
                              DropdownMenuItem(
                                  modifier =
                                      Modifier.fillMaxWidth()
                                          .padding(defaultPadding)
                                          .testTag("Vehicle $id"),
                                  onClick = {
                                    vehicle = item
                                    expandedVehicleMenu = false
                                  },
                                  text = { Text(item.name) })
                            }
                          }
                    }
                // Drop down menu for the time slot. Only AM and PM are available
                Box(modifier = Modifier.fillMaxWidth()) {
                  OutlinedTextField(
                      placeholder = { Text("Time Slot") },
                      modifier =
                          Modifier.fillMaxWidth()
                              .padding(defaultPadding)
                              .clickable { expandedTimeSlot = true }
                              .testTag("Time Slot Menu"),
                      enabled = false,
                      value = timeSlot.toString(),
                      onValueChange = {})
                  DropdownMenu(
                      expanded = expandedTimeSlot,
                      onDismissRequest = { expandedTimeSlot = false }) {
                        TimeSlot.entries.withIndex().forEach { (id, item) ->
                          DropdownMenuItem(
                              modifier =
                                  Modifier.fillMaxWidth()
                                      .padding(defaultPadding)
                                      .testTag("Time Slot $id"),
                              onClick = {
                                timeSlot = item
                                expandedTimeSlot = false
                              },
                              text = { Text(item.name) })
                        }
                      }
                }
                // Drop down menu for the balloon
                ExposedDropdownMenuBox(
                    modifier =
                        Modifier.fillMaxWidth()
                            .padding(defaultPadding)
                            .clickable { expandedBalloonMenu = true }
                            .testTag("Balloon Menu"),
                    expanded = expandedBalloonMenu,
                    onExpandedChange = { expandedBalloonMenu = !expandedBalloonMenu }) {
                      OutlinedTextField(
                          value = balloon?.name ?: "Balloon",
                          modifier = Modifier.menuAnchor().fillMaxWidth(),
                          readOnly = true,
                          onValueChange = {},
                          trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedBalloonMenu)
                          })

                      DropdownMenu(
                          expanded = expandedBalloonMenu,
                          onDismissRequest = { expandedBalloonMenu = false }) {
                            balloons.withIndex().forEach { (id, item) ->
                              DropdownMenuItem(
                                  modifier =
                                      Modifier.fillMaxWidth()
                                          .padding(defaultPadding)
                                          .testTag("Balloon $id"),
                                  onClick = {
                                    balloon = item
                                    expandedBalloonMenu = false
                                  },
                                  text = { Text(item.name) })
                            }
                          }
                    }
                // Drop down menu for the basket
                ExposedDropdownMenuBox(
                    modifier =
                        Modifier.fillMaxWidth()
                            .padding(defaultPadding)
                            .clickable { expandedVehicleMenu = true }
                            .testTag("Basket Menu"),
                    expanded = expandedBasketMenu,
                    onExpandedChange = { expandedBasketMenu = !expandedBasketMenu }) {
                      OutlinedTextField(
                          value = basket?.name ?: "Basket",
                          modifier = Modifier.menuAnchor().fillMaxWidth(),
                          readOnly = true,
                          onValueChange = {},
                          trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedBasketMenu)
                          })

                      DropdownMenu(
                          expanded = expandedBasketMenu,
                          onDismissRequest = { expandedBasketMenu = false }) {
                            baskets.withIndex().forEach { (id, item) ->
                              DropdownMenuItem(
                                  modifier =
                                      Modifier.fillMaxWidth()
                                          .padding(defaultPadding)
                                          .testTag("Basket $id"),
                                  onClick = {
                                    basket = item
                                    expandedBasketMenu = false
                                  },
                                  text = { Text(item.name) })
                            }
                          }
                    }
                // Field for the fondue role. Only displayed if the flight type is FONDUE
                if (flightType == FlightType.FONDUE) {
                  OutlinedTextField(
                      placeholder = { Text("Fondue Role") },
                      modifier =
                          Modifier.fillMaxWidth()
                              .padding(defaultPadding)
                              .clickable { expandedTimeSlot = true }
                              .testTag("Fondue Role Field"),
                      value = fondueRole ?: "",
                      onValueChange = { fondueRole = it })
                }
              }
          // Button to add the flight to the list of flights
          Button(
              modifier =
                  Modifier.fillMaxWidth().padding(defaultPadding).testTag("Add Flight Button"),
              onClick = {
                val newFlight =
                    PlannedFlight(
                        nPassengers = nbPassenger.toInt(),
                        date = date,
                        flightType = flightType!!,
                        timeSlot = timeSlot,
                        balloon = balloon,
                        basket = basket,
                        vehicles = listOf(vehicle!!),
                        id = "testId")
                flights.add(newFlight)
                navController.navigate(Route.HOME) {
                  launchSingleTop = true
                  popUpTo(Route.ADD_FLIGHT) { inclusive = true }
                }
              }) {
                Text("Add Flight")
              }
        }
      }
}

@Composable
@Preview
fun AddFlightScreenPreview() {
  val navController = rememberNavController()
  AddFlightScreen(navController = navController, flights = mutableListOf())
}
