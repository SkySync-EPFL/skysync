package ch.epfl.skysync.model.location

import ch.epfl.skysync.models.location.Location
import ch.epfl.skysync.models.location.LocationPoint
import ch.epfl.skysync.models.location.computeCorrectedTrace
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class TestFlightTrace {
  lateinit var location1: Location
  lateinit var location2: Location
  lateinit var trace: List<Location>

  @Before
  fun setUp() {
    location1 =
        Location(
            id = "id",
            userId = "userId",
            point = LocationPoint(time = 0, latitude = 46.517076, longitude = 6.565593))

    location2 =
        Location(
            id = "id",
            userId = "userId",
            point = LocationPoint(time = 0, latitude = 46.518137, longitude = 6.565538))
    trace = mutableListOf()
    for (i in 0..50) {
      trace += if (i % 2 == 0) location1 else location2
    }
  }

  @Test
  fun `test computeCorrectedTrace for short trace`() {
    val singletonTrace = listOf(location1)
    val correctedTrace = computeCorrectedTrace(singletonTrace)
    Assert.assertEquals(singletonTrace, correctedTrace)
  }

  @Test
  fun `test computeCorrectedTrace for trace with anomaly`() {
    val anomaly =
        Location(
            id = "id",
            userId = "userId",
            point = LocationPoint(time = 0, latitude = 0.0, longitude = 0.0))
    trace += anomaly
    val correctedTrace = computeCorrectedTrace(trace)
    Assert.assertFalse(correctedTrace.contains(anomaly))
    Assert.assertEquals(trace.size - 1, correctedTrace.size)
  }

  @Test
  fun `test computeCorrectedTrace for trace without anomaly`() {
    val correctedTrace = computeCorrectedTrace(trace)
    Assert.assertEquals(trace, correctedTrace)
  }
}
