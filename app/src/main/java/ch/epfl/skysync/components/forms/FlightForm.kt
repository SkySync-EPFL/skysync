package ch.epfl.skysync.components.forms

import android.annotation.SuppressLint
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
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import ch.epfl.skysync.components.CustomTopAppBar
import ch.epfl.skysync.components.LoadingComponent
import ch.epfl.skysync.database.DateUtility
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
import ch.epfl.skysync.util.hasError
import ch.epfl.skysync.util.inputNonNullValidation
import ch.epfl.skysync.util.nbPassengerInputValidation
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlightForm(
    navController: NavHostController,
    currentFlight: Flight?,
    modifyFlight: Boolean,
    title: String,
    allFlightTypes: State<List<FlightType>>,
    allRoleTypes: List<RoleType>,
    availableVehicles: State<List<Vehicle>>,
    availableBalloons: State<List<Balloon>>,
    availableBaskets: State<List<Basket>>,
    availableUsers: State<List<User>>,
    onSaveFlight: (PlannedFlight) -> Unit,
    refreshDate: (LocalDate, TimeSlot) -> Unit,
) {
  Scaffold(modifier = Modifier.fillMaxSize(), topBar = { CustomTopAppBar(navController, title) }) {
      padding ->
    // If there is a flight to be modified wait until it is not null
    if (currentFlight == null && modifyFlight) {
      LoadingComponent(isLoading = true, onRefresh = { /*TODO*/}) {}
    } else {
      Column {
        val defaultPadding = 16.dp
        val defaultTimeSlot = TimeSlot.AM

        var nbPassengersValue = remember {
          mutableStateOf(currentFlight?.nPassengers?.toString() ?: "")
        }
        var flightTypeValue: FlightType? by remember { mutableStateOf(currentFlight?.flightType) }
        var dateValue by remember { mutableStateOf(currentFlight?.date ?: LocalDate.now()) }
        var timeSlotValue: TimeSlot by remember {
          mutableStateOf(currentFlight?.timeSlot ?: defaultTimeSlot)
        }
        val selectedVehicles: MutableState<List<Vehicle?>> = remember {
          mutableStateOf(currentFlight?.vehicles ?: listOf())
        }

        var balloonValue: Balloon? by remember { mutableStateOf(currentFlight?.balloon) }
        var basketValue: Basket? by remember { mutableStateOf(currentFlight?.basket) }

        val selectedTeamMembers = remember {
          mutableStateListOf(
              *currentFlight?.team?.roles?.toTypedArray()
                  ?: Role.initRoles(BASE_ROLES).toTypedArray())
        }

        var openDatePicker by remember { mutableStateOf(false) }
        val lazyListState = rememberLazyListState()

        var nbPassengersValueError by remember { mutableStateOf(false) }
        var flightTypeValueError by remember { mutableStateOf(false) }

        val isSelectedBalloonAvailable =
            balloonValue == null || availableBalloons.value.contains(balloonValue)
        val isSelectedBasketAvailable =
            basketValue == null || availableBaskets.value.contains(basketValue)

        val isSelectedVehicleAvailable =
            selectedVehicles.value.filterNotNull().associateWith {
              availableVehicles.value.contains(it)
            }
        val isSelectedUserAvailable =
            Team(selectedTeamMembers).getUsers().associateWith { availableUsers.value.contains(it) }

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
                        dateValue = DateUtility.dateToLocalDate(timeInMillis)
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
                      refreshDate(dateValue, timeSlotValue)
                    },
                    items = TimeSlot.entries)
              }
              // Drop down menu for the flight type
              item {
                TitledDropDownMenu(
                    defaultPadding = defaultPadding,
                    title = "Flight Type",
                    value = flightTypeValue,
                    onclickMenu = { item -> flightTypeValue = item },
                    items = allFlightTypes.value,
                    showString = { it?.name ?: "Choose the flightType" },
                    isError = flightTypeValueError,
                    messageError = "Please choose a flight type")
              }
              // Section to add the crew members
              item {
                TeamHeader(
                    defaultPadding = defaultPadding,
                    crewMembers = selectedTeamMembers,
                    allRoleTypes = allRoleTypes,
                    availableUsers =
                        availableUsers.value.filterNot {
                          Team(selectedTeamMembers).getUsers().contains(it)
                        })
              }
              selectedTeamMembers.withIndex().forEach { (index, role) ->
                item {
                  RoleField(
                      defaultPadding = defaultPadding,
                      role = role,
                      index = index,
                      onDelete = { selectedTeamMembers.removeAt(index) },
                      onReassign = { user ->
                        selectedTeamMembers[index] = Role(role.roleType, user)
                      },
                      availableUsers =
                          availableUsers.value.filter {
                            !Team(selectedTeamMembers).getUsers().contains(it)
                          },
                      isSelectedUserAvailable =
                          isSelectedUserAvailable.getOrDefault(role.assignedUser, true))
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
                            val tempMutableList = selectedVehicles.value.toMutableList()
                            tempMutableList.add(null)
                            selectedVehicles.value = tempMutableList.toList()
                          },
                      ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Vehicle")
                      }
                    }
              }
              selectedVehicles.value.withIndex().forEach() { (index, vehicle) ->
                item {
                  Row(
                      modifier = Modifier.fillMaxWidth(),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically) {
                        CustomDropDownMenu(
                            modifier = Modifier.weight(1f),
                            defaultPadding = defaultPadding,
                            title = "Vehicle $index",
                            value = vehicle,
                            onclickMenu = { item ->
                              val tempMutableList = selectedVehicles.value.toMutableList()
                              tempMutableList[index] = item
                              selectedVehicles.value = tempMutableList.toList()
                            },
                            items =
                                availableVehicles.value.filter {
                                  !selectedVehicles.value.contains(it)
                                },
                            showString = { it?.name ?: "choose vehicle" },
                            isError = !isSelectedVehicleAvailable.getOrDefault(vehicle, true),
                            messageError = "Vehicle not available")

                        IconButton(
                            modifier = Modifier.testTag("Delete Vehicle $index Button"),
                            onClick = {
                              val tempMutableList = selectedVehicles.value.toMutableList()
                              tempMutableList.removeAt(index)
                              selectedVehicles.value = tempMutableList.toList()
                            },
                        ) {
                          Icon(Icons.Default.Delete, contentDescription = "Delete Vehicle $index")
                        }
                      }
                }
              }
              // Drop down menu for the balloon

              item {
                TitledIconDropDownMenu(
                    defaultPadding = defaultPadding,
                    title = "Balloon",
                    value = balloonValue,
                    onclickMenu = { item -> balloonValue = item },
                    onDeletion = { balloonValue = null },
                    items = availableBalloons.value.filter { it != balloonValue },
                    showString = { it?.name ?: "Choose the balloon" },
                    isError = !isSelectedBalloonAvailable,
                    messageError = "Balloon not available")
              }
              // Drop down menu for the basket
              item {
                TitledIconDropDownMenu(
                    defaultPadding = defaultPadding,
                    title = "Basket",
                    value = basketValue,
                    onclickMenu = { item -> basketValue = item },
                    onDeletion = { basketValue = null },
                    items = availableBaskets.value.filter { it != basketValue },
                    showString = { it?.name ?: "Choose the basket" },
                    isError = !isSelectedBasketAvailable,
                    messageError = "Basket not available")
              }
            }
        // Button to add/modify the flight to the list of flights
        Button(
            modifier = Modifier.fillMaxWidth().padding(defaultPadding).testTag("$title Button"),
            onClick = {
              nbPassengersValueError = !nbPassengerInputValidation(nbPassengersValue.value)
              flightTypeValueError = !inputNonNullValidation(flightTypeValue)
              val vehiclesError = isSelectedVehicleAvailable.values.any { !it }
              val usersError = isSelectedUserAvailable.values.any { !it }
              isError =
                  hasError(
                      nbPassengersValueError,
                      flightTypeValueError,
                      !isSelectedBalloonAvailable,
                      !isSelectedBasketAvailable,
                      vehiclesError,
                      usersError)
              if (!isError) {
                val allRoles = selectedTeamMembers.toList()
                val team = Team(allRoles)
                val newFlight =
                    PlannedFlight(
                        nPassengers = nbPassengersValue.value.toInt(),
                        date = dateValue,
                        flightType = flightTypeValue!!,
                        timeSlot = timeSlotValue,
                        balloon = balloonValue,
                        basket = basketValue,
                        vehicles = selectedVehicles.value.filterNotNull(),
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

@SuppressLint("DefaultLocale")
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

@Composable
fun RoleField(
    defaultPadding: Dp,
    role: Role,
    index: Int,
    onDelete: () -> Unit,
    onReassign: (User?) -> Unit,
    specialName: String = "",
    availableUsers: List<User>,
    isSelectedUserAvailable: Boolean
) {
  Text(
      modifier =
          Modifier.fillMaxWidth().padding(horizontal = defaultPadding).testTag("RoleField $index"),
      text = role.roleType.description,
  )
  Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically) {
        CustomDropDownMenu(
            modifier = Modifier.weight(1f),
            isError = !isSelectedUserAvailable,
            defaultPadding = defaultPadding,
            title = "overview:${role.roleType.description}",
            value = role.assignedUser,
            onclickMenu = { item -> onReassign(item) },
            items = availableUsers,
            showString = { it?.name() ?: "choose a user" },
            messageError = "User not available")

        IconButton(
            modifier = Modifier.testTag("Delete Crew Member $index"), onClick = { onDelete() }) {
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
                showString = { it?.name() ?: "choose a user" },
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
/**
 * @Composable
 * @Preview fun FlightFormPreview() { val navController = rememberNavController() val currentFlight?
 *   PlannedFlight( nPassengers = 1, date = LocalDate.now(), flightType = FlightType.PREMIUM,
 *   timeSlot = TimeSlot.AM, vehicles = listOf(), balloon = null, basket = null, id = "testId")
 *   FlightForm( navController = navController, currentFlight? modifyFlight = false, "Flight Form",
 *   emptyList(), emptyList(), emptyList(), emptyList(), emptyList(), emptyList(), onSaveFlight =
 *   {}, refreshDate = { _, _ -> }) }
 */
