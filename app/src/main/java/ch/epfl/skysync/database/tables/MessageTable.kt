package ch.epfl.skysync.database.tables

import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.database.Table
import ch.epfl.skysync.database.schemas.MessageSchema
import ch.epfl.skysync.models.message.Message
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.Query
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class MessageTable(db: FirestoreDatabase) :
    Table<Message, MessageSchema>(db, MessageSchema::class, PATH) {
  private val userTable = UserTable(db)

  override suspend fun get(id: String, onError: ((Exception) -> Unit)?): Message? {
    return withErrorCallback(onError) {
      var message = super.get(id, onError = null) ?: return@withErrorCallback null
      val user = userTable.get(message.user.id, onError = null) ?: return@withErrorCallback null
      message = message.copy(user = user)
      message
    }
  }

  override suspend fun getAll(onError: ((Exception) -> Unit)?): List<Message> = coroutineScope {
    withErrorCallback(onError) {
      val messages = super.getAll(onError = null)
      messages
          .map { message ->
            async {
              val user = userTable.get(message.user.id) ?: return@async null
              message.copy(user = user)
            }
          }
          .awaitAll()
          .filterNotNull()
    }
  }

  override suspend fun query(
      filter: Filter,
      limit: Long?,
      orderBy: String?,
      orderByDirection: Query.Direction,
      onError: ((Exception) -> Unit)?
  ): List<Message> = coroutineScope {
    withErrorCallback(onError) {
      val messages = super.query(filter, limit, orderBy, orderByDirection, onError = null)
      messages
          .map { message ->
            async {
              val user = userTable.get(message.user.id) ?: return@async null
              message.copy(user = user)
            }
          }
          .awaitAll()
          .filterNotNull()
    }
  }

  /**
   * Add a new message to the database
   *
   * This will generate a new id for this message and disregard any previously set id.
   *
   * @param groupId The ID of the group the message is sent to
   * @param item The message to add to the database
   * @param onError Callback called when an error occurs
   */
  suspend fun add(groupId: String, item: Message, onError: ((Exception) -> Unit)? = null): String {
    return withErrorCallback(onError) { db.addItem(path, MessageSchema.fromModel(groupId, item)) }
  }

  companion object {
    const val PATH = "message"
  }
}
