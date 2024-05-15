package ch.epfl.skysync.models.flight

import androidx.compose.ui.graphics.Color
import ch.epfl.skysync.ui.theme.blue
import ch.epfl.skysync.ui.theme.lightGreen
import ch.epfl.skysync.ui.theme.lightOrange
import ch.epfl.skysync.ui.theme.lightPink
import ch.epfl.skysync.ui.theme.lightRed

enum class FlightColor {
  RED,
  BLUE,
  ORANGE,
  GREEN,
  PINK,
  NO_COLOR
}

val flightColorOptions =
    mapOf(
        FlightColor.RED to lightRed,
        FlightColor.BLUE to blue,
        FlightColor.GREEN to lightGreen,
        FlightColor.PINK to lightPink,
        FlightColor.ORANGE to lightOrange,
        FlightColor.NO_COLOR to Color.Gray)
