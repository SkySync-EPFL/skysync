package ch.epfl.skysync.components.forms

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
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
import ch.epfl.skysync.components.CustomTopAppBar
import ch.epfl.skysync.models.UNSET_ID
import ch.epfl.skysync.models.calendar.TimeSlot
import ch.epfl.skysync.models.flight.BASE_ROLES
import ch.epfl.skysync.models.flight.Balloon
import ch.epfl.skysync.models.flight.Basket
import ch.epfl.skysync.models.flight.Flight
import ch.epfl.skysync.models.flight.FlightType
import ch.epfl.skysync.models.flight.PlannedFlight
import ch.epfl.skysync.models.flight.Role
import ch.epfl.skysync.models.flight.RoleType
import ch.epfl.skysync.models.flight.Team
import ch.epfl.skysync.models.flight.Vehicle
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlightForm(
    navController: NavHostController,
    currentFlight: Flight?,
    modifyMode: Boolean,
    title: String,
    allFlightTypes: List<FlightType>,
    allRoleTypes: List<RoleType>,
    availableVehicles: List<Vehicle>,
    availableBalloons: List<Balloon>,
    availableBaskets: List<Basket>,
    flightAction: (PlannedFlight) -> Unit,
) {
  Scaffold(modifier = Modifier.fillMaxSize(), topBar = { CustomTopAppBar(navController, title) }) {
      padding ->
    if (currentFlight == null && modifyMode) {
      Text("Flight is loading...")
    } else {
      Column {
        val defaultPadding = 16.dp
        val smallPadding = 8.dp

        var nbPassengersValue = remember {
          mutableStateOf(currentFlight?.nPassengers?.toString() ?: "")
        }
        var nbPassengersValueError by remember { mutableStateOf(false) }

        var openDatePicker by remember { mutableStateOf(false) }
        var dateValue by remember { mutableStateOf(currentFlight?.date ?: LocalDate.now()) }

        var flightTypeValue: FlightType? by remember { mutableStateOf(currentFlight?.flightType) }
        var flightTypeValueError by remember { mutableStateOf(false) }

        val vehicle: Vehicle? by remember { mutableStateOf(null) }
        val listVehiclesValue = remember {
          mutableStateListOf(*currentFlight?.vehicles?.toTypedArray() ?: emptyArray())
        }
        var addVehicle by remember { mutableStateOf(listVehiclesValue.isEmpty()) }

        var timeSlotValue: TimeSlot by remember {
          mutableStateOf(currentFlight?.timeSlot ?: TimeSlot.AM)
        }

        var balloonValue: Balloon? by remember { mutableStateOf(currentFlight?.balloon) }

        var basketValue: Basket? by remember { mutableStateOf(currentFlight?.basket) }

        val crewMembers = remember {
          mutableStateListOf(
              *currentFlight?.team?.roles?.toTypedArray()
                  ?: Role.initRoles(BASE_ROLES).toTypedArray())
        }
        val specialRoles: MutableList<Role> = remember { mutableStateListOf() }

        var showAddMemberDialog by remember { mutableStateOf(false) }
        var addNewRole: RoleType? by remember { mutableStateOf(null) }
        var addNewRoleError by remember { mutableStateOf(false) }
        var expandedAddNewRole by remember { mutableStateOf(false) }

        var addNewUserQuery: String by remember { mutableStateOf("") }

        val lazyListState = rememberLazyListState()

        var isError by remember { mutableStateOf(false) }

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
                  EnterPassengerNumber(defaultPadding, nbPassengersValue, nbPassengersValueError)
              }
              // The date field is a clickable field that opens a date picker
              item {
                val today = Calendar.getInstance().timeInMillis
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
                TitledDropDownMenu(
                    defaultPadding = defaultPadding,
                    title = timeSlotTitle,
                    value = timeSlotValue,
                    onclickMenu = { item -> timeSlotValue = item },
                    items = TimeSlot.entries)
              }
              // Drop down menu for the flight type
              item {
                val flightTypeTitle = "Flight Type"
                TitledDropDownMenu(
                    defaultPadding = defaultPadding,
                    title = flightTypeTitle,
                    value = flightTypeValue,
                    onclickMenu = { item -> flightTypeValue = item },
                    items = allFlightTypes,
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
                          CustomDropDownMenu(
                              defaultPadding = defaultPadding,
                              title = "Role Type",
                              value = addNewRole,
                              onclickMenu = { item -> addNewRole = item },
                              items = allRoleTypes,
                              showString = { it?.name ?: "Choose a role" },
                          )
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

                            // add user
                            val addUserTitle = "choose user"
                            TitledDropDownMenu(
                                defaultPadding = defaultPadding,
                                title = addUserTitle,
                                value = flightTypeValue,
                                onclickMenu = { item -> flightTypeValue = item },
                                items = allFlightTypes,
                                showString = { it?.name ?: "Choose the flightType" },
                                isError = flightTypeValueError,
                                messageError = "Please choose a flight type")
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
                item { RoleField(defaultPadding, smallPadding, role, id, crewMembers) }
              }
              if (flightTypeValue != null) {
                flightTypeValue!!.specialRoles.withIndex().forEach { (id, roleType) ->
                  item {
                    RoleField(
                        defaultPadding, smallPadding, Role(roleType), id, specialRoles, "Special")
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
                            if (listVehiclesValue.size < availableVehicles.size) {
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
                  Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically) {
                        CustomDropDownMenu(
                            defaultPadding = defaultPadding,
                            title = "Vehicle $idList",
                            value = car,
                            onclickMenu = { item -> listVehiclesValue[idList] = item },
                            items = availableVehicles,
                            showString = { it.name })
                        IconButton(
                            modifier = Modifier.testTag("Delete Vehicle $idList Button"),
                            onClick = {
                              listVehiclesValue.removeAt(idList)
                              if (listVehiclesValue.isEmpty()) {
                                addVehicle = true
                              }
                            },
                        ) {
                          Icon(Icons.Default.Delete, contentDescription = "Delete Vehicle $idList")
                        }
                      }
                }
              }
              if (addVehicle) {
                item {
                  val id = listVehiclesValue.size
                  Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically) {
                        CustomDropDownMenu(
                            defaultPadding = defaultPadding,
                            title = "Vehicle $id",
                            value = vehicle,
                            onclickMenu = { item ->
                              addVehicle = false
                              listVehiclesValue.add(item!!)
                            },
                            items = availableVehicles,
                            showString = { it?.name ?: "Choose a vehicle" })
                        IconButton(
                            modifier = Modifier.testTag("Delete Vehicle $id Button"),
                            onClick = {
                              listVehiclesValue.removeAt(id)
                              if (listVehiclesValue.isEmpty()) {
                                addVehicle = true
                              }
                            },
                        ) {
                          Icon(Icons.Default.Delete, contentDescription = "Delete Vehicle $id")
                        }
                      }
                }
              }
              // Drop down menu for the balloon
              item {
                val balloonTitle = "Balloon"
                TitledDropDownMenu(
                    defaultPadding = defaultPadding,
                    title = balloonTitle,
                    value = balloonValue,
                    onclickMenu = { item -> balloonValue = item },
                    items = availableBalloons,
                    showString = { it?.name ?: "Choose the balloon" })
              }
              // Drop down menu for the basket
              item {
                val basketTitle = "Basket"
                TitledDropDownMenu(
                    defaultPadding = defaultPadding,
                    title = basketTitle,
                    value = basketValue,
                    onclickMenu = { item -> basketValue = item },
                    items = availableBaskets,
                    showString = { it?.name ?: "Choose the basket" })
              }
            }
        // Button to add the flight to the list of flights
        Button(
            modifier = Modifier.fillMaxWidth().padding(defaultPadding).testTag("$title Button"),
            onClick = {
              nbPassengersValueError = nbPassengerInputValidation(nbPassengersValue.value)
              flightTypeValueError = flightTypeInputValidation(flightTypeValue)
              isError = inputValidation(nbPassengersValueError, flightTypeValueError)
              if (!isError) {
                val vehicles: List<Vehicle> =
                    if (vehicle == null) emptyList() else listOf(vehicle!!)
                val allRoles = crewMembers.toList() + specialRoles
                val team = Team(allRoles)
                val newFlight =
                    PlannedFlight(
                        nPassengers = nbPassengersValue.value.toInt(),
                        date = dateValue,
                        flightType = flightTypeValue!!,
                        timeSlot = timeSlotValue,
                        balloon = balloonValue,
                        basket = basketValue,
                        vehicles = vehicles,
                        team = team,
                        id = currentFlight?.id ?: UNSET_ID)
                flightAction(newFlight)
              }
            }) {
              Text(title)
            }
      }
    }
  }
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

fun nbPassengerInputValidation(nbPassengersValue: String): Boolean {
  return nbPassengersValue.isEmpty() || nbPassengersValue.toInt() <= 0
}

fun flightTypeInputValidation(flightTypeValue: FlightType?): Boolean {
  return flightTypeValue == null
}

fun inputValidation(nbPassengersValueError: Boolean, flightTypeValueError: Boolean): Boolean {
  return nbPassengersValueError || flightTypeValueError
}

@Composable
fun RoleField(
    defaultPadding: Dp,
    smallPadding: Dp,
    role: Role,
    id: Int,
    crewMembers: MutableList<Role>,
    specialName: String = ""
) {
  var query by remember { mutableStateOf("") }
  Text(modifier = Modifier.padding(horizontal = defaultPadding), text = role.roleType.toString())
  Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically) {
        OutlinedTextField(
            modifier =
                Modifier.fillMaxWidth()
                    .padding(horizontal = defaultPadding, vertical = smallPadding)
                    .weight(1f)
                    .testTag("$specialName User $id"),
            value = query,
            onValueChange = { query = it },
            placeholder = { Text("User ${id + 1}") },
            singleLine = true)
        IconButton(onClick = { crewMembers.removeAt(id) }) {
          Icon(
              modifier = Modifier.testTag("Delete $specialName Crew Member $id"),
              imageVector = Icons.Default.Delete,
              contentDescription = "Delete $specialName Crew Member")
        }
      }
}

@Composable
fun EnterPassengerNumber(defaultPadding: Dp,
                         nbPassengersValue: MutableState<String>,
                         nbPassengersValueError: Boolean){
    TitledInputTextField(
        padding = defaultPadding,
        title = "Number of passengers",
        value = nbPassengersValue.value,
        onValueChange = { value -> nbPassengersValue.value = value.filter { it.isDigit() } },
        isError = nbPassengersValueError,
        messageError =
        if (nbPassengersValueError) "Please enter a valid number" else "",
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
    )
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
  FlightForm(
      navController = navController,
      currentFlight,
      modifyMode = false,
      "Flight Form",
      emptyList(),
      emptyList(),
      emptyList(),
      emptyList(),
      emptyList(),
      flightAction = {})
}
