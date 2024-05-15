package ch.epfl.skysync.util

import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date

/** Useful methods for input validation */
fun hasError(vararg errors: Boolean): Boolean {
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

fun validateEmail(email: String): Boolean {
  return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

fun textInputValidation(name: String): Boolean {
  return name.isEmpty()
}

/** Useful formatting methods for dates and times */
fun dateToLocalDate(date: Long): LocalDate {
  return Instant.ofEpochMilli(date).atZone(ZoneId.of("GMT")).toLocalDate()
}


/** extract time from date*/
fun dateToLocalTime(date: Date?): LocalTime =
    Instant.ofEpochMilli(date?.time?: 0).atZone(ZoneId.of("GMT")).toLocalTime()


fun getFormattedDate(date: LocalDate?): String {
  return date?.format(DateTimeFormatter.ofPattern("dd/MM/yy")) ?: "--/--/--"
}


fun getFormattedTime(time: LocalTime?): String {
  time?.let {
    return time.format(DateTimeFormatter.ofPattern("HH:mm"))
  }
  return "--:--"
}
