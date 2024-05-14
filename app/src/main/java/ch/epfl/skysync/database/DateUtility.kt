package ch.epfl.skysync.database

import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Date

/**
 * Utility object for date, time objects
 *
 * Converts between [Date] and [LocalDate]. Firestore stores [Date] as a string, simplifying
 * queries. [LocalDate], on the other hand, is stored as a collection of fields, which can
 * complicate querying in Firestore.
 *
 * Format [LocalTime] to string, parse from string.
 */
object DateUtility {
  /**
   * Converts a [Date] object to a [LocalDate] object.
   *
   * @param date The [Date] object to be converted.
   * @return The equivalent [LocalDate] object.
   */
  fun dateToLocalDate(date: Date): LocalDate {
    return date.toInstant().atZone(ZoneOffset.systemDefault()).toLocalDate()
  }

  /**
   * Converts a [LocalDate] object to a [Date] object.
   *
   * @param localDate The [LocalDate] object to be converted.
   * @return The equivalent [Date] object.
   */
  fun localDateToDate(localDate: LocalDate): Date {
    return Date.from(localDate.atStartOfDay(ZoneOffset.UTC).toInstant())
  }

  /**
   * Converts a [LocalTime] object to a string in "HH:mm:ss" format.
   *
   * @param localTime The [LocalTime] object to be converted.
   * @return The string representation of the [LocalTime] object.
   */
  fun localTimeToString(localTime: LocalTime): String {
    val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
    return localTime.format(formatter)
  }

  /**
   * Parses a string in "HH:mm:ss" format to a [LocalTime] object.
   *
   * @param raw The string to be parsed.
   * @return The [LocalTime] object parsed from the string.
   */
  fun stringToLocalTime(raw: String): LocalTime {
    val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
    return LocalTime.parse(raw, formatter)
  }

  /** Formats the given time in milliseconds to a string in the format "HH:MM:SS". */
  fun formatTime(milliseconds: Long): String {
    val secondsRounded = milliseconds / 1000
    val hours = secondsRounded / 3600
    val minutes = (secondsRounded % 3600) / 60
    val remainingSeconds = secondsRounded % 60
    return String.format("%02d:%02d:%02d", hours, minutes, remainingSeconds)
  }
}
