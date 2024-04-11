package ch.epfl.skysync.database

/**
 * Represents a mechanism for the case when n operations with callback are executed in parallel,
 * where a specific callback should be called once by the last of the n operation finishes.
 *
 * @property numParallelOperations The number of operations executed in parallel
 * @property callback The callback function to be executed once by the last operation.
 */
class ParallelOperationsEndCallback(
    private var numParallelOperations: Int,
    private val callback: () -> Unit
) {

  init {
    if (numParallelOperations < 1) {
      callback()
    }
  }

  /** To be executed at the end of each parallel operation at the last one, execute the callback */
  fun run() {
    if (numParallelOperations <= 1) {
      callback()
    } else {
      numParallelOperations -= 1
    }
  }
}
