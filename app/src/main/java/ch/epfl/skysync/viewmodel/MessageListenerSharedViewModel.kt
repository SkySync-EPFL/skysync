package ch.epfl.skysync.viewmodel

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import ch.epfl.skysync.Repository
import ch.epfl.skysync.components.SnackbarManager
import ch.epfl.skysync.database.ListenerUpdate
import ch.epfl.skysync.database.tables.MessageGroupTable
import ch.epfl.skysync.models.message.Message
import ch.epfl.skysync.models.message.MessageGroup
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

typealias MessageListenerCallback = (group: MessageGroup, update: ListenerUpdate<Message>) -> Unit

/**
 * Shared view model for message listeners
 *
 * Responsible of the listeners for the message groups
 */
class MessageListenerSharedViewModel : ViewModel() {
  private var initialized = false
  private lateinit var uid: String
  private lateinit var messageGroupTable: MessageGroupTable

  /**
   * This coroutineScope attribute is only used for testing (until a better solution is found to
   * inject the test scope in the view model)
   */
  var coroutineScope: CoroutineScope? = null

  private val listeners: MutableMap<String, ListenerRegistration> = mutableMapOf()
  private val callbackStack: MutableList<MessageListenerCallback> = mutableListOf()
  private lateinit var defaultCallback: MessageListenerCallback

  companion object {
    /**
     * This is only used for the tests, use [NavBackStackEntry.sharedViewModel] to get an instance
     * of the shared view model in the app.
     */
    @Composable
    fun createViewModel(): MessageListenerSharedViewModel {
      return viewModel<MessageListenerSharedViewModel>(
          factory =
              object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                  return MessageListenerSharedViewModel() as T
                }
              })
    }
  }

  init{
    println("Init shared view model")
  }


  /**
   * Initialize the view model.
   *
   * This method can be called multiple times, it will only be executed once
   *
   * @param uid The uid of the user
   * @param repository App repository
   * @param defaultCallback A default callback called only if no other callbacks have been setup via
   *   [pushCallback]
   */
  fun init(uid: String, repository: Repository, defaultCallback: MessageListenerCallback) {
    if (!initialized) {
      initialized = true
      this.uid = uid
      messageGroupTable = repository.messageGroupTable
      this.defaultCallback = defaultCallback
      fetchGroups()
    }
    Log.d("onInit", "Viewmodel init (MessageListenerSharedViewModel)")
  }

  /**
   * Add a new callback to the callback stack
   *
   * Every callback on the stack will be executed on a listener update
   */
  fun pushCallback(callback: MessageListenerCallback) {
    callbackStack.add(callback)
  }

  /** Pop the callback at the top of the stack */
  fun popCallback() {
    if (callbackStack.isNotEmpty()) {
      callbackStack.remove(callbackStack.last())
    }
  }

  /**
   * The function executed on listener update, executes callbacks in the callback stack, or default
   * callback
   */
  private fun onListenerUpdate(group: MessageGroup, update: ListenerUpdate<Message>) {
    if (callbackStack.isEmpty()) {
      defaultCallback(group, update)
    } else {
      callbackStack.forEach { it(group, update) }
    }
  }

  /** Fetch all groups the user is part of and setup a listener for each one */
  private fun fetchGroups() =
      viewModelScope.launch {
        val groups =
            messageGroupTable.query(Filter.arrayContains("userIds", uid), onError = { onError(it) })
        groups.forEach { group ->
          listeners[group.id] =
              messageGroupTable.addGroupListener(
                  group.id,
                  { onListenerUpdate(group, it) },
                  // for testing, use the injected scope, otherwise the viewModelScope
                  coroutineScope ?: viewModelScope,
                  onError = { onError(it) })
        }
      }

  /** Remove all listeners and clear the callback stack */
  fun reset() {
    listeners.forEach { it.value.remove() }
    listeners.clear()
    callbackStack.clear()
  }

  override fun onCleared() {
    reset()
    super.onCleared()
    Log.d("onCleared", "Viewmodel cleared (MessageListenerSharedViewModel)")
  }

  /** Callback executed when an error occurs on database-related operations */
  private fun onError(e: Exception) {
    SnackbarManager.showMessage(e.message ?: "An unknown error occurred")
  }
}
