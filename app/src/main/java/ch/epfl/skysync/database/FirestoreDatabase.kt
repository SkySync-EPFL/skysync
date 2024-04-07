package ch.epfl.skysync.database

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.MemoryCacheSettings
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.firestoreSettings
import java.lang.Exception
import java.lang.UnsupportedOperationException
import kotlin.reflect.KClass

/** Represent a connection to a Firestore database
 *
 * @param useEmulator If true, it will try to connect to Firestore database of a local emulator
 */
class FirestoreDatabase(private val useEmulator: Boolean = false) {
    private val db = Firebase.firestore
    private val TAG = "Firebase"

    init {
        if (useEmulator) {
            val firestore = Firebase.firestore
            firestore.useEmulator("10.0.2.2", 5002)

            firestore.firestoreSettings = firestoreSettings {
                setLocalCacheSettings(MemoryCacheSettings.newBuilder().build())
            }
        }
    }

    /**
     * Add a new item to the database
     *
     * This will generate a new id for this item and override any previously set id.
     *
     * @param path A filesystem-like path that specify the location of the table
     * @param item The item to add to the database, the types of the attributes have to be Firestore
     *   types
     * @param onCompletion Callback called on completion of the operation
     * @param onError Callback called when an error occurs
     */
    fun <T : Any> addItem(
        path: String,
        item: T,
        onCompletion: (id: String) -> Unit,
        onError: (Exception) -> Unit
    ) {
        db.collection(path)
            .add(item)
            .addOnSuccessListener {
                Log.d(TAG, "Added $path/${it.id}")
                onCompletion(it.id)
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error adding document: ", exception)
                onError(exception)
            }
    }

    /**
     * Get an item from the database
     *
     * @param path A filesystem-like path that specify the location of the table
     * @param id The id of the item
     * @param clazz The class of the item, this is used to reconstruct an instance of the item class
     * @param onCompletion Callback called on completion of the operation
     * @param onError Callback called when an error occurs
     */
    fun <T : Any> getItem(
        path: String,
        id: String,
        clazz: KClass<T>,
        onCompletion: (T?) -> Unit,
        onError: (Exception) -> Unit
    ) {
        db.collection(path)
            .document(id)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                Log.d(TAG, "Got $path/${documentSnapshot.id}")
                onCompletion(documentSnapshot.toObject(clazz.java))
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error getting document: ", exception)
                onError(exception)
            }
    }

    /**
     * Get all items from a table
     *
     * @param path A filesystem-like path that specify the location of the table
     * @param clazz The class of the item, this is used to reconstruct an instance of the item class
     * @param onCompletion Callback called on completion of the operation
     * @param onError Callback called when an error occurs
     */
    fun <T : Any> getAll(
        path: String,
        clazz: KClass<T>,
        onCompletion: (List<T>) -> Unit,
        onError: (Exception) -> Unit
    ) {
        db.collection(path)
            .get()
            .addOnSuccessListener { querySnapshot ->
                Log.d(TAG, "Got $path")
                onCompletion(querySnapshot.documents.mapNotNull {
                    val res = it.toObject(clazz.java)
                    if (res == null) {
                        Log.w(TAG, "Casting failed for $path/${it.id}")
                    }
                    res
                })
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error getting document: ", exception)
                onError(exception)
            }
    }

    /**
     * Delete an item from the database
     *
     * @param path A filesystem-like path that specify the location of the table
     * @param id The id of the item
     * @param onCompletion Callback called on completion of the operation
     * @param onError Callback called when an error occurs
     */
    fun deleteItem(
        path: String,
        id: String,
        onCompletion: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        db.collection(path)
            .document(id)
            .delete()
            .addOnSuccessListener {
                Log.d(TAG, "Deleted $path/$id")
                onCompletion()
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error deleting document: ", exception)
                onError(exception)
            }
    }

    /**
     * Delete a table (collection of items)
     *
     * This is only used for testing, as such it is only supported if using the emulator.
     *
     * @param path A filesystem-like path that specify the location of the table
     * @param onError Callback called when an error occurs
     */
    fun deleteTable(
        path: String,
        onError: (Exception) -> Unit
    ) {
        if (!useEmulator) {
            throw UnsupportedOperationException("Can only delete collection on the emulator.")
        }
        db.collection(path).get()
            .addOnSuccessListener { for (d in it.documents) d.reference.delete() }
            .addOnFailureListener { exception ->
                onError(exception)
            }
    }
}
