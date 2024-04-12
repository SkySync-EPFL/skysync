package ch.epfl.skysync.database

import java.time.LocalDate
import java.time.ZoneOffset
import java.util.Date

/**
 * Utility object for converting between [Date] and [LocalDate]. Firestore stores [Date] as a
 * string, simplifying queries. [LocalDate], on the other hand, is stored as a collection of fields,
 * which can complicate querying in Firestore.
 */
object DateLocalDateConverter {
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
}
