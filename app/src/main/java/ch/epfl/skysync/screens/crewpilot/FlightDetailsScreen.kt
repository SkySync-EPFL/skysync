package ch.epfl.skysync.screens.crewpilot

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import ch.epfl.skysync.components.ConfirmedFlightDetailBottom
import ch.epfl.skysync.components.CustomTopAppBar
import ch.epfl.skysync.components.FinishedFlightDetailBottom
import ch.epfl.skysync.components.FlightDetails
import ch.epfl.skysync.components.LoadingComponent
import ch.epfl.skysync.models.flight.ConfirmedFlight
import ch.epfl.skysync.models.flight.FinishedFlight
import ch.epfl.skysync.models.user.Crew
import ch.epfl.skysync.navigation.Route
import ch.epfl.skysync.ui.theme.lightGray
import ch.epfl.skysync.viewmodel.FinishedFlightsViewModel
import ch.epfl.skysync.viewmodel.FlightsViewModel
import ch.epfl.skysync.viewmodel.InFlightViewModel

@Composable
fun FlightDetailScreen(
    navController: NavHostController,
    flightId: String,
    viewModel: FlightsViewModel,
    inFlightViewModel: InFlightViewModel,
    finishedFlightsViewModel: FinishedFlightsViewModel
) {
    val uncompletedFlight by viewModel.getFlight(flightId).collectAsStateWithLifecycle()
    val finishedFlight by finishedFlightsViewModel.getFlight(flightId).collectAsStateWithLifecycle()
    val flight = if (uncompletedFlight != null) uncompletedFlight else finishedFlight

    val user by viewModel.currentUser.collectAsStateWithLifecycle()
    Scaffold(
        topBar = { CustomTopAppBar(navController = navController, title = "Flight Detail") },
        containerColor = lightGray
    ) { padding ->
        if (flight == null) {
            LoadingComponent(isLoading = true, onRefresh = {}) {}
        } else {
            FlightDetails(flight = flight, padding = padding) {
                if (flight is ConfirmedFlight) {
                    ConfirmedFlightDetailBottom(
                        { navController.popBackStack() },
                        {},
                        false,
                    )
                } else if (flight is FinishedFlight) {
                    FinishedFlightDetailBottom(
                        reportClick = {
                            var reportFound =
                            flight.reportId.any { report -> (report.author == user!!.id && report.id ==flightId) }
                                if (reportFound) {
                                    navController.navigate(Route.REPORT + "/${flightId}")
                                } else if (user!! is Crew) {
                                    navController.navigate(Route.CREW_REPORT + "/${flightId}")
                                } else {
                                    navController.navigate(Route.PILOT_REPORT + "/${flightId}")
                                }
                        },
                        flightTraceClick = {
                            inFlightViewModel.setCurrentFlight(flightId)
                            inFlightViewModel.startDisplayFlightTrace()
                            navController.navigate(Route.FLIGHT)
                        })
                }
            }
        }
    }
}

