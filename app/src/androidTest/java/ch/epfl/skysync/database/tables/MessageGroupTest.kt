package ch.epfl.skysync.database.tables

import androidx.test.ext.junit.runners.AndroidJUnit4
import ch.epfl.skysync.database.DatabaseSetup
import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.database.ListenerUpdate
import ch.epfl.skysync.models.message.Message
import java.time.Instant
import java.util.Date
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MessageGroupTest {
  private val db = FirestoreDatabase(useEmulator = true)
  private val dbs = DatabaseSetup()
  private val messageGroupTable = MessageGroupTable(db)
  private val messageTable = MessageTable(db)

  @Before
  fun testSetup() = runTest {
    dbs.clearDatabase(db)
    dbs.fillDatabase(db)
  }

  @Test
  fun getTest() = runTest {
    val messageGroup = messageGroupTable.get(dbs.messageGroup1.id, onError = { assertNull(it) })
    assertNotNull(messageGroup)
    assertTrue(
        listOf(dbs.admin2.id, dbs.pilot1.id, dbs.crew1.id).containsAll(messageGroup!!.userIds))

    val messages = messageGroupTable.retrieveMessages(messageGroup.id, onError = { assertNull(it) })
    assertNotNull(messageGroup)
    assertTrue(listOf(dbs.message1, dbs.message2).containsAll(messages))
  }

  @Test
  fun listenerTest() = runTest {
    var listenerUpdates: MutableList<ListenerUpdate<Message>> = mutableListOf()

    val listener =
        messageGroupTable.addGroupListener(dbs.messageGroup1.id) { update ->
          listenerUpdates.add(update)
        }
    var newMessage1 = Message(date = Date.from(Instant.now()), content = "New")
    newMessage1 =
        newMessage1.copy(id = messageTable.add(dbs.messageGroup1.id, dbs.crew1.id, newMessage1))

    messageTable.delete(newMessage1.id)

    var newMessage2 = Message(date = Date.from(Instant.now()), content = "New again")
    newMessage2 =
        newMessage2.copy(id = messageTable.add(dbs.messageGroup1.id, dbs.crew1.id, newMessage2))

    assertEquals(4, listenerUpdates.size)
    assertEquals(true, listenerUpdates[0].isFirstUpdate)

    // the listener is triggered to fetch query from the database a first time
    // this will however (because this is a test) not to be the first update as it needs to wait
    // for a requests and is not a coroutine (it is not blocking as it is executed by the listener)
    assertEquals(
        ListenerUpdate(
            isFirstUpdate = false,
            isLocalUpdate = true,
            adds = listOf(dbs.message1, dbs.message2),
            updates = listOf(),
            deletes = listOf()),
        listenerUpdates[1])
    assertEquals(
        ListenerUpdate(
            isFirstUpdate = true,
            isLocalUpdate = true,
            adds = listOf(newMessage1),
            updates = listOf(),
            deletes = listOf()),
        listenerUpdates[0])
    assertEquals(
        ListenerUpdate(
            isFirstUpdate = false,
            isLocalUpdate = false,
            adds = listOf(),
            updates = listOf(),
            deletes = listOf(newMessage1)),
        listenerUpdates[2])
    assertEquals(
        ListenerUpdate(
            isFirstUpdate = false,
            isLocalUpdate = true,
            adds = listOf(newMessage2),
            updates = listOf(),
            deletes = listOf()),
        listenerUpdates[3])

    listener.remove()
  }
}
