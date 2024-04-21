package ch.epfl.skysync.database.tables

import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.database.Table
import ch.epfl.skysync.database.schemas.MessageSchema
import ch.epfl.skysync.models.message.Message

class MessageTable(db: FirestoreDatabase) :
    Table<Message, MessageSchema>(db, MessageSchema::class, PATH) {

  /**
   * Add a new message to the database
   *
   * This will generate a new id for this message and disregard any previously set id.
   *
   * @param groupId The ID of the group the message is sent to
   * @param userId The ID of the user who sent the message
   * @param item The message to add to the database
   * @param onError Callback called when an error occurs
   */
  suspend fun add(
      groupId: String,
      userId: String,
      item: Message,
      onError: ((Exception) -> Unit)? = null
  ): String {
    return withErrorCallback(onError) {
      db.addItem(path, MessageSchema.fromModel(groupId, userId, item))
    }
  }

  companion object {
    const val PATH = "message"
  }
}
