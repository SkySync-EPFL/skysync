package ch.epfl.skysync.util

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
