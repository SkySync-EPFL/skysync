package ch.epfl.skysync.util

/** Check if there is no error */
fun hasError(vararg errors: Boolean): Boolean {
  return errors.any { it }
}

/** Check if the input is not null */
fun <T> inputNonNullValidation(value: T?): Boolean {
  return value != null
}

/** Check if the input for a number of passenger is right */
fun nbPassengerInputValidation(nbPassenger: String): Boolean {
  return nbPassenger.toIntOrNull() != null && nbPassenger.toInt() > 0
}

/** Check if the input for a bottle is right */
fun bottleInputValidation(bottle: String): Boolean {
  return bottle.toIntOrNull() != null && bottle.toInt() >= 0
}

/** Check if the input for an email is right */
fun validateEmail(email: String): Boolean {
  return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

/** Check if the input for a text is right */
fun textInputValidation(name: String): Boolean {
  return name.isEmpty()
}
