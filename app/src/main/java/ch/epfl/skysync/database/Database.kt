package ch.epfl.skysync.database

import java.lang.Exception
import kotlin.reflect.KClass

/**
 * Represent a database connection
 *
 * Define low-level method used to interact with the database,
 * intended to be used with dependency injection in some wrapper class
 * specific to a logical table in the database.
 */
interface Database {
    /**
     * Add a new item to the database
     *
     * This will generate a new id for this item
     * and override any previously set id.
     *
     * @param path A filesystem-like path that specify the location of the table
     * @param item The item to add to the database, the types of the attributes have to be
     * Firestore types
     * @param onCompletion Callback called on completion of the operation
     * @param onError Callback called when an error occurs
     */
    fun <T : Any> add(path: String, item: T, onCompletion: () -> Unit, onError: (Exception) -> Unit)

    /**
     * Get an item from the database
     *
     * @param path A filesystem-like path that specify the location of the table
     * @param id The id of the item
     * @param clazz The class of the item, this is used to reconstruct an instance of the item class
     * @param onCompletion Callback called on completion of the operation
     * @param onError Callback called when an error occurs
     */
    fun <T : Any> get(
        path: String,
        id: String,
        clazz: KClass<T>,
        onCompletion: (T?) -> Unit,
        onError: (Exception) -> Unit
    )

    /**
     * Delete an item from the database
     *
     * @param path A filesystem-like path that specify the location of the table
     * @param id The id of the item
     * @param onCompletion Callback called on completion of the operation
     * @param onError Callback called when an error occurs
     */
    fun delete(path: String, id: String, onCompletion: () -> Unit, onError: (Exception) -> Unit)
}