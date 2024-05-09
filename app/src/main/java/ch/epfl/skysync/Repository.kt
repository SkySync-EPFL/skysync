package ch.epfl.skysync

import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.database.tables.AvailabilityTable
import ch.epfl.skysync.database.tables.BalloonTable
import ch.epfl.skysync.database.tables.BasketTable
import ch.epfl.skysync.database.tables.FlightTable
import ch.epfl.skysync.database.tables.FlightTraceTable
import ch.epfl.skysync.database.tables.FlightTypeTable
import ch.epfl.skysync.database.tables.LocationTable
import ch.epfl.skysync.database.tables.MessageGroupTable
import ch.epfl.skysync.database.tables.MessageTable
import ch.epfl.skysync.database.tables.TempUserTable
import ch.epfl.skysync.database.tables.UserTable
import ch.epfl.skysync.database.tables.VehicleTable

/** Container class that store all database tables */
class Repository(db: FirestoreDatabase) {
  val availabilityTable: AvailabilityTable = AvailabilityTable(db)
  val balloonTable: BalloonTable = BalloonTable(db)
  val basketTable: BasketTable = BasketTable(db)
  val flightTable: FlightTable = FlightTable(db)
  val flightTraceTable: FlightTraceTable = FlightTraceTable(db)
  val flightTypeTable: FlightTypeTable = FlightTypeTable(db)
  val userTable: UserTable = UserTable(db)
  val vehicleTable: VehicleTable = VehicleTable(db)
  val messageGroupTable: MessageGroupTable = MessageGroupTable(db)
  val messageTable: MessageTable = MessageTable(db)
  val tempUserTable: TempUserTable = TempUserTable(db)
  val locationTable: LocationTable = LocationTable(db)
}
