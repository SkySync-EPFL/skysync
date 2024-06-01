package ch.epfl.skysync.models.flight

import androidx.compose.ui.graphics.Color
import ch.epfl.skysync.models.user.User
import ch.epfl.skysync.ui.theme.lightBlue
import ch.epfl.skysync.ui.theme.lightGray
import ch.epfl.skysync.ui.theme.lightGreen
import ch.epfl.skysync.ui.theme.lightViolet
import ch.epfl.skysync.ui.theme.middleLightRed

/**
 * Enum class representing the different flight statuses.
 *
 * The statuses are ordered as follows:
 * - IN_PLANNING
 * - READY_FOR_CONFIRMATION
 * - CONFIRMED
 * - MISSING_REPORT
 * - COMPLETED
 *
 * Each status is associated with a text and a display color.
 *
 * The [toString] function is used to get the text of the status.
 *
 * The [filterCompletedFlights] function is used to update the status of all FinishedFlights and
 * filter out the flights that are completed.
 */
enum class FlightStatus(val text: String, val displayColor: Color) {
  IN_PLANNING("planned", lightGray), // still missing some information
  READY_FOR_CONFIRMATION("ready", lightBlue), // has all the information needed to be confirmed
  CONFIRMED("confirmed", lightGreen), // has been confirmed
  //  IN_PROGRESS("in progress", lightOrange), // is currently happening (flight day)
  MISSING_REPORT("missing report", middleLightRed), // landed but missing the report
  COMPLETED("completed", lightViolet);

  override fun toString(): String {
    return text
  }

  companion object {
    /**
     * Updates the status of all FinishedFlights and filters out the flights that are completed.
     *
     * @param flights The list of flights to update and filter.
     * @param user The user to check the report status for.
     * @return The list of flights that are not completed.
     */
    fun filterCompletedFlights(flights: List<Flight>, user: User): List<Flight> {
      return flights
          .map {
            if (it is FinishedFlight) {
              it.updateFlightStatus(user)
            } else {
              it
            }
          }
          .filter { it.getFlightStatus() != COMPLETED }
    }
  }
}
