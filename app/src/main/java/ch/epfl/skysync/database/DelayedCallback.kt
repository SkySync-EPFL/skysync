package ch.epfl.skysync.database

/**
 * Represents a mechanism for delaying the execution of a callback function until a certain delay
 * has passed.
 *
 * If the provided delay is negative, execute the callback on initialization (this is a useful
 * feature when using this with lists of size 0 (thus delay = -1)).
 *
 * This class allows for scheduling a callback function to be executed after a specified delay
 * period. It provides functionality to decrement the delay period on each invocation of the `run()`
 * method until the delay reaches 0, at which point the callback function is executed.
 *
 * @property delay The initial delay period before executing the callback, specified in units of
 *   invocation cycles.
 * @property callback The callback function to be executed after the delay period.
 */
class DelayedCallback(private var delay: Int, private val callback: () -> Unit) {

  init {
    if (delay < 0) {
      callback()
    }
  }

  /** Executes the callback function after the specified delay period has passed. */
  fun run() {
    if (delay <= 0) {
      callback()
    } else {
      delay -= 1
    }
  }
}
