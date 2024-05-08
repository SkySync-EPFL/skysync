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
import ch.epfl.skysync.models.user.User
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
    availableUsers: List<User>,
    onSaveFlight: (PlannedFlight) -> Unit,
    refreshDate: (LocalDate, TimeSlot) -> Unit,
) {
  Scaffold(modifier = Modifier.fillMaxSize(), topBar = { CustomTopAppBar(navController, title) }) {
      padding ->
    if (currentFlight == null && modifyMode) {
      Text("Flight is loading...")
    } else {
      Column {
        val defaultPadding = 16.dp
        val smallPadding = 8.dp

        val defaultTimeSlot = TimeSlot.AM

        var nbPassengersValue = remember {
          mutableStateOf(currentFlight?.nPassengers?.toString() ?: "")
        }
        var nbPassengersValueError by remember { mutableStateOf(false) }

        var openDatePicker by remember { mutableStateOf(false) }
        var dateValue by remember {
          mutableStateOf(
              if (currentFlight != null) {
                // in order to refresh date & timeslot once on init of FlightForm
                refreshDate(currentFlight.date, currentFlight.timeSlot)
                currentFlight.date
              } else {
                refreshDate(LocalDate.now(), defaultTimeSlot)
                LocalDate.now()
              })
        }

        var flightTypeValue: FlightType? by remember { mutableStateOf(currentFlight?.flightType) }
        var flightTypeValueError by remember { mutableStateOf(false) }

        val chosenVehicles: MutableState<List<Vehicle?>> = remember {
          if (currentFlight?.vehicles == null) {
            mutableStateOf(listOf(null))
          } else {
            mutableStateOf(currentFlight.vehicles)
          }
        }

        var timeSlotValue: TimeSlot by remember {
          mutableStateOf(currentFlight?.timeSlot ?: defaultTimeSlot)
        }

        var balloonValue: Balloon? by remember { mutableStateOf(currentFlight?.balloon) }

        var basketValue: Basket? by remember { mutableStateOf(currentFlight?.basket) }

        val teamMembers = remember {
          mutableStateListOf(
              *currentFlight?.team?.roles?.toTypedArray()
                  ?: Role.initRoles(BASE_ROLES).toTypedArray())
        }
        val specialRoles: MutableList<Role> = remember { mutableStateListOf() }

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
                        refreshDate(dateValue, timeSlotValue)
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
                    onclickMenu = { item ->
                      timeSlotValue = item
                      refreshDate(dateValue, item)
                    },
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
                TeamHeader(
                    defaultPadding = defaultPadding,
                    crewMembers = teamMembers,
                    allRoleTypes = allRoleTypes,
                    availableUsers =
                        availableUsers.filterNot { Team(teamMembers).getUsers().contains(it) })
              }
              teamMembers.withIndex().forEach() { (id, role) ->
                item {
                  RoleField(
                      defaultPadding = defaultPadding,
                      role = role,
                      id = id,
                      onDelete = { teamMembers.removeAt(id) },
                      onReassign = { user -> teamMembers[id] = Role(role.roleType, user) },
                      availableUsers =
                          availableUsers.filterNot { Team(teamMembers).getUsers().contains(it) },
                  )
                }
              }
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
                            val tempMutableList = chosenVehicles.value.toMutableList()
                            tempMutableList.add(null)
                            chosenVehicles.value = tempMutableList.toList()
                          },
                      ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Vehicle")
                      }
                    }
              }
              chosenVehicles.value.withIndex().forEach() { (idList, car) ->
                item {
                  Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically) {
                        CustomDropDownMenu(
                            modifier = Modifier.weight(1f),
                            defaultPadding = defaultPadding,
                            title = "Vehicle $idList",
                            value = car,
                            onclickMenu = { item ->
                              val tempMutableList = chosenVehicles.value.toMutableList()
                              tempMutableList[idList] = item
                              chosenVehicles.value = tempMutableList.toList()
                            },
                            items = availableVehicles,
                            showString = { it?.name ?: "choose vehicle" })
                        IconButton(
                            modifier = Modifier.testTag("Delete Vehicle $idList Button"),
                            onClick = {
                              val tempMutableList = chosenVehicles.value.toMutableList()
                              tempMutableList.removeAt(idList)
                              chosenVehicles.value = tempMutableList.toList()
                            },
                        ) {
                          Icon(Icons.Default.Delete, contentDescription = "Delete Vehicle $idList")
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
                val allRoles = teamMembers.toList() + specialRoles
                val team = Team(allRoles)
                val newFlight =
                    PlannedFlight(
                        nPassengers = nbPassengersValue.value.toInt(),
                        date = dateValue,
                        flightType = flightTypeValue!!,
                        timeSlot = timeSlotValue,
                        balloon = balloonValue,
                        basket = basketValue,
                        vehicles = chosenVehicles.value.filterNotNull(),
                        team = team,
                        id = currentFlight?.id ?: UNSET_ID)
                onSaveFlight(newFlight)
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
    role: Role,
    id: Int,
    onDelete: () -> Unit,
    onReassign: (User?) -> Unit,
    specialName: String = "",
    availableUsers: List<User>,
) {
  Text(
      modifier =
          Modifier.fillMaxWidth()
              .padding(horizontal = defaultPadding)
              .testTag("RoleField $id"),
      text = role.roleType.description,
  )
  Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically) {
        CustomDropDownMenu(
            modifier = Modifier.weight(1f),
            defaultPadding = defaultPadding,
            title = "overview:${role.roleType.description}",
            value = role.assignedUser,
            onclickMenu = { item -> onReassign(item) },
            items = availableUsers,
            showString = { it?.displayString() ?: "choose a user" },
            isError = false,
            messageError = "no message")

        IconButton(
            modifier = Modifier.testTag("Delete Crew Member $id"),
            onClick = { onDelete() }) {
          Icon(
              imageVector = Icons.Default.Delete,
              contentDescription = "Delete $specialName Crew Member")
        }
      }
}

@Composable
fun EnterPassengerNumber(
    defaultPadding: Dp,
    nbPassengersValue: MutableState<String>,
    nbPassengersValueError: Boolean
) {
  TitledInputTextField(
      padding = defaultPadding,
      title = "Number of passengers",
      value = nbPassengersValue.value,
      onValueChange = { value -> nbPassengersValue.value = value.filter { it.isDigit() } },
      isError = nbPassengersValueError,
      messageError = if (nbPassengersValueError) "Please enter a valid number" else "",
      keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
  )
}

@Composable
fun AddRole(
    showAddMemberDialog: Boolean,
    onclickDismiss: () -> Unit,
    defaultPadding: Dp,
    allRoleTypes: List<RoleType>,
    allAvailableUsers: List<User>,
    onConfirm: (RoleType, User?) -> Unit,
) {
  var addNewRole: RoleType? by remember { mutableStateOf(null) }
  var roleNotChosenError: Boolean by remember { mutableStateOf(false) }
  var addNewAssignee: User? by remember { mutableStateOf(null) }
  if (showAddMemberDialog) {
    AlertDialog(
        modifier = Modifier.testTag("User Dialog Field"),
        onDismissRequest = {
          onclickDismiss()
          addNewRole = null
          roleNotChosenError = false
          addNewAssignee = null
        },
        title = { Text("Add New Member") },
        text = {
          Column {
            CustomDropDownMenu(
                defaultPadding = defaultPadding,
                title = "Role Type",
                value = addNewRole,
                onclickMenu = { item ->
                  addNewRole = item
                  roleNotChosenError = false
                },
                items = allRoleTypes,
                showString = { it?.description ?: "choose a role *" },
                isError = roleNotChosenError,
                messageError = "Please choose a role type")
            CustomDropDownMenu(
                defaultPadding = defaultPadding,
                title = "Assigned User",
                value = addNewAssignee,
                onclickMenu = { item -> addNewAssignee = item },
                items = allAvailableUsers,
                showString = { it?.displayString() ?: "choose a user" },
            )
          }
        },
        confirmButton = {
          Button(
              modifier = Modifier.testTag("Add Role Button"),
              onClick = {
                roleNotChosenError = addNewRole == null
                if (!roleNotChosenError) {
                  onConfirm(addNewRole!!, addNewAssignee)
                  addNewRole = null
                  roleNotChosenError = false
                  addNewAssignee = null
                }
              }) {
                Text("Add")
              }
        },
        dismissButton = {
          Button(
              onClick = {
                onclickDismiss()
                addNewRole = null
                roleNotChosenError = false
                addNewAssignee = null
              }) {
                Text("Cancel")
              }
        })
  }
}

@Composable
fun TeamHeader(
    defaultPadding: Dp,
    crewMembers: MutableList<Role>,
    allRoleTypes: List<RoleType>,
    availableUsers: List<User>,
) {
  var showAddMemberDialog by remember { mutableStateOf(false) }
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
            modifier = Modifier.padding(horizontal = defaultPadding).testTag("Add Crew Button"),
            onClick = { showAddMemberDialog = true },
        ) {
          Icon(Icons.Default.Add, contentDescription = "Add Crew Member")
        }
        AddRole(
            showAddMemberDialog = showAddMemberDialog,
            onclickDismiss = { showAddMemberDialog = false },
            defaultPadding = defaultPadding,
            allRoleTypes = allRoleTypes,
            allAvailableUsers = availableUsers,
            onConfirm = { roleType, user ->
              crewMembers.add(Role(roleType, user))
              showAddMemberDialog = false
            })
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
      emptyList(),
      onSaveFlight = {},
      refreshDate = { _, _ -> })
}
