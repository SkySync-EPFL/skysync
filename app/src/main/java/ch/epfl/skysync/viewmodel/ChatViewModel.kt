package ch.epfl.skysync.viewmodel

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import ch.epfl.skysync.Repository
import ch.epfl.skysync.database.ListenerUpdate
import ch.epfl.skysync.models.message.ChatMessage
import ch.epfl.skysync.models.message.GroupDetails
import ch.epfl.skysync.models.message.Message
import ch.epfl.skysync.models.message.MessageGroup
import ch.epfl.skysync.models.message.MessageType
import ch.epfl.skysync.models.user.User
import ch.epfl.skysync.util.WhileUiSubscribed
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.ListenerRegistration
import java.time.Instant
import java.util.Date
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

class ChatViewModel(
    private val uid: String,
    repository: Repository,
) : ViewModel() {
  companion object {
    @Composable
    fun createViewModel(uid: String, repository: Repository): ChatViewModel {
      return viewModel<ChatViewModel>(
          factory =
              object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                  return ChatViewModel(uid, repository) as T
                }
              })
    }
  }

  private val userTable = repository.userTable
  private val messageGroupTable = repository.messageGroupTable
  private val messageTable = repository.messageTable

  private val listeners: MutableList<ListenerRegistration> = mutableListOf()
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

  fun getGroupDetails(): StateFlow<List<GroupDetails>> {
    return messageGroups
        .map { groups ->
          groups.map { group ->
            GroupDetails(group.id, group.name, null, group.messages.getOrNull(0))
          }
        }
        .stateIn(scope = viewModelScope, started = WhileUiSubscribed, initialValue = listOf())
  }

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

  private fun onMessageGroupChange(groupId: String, update: ListenerUpdate<Message>) {
    val group = messageGroups.value.find { it.id == groupId } ?: return
    var messages = group.messages

    // remove updated/deleted messages
    messages =
        messages.filter { message ->
          update.updates.find { it.id == message.id } == null &&
              update.deletes.find { it.id == message.id } == null
        }

    messages = messages + (update.updates + update.adds)
    messages = messages.sortedBy { it.date }.reversed()
    updateMessageGroupState(group.copy(messages = messages))
  }

  fun refresh() =
      viewModelScope.launch {
        isLoading.value = true
        val groups =
            messageGroupTable.query(Filter.arrayContains("userIds", uid), onError = { onError(it) })

        // setup listeners
        groups.forEach { group ->
          listeners.add(
              messageGroupTable.addGroupListener(
                  group.id,
                  { onMessageGroupChange(group.id, it) },
                  viewModelScope,
                  onError = { onError(it) }))
        }

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

  fun refreshUser() =
      viewModelScope.launch { _user = userTable.get(uid, onError = { onError(it) }) }

  fun sendMessage(groupId: String, content: String): Message? {
    if (_user == null) {
      // this should in principle never happen
      onError(IllegalStateException("User is not defined"))
      return null
    }
    val message = Message(user = _user!!, date = Date.from(Instant.now()), content = content)
    val messageGroup = messageGroups.value.find { it.id == groupId }
    if (messageGroup == null) {
      onError(IllegalArgumentException("Group ID is invalid"))
      return null
    }

    viewModelScope.launch {
      val id = messageTable.add(groupId, message, onError = { onError(it) })
      updateMessageGroupState(messageGroup.withNewMessage(message.copy(id = id)))
    }
    return message
  }

  init {
    refreshUser()
    refresh()
  }

  override fun onCleared() {
    listeners.forEach { it.remove() }
    super.onCleared()
  }

  /** Callback executed when an error occurs on database-related operations */
  private fun onError(e: Exception) {
    // TODO: display error message
  }
}
