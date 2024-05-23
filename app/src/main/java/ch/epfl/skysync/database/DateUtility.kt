package ch.epfl.skysync.database

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
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
  fun localDateAndTimeToDate(date: LocalDate, time: LocalTime): Date {
    val localDateTime = LocalDateTime.of(date, time)
    return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant())
  }

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
    return Instant.ofEpochMilli(date).atZone(ZoneId.systemDefault()).toLocalDate()
  }
  /**
   * Converts a [LocalDate] object to a [Date] object.
   *
   * @param localDate The [LocalDate] object to be converted.
   * @return The equivalent [Date] object.
   */
  fun localDateToDate(localDate: LocalDate): Date {
    return Date.from(localDate.atStartOfDay(ZoneOffset.systemDefault()).toInstant())
  }

  /**
   * Creates a [Date] object from the given year, month, day, hour, and minute.
   *
   * @return The [Date] object created.
   */
  fun createDate(year: Int, month: Int, day: Int, hour: Int, minute: Int): Date {
    return Date.from(LocalDate.of(year, month, day).atTime(hour, minute).toInstant(ZoneOffset.UTC))
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
   * Retrieves the hours and minutes from a [Date] object and returns a String. If null returns
   * --:--
   *
   * @param date The [Date] object to be converted.
   * @return The date in "HH:mm" format or --:--
   */
  fun dateToHourMinuteString(date: Date?): String {
    date?.let {
      val zoneId = ZoneId.systemDefault()
      val localTime = Instant.ofEpochMilli(date.time).atZone(zoneId).toLocalTime()
      return localTime.format(DateTimeFormatter.ofPattern("HH:mm"))
    }
    return "--:--"
  }

  fun hourMinuteStringToDate(time: String, date: LocalDate): Date {
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    val localTime = LocalTime.parse(time, formatter)
    val localDateTime = LocalDateTime.of(date, localTime)
    return Date.from(localDateTime.atZone(ZoneOffset.systemDefault()).toInstant())
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
