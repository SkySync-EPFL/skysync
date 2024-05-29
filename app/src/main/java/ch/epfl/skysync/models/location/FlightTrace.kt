package ch.epfl.skysync.models.location

import ch.epfl.skysync.models.UNSET_ID

data class FlightTrace(
    val id: String = UNSET_ID,
    val trace: List<LocationPoint>,
){
    /**
     * removes points that are exceptionally far away
      */
    fun computeCorrectedTrace(): FlightTrace {
        if (trace.size < 4) {
            return this
        }
        val correctedTrace = mutableListOf<LocationPoint>()
        for (j in 0 until 3) {
            correctedTrace.add(trace[j])
        }

        for (i in 3 until trace.size) {
            val twoBeforeDistance = trace[i-3].distanceTo(trace[i-2])
            val beforeDistance = trace[i-2].distanceTo(trace[i-1])
            val meanDistance = (twoBeforeDistance + beforeDistance) / 2
            val currentDistance = trace[i-1].distanceTo(trace[i])
            if (currentDistance <=  meanDistance * 1.5) {
                correctedTrace.add(trace[i])
            }
        }
        return if (correctedTrace.size == trace.size) {
            this
        } else
            FlightTrace(id, correctedTrace)
    }
}

