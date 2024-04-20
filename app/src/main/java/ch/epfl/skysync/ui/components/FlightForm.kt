package ch.epfl.skysync.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
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
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlightForm(
    navController: NavHostController,
    flights: MutableList<PlannedFlight>,
    currentFlight: PlannedFlight?,
    title: String
) {
  Scaffold(
      modifier = Modifier.fillMaxSize(),
      topBar = {
        TopAppBar(
            title = { Text(title) },
            navigationIcon = {
              IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
              }
            })
      }) { padding ->
        Column {
          val defaultPadding = 16.dp
          val smallPadding = 8.dp

          var nbPassengersValue by remember {
            mutableStateOf(currentFlight?.nPassengers?.toString() ?: "")
          }
          var nbPassengersValueError by remember { mutableStateOf(false) }

          var openDatePicker by remember { mutableStateOf(false) }
          var dateValue by remember { mutableStateOf(currentFlight?.date ?: LocalDate.now()) }

          var flightTypeValue: FlightType? by remember { mutableStateOf(currentFlight?.flightType) }
          var flightTypeValueError by remember { mutableStateOf(false) }

          val vehicle: Vehicle? by remember { mutableStateOf(null) }
          // List of all vehicles to be removed when connected to the database
          val allVehicles = listOf<Vehicle>(Vehicle("Car"), Vehicle("Bus"), Vehicle("Bike"))
          val listVehiclesValue = remember {
            mutableStateListOf(*currentFlight?.vehicles?.toTypedArray() ?: emptyArray())
          }
          var addVehicle by remember { mutableStateOf(listVehiclesValue.isEmpty()) }

          var timeSlotValue: TimeSlot by remember {
            mutableStateOf(currentFlight?.timeSlot ?: TimeSlot.AM)
          }

          var balloonValue: Balloon? by remember { mutableStateOf(currentFlight?.balloon) }
          // List of all balloons to be removed when connected to the database
          val balloons: List<Balloon> =
              listOf(
                  Balloon("Balloon1", BalloonQualification.LARGE),
                  Balloon("Balloon2", BalloonQualification.MEDIUM),
                  Balloon("Balloon3", BalloonQualification.SMALL))

          var basketValue: Basket? by remember { mutableStateOf(currentFlight?.basket) }
          // List of all baskets to be removed when connected to the database
          val baskets: List<Basket> =
              listOf(
                  Basket("Basket1", hasDoor = false),
                  Basket("Basket2", hasDoor = true),
                  Basket("Basket3", hasDoor = true))

          val crewMembers = remember {
            mutableStateListOf(
                *currentFlight?.team?.roles?.toTypedArray()
                    ?: Role.initRoles(BASE_ROLES).toTypedArray())
          }
          var showAddMemberDialog by remember { mutableStateOf(false) }
          var addNewRole: RoleType? by remember { mutableStateOf(null) }
          var addNewRoleError by remember { mutableStateOf(false) }
          var expandedAddNewRole by remember { mutableStateOf(false) }

          var addNewUserQuery: String by remember { mutableStateOf("") }

          val lazyListState = rememberLazyListState()

          var isError by remember { mutableStateOf(false) }

          var specialRoleAdded by remember { mutableStateOf(false) }
          var specialRoleId by remember { mutableIntStateOf(0) }

          // Scroll to the first field with an error
          LaunchedEffect(isError) {
            if (nbPassengersValueError) {
              lazyListState.animateScrollToItem(0)
            } else if (flightTypeValueError) {
              lazyListState.animateScrollToItem(3)
            }
          }
          LazyColumn(
              modifier = Modifier.padding(padding).weight(1f).testTag("Flight Lazy Column"),
              state = lazyListState,
              verticalArrangement = Arrangement.SpaceBetween) {
                // Field getting the number of passengers. Only number can be entered
                item {
                  nbPassengersField(
                      nbPassengersValue = nbPassengersValue,
                      onNbPassengersValueChange = { value ->
                        nbPassengersValue = value.filter { it.isDigit() }
                      },
                      defaultPadding = defaultPadding,
                      nbPassengersValueError = nbPassengersValueError)
                }
                // The date field is a clickable field that opens a date picker
                item {
                  val today = java.util.Calendar.getInstance().timeInMillis
                  val datePickerState = rememberDatePickerState(initialSelectedDateMillis = today)
                  DatePickerField(
                      dateValue = dateValue,
                      defaultPadding = defaultPadding,
                      openDatePicker = openDatePicker,
                      onclickConfirm = {
                        openDatePicker = false
                        Calendar.getInstance().apply {
                          timeInMillis = datePickerState.selectedDateMillis!!
                          dateValue =
                              Instant.ofEpochMilli(timeInMillis)
                                  .atZone(ZoneId.of("GMT"))
                                  .toLocalDate()
                        }
                      },
                      onclickDismiss = { openDatePicker = false },
                      onclickField = { openDatePicker = true },
                      datePickerState = datePickerState,
                      today = today)
                }
                // Drop down menu for the time slot. Only AM and PM are available
                item {
                  val timeSlotTitle = "Time Slot"
                  Text(
                      modifier = Modifier.padding(horizontal = defaultPadding),
                      text = timeSlotTitle,
                      style = MaterialTheme.typography.headlineSmall)
                  MenuDropDown(
                      defaultPadding = defaultPadding,
                      title = timeSlotTitle,
                      value = timeSlotValue,
                      onclickMenu = { item -> timeSlotValue = item },
                      items = TimeSlot.entries)
                }
                // Drop down menu for the flight type
                item {
                  val flightTypeTitle = "Flight Type"
                  Text(
                      modifier = Modifier.padding(horizontal = defaultPadding),
                      text = flightTypeTitle,
                      style = MaterialTheme.typography.headlineSmall)
                  MenuDropDown(
                      defaultPadding = defaultPadding,
                      title = flightTypeTitle,
                      value = flightTypeValue,
                      onclickMenu = { item ->
                        flightTypeValue = item
                        if (flightTypeValue == FlightType.FONDUE) {
                          crewMembers.add(Role(RoleType.MAITRE_FONDUE))
                          specialRoleAdded = true
                          specialRoleId = crewMembers.size - 1
                        } else if (flightTypeValue == FlightType.HIGH_ALTITUDE) {
                          crewMembers.add(Role(RoleType.OXYGEN_MASTER))
                          specialRoleAdded = true
                          specialRoleId = crewMembers.size - 1
                        } else if (specialRoleAdded) {
                          specialRoleAdded = false
                          crewMembers.removeAt(specialRoleId)
                        }
                      },
                      items = FlightType.ALL_FLIGHTS,
                      showString = { it?.name ?: "Choose the flightType" },
                      isError = flightTypeValueError,
                      messageError = "Please choose a flight type")
                }
                // Section to add the crew members
                item {
                  Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically) {
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
                                modifier =
                                    Modifier.fillMaxWidth()
                                        .padding(defaultPadding)
                                        .testTag("User Dialog Field"),
                                value = addNewUserQuery,
                                onValueChange = { addNewUserQuery = it },
                                placeholder = { Text("User") },
                                singleLine = true)
                          }
                        },
                        confirmButton = {
                          Button(
                              onClick = {
                                addNewRoleError = addNewRole == null
                                if (!addNewRoleError) {
                                  crewMembers.add(Role(addNewRole!!))
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
                    Text(
                        modifier = Modifier.padding(horizontal = defaultPadding),
                        text = role.roleType.name)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically) {
                          OutlinedTextField(
                              modifier =
                                  Modifier.fillMaxWidth()
                                      .padding(horizontal = defaultPadding, vertical = smallPadding)
                                      .weight(1f)
                                      .testTag("User $id"),
                              value = query,
                              onValueChange = { query = it },
                              placeholder = { Text("User ${id + 1}") },
                              singleLine = true)
                          IconButton(
                              onClick = {
                                crewMembers.removeAt(id)
                                if (specialRoleAdded && id == specialRoleId) {
                                  specialRoleAdded = false
                                }
                              }) {
                                Icon(
                                    modifier = Modifier.testTag("Delete Crew Member $id"),
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete Crew Member")
                              }
                        }
                  }
                }
                // Drop down menu for the vehicle
                item {
                  Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically) {
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
                          Row(
                              modifier = Modifier.fillMaxWidth(),
                              horizontalArrangement = Arrangement.SpaceBetween,
                              verticalAlignment = Alignment.CenterVertically) {
                                MenuDropDown(
                                    defaultPadding = defaultPadding,
                                    title = "Vehicle",
                                    value = car,
                                    onclickMenu = { item -> listVehiclesValue[idList] = item },
                                    items = allVehicles,
                                    showString = { it.name })
                                IconButton(
                                    modifier = Modifier.testTag("Delete Vehicle Button"),
                                    onClick = {
                                      listVehiclesValue.removeAt(idList)
                                      if (listVehiclesValue.isEmpty()) {
                                        addVehicle = true
                                      }
                                    },
                                ) {
                                  Icon(
                                      Icons.Default.Delete,
                                      contentDescription = "Delete Vehicle $idList")
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
                  val balloonTitle = "Balloon"
                  Text(
                      modifier = Modifier.padding(horizontal = defaultPadding),
                      text = balloonTitle,
                      style = MaterialTheme.typography.headlineSmall)
                  MenuDropDown(
                      defaultPadding = defaultPadding,
                      title = balloonTitle,
                      value = balloonValue,
                      onclickMenu = { item -> balloonValue = item },
                      items = balloons)
                }
                // Drop down menu for the basket
                item {
                  val basketTitle = "Basket"
                  Text(
                      modifier = Modifier.padding(horizontal = defaultPadding),
                      text = basketTitle,
                      style = MaterialTheme.typography.headlineSmall)
                  MenuDropDown(
                      defaultPadding = defaultPadding,
                      title = basketTitle,
                      value = basketValue,
                      onclickMenu = { item -> basketValue = item },
                      items = baskets)
                }
              }
          // Button to add the flight to the list of flights
          Button(
              modifier = Modifier.fillMaxWidth().padding(defaultPadding).testTag("$title Button"),
              onClick = {
                nbPassengersValueError =
                    nbPassengersValue.isEmpty() || nbPassengersValue.toInt() <= 0
                flightTypeValueError = flightTypeValue == null
                isError = nbPassengersValueError || flightTypeValueError
                if (!isError) {
                  val vehicles: List<Vehicle> =
                      if (vehicle == null) emptyList() else listOf(vehicle!!)
                  val team = Team(crewMembers.toList())
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
                          id = currentFlight?.id ?: "unset id")
                  flights.add(newFlight)
                  navController.navigate(Route.HOME) {
                    launchSingleTop = true
                    popUpTo(Route.ADD_FLIGHT) { inclusive = true }
                  }
                }
              }) {
                Text(title)
              }
        }
      }
}

@Composable
fun nbPassengersField(
    nbPassengersValue: String,
    onNbPassengersValueChange: (String) -> Unit,
    nbPassengersValueError: Boolean,
    defaultPadding: Dp
) {
  Text(
      modifier = Modifier.padding(horizontal = defaultPadding),
      text = "Number of passengers",
      style = MaterialTheme.typography.headlineSmall)
  OutlinedTextField(
      value = nbPassengersValue,
      onValueChange = { value -> onNbPassengersValueChange(value) },
      placeholder = { Text("Enter a number bigger than 0") },
      singleLine = true,
      keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
      modifier = Modifier.fillMaxWidth().padding(defaultPadding).testTag("nb Passenger"),
      isError = nbPassengersValueError,
      supportingText = { if (nbPassengersValueError) Text("Please enter a valid number") })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(
    dateValue: LocalDate,
    defaultPadding: Dp,
    openDatePicker: Boolean,
    onclickConfirm: () -> Unit,
    onclickDismiss: () -> Unit,
    onclickField: () -> Unit,
    datePickerState: DatePickerState,
    today: Long
) {
  Text(
      modifier = Modifier.padding(horizontal = defaultPadding),
      text = "Date",
      style = MaterialTheme.typography.headlineSmall)
  if (openDatePicker) {

    DatePickerDialog(
        onDismissRequest = {},
        confirmButton = { TextButton(onClick = onclickConfirm) { Text("OK") } },
        dismissButton = { TextButton(onClick = onclickDismiss) { Text("Cancel") } }) {
          // The date picker is only available for the current date and later
          val todayDate =
              Instant.ofEpochMilli(today)
                  .atZone(ZoneId.of("GMT"))
                  .toLocalDate()
                  .atStartOfDay(ZoneId.of("GMT"))
                  .toInstant()
                  .toEpochMilli()
          DatePicker(state = datePickerState, dateValidator = { it >= todayDate })
        }
  }

  OutlinedTextField(
      modifier =
          Modifier.fillMaxWidth()
              .padding(defaultPadding)
              .clickable(onClick = onclickField)
              .testTag("Date Field"),
      enabled = false,
      value =
          String.format(
              "%02d/%02d/%04d", dateValue.dayOfMonth, dateValue.monthValue, dateValue.year),
      onValueChange = {})
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> MenuDropDown(
    defaultPadding: Dp,
    title: String,
    value: T,
    onclickMenu: (T) -> Unit,
    items: List<T>,
    showString: (T) -> String = { it.toString() },
    isError: Boolean = false,
    messageError: String = "",
) {

  var expanded by remember { mutableStateOf(false) }
  ExposedDropdownMenuBox(
      modifier =
          Modifier.fillMaxWidth()
              .padding(defaultPadding)
              .clickable(onClick = { expanded = true })
              .testTag("$title Menu"),
      expanded = expanded,
      onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = showString(value),
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            readOnly = true,
            onValueChange = {},
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            isError = isError,
            supportingText = { if (isError) Text(messageError) })
      }
  DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
    items.withIndex().forEach { (id, item) ->
      DropdownMenuItem(
          modifier = Modifier.fillMaxWidth().padding(defaultPadding).testTag("$title $id"),
          onClick = {
            onclickMenu(item)
            expanded = false
          },
          text = { Text(showString(item)) })
    }
  }
}

@Composable
@Preview
fun FlightFormPreview() {
  val navController = rememberNavController()
  val currentFlight =
      PlannedFlight(
          nPassengers = 1,
          date = LocalDate.now(),
          flightType = FlightType.PREMIUM,
          timeSlot = TimeSlot.AM,
          vehicles = listOf(),
          balloon = null,
          basket = null,
          id = "testId")
  FlightForm(navController = navController, flights = mutableListOf(), currentFlight, "Flight Form")
}
