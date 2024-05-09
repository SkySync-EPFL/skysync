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

fun <T> inputNonNullValidation(value: T?): Boolean {
  return value != null
}

fun nbPassengerInputValidation(nbPassenger: String): Boolean {
  return nbPassenger.toIntOrNull() != null && nbPassenger.toInt() > 0
}

fun bottleInputValidation(bottle: String): Boolean {
  return bottle.toIntOrNull() != null && bottle.toInt() >= 0
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
