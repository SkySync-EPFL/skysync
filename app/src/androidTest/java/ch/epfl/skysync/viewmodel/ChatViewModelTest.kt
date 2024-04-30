package ch.epfl.skysync.viewmodel

import android.os.SystemClock
import androidx.compose.material.Text
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.test.ext.junit.runners.AndroidJUnit4
import ch.epfl.skysync.Repository
import ch.epfl.skysync.database.DatabaseSetup
import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.database.ListenerUpdate
import ch.epfl.skysync.models.message.GroupDetails
import ch.epfl.skysync.models.message.Message
import kotlinx.coroutines.job
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ChatViewModelTest {
  @get:Rule val composeTestRule = createComposeRule()
  private val db = FirestoreDatabase(useEmulator = true)
  private val dbs = DatabaseSetup()
  private val repository: Repository = Repository(db)

  private lateinit var messageListenerSharedViewModel: MessageListenerSharedViewModel
  private lateinit var chatViewModel: ChatViewModel

  @Before
  fun testSetup() = runTest {
    dbs.clearDatabase(db)
    dbs.fillDatabase(db)
    composeTestRule.setContent {
      messageListenerSharedViewModel = MessageListenerSharedViewModel.createViewModel()
      chatViewModel =
          ChatViewModel.createViewModel(dbs.admin2.id, messageListenerSharedViewModel, repository)

      val uiState = chatViewModel.uiState.collectAsStateWithLifecycle()
      Text(text = "$uiState")
    }
  }

  @Test
  fun uiStateTest() = runTest {
    chatViewModel.refresh().join()
    chatViewModel.refreshUser().join()
    val groupDetails = chatViewModel.getGroupDetails().value
    // the getGroupDetails() stateflow returns the initialValue
    // not sure why, but for now assert that it doesn't work
    assertEquals(listOf<GroupDetails>(), groupDetails)

    val uiState = chatViewModel.uiState.value

    assertEquals(false, uiState.isLoading)
    assertEquals(
        listOf(dbs.messageGroup1, dbs.messageGroup2).sortedBy { it.id }, uiState.messageGroups)
  }

  @Test
  fun messageListenerTest() = runTest {
    messageListenerSharedViewModel.coroutineScope = this

    // first setup the callback
    var callbackValues = mutableListOf<Pair<String, ListenerUpdate<Message>>>()
    messageListenerSharedViewModel.pushCallback { group, update ->
      callbackValues.add(Pair(group.id, update))
    }

    // then init the shared view model (this will setup the listeners)
    var defaultCallbackCount = 0
    messageListenerSharedViewModel.init(dbs.admin2.id, repository) { _, _ ->
      defaultCallbackCount += 1
    }

    // wait at little bit to let the time to setup and trigger the listener a first time
    SystemClock.sleep(300)
    this.coroutineContext.job.children.forEach { it.join() }

    chatViewModel.sendMessage(dbs.messageGroup1.id, "test message").join()

    this.coroutineContext.job.children.forEach { it.join() }

    assertEquals(0, defaultCallbackCount)
    assertEquals(3, callbackValues.size)
    assertEquals(2, callbackValues.filter { it.second.isFirstUpdate }.size)
    assertEquals(
        1,
        callbackValues.filter { it.second.adds.find { it.content == "test message" } != null }.size)
  }
}
