package ch.epfl.skysync.models.location

import ch.epfl.skysync.models.UNSET_ID

data class FlightTrace(
    val id: String = UNSET_ID,
    val trace: List<LocationPoint>,
) {
  /** removes points that are exceptionally far away */
}

fun computeCorrectedTrace(trace: List<Location>): List<Location> {
  val numberOfNeighbors = 4
  if (trace.size < numberOfNeighbors) {
    return trace
  }
  val allowedFailFactor = 1.3
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
    if (currentDistance <= meanDistance * allowedFailFactor) {
      correctedTrace.add(trace[i])
    }
  }
  return if (correctedTrace.size == trace.size) {
    trace
  } else correctedTrace
}
