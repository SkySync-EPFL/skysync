package ch.epfl.skysync.screens.crewpilot

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import ch.epfl.skysync.components.CrewConfirmedFlightDetailBottom
import ch.epfl.skysync.components.CustomTopAppBar
import ch.epfl.skysync.components.FinishedFlightDetailBottom
import ch.epfl.skysync.components.FlightDetails
import ch.epfl.skysync.components.LoadingComponent
import ch.epfl.skysync.models.flight.ConfirmedFlight
import ch.epfl.skysync.models.flight.FinishedFlight
import ch.epfl.skysync.models.flight.RoleType
import ch.epfl.skysync.navigation.Route
import ch.epfl.skysync.ui.theme.lightGray
import ch.epfl.skysync.viewmodel.FlightsViewModel
import ch.epfl.skysync.viewmodel.InFlightViewModel

@Composable
fun CrewFlightDetailScreen(
    navController: NavHostController,
    flightId: String,
    viewModel: FlightsViewModel,
    inFlightViewModel: InFlightViewModel,
) {

  val flight by viewModel.getFlight(flightId).collectAsStateWithLifecycle()
  Scaffold(
      topBar = { CustomTopAppBar(navController = navController, title = "Flight Detail") },
      bottomBar = {
        when (flight) {
          is ConfirmedFlight -> {
            CrewConfirmedFlightDetailBottom(okClick = { navController.popBackStack() })
          }
          is FinishedFlight -> {
            FinishedFlightDetailBottom(
                reportClick = {
                    val reportDone = (flight as FinishedFlight).reportId.any { report ->
                        report.author == viewModel.userId
                    }
                    if (reportDone) {
                        navController.navigate(Route.REPORT + "/${flightId}")
                    }
                    else{
                        if ((flight as FinishedFlight).team.hasUserRole(RoleType.PILOT,viewModel.userId!!)){
                            navController.navigate(Route.PILOT_REPORT + "/${flightId}")
                        }
                        else{

                            navController.navigate(Route.CREW_REPORT + "/${flightId}")
                        }
                    }
                }
                ,
                flightTraceClick = {
                  inFlightViewModel.startDisplayFlightTrace(flight as FinishedFlight)
                  navController.navigate(Route.FLIGHT)
                })
          }
        }
      },
      containerColor = lightGray) { padding ->
        if (flight == null) {
          LoadingComponent(isLoading = true) {}
        } else {
          FlightDetails(flight = flight!!, padding = padding)
        }
      }
}
