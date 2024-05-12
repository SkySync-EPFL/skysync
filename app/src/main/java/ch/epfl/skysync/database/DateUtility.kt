package ch.epfl.skysync.database

import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
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
   * Converts a date long in millisecond from since epoch a [LocalDate] object.
   *
   * @param date The long to be converted.
   * @return The equivalent [LocalDate] object.
   */
  fun dateToLocalDate(date: Long): LocalDate {
    return Instant.ofEpochMilli(date).atZone(ZoneId.of("GMT")).toLocalDate()
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

  /**
   * Converts a [Date] object to a [LocalTime] object.
   *
   * @param date The [Date] object to be converted.
   * @return The equivalent [LocalTime] object.
   */
  fun dateToLocalTime(date: Date): LocalTime {
    val instant = date.toInstant()
    val zonedDateTime = instant.atZone(ZoneId.systemDefault())
    return zonedDateTime.toLocalTime()
  }

  /**
   * Converts a [LocalDate] object to a string.
   *
   * @param date The [LocalDate] object to be converted.
   * @return The equivalent [LocalTime] object or --/--/-- if null.
   */
  fun localDateToString(date: LocalDate?): String {
    return date?.format(DateTimeFormatter.ofPattern("dd/MM/yy")) ?: "--/--/--"
  }

  /**
   * Retrieves the hours and minutes from a [Date] object and returns a String. I null returns --:--
   *
   * @param date The [Date] object to be converted.
   * @return The date in "HH:mm" format or --:--
   */
  fun dateToHourMinuteString(date: Date?): String {
    date?.let {
      val localTime = Instant.ofEpochMilli(date.time).atZone(ZoneId.of("GMT")).toLocalTime()
      return localTime.format(DateTimeFormatter.ofPattern("HH:mm"))
    }
    return "--:--"
  }
}
