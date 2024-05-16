package ch.epfl.skysync.viewmodel

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import ch.epfl.skysync.Repository
import ch.epfl.skysync.components.SnackbarManager
import ch.epfl.skysync.database.DateUtility
import ch.epfl.skysync.models.UNSET_ID
import ch.epfl.skysync.models.calendar.TimeSlot
import ch.epfl.skysync.models.flight.Balloon
import ch.epfl.skysync.models.flight.BalloonQualification
import ch.epfl.skysync.models.flight.Basket
import ch.epfl.skysync.models.flight.FinishedFlight
import ch.epfl.skysync.models.flight.FlightColor
import ch.epfl.skysync.models.flight.FlightType
import ch.epfl.skysync.models.flight.Role
import ch.epfl.skysync.models.flight.RoleType
import ch.epfl.skysync.models.flight.Team
import ch.epfl.skysync.models.flight.Vehicle
import ch.epfl.skysync.models.location.LocationPoint
import ch.epfl.skysync.models.user.Admin
import ch.epfl.skysync.models.user.Crew
import ch.epfl.skysync.models.user.Pilot
import ch.epfl.skysync.models.user.User
import java.time.LocalDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** ViewModel for the user for the finished flights */
class FinishedFlightsViewModel(val repository: Repository, val userId: String?) : ViewModel() {
  companion object {
    @Composable
    fun createViewModel(
        repository: Repository,
        userId: String?,
    ): FinishedFlightsViewModel {
      return viewModel<FinishedFlightsViewModel>(
          factory =
              object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                  return FinishedFlightsViewModel(repository, userId) as T
                }
              })
    }
  }

  // TODO suppress it when the database is implemented
  var pilot1 =
      Pilot(
          id = "id-pilot-1",
          firstname = "pilot-1",
          lastname = "lastname-pilot-1",
          email = "pilot1.bob@skysnc.ch",
          qualification = BalloonQualification.LARGE)
  var crew1 =
      Crew(
          id = "id-crew-1",
          firstname = "crew-1",
          lastname = "lastname-crew-1",
          email = "crew1.bob@skysnc.ch",
      )
  var crew2 =
      Crew(
          id = "id-crew-2",
          firstname = "crew-2",
          lastname = "lastname-crew-2",
          email = "crew2.denis@skysnc.ch",
      )
  var vehicle2 = Vehicle(name = "vehicle-2")
  var date1 = LocalDate.of(2024, 8, 14)
  val flight1 =
      FinishedFlight(
          id = UNSET_ID,
          nPassengers = 3,
          team = Team(roles = listOf(Role(RoleType.PILOT, pilot1), Role(RoleType.CREW, crew1))),
          flightType = FlightType.DISCOVERY,
          balloon = Balloon("Balloon-1", BalloonQualification.SMALL),
          basket = Basket("Basket-1", false),
          date = date1,
          timeSlot = TimeSlot.PM,
          vehicles = listOf(vehicle2),
          color = FlightColor.GREEN,
          flightTime = 3_600_000, // 1 hour
          landingTime =
              DateUtility.createDate(date1.year, date1.monthValue, date1.dayOfMonth, 15, 0),
          takeOffTime =
              DateUtility.createDate(date1.year, date1.monthValue, date1.dayOfMonth, 14, 0),
          landingLocation = LocationPoint(0, 0.0, 0.0, "Cossonay"),
          takeOffLocation = LocationPoint(0, 1.0, 1.0, "Ecublens"))
  val flight2 =
      FinishedFlight(
          id = UNSET_ID,
          nPassengers = 2,
          team = Team(roles = listOf(Role(RoleType.PILOT, pilot1), Role(RoleType.CREW, crew2))),
          flightType = FlightType.DISCOVERY,
          balloon = Balloon("Balloon-1", BalloonQualification.SMALL),
          basket = Basket("Basket-1", false),
          date = date1,
          timeSlot = TimeSlot.AM,
          vehicles = listOf(vehicle2),
          color = FlightColor.GREEN,
          flightTime = 3_600_000, // 1 hour
          landingTime =
              DateUtility.createDate(date1.year, date1.monthValue, date1.dayOfMonth, 7, 0),
          takeOffTime =
              DateUtility.createDate(date1.year, date1.monthValue, date1.dayOfMonth, 6, 0),
          landingLocation = LocationPoint(0, 0.0, 0.0, "Lausanne"),
          takeOffLocation = LocationPoint(0, 1.0, 1.0, "Geneva"),
      )

  private val _currentFlights: MutableStateFlow<List<FinishedFlight>?> = MutableStateFlow(null)
  private val _currentUser: MutableStateFlow<User?> = MutableStateFlow(null)
  private val _selectedFlight: MutableStateFlow<FinishedFlight?> = MutableStateFlow(null)

  var currentFlights = _currentFlights.asStateFlow()
  val currentUser = _currentUser.asStateFlow()
  val selectedFlight = _selectedFlight.asStateFlow()
  private val allFlights = mutableListOf(flight1, flight2)

  fun refresh() {
    refreshUser()
    // refreshFlights()
  }

  private fun refreshUser() =
      viewModelScope.launch {
        _currentUser.value = repository.userTable.get(userId ?: UNSET_ID, onError = { onError(it) })
        getFlights()
      }

  /**
   * get the flights of the current user (replace refreshFlights() while finished flights are not in
   * the database)
   */
  fun getFlights() {
    when (_currentUser.value) {
      is Admin -> {
        _currentFlights.value = allFlights
      }
      is Pilot,
      is Crew -> {
        _currentFlights.value =
            allFlights.filter { it.team.roles.any { role -> role.assignedUser?.id == userId } }
      }
    }
  }
  /*fun refreshFlights() =
  viewModelScope.launch {
    if (_currentUser.value is Admin) {
      _currentFlights.value =
      repository.flightTable
          .getAll(onError = { onError(it) })
          .filterIsInstance<FinishedFlight>()
    } else if (_currentUser.value is Pilot || _currentUser.value is Crew) {
      _currentFlights.value =
      repository.userTable
          .retrieveAssignedFlights(
              repository.flightTable, userId ?: UNSET_ID, onError = { onError(it) })
          .filterIsInstance<FinishedFlight>()
    }
  }

  fun addFlight(flight: FinishedFlight) =
  viewModelScope.launch{
    repository.flightTable.add(flight, onError = { onError(it) })
    refreshFlights()
  }*/

  /*fun selectFlight(id: String) {
    _selectedFlight.value = currentFlights?.find { it.id == id }
  }*/
  /*
    /** Might be used in the future to get the flights by location with a search bar */
    fun getFlightByLocation(location: String): List<FinishedFlight>? {
      return _currentFlights.value?.filter {
        it.landingLocation.name == location ||
            it.takeOffLocation.name == location ||
            it.takeOffLocation.latlng().toString() == location ||
            it.landingLocation.latlng().toString() == location
      }
    }
  */
  /** Callback executed when an error occurs on database-related operations */
  private fun onError(e: Exception) {
    SnackbarManager.showMessage(e.message ?: "An unknown error occurred")
  }
}
