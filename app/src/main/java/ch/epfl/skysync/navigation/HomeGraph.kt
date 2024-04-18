package ch.epfl.skysync.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.database.tables.BalloonTable
import ch.epfl.skysync.database.tables.BasketTable
import ch.epfl.skysync.database.tables.FlightTable
import ch.epfl.skysync.database.tables.FlightTypeTable
import ch.epfl.skysync.database.tables.VehicleTable
import ch.epfl.skysync.models.UNSET_ID
import ch.epfl.skysync.models.calendar.TimeSlot
import ch.epfl.skysync.models.flight.FlightType
import ch.epfl.skysync.models.flight.PlannedFlight
import ch.epfl.skysync.screens.AddFlightScreen
import ch.epfl.skysync.screens.ChatScreen
import ch.epfl.skysync.screens.FlightScreen
import ch.epfl.skysync.screens.HomeScreen
import ch.epfl.skysync.viewmodel.FlightsViewModel
import com.google.firebase.auth.FirebaseUser
import java.time.LocalDate

/** Graph of the main screens of the app */
fun NavGraphBuilder.homeGraph(navController: NavHostController, user: FirebaseUser?) {
  // Only there for preview purposes. It will then be integrated in a model view
  val listFlights =
      mutableListOf(
          PlannedFlight(
              nPassengers = 4,
              date = LocalDate.of(2024, 3, 19),
              timeSlot = TimeSlot.AM,
              flightType = FlightType.FONDUE,
              vehicles = listOf(),
              balloon = null,
              basket = null,
              id = UNSET_ID),
          PlannedFlight(
              nPassengers = 2,
              date = LocalDate.of(2024, 3, 20),
              timeSlot = TimeSlot.AM,
              flightType = FlightType.DISCOVERY,
              vehicles = listOf(),
              balloon = null,
              basket = null,
              id = UNSET_ID),
          PlannedFlight(
              nPassengers = 3,
              date = LocalDate.of(2024, 3, 22),
              timeSlot = TimeSlot.PM,
              flightType = FlightType.DISCOVERY,
              vehicles = listOf(),
              balloon = null,
              basket = null,
              id = UNSET_ID),
      )
  navigation(startDestination = Route.HOME, route = Route.MAIN) {
    personalCalendar(navController, user)
    composable(Route.CHAT) { ChatScreen(navController) }
    composable(Route.FLIGHT) { FlightScreen(navController) }
    composable(Route.HOME) {
        val db = FirestoreDatabase()
        val flightTable = FlightTable(db)
        val balloonTable = BalloonTable(db)
        val basketTable = BasketTable(db)
        val flightTypeTable = FlightTypeTable(db)
        val vehicleTable = VehicleTable(db)
        val viewModel =
            FlightsViewModel.createViewModel(
                flightTable = flightTable,
                balloonTable = balloonTable,
                basketTable = basketTable,
                flightTypeTable = flightTypeTable,
                vehicleTable = vehicleTable
            )
        HomeScreen(navController, viewModel)

    }
    composable(Route.ADD_FLIGHT) { AddFlightScreen(navController, listFlights) }
  }
}
