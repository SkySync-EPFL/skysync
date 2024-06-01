package ch.epfl.skysync.database.tables

import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.database.Table
import ch.epfl.skysync.database.schemas.TempUserSchema
import ch.epfl.skysync.models.user.TempUser

/**
 * Represents the "temporary user" table in the database.
 *
 * @property db The FirestoreDatabase instance for interacting with the Firestore database.
 */
class TempUserTable(db: FirestoreDatabase) :
    Table<TempUser, TempUserSchema>(db, TempUserSchema::class, PATH) {
  /**
   * Sets a temporary user at the given id which in this case is the email of the user
   *
   * @param email The email used as id
   * @param item The temporary user to add to the database
   */
  suspend fun set(email: String, item: TempUser, onError: ((Exception) -> Unit)? = null) {
    return withErrorCallback(onError) { db.setItem(path, email, TempUserSchema.fromModel(item)) }
  }

  companion object {
    const val PATH = "temp-user"
  }
}
