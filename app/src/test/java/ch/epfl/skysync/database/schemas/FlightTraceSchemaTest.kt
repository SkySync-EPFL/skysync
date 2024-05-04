package ch.epfl.skysync.database.schemas

import ch.epfl.skysync.models.location.FlightTrace
import ch.epfl.skysync.models.location.LocationPoint
import org.junit.Assert.*
import org.junit.Test

class FlightTraceSchemaTest {

  @Test
  fun packingUnpackingTest() {
    val flightTrace =
        FlightTrace(
            id = "flight-id",
            data =
                listOf(
                    LocationPoint(0, 1.0, -1.0),
                    LocationPoint(12, 123.003453, -17.0543675),
                    LocationPoint(18, -23.10134501, 19.143034),
                ))

    val schema = FlightTraceSchema.fromModel(flightTrace)
    val unpackedFlightTrace = schema.toModel()

    assertEquals(flightTrace, unpackedFlightTrace)
  }
}
