package ch.epfl.skysync.database.tables

import ch.epfl.skysync.database.MockDatabase
import ch.epfl.skysync.database.schemas.AvailabilitySchema
import ch.epfl.skysync.model.Availability
import ch.epfl.skysync.model.AvailabilityStatus
import ch.epfl.skysync.model.TimeSlot
import org.junit.Test
import org.junit.Assert.*
import java.time.LocalDate

class AvailabilityTableUnitTest {

    @Test
    fun getTest() {
        val id = "id-1"
        val personId = "personId"
        val availability = Availability(
            id = id,
            status = AvailabilityStatus.MAYBE,
            timeSlot = TimeSlot.PM,
            date = LocalDate.now()
        )
        val db = MockDatabase()
        val path = "${AvailabilityTable.PATH}/$id"

        // first manually put a AvailabilitySchema instance in the db
        db.getState()[path] = AvailabilitySchema.fromModel(personId, availability)

        val table = AvailabilityTable(db)
        table.get(id, {
            // then check that we can retrieve it
            assertEquals(availability, it)
        }, {})
    }

    @Test
    fun addTest() {
        val id = "0"
        val personId = "personId"
        val availability = Availability(
            id = id,
            status = AvailabilityStatus.MAYBE,
            timeSlot = TimeSlot.PM,
            date = LocalDate.now()
        )
        val availabilitySchema = AvailabilitySchema.fromModel(personId, availability)
        val db = MockDatabase()

        val table = AvailabilityTable(db)

        // add an availability model instance
        table.add(personId, availability, {
            // here id is 0 because the MockDatabase use
            // a counter for id generation
            val path = "${AvailabilityTable.PATH}/0"
            // check that it was added correctly
            assertEquals(availabilitySchema, db.getState()[path])
        }, {})

    }
}