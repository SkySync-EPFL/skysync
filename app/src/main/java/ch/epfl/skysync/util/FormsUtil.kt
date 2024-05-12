package ch.epfl.skysync.util

/** Useful methods for input validation */
fun hasNoError(vararg errors: Boolean): Boolean {
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
