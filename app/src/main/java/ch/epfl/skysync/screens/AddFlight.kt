package ch.epfl.skysync.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import ch.epfl.skysync.models.flight.BASE_ROLES
import ch.epfl.skysync.models.flight.Balloon
import ch.epfl.skysync.models.flight.BalloonQualification
import ch.epfl.skysync.models.flight.Basket
import ch.epfl.skysync.models.flight.FlightType
import ch.epfl.skysync.models.flight.PlannedFlight
import ch.epfl.skysync.models.flight.Role
import ch.epfl.skysync.models.flight.RoleType
import ch.epfl.skysync.models.flight.Team
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
          val smallPadding = 8.dp

          var nbPassengersValue by remember { mutableStateOf("") }
          var nbPassengersValueError by remember { mutableStateOf(false) }

          var openDatePicker by remember { mutableStateOf(false) }
          var dateValue by remember { mutableStateOf(LocalDate.now()) }

          var flightTypeValue: FlightType? by remember { mutableStateOf(null) }
          var expandedFlightTypeMenu by remember { mutableStateOf(false) }
          var flightTypeValueError by remember { mutableStateOf(false) }

          val vehicle: Vehicle? by remember { mutableStateOf(null) }
          // List of all vehicles to be removed when connected to the database
          val allVehicles = listOf<Vehicle>(Vehicle("Car"), Vehicle("Bus"), Vehicle("Bike"))
          val listVehiclesValue = remember { mutableStateListOf<Vehicle>() }
          var addVehicle by remember { mutableStateOf(listVehiclesValue.isEmpty()) }

          var timeSlotValue: TimeSlot by remember { mutableStateOf(TimeSlot.AM) }
          var expandedTimeSlot by remember { mutableStateOf(false) }

          var balloonValue: Balloon? by remember { mutableStateOf(null) }
          // List of all balloons to be removed when connected to the database
          val balloons: List<Balloon> =
              listOf(
                  Balloon("Balloon1", BalloonQualification.LARGE),
                  Balloon("Balloon2", BalloonQualification.MEDIUM),
                  Balloon("Balloon3", BalloonQualification.SMALL))
          var expandedBalloonMenu by remember { mutableStateOf(false) }

          var basketValue: Basket? by remember { mutableStateOf(null) }
          // List of all baskets to be removed when connected to the database
          val baskets: List<Basket> =
              listOf(
                  Basket("Basket1", hasDoor = false),
                  Basket("Basket2", hasDoor = true),
                  Basket("Basket3", hasDoor = true))
          var expandedBasketMenu by remember { mutableStateOf(false) }

          val crewMembers = remember { mutableStateListOf(*BASE_ROLES.toTypedArray()) }
          var showAddMemberDialog by remember { mutableStateOf(false) }
          var addNewRole: RoleType? by remember { mutableStateOf(null) }
          var addNewRoleError by remember { mutableStateOf(false) }
          var expandedAddNewRole by remember { mutableStateOf(false) }

          var addNewUserQuery: String by remember { mutableStateOf("") }

          /* TODO change this Column to a LazyColumn and adapt the code accordingly (including
          tests)*/
          LazyColumn(
              modifier = Modifier.padding(padding).weight(1f).testTag("Flight Lazy Column"),
              verticalArrangement = Arrangement.SpaceBetween) {
                // Field getting the number of passengers. Only number can be entered
                item {
                  OutlinedTextField(
                      value = nbPassengersValue,
                      onValueChange = { value ->
                        nbPassengersValue = value.filter { it.isDigit() }
                      },
                      placeholder = { Text("Number of passengers") },
                      singleLine = true,
                      keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                      modifier =
                          Modifier.fillMaxWidth().padding(defaultPadding).testTag("nb Passenger"),
                      isError = nbPassengersValueError,
                  )
                }
                // The date field is a clickable field that opens a date picker
                item {
                  if (openDatePicker) {
                      val today = java.util.Calendar.getInstance().timeInMillis
                    val datePickerState =
                        rememberDatePickerState(
                            initialSelectedDateMillis = today)

                    DatePickerDialog(
                        onDismissRequest = {},
                        confirmButton = {
                          TextButton(
                              onClick = {
                                openDatePicker = false
                                java.util.Calendar.getInstance().apply {
                                  timeInMillis = datePickerState.selectedDateMillis!!
                                  dateValue =
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
                        // The date picker is only available for the current date and later
                        val todayDate =  Instant.ofEpochMilli(today)
                            .atZone(ZoneId.of("GMT"))
                            .toLocalDate().atStartOfDay(ZoneId.of("GMT")).toInstant().toEpochMilli()
                          DatePicker(state = datePickerState, dateValidator = { it >= todayDate})
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
                              "%02d/%02d/%04d",
                              dateValue.dayOfMonth,
                              dateValue.monthValue,
                              dateValue.year),
                      onValueChange = {})
                }
                // Drop down menu for the time slot. Only AM and PM are available
                item {
                  ExposedDropdownMenuBox(
                      modifier =
                          Modifier.fillMaxWidth()
                              .padding(defaultPadding)
                              .clickable { expandedTimeSlot = true }
                              .testTag("Time Slot Menu"),
                      expanded = expandedTimeSlot,
                      onExpandedChange = { expandedTimeSlot = !expandedTimeSlot }) {
                        OutlinedTextField(
                            value = timeSlotValue.toString(),
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            readOnly = true,
                            onValueChange = {},
                            trailingIcon = {
                              ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTimeSlot)
                            })

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
                                      timeSlotValue = item
                                      expandedTimeSlot = false
                                    },
                                    text = { Text(item.name) })
                              }
                            }
                      }
                }
                // Drop down menu for the flight type
                item {
                  ExposedDropdownMenuBox(
                      modifier =
                          Modifier.fillMaxWidth()
                              .padding(defaultPadding)
                              .clickable { expandedFlightTypeMenu = true }
                              .testTag("Flight Type Menu"),
                      expanded = expandedFlightTypeMenu,
                      onExpandedChange = { expandedFlightTypeMenu = !expandedFlightTypeMenu }) {
                        OutlinedTextField(
                            value = flightTypeValue?.name ?: "Flight Type",
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            readOnly = true,
                            onValueChange = {},
                            trailingIcon = {
                              ExposedDropdownMenuDefaults.TrailingIcon(
                                  expanded = expandedFlightTypeMenu)
                            },
                            isError = flightTypeValueError)

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
                                      flightTypeValue = item
                                      expandedFlightTypeMenu = false
                                      // It is assumed that these special roles are not used with
                                      // other flight types
                                      if (flightTypeValue == FlightType.FONDUE) {
                                        crewMembers.add(RoleType.MAITRE_FONDUE)
                                      } else if (crewMembers.contains(RoleType.MAITRE_FONDUE)) {
                                        crewMembers.remove(RoleType.MAITRE_FONDUE)
                                      }
                                      if (flightTypeValue == FlightType.HIGH_ALTITUDE) {
                                        crewMembers.add(RoleType.OXYGEN_MASTER)
                                      } else if (crewMembers.contains(RoleType.OXYGEN_MASTER)) {
                                        crewMembers.remove(RoleType.OXYGEN_MASTER)
                                      }
                                    },
                                    text = { Text(item.name) })
                              }
                            }
                      }
                }
                // Section to add the crew members
                item {
                  Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(
                            modifier = Modifier.padding(horizontal = defaultPadding),
                            text = "Team",
                            style = MaterialTheme.typography.headlineSmall,
                        )
                        IconButton(
                            modifier =
                                Modifier.padding(horizontal = defaultPadding)
                                    .testTag("Add Crew Button"),
                            onClick = { showAddMemberDialog = true },
                        ) {
                          Icon(Icons.Default.Add, contentDescription = "Add Crew Member")
                        }
                      }
                  // Dialog to add a new crew member and its corresponding field
                  if (showAddMemberDialog) {
                    AlertDialog(
                        onDismissRequest = { showAddMemberDialog = false },
                        title = { Text("Add Crew Member") },
                        text = {
                          Column {
                            ExposedDropdownMenuBox(
                                modifier =
                                    Modifier.fillMaxWidth()
                                        .padding(defaultPadding)
                                        .clickable { expandedAddNewRole = true }
                                        .testTag("Role Type Menu"),
                                expanded = expandedAddNewRole,
                                onExpandedChange = { expandedAddNewRole = !expandedAddNewRole }) {
                                  OutlinedTextField(
                                      value = addNewRole?.name ?: "Role Type",
                                      modifier = Modifier.menuAnchor().fillMaxWidth(),
                                      readOnly = true,
                                      onValueChange = {},
                                      trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(
                                            expanded = expandedAddNewRole)
                                      })

                                  DropdownMenu(
                                      expanded = expandedAddNewRole,
                                      onDismissRequest = { expandedAddNewRole = false }) {
                                        RoleType.entries.withIndex().forEach { (id, item) ->
                                          DropdownMenuItem(
                                              modifier =
                                                  Modifier.fillMaxWidth()
                                                      .padding(horizontal = defaultPadding)
                                                      .testTag("Role Type $id"),
                                              onClick = {
                                                addNewRole = item
                                                expandedAddNewRole = false
                                              },
                                              text = { Text(item.name) })
                                        }
                                      }
                                }
                            // TODO: Handle correctly the user for now it is just a text field
                            OutlinedTextField(
                                modifier = Modifier.fillMaxWidth().padding(defaultPadding).testTag("User Dialog Field"),
                                value = addNewUserQuery,
                                onValueChange = { addNewUserQuery = it },
                                placeholder = { Text("User") },
                                singleLine = true
                            )
                          }
                        },
                        confirmButton = {
                          Button(
                              onClick = {
                                addNewRoleError = addNewRole == null
                                if (!addNewRoleError) {
                                  crewMembers.add(addNewRole!!)
                                  showAddMemberDialog = false
                                }
                              }) {
                                Text("Add")
                              }
                        },
                        dismissButton = {
                          Button(onClick = { showAddMemberDialog = false }) { Text("Cancel") }
                        })
                  }
                }
                crewMembers.withIndex().forEach() { (id, role) ->
                  item {
                    var query by remember { mutableStateOf("") }
                    Text(modifier = Modifier.padding(horizontal = defaultPadding), text = role.name)
                    OutlinedTextField(
                        modifier =
                            Modifier.fillMaxWidth()
                                .padding(horizontal = defaultPadding, vertical = smallPadding)
                                .testTag("User $id"),
                        value = query,
                        onValueChange = { query = it },
                        placeholder = { Text("User ${id + 1}") },
                        singleLine = true)
                  }
                }
                // Drop down menu for the vehicle
                item {
                  Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(
                            modifier = Modifier.padding(horizontal = defaultPadding),
                            text = "Vehicles",
                            style = MaterialTheme.typography.headlineSmall)
                        IconButton(
                            modifier =
                                Modifier.padding(horizontal = defaultPadding)
                                    .testTag("Add Vehicle Button"),
                            onClick = {
                              if (listVehiclesValue.size < allVehicles.size) {
                                addVehicle = true
                              }
                            },
                        ) {
                          Icon(Icons.Default.Add, contentDescription = "Add Vehicle")
                        }
                      }
                }
                listVehiclesValue.withIndex().forEach() { (idList, car) ->
                  item {
                      var expandedVehicleMenu by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        modifier =
                            Modifier.fillMaxWidth()
                                .clickable { expandedVehicleMenu = true }
                                .testTag("Vehicle Menu $idList"),
                        expanded = expandedVehicleMenu,
                        onExpandedChange = { expandedVehicleMenu = !expandedVehicleMenu }) {
                          OutlinedTextField(
                              value = car.name,
                              modifier =
                                  Modifier.menuAnchor().fillMaxWidth().padding(defaultPadding),
                              readOnly = true,
                              onValueChange = {},
                              trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(
                                    expanded = expandedVehicleMenu)
                              })

                          DropdownMenu(
                              expanded = expandedVehicleMenu,
                              onDismissRequest = { expandedVehicleMenu = false }) {
                                allVehicles.withIndex().forEach { (id, item) ->
                                  if (!listVehiclesValue.contains(item)) {
                                    DropdownMenuItem(
                                        modifier =
                                            Modifier.fillMaxWidth()
                                                .padding(
                                                    horizontal = defaultPadding,
                                                    vertical = smallPadding)
                                                .testTag("Vehicle $id"),
                                        onClick = {
                                          listVehiclesValue[idList] = item
                                          expandedVehicleMenu = false
                                        },
                                        text = { Text(item.name) })
                                  }
                                }
                              }
                        }
                  }
                }
                if (addVehicle) {
                  item {
                      var expandedVehicleMenu by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        modifier =
                            Modifier.fillMaxWidth()
                                .clickable { expandedVehicleMenu = true }
                                .testTag("Vehicle Menu ${listVehiclesValue.size}"),
                        expanded = expandedVehicleMenu,
                        onExpandedChange = { expandedVehicleMenu = !expandedVehicleMenu }) {
                          OutlinedTextField(
                              value = vehicle?.name ?: "Vehicle ${listVehiclesValue.size + 1}",
                              modifier =
                                  Modifier.menuAnchor().fillMaxWidth().padding(defaultPadding),
                              readOnly = true,
                              onValueChange = {},
                              trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(
                                    expanded = expandedVehicleMenu)
                              })

                          DropdownMenu(
                              expanded = expandedVehicleMenu,
                              onDismissRequest = { expandedVehicleMenu = false }) {
                                allVehicles.withIndex().forEach { (id, item) ->
                                  if (!listVehiclesValue.contains(item)) {
                                    DropdownMenuItem(
                                        modifier =
                                            Modifier.fillMaxWidth()
                                                .padding(defaultPadding)
                                                .testTag("Vehicle $id"),
                                        onClick = {
                                          listVehiclesValue.add(item)
                                          expandedVehicleMenu = false
                                          addVehicle = false
                                        },
                                        text = { Text(item.name) })
                                  }
                                }
                              }
                        }
                  }
                }
                // Drop down menu for the balloon
                item {
                  ExposedDropdownMenuBox(
                      modifier =
                          Modifier.fillMaxWidth()
                              .padding(defaultPadding)
                              .clickable { expandedBalloonMenu = true }
                              .testTag("Balloon Menu"),
                      expanded = expandedBalloonMenu,
                      onExpandedChange = { expandedBalloonMenu = !expandedBalloonMenu }) {
                        OutlinedTextField(
                            value = balloonValue?.name ?: "Balloon",
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            readOnly = true,
                            onValueChange = {},
                            trailingIcon = {
                              ExposedDropdownMenuDefaults.TrailingIcon(
                                  expanded = expandedBalloonMenu)
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
                                      balloonValue = item
                                      expandedBalloonMenu = false
                                    },
                                    text = { Text(item.name) })
                              }
                            }
                      }
                }
                // Drop down menu for the basket
                item {
                  ExposedDropdownMenuBox(
                      modifier =
                          Modifier.fillMaxWidth()
                              .padding(defaultPadding)
                              .clickable { expandedBasketMenu = true }
                              .testTag("Basket Menu"),
                      expanded = expandedBasketMenu,
                      onExpandedChange = { expandedBasketMenu = !expandedBasketMenu }) {
                        OutlinedTextField(
                            value = basketValue?.name ?: "Basket",
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            readOnly = true,
                            onValueChange = {},
                            trailingIcon = {
                              ExposedDropdownMenuDefaults.TrailingIcon(
                                  expanded = expandedBasketMenu)
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
                                      basketValue = item
                                      expandedBasketMenu = false
                                    },
                                    text = { Text(item.name) })
                              }
                            }
                      }
                }
              }
          // Button to add the flight to the list of flights
          Button(
              modifier =
                  Modifier.fillMaxWidth().padding(defaultPadding).testTag("Add Flight Button"),
              onClick = {
                nbPassengersValueError =
                    nbPassengersValue.isEmpty() || nbPassengersValue.toInt() <= 0
                flightTypeValueError = flightTypeValue == null
                val isError = flightTypeValueError || nbPassengersValueError
                if (!isError) {
                  val vehicles: List<Vehicle> =
                      if (vehicle == null) emptyList() else listOf(vehicle!!)
                  val team = Team(Role.initRoles(crewMembers.toList()))
                  val newFlight =
                      PlannedFlight(
                          nPassengers = nbPassengersValue.toInt(),
                          date = dateValue,
                          flightType = flightTypeValue!!,
                          timeSlot = timeSlotValue,
                          balloon = balloonValue,
                          basket = basketValue,
                          vehicles = vehicles,
                          team = team,
                          id = "testId")
                  flights.add(newFlight)
                  navController.navigate(Route.HOME) {
                    launchSingleTop = true
                    popUpTo(Route.ADD_FLIGHT) { inclusive = true }
                  }
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
