package ch.epfl.skysync.models.flight

import androidx.compose.ui.graphics.Color
import ch.epfl.skysync.ui.theme.lightBlue
import ch.epfl.skysync.ui.theme.lightBrown
import ch.epfl.skysync.ui.theme.lightGray
import ch.epfl.skysync.ui.theme.lightGreen
import ch.epfl.skysync.ui.theme.lightViolet

enum class FlightStatus(val text: String, val displayColor: Color) {
  IN_PLANNING("planned", lightGray), // still missing some information
  READY_FOR_CONFIRMATION("ready", lightBlue), // has all the information needed to be confirmed
  CONFIRMED("confirmed", lightGreen), // has been confirmed
  //  IN_PROGRESS("in progress", lightOrange), // is currently happening (flight day)
  MISSING_REPORT("missing report", lightBrown), // landed but missing the report
  COMPLETED("completed", lightViolet);

  override fun toString(): String {
    return text
  }
}
