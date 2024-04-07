package ch.epfl.skysync.database.tables

import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.database.schemas.AvailabilitySchema
import ch.epfl.skysync.models.calendar.Availability

/** Represent the "availability" table */
class AvailabilityTable(private val db: FirestoreDatabase) {

    /**
     * Get an availability by ID
     *
     * @param id The id of the availability
     * @param onCompletion Callback called on completion of the operation
     * @param onError Callback called when an error occurs
     */
    fun get(id: String, onCompletion: (Availability?) -> Unit, onError: (Exception) -> Unit) {
        db.getItem(PATH, id, AvailabilitySchema::class, { onCompletion(it?.toModel()) }, onError)
    }

    /**
     * Get all the availabilities
     *
     * @param onCompletion Callback called on completion of the operation
     * @param onError Callback called when an error occurs
     */
    fun getAll(onCompletion: (List<Availability>) -> Unit, onError: (Exception) -> Unit) {
        db.getAll(
            PATH,
            AvailabilitySchema::class,
            { schemas -> onCompletion(schemas.map { it.toModel() }) },
            onError
        )
    }

    /**
     * Add a new availability to the database
     *
     * This will generate a new id for this availability and override any previously set id.
     *
     * @param personId The ID of the person whose availability it is
     * @param item The availability to add to the database
     * @param onCompletion Callback called on completion of the operation
     * @param onError Callback called when an error occurs
     */
    fun add(
        personId: String,
        item: Availability,
        onCompletion: (id: String) -> Unit,
        onError: (Exception) -> Unit
    ) {
        db.addItem(PATH, AvailabilitySchema.fromModel(personId, item), onCompletion, onError)
    }

    /**
     * Delete an availability from the database
     *
     * @param id The id of the availability
     * @param onCompletion Callback called on completion of the operation
     * @param onError Callback called when an error occurs
     */
    fun delete(
        id: String,
        onCompletion: () -> Unit,
        onError: (java.lang.Exception) -> Unit
    ) {
        db.deleteItem(PATH, id, onCompletion, onError)
    }

    /**
     * Delete the table
     *
     * This is only used for testing, as such it is only supported if using the emulator.
     *
     * @param onError Callback called when an error occurs
     */
    fun deleteTable(
        onError: (Exception) -> Unit
    ) {
        db.deleteTable(PATH, onError)
    }

    companion object {
        const val PATH = "availability"
    }
}
