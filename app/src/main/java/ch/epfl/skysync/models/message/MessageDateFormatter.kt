package ch.epfl.skysync.models.message

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Date

object MessageDateFormatter {
  /** Format a [Date] object to a readable date/time */
  fun format(date: Date): String {
    val now = Instant.now()
    val hoursDiff = date.toInstant().until(now, ChronoUnit.MINUTES)
    if (hoursDiff < 24 * 60) {
      return DateTimeFormatter.ofPattern("HH:mm")
          .format(date.toInstant().atZone(ZoneId.systemDefault()))
    }
    return DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
        .format(date.toInstant().atZone(ZoneId.systemDefault()))
  }
}
