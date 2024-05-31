package ch.epfl.skysync.viewmodel

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import ch.epfl.skysync.Repository
import ch.epfl.skysync.components.SnackbarManager
import ch.epfl.skysync.database.ListenerUpdate
import ch.epfl.skysync.models.message.ChatMessage
import ch.epfl.skysync.models.message.GroupDetails
import ch.epfl.skysync.models.message.Message
import ch.epfl.skysync.models.message.MessageGroup
import ch.epfl.skysync.models.message.MessageType
import ch.epfl.skysync.models.user.User
import ch.epfl.skysync.util.WhileUiSubscribed
import com.google.firebase.firestore.Filter
import java.time.Instant
import java.util.Date
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ChatUiState(
    val messageGroups: List<MessageGroup> = listOf(),
    val isLoading: Boolean = true,
)

/**
 * ViewModel for the chat screens
 *
 * @param uid The Firebase authentication uid of the user
 * @param messageListenerViewModel The message listener shared view model
 * @param repository App repository
 */
class ChatViewModel(
    private val uid: String,
    private val messageListenerViewModel: MessageListenerViewModel,
    repository: Repository,
) : ViewModel() {
  companion object {
    @Composable
    fun createViewModel(
        uid: String,
        messageListenerViewModel: MessageListenerViewModel,
        repository: Repository
    ): ChatViewModel {
      return viewModel<ChatViewModel>(
          factory =
              object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                  return ChatViewModel(uid, messageListenerViewModel, repository) as T
                }
              })
    }
  }

  private val userTable = repository.userTable
  private val messageGroupTable = repository.messageGroupTable
  private val messageTable = repository.messageTable

  private var _user: User? = null
  private val messageGroups: MutableStateFlow<List<MessageGroup>> = MutableStateFlow(emptyList())
  private val isLoading = MutableStateFlow(false)

  val uiState: StateFlow<ChatUiState> =
      combine(messageGroups, isLoading) { messageGroups, isLoading ->
            ChatUiState(messageGroups, isLoading)
          }
          .stateIn(
              scope = viewModelScope,
              started = WhileUiSubscribed,
              initialValue = ChatUiState(isLoading = true))

  /** Returns a flow of the group details, updated on new messages */
  fun getGroupDetails(): StateFlow<List<GroupDetails>> {
    return messageGroups
        .map { groups ->
          groups
              .map { group ->
                GroupDetails(group.id, group.name, group.color, null, group.messages.getOrNull(0))
              }
              .sortedByDescending { it.lastMessage?.date ?: Date.from(Instant.now()) }
        }
        .stateIn(scope = viewModelScope, started = WhileUiSubscribed, initialValue = listOf())
  }

  /** Returns a flow of the messages of a message group, updated on new messages */
  fun getGroupChatMessages(groupId: String): StateFlow<List<ChatMessage>> {
    return messageGroups
        .map { groups ->
          (groups
                  .find { it.id == groupId }
                  ?.messages
                  ?.map { message ->
                    ChatMessage(
                        message,
                        if (message.user.id == uid) MessageType.SENT else MessageType.RECEIVED,
                        null)
                  } ?: listOf())
              .reversed()
        }
        .stateIn(scope = viewModelScope, started = WhileUiSubscribed, initialValue = listOf())
  }

  private fun updateMessageGroupState(messageGroup: MessageGroup) {
    messageGroups.value =
        (messageGroups.value.filter { it.id != messageGroup.id } + messageGroup).sortedBy { it.id }
  }

  /** Callback called on message listener update. */
  private fun onMessageGroupChange(group: MessageGroup, update: ListenerUpdate<Message>) {
    val group = messageGroups.value.find { it.id == group.id } ?: return
    var messages = group.messages

    // remove updated/deleted messages
    messages =
        messages.filter { message ->
          update.updates.find { it.id == message.id } == null &&
              update.deletes.find { it.id == message.id } == null
        }

    messages = messages + (update.updates + update.adds)
    messages = messages.sortedByDescending { it.date }
    updateMessageGroupState(group.copy(messages = messages))
  }

  /** Fetch all message groups the user is part of, as well as the messages of these groups. */
  fun refresh() =
      viewModelScope.launch {
        isLoading.value = true
        val groups =
            messageGroupTable.query(Filter.arrayContains("userIds", uid), onError = { onError(it) })

        groups
            .map { messageGroup ->
              launch {
                val messages =
                    messageGroupTable.retrieveMessages(messageGroup.id, onError = { onError(it) })
                updateMessageGroupState(messageGroup.copy(messages = messages))
              }
            }
            .forEach { it.join() }
        isLoading.value = false
      }

  /** Fetch the user */
  fun refreshUser() =
      viewModelScope.launch { _user = userTable.get(uid, onError = { onError(it) }) }

  /**
   * Send a message on a message group
   *
   * This will trigger a listener update that will update the group messages
   *
   * @param groupId The ID of the message group to send the message to
   * @param content The content of the message
   */
  fun sendMessage(groupId: String, content: String) =
      viewModelScope.launch {
        if (_user == null) {
          // this should in principle never happen
          onError(IllegalStateException("User is not defined"))
          return@launch
        }
        val message = Message(user = _user!!, date = Date.from(Instant.now()), content = content)
        val messageGroup = messageGroups.value.find { it.id == groupId }
        if (messageGroup == null) {
          onError(IllegalArgumentException("Group ID is invalid"))
          return@launch
        }
        messageTable.add(groupId, message, onError = { onError(it) })
      }

  /** Delete a message group */
  fun deleteGroup(groupId: String) =
      viewModelScope.launch {
        messageGroupTable.delete(groupId, onError = { onError(it) })
        messageGroups.value = messageGroups.value.filter { it.id != groupId }
        refresh()
      }

  init {
    messageListenerViewModel.pushCallback(this::onMessageGroupChange)
    println("Debug ChatViewModel PUSH")
    refreshUser()
    refresh()
  }

  override fun onCleared() {
    messageListenerViewModel.popCallback()
    println("Debug ChatViewModel POP")

    super.onCleared()
  }

  /** Callback executed when an error occurs on database-related operations */
  private fun onError(e: Exception) {
    if (e !is CancellationException) {
      SnackbarManager.showMessage(e.message ?: "An unknown error occurred")
    }
  }
}
