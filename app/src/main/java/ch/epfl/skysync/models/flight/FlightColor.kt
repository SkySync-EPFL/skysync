package ch.epfl.skysync.models.flight

import androidx.compose.ui.graphics.Color
import ch.epfl.skysync.ui.theme.blue
import ch.epfl.skysync.ui.theme.lightGreen
import ch.epfl.skysync.ui.theme.lightOrange
import ch.epfl.skysync.ui.theme.lightPink
import ch.epfl.skysync.ui.theme.lightRed

/**
 * Enum class representing the different flight colors.
 *
 * The colors are ordered as follows:
 * - RED
 * - BLUE
 * - ORANGE
 * - GREEN
 * - PINK
 * - NO_COLOR
 *
 * The [toColor] function is used to convert this enum to a [Color] object.
 */
enum class FlightColor {
  RED,
  BLUE,
  ORANGE,
  GREEN,
  PINK,
  NO_COLOR;

  /**
   * Converts this enum to a [Color] object.
   *
   * @return The [Color] object corresponding to this enum, or null if the color is not defined.
   */
  fun toColor(): Color? {
    return flightColorOptions[this]
  }
}

/** A map that associates each [FlightColor] with its corresponding [Color] object. */
val flightColorOptions =
    mapOf(
        FlightColor.RED to lightRed,
        FlightColor.BLUE to blue,
        FlightColor.GREEN to lightGreen,
        FlightColor.PINK to lightPink,
        FlightColor.ORANGE to lightOrange,
        FlightColor.NO_COLOR to Color.Gray)
