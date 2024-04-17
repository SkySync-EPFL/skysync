package ch.epfl.skysync.viewmodel

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import ch.epfl.skysync.database.tables.FlightTable
import ch.epfl.skysync.database.tables.UserTable
import ch.epfl.skysync.models.flight.Flight
import ch.epfl.skysync.models.flight.PlannedFlight
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.reflect.KFunction2
import kotlin.reflect.KFunction3

/**
 * ViewModel for the user
 *
 * @param firebaseUser: FirebaseUser? the firebase user
 */
class FlightsViewModel(
  firebaseUser: FirebaseUser,
  private val userTable: UserTable,
  private val flightTable: FlightTable,
) : ViewModel() {
  companion object {
    /** creates a view model by accepting the firebase user as an argument */
    @Composable
    fun createViewModel(
      firebaseUser: FirebaseUser,
      userTable: UserTable,
      flightTable: FlightTable

    ): FlightsViewModel {
      return viewModel<FlightsViewModel>(
          factory =
              object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                  return FlightsViewModel(firebaseUser, userTable, flightTable) as T
                }
              })
    }
  }

  lateinit var currentFlights: MutableStateFlow<List<Flight>>
  private val uid: String


  fun refreshCurrentFlights(

  ) {
    //todo: check for dirty data (flights added/modified/deleted while offline)

    flightTable.getAll(
      { flights ->
        currentFlights.value = flights
      },
      { exception ->
        // TODO: display connection error msg
      }
    )
  }

  /**
   * modifies the flight by deleting the old flight and adding a new one
   */
  fun modifyFlight(
    newFlight: PlannedFlight,
  ) {
    flightTable.delete(
      newFlight.id,
      {
        addFlight(newFlight)
      },
      { exception ->}
    )
  }

  fun deleteFlight(
    flight: Flight,
  ) {
    flightTable.delete(
      flight.id,
      {
        currentFlights.value = currentFlights.value - flight
        refreshCurrentFlights()
      },
      { exception ->}
    )
  }


  fun addFlight(
    flight: PlannedFlight,
  ) {

    flightTable.add(
      flight,
        {
          val flightWithCurrentId = flight.setId(it)
          currentFlights.value = currentFlights.value + flightWithCurrentId
            refreshCurrentFlights()
        },
        { exception ->}
    )

  }

  init {
    uid = firebaseUser.uid // directly pass as
  }
}

