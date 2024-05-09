package ch.epfl.skysync.util

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date

/** Useful methods for input validation */
fun inputValidation(vararg errors: Boolean): Boolean {
  return errors.any { it }
}

/** Useful formatting methods for dates and times */
fun dateToLocalDate(date: Long): LocalDate {
  return Instant.ofEpochMilli(date).atZone(ZoneId.of("GMT")).toLocalDate()
}

fun getFormattedDate(date: LocalDate?): String {
  return date?.format(DateTimeFormatter.ofPattern("dd/MM/yy")) ?: "--/--/--"
}

fun getFormattedTime(time: Date?): String {
  time?.let {
    val localTime = Instant.ofEpochMilli(it.time).atZone(ZoneId.of("GMT")).toLocalTime()
    return localTime.format(DateTimeFormatter.ofPattern("HH:mm"))
  }
  return "--:--"
}
