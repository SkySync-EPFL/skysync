package ch.epfl.skysync.models.location

import ch.epfl.skysync.models.UNSET_ID

data class FlightTrace(
    val id: String = UNSET_ID,
    val trace: List<LocationPoint>,
) {
  /** removes points that are exceptionally far away */
}

/**
 * tries to identify and remove points that are exceptionally far away from the preceding points of
 * the trace
 *
 * @param trace the trace to be corrected
 * @param numberOfNeighbors the number of preceding points to be considered for the correction of
 *   each point
 * @param allowedIncreasedDistanceFactor the factor by which the distance between a given point and
 *   its predecessor is allowed to increment with respect to the mean distance between the other
 *   predecessors
 */
fun computeCorrectedTrace(
    trace: List<Location>,
    numberOfNeighbors: Int = 4,
    allowedIncreasedDistanceFactor: Double = 1.3
): List<Location> {
  if (trace.size < numberOfNeighbors) {
    return trace
  }
  val correctedTrace = mutableListOf<Location>()
  for (j in 0 until numberOfNeighbors) {
    correctedTrace.add(trace[j])
  }

  for (i in numberOfNeighbors until trace.size) {
    val currentDistance = trace[i - 1].point.distanceTo(trace[i].point)
    val distances = mutableListOf<Double>()
    for (j in 1 until numberOfNeighbors) {
      val distance = trace[i - j].point.distanceTo(trace[i - j - 1].point)
      distances.add(distance)
    }
    val meanDistance = distances.average()
    if (currentDistance <= meanDistance * allowedIncreasedDistanceFactor) {
      correctedTrace.add(trace[i])
    }
  }
  return if (correctedTrace.size == trace.size) {
    trace
  } else correctedTrace
}
