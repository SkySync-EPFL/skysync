package ch.epfl.skysync.database.schemas

import ch.epfl.skysync.database.Schema
import ch.epfl.skysync.models.location.FlightTrace
import ch.epfl.skysync.models.location.LocationPoint
import com.google.firebase.firestore.Blob
import com.google.firebase.firestore.DocumentId
import java.nio.ByteBuffer

/** Each latitude longitude pair is composed of 1 int of 4 bytes and 2 doubles of 8 bytes */
const val LOCATION_POINT_BYTES = 20

data class FlightTraceSchema(
    /** Flight id */
    @DocumentId val id: String? = null,
    val data: Blob? = null,
) : Schema<FlightTrace> {

  override fun toModel(): FlightTrace {
    return FlightTrace(id = id!!, data = unpackData(data!!.toBytes()))
  }

  companion object {
    private fun packData(data: List<LocationPoint>): ByteArray {
      val numBytes = data.size * LOCATION_POINT_BYTES
      val buffer = ByteBuffer.allocate(numBytes)
      for (dp in data) {
        buffer.putInt(dp.time)
        buffer.putDouble(dp.latitude)
        buffer.putDouble(dp.longitude)
      }
      return buffer.array()
    }

    private fun unpackData(data: ByteArray): List<LocationPoint> {
      val buffer = ByteBuffer.wrap(data)
      val values = mutableListOf<LocationPoint>()
      val numLatLng = data.size / LOCATION_POINT_BYTES
      for (i in 0 ..< numLatLng) {
        values.add(
            LocationPoint(
                buffer.getInt(),
                buffer.getDouble(),
                buffer.getDouble(),
            ))
      }
      return values
    }

    fun fromModel(model: FlightTrace): FlightTraceSchema {
      return FlightTraceSchema(
          id = model.id,
          data = Blob.fromBytes(packData(model.data)),
      )
    }
  }
}
