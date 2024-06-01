package ch.epfl.skysync.util

/**
 * Check if there is no error
 *
 * @param errors The list of errors to check
 * @return True if there is an error, false otherwise
 */
fun hasError(vararg errors: Boolean): Boolean {
  return errors.any { it }
}

/**
 * Check if the input is not null
 *
 * @param value The value to check
 * @return True if the value is not null, false otherwise
 */
fun <T> inputNonNullValidation(value: T?): Boolean {
  return value != null
}

/**
 * Check if the input for a number of passenger is right
 *
 * @param nbPassenger The number of passenger to check
 * @return True if the input is right, false otherwise
 */
fun nbPassengerInputValidation(nbPassenger: String): Boolean {
  return nbPassenger.toIntOrNull() != null && nbPassenger.toInt() > 0
}

/**
 * Check if the input for a bottle is right
 *
 * @param bottle The number of bottle to check
 * @return True if the input is right, false otherwise
 */
fun bottleInputValidation(bottle: String): Boolean {
  return bottle.toIntOrNull() != null && bottle.toInt() >= 0
}

/**
 * Check if the input for an email is right
 *
 * @param email The email to check
 * @return True if the input is right, false otherwise
 */
fun validateEmail(email: String): Boolean {
  return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

/**
 * Check if the input for a text is right
 *
 * @param name The text to check
 * @return True if the input is right, false otherwise
 */
fun textInputValidation(name: String): Boolean {
  return name.isEmpty()
}
