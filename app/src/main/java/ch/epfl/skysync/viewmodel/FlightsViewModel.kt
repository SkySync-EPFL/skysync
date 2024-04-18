package ch.epfl.skysync.viewmodel
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import ch.epfl.skysync.database.tables.BalloonTable
import ch.epfl.skysync.database.tables.BasketTable
import ch.epfl.skysync.database.tables.FlightTable
import ch.epfl.skysync.database.tables.FlightTypeTable
import ch.epfl.skysync.database.tables.VehicleTable
import ch.epfl.skysync.models.flight.Balloon
import ch.epfl.skysync.models.flight.Basket
import ch.epfl.skysync.models.flight.Flight
import ch.epfl.skysync.models.flight.FlightType
import ch.epfl.skysync.models.flight.PlannedFlight
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow


/**
 * ViewModel for the user
 *
 * @param firebaseUser: FirebaseUser? the firebase user
 */
class FlightsViewModel(
//  private val firebaseUserId: String,
//  private val userTable: UserTable,
    private val flightTable: FlightTable,
    private val balloonTable: BalloonTable,
    private val basketTable: BasketTable,
    private val flightTypeTable: FlightTypeTable,
    private val vehicleTable: VehicleTable,
) : ViewModel() {
    companion object {
        /** creates a view model by accepting the firebase user as an argument */
        @Composable
        fun createViewModel(
            flightTable: FlightTable,
            balloonTable: BalloonTable,
            basketTable: BasketTable,
            flightTypeTable: FlightTypeTable,
            vehicleTable: VehicleTable,



        ): FlightsViewModel {
            return viewModel<FlightsViewModel>(
                factory =
                object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return FlightsViewModel(
                            flightTable,
                            balloonTable,
                            basketTable,
                            flightTypeTable,
                            vehicleTable,

                            ) as T
                    }
                })
        }
    }

    val _currentFlights: MutableStateFlow<List<Flight>> = MutableStateFlow(emptyList())
    val _currentBalloons: MutableStateFlow<List<Balloon>> = MutableStateFlow(emptyList())
    val _currentBaskets: MutableStateFlow<List<Basket>> = MutableStateFlow(emptyList())
    val _currentFlightTypes: MutableStateFlow<List<FlightType>> = MutableStateFlow(emptyList())


    val currentFlights = _currentFlights.asStateFlow()
    val currentBalloons = _currentBalloons.asStateFlow()
    val currentBaskets = _currentBaskets.asStateFlow()
    val currentFlightTypes = _currentFlightTypes.asStateFlow()



    fun refreshAll(
    ) {
        refreshCurrentFlights()
        refreshCurrentBalloons()
        refreshCurrentBaskets()
        refreshCurrentFlightTypes()
    }



    fun refreshCurrentBalloons(
    ) {
        balloonTable.getAll(
            { balloons ->
                _currentBalloons.value = balloons
            },
            { exception ->
                Log.d("Balloonrefresh", exception.toString())
            }
        )
    }

    fun refreshCurrentBaskets(
    ) {
        basketTable.getAll(
            { baskets ->
                _currentBaskets.value = baskets
            },
            { exception ->
                Log.d("Basketrefresh", exception.toString())
            }
        )
    }

    fun refreshCurrentFlightTypes(
    ) {
        flightTypeTable.getAll(
            { flightTypes ->
                _currentFlightTypes.value = flightTypes
            },
            { exception ->
                Log.d("FlightTyperefresh", exception.toString())
            }
        )
    }


    fun refreshCurrentFlights(

    ) {
        //todo: check for dirty data (flights added/modified/deleted while offline)

        flightTable.getAll(
            { flights ->
                _currentFlights.value = flights
            },
            { exception ->
                Log.d("FLightrefresh", exception.toString())
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
                _currentFlights.value -= flight
                refreshCurrentFlights()
            },
            { exception ->}
        )
    }

    fun deleteFlight(flightId: String) {
        getFlightFromId(flightId)?.let { deleteFlight(it) }
    }


    fun addFlight(
        flight: PlannedFlight,
    ) {

        flightTable.add(
            flight,
            {
                val flightWithCurrentId = flight.setId(it)
                _currentFlights.value += flightWithCurrentId
                refreshCurrentFlights()
            },
            { exception -> }
        )

    }


    /**
     * return the flight with flight id if it exists in the list of current flights
     */
    fun getFlightFromId(flightId: String): Flight? {
        return currentFlights.value.find { it.id == flightId }
    }

    init {
        refreshAll()
    }
}
