package ch.epfl.skysync.database.tables

import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.database.ListenerUpdate
import ch.epfl.skysync.database.Table
import ch.epfl.skysync.database.schemas.MessageGroupSchema
import ch.epfl.skysync.database.schemas.MessageSchema
import ch.epfl.skysync.models.message.Message
import ch.epfl.skysync.models.message.MessageGroup
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

/**
 * Represents the "message groups" table in the database.
 *
 * @property db The FirestoreDatabase instance for interacting with the Firestore database.
 */
class MessageGroupTable(db: FirestoreDatabase) :
    Table<MessageGroup, MessageGroupSchema>(db, MessageGroupSchema::class, PATH) {
  private val messageTable = MessageTable(db)
  private val userTable = UserTable(db)

  /**
   * Add a listener on a message group
   *
   * The listener will be triggered each time a new message is sent/received
   *
   * @param groupId The ID of the group
   * @param onChange Callback called each time the listener is triggered, passed the adds, updates,
   *   deletes that happened since the last listener trigger.
   */
  fun addGroupListener(
      groupId: String,
      onChange: suspend (ListenerUpdate<Message>) -> Unit,
      coroutineScope: CoroutineScope,
      onError: ((Exception) -> Unit)? = null
  ): ListenerRegistration {
    // the query limit is there to avoid wasting resources
    // as the callback will receive the result of the query
    // the first time it is triggered, we could theoretically
    // set the limit to 1 as the callback is called on each new
    // message, for now set 10 as a safety precaution for if
    // multiple messages get sent at the same time
    val limit = 10L
    return messageTable.queryListener(
        Filter.equalTo("groupId", groupId),
        limit = limit,
        orderBy = "date",
        orderByDirection = Query.Direction.DESCENDING,
        onChange = { update ->
          coroutineScope {
            withErrorCallback(onError) {
              val adds = update.adds.map { retrieveMessage(it) }
              val updates = update.updates.map { retrieveMessage(it) }
              val deletes = update.deletes.map { retrieveMessage(it) }
              onChange(
                  ListenerUpdate(
                      isFirstUpdate = update.isFirstUpdate,
                      isLocalUpdate = update.isLocalUpdate,
                      adds = adds.awaitAll().filterNotNull(),
                      updates = updates.awaitAll().filterNotNull(),
                      deletes = deletes.awaitAll().filterNotNull(),
                  ))
            }
          }
        },
        coroutineScope = coroutineScope)
  }

  private suspend fun retrieveMessage(messageSchema: MessageSchema): Deferred<Message?> =
      coroutineScope {
        async {
          val message = messageSchema.toModel()
          val user = userTable.get(message.user.id) ?: return@async null
          message.copy(user = user)
        }
      }

  /**
   * Retrieve all messages of a message group
   *
   * @param groupId The ID of the group
   * @param onError Callback called when an error occurs
   */
  suspend fun retrieveMessages(
      groupId: String,
      onError: ((Exception) -> Unit)? = null
  ): List<Message> {
    return withErrorCallback(onError) {
      messageTable.query(
          Filter.equalTo("groupId", groupId),
          orderBy = "date",
          orderByDirection = Query.Direction.DESCENDING)
    }
  }

  /**
   * Get an message group by ID
   *
   * This will not load the group messages, this has to be done separately using [retrieveMessages].
   *
   * @param id The id of the message group
   * @param onError Callback called when an error occurs
   */
  override suspend fun get(id: String, onError: ((Exception) -> Unit)?): MessageGroup? {
    return super.get(id, onError)
  }

  /**
   * Add a new message group to the database
   *
   * This will generate a new id for this message group and disregard any previously set id. This
   * will not add any message in this message group, this has to be done separately (see
   * [MessageTable.add]).
   *
   * @param item The message group to add to the database
   * @param onError Callback called when an error occurs
   */
  suspend fun add(item: MessageGroup, onError: ((Exception) -> Unit)? = null): String {
    return withErrorCallback(onError) { db.addItem(path, MessageGroupSchema.fromModel(item)) }
  }

  /**
   * Update a message group
   *
   * This will not update the group messages, only the list of users who are part of it.
   *
   * @param item The message group to update
   * @param id The id of the message group
   * @param onError Callback called when an error occurs
   */
  suspend fun updateGroupUsers(
      id: String,
      item: MessageGroup,
      onError: ((Exception) -> Unit)? = null
  ) {
    return withErrorCallback(onError) { db.setItem(path, id, MessageGroupSchema.fromModel(item)) }
  }

  /**
   * Deletes a message group by its ID.
   *
   * This function will delete the message group and all its associated messages from the database.
   *
   * @param id The ID of the message group to be deleted.
   * @param onError Callback invoked when an error occurs during the deletion process.
   */
  override suspend fun delete(id: String, onError: ((Exception) -> Unit)?) = coroutineScope {
    withErrorCallback(onError) {
      listOf(
              launch { super.delete(id, onError = null) },
              launch { messageTable.queryDelete(Filter.equalTo("groupId", id)) })
          .forEach { it.join() }
    }
  }

  companion object {
    const val PATH = "message-group"
  }
}
