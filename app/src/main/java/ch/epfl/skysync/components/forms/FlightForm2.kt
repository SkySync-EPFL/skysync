package ch.epfl.skysync.components.forms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import java.time.LocalDate
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlightForm2(
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
          mutableStateOf(currentFlight?.vehicles ?: listOf(null))
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

@Composable
fun RoleField2(
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
