package ch.epfl.skysync.database.tables

import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.database.ParallelOperationsEndCallback
import ch.epfl.skysync.database.Table
import ch.epfl.skysync.database.schemas.UserSchema
import ch.epfl.skysync.models.user.User
import com.google.firebase.firestore.Filter

/** Represent the "user" table */
class UserTable(db: FirestoreDatabase) : Table<User, UserSchema>(db, UserSchema::class, PATH) {
  private val availabilityTable = AvailabilityTable(db)

  /** Retrieve and set all availabilities linked to the user */
  private fun retrieveAvailabilities(
      user: User,
      onCompletion: (User?) -> Unit,
      onError: (Exception) -> Unit
  ) {
    availabilityTable.query(
        Filter.equalTo("userId", user.id),
        { availabilities ->
          user.availabilities.addCells(availabilities)
          onCompletion(user)
        },
        onError)
  }

  /** Delete all availabilities linked to the user */
  private fun deleteAvailabilities(
      id: String,
      onCompletion: () -> Unit,
      onError: (Exception) -> Unit
  ) {
    availabilityTable.queryDelete(Filter.equalTo("userId", id), onCompletion, onError)
  }

  override fun get(id: String, onCompletion: (User?) -> Unit, onError: (Exception) -> Unit) {
    super.get(
        id,
        { user ->
          if (user == null) {
            onCompletion(null)
          } else {
            retrieveAvailabilities(user, onCompletion, onError)
          }
        },
        onError)
  }

  override fun getAll(onCompletion: (List<User>) -> Unit, onError: (Exception) -> Unit) {
    super.getAll(
        { users ->
          val delayedCallback = ParallelOperationsEndCallback(users.size) { onCompletion(users) }
          for (user in users) {
            retrieveAvailabilities(user, { delayedCallback.run() }, onError)
          }
        },
        onError)
  }

  override fun query(
      filter: Filter,
      onCompletion: (List<User>) -> Unit,
      onError: (Exception) -> Unit
  ) {
    super.query(
        filter,
        { users ->
          val delayedCallback = ParallelOperationsEndCallback(users.size) { onCompletion(users) }
          for (user in users) {
            retrieveAvailabilities(user, { delayedCallback.run() }, onError)
          }
        },
        onError)
  }

  /**
   * Add a new user to the database
   *
   * This will generate a new id for this user and disregard any previously set id. This will not
   * add availabilities or assigned flights to the database, it must be done separately.
   *
   * @param item The user to add to the database
   * @param onCompletion Callback called on completion of the operation
   * @param onError Callback called when an error occurs
   */
  fun add(item: User, onCompletion: (id: String) -> Unit, onError: (Exception) -> Unit) {
    db.addItem(path, UserSchema.fromModel(item), onCompletion, onError)
  }

  override fun delete(
      id: String,
      onCompletion: () -> Unit,
      onError: (java.lang.Exception) -> Unit
  ) {
    deleteAvailabilities(id, { super.delete(id, onCompletion, onError) }, onError)
  }

  /**
   * Set a new user to the database
   *
   * Set item at id (if null a new id is generated) and override any previously set id.
   *
   * @param item The user to add to the database
   * @param id the id of the item (if null a new id is generated)
   * @param onCompletion Callback called on completion of the operation
   * @param onError Callback called when an error occurs
   */
  fun set(
      id: String?,
      item: User,
      onCompletion: (id: String) -> Unit,
      onError: (Exception) -> Unit
  ) {
    db.setItem(path, id, UserSchema.fromModel(item), onCompletion, onError)
  }

  companion object {
    const val PATH = "user"
  }
}
