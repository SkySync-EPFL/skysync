package ch.epfl.skysync.database

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import java.lang.Exception
import kotlin.reflect.KClass

/**
 * Represent a connection to a Firestore database
 */
class FirestoreDatabase : Database {
    private val db = Firebase.firestore

    override fun <T : Any> add(
        path: String,
        item: T,
        onCompletion: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        db.collection(path)
            .add(item)
            .addOnSuccessListener {
                println("Added $path/${it.id}")
                onCompletion()
            }
            .addOnFailureListener { exception ->
                println("Error adding document: $exception")
                onError(exception)
            }
    }

    override fun <T : Any> get(
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
                println("Got $path/${documentSnapshot.id}")
                onCompletion(documentSnapshot.toObject(clazz.java))
            }
            .addOnFailureListener { exception ->
                println("Error getting document: $exception")
                onError(exception)
            }
    }

    override fun delete(
        path: String,
        id: String,
        onCompletion: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        db.collection(path)
            .document(id)
            .delete()
            .addOnSuccessListener {
                println("Deleted $path/$id")
                onCompletion()
            }
            .addOnFailureListener { exception ->
                println("Error deleting document: $exception")
                onError(exception)
            }
    }
}