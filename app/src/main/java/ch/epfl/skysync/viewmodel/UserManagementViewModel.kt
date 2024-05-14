package ch.epfl.skysync.viewmodel

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import ch.epfl.skysync.Repository
import ch.epfl.skysync.components.SnackbarManager
import ch.epfl.skysync.models.user.TempUser
import ch.epfl.skysync.models.user.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** viewmodel for the user management */
class UserManagementViewModel(
    val repository: Repository,
    val userId: String?,
) : ViewModel() {
  companion object {
    @Composable
    fun createViewModel(
        repository: Repository,
        userId: String?,
    ): UserManagementViewModel {
      return viewModel<UserManagementViewModel>(
          factory =
              object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                  return UserManagementViewModel(repository, userId) as T
                }
              })
    }
  }

  private val _allUsers: MutableStateFlow<List<User>> = MutableStateFlow(emptyList())
  private val _selectedUser: MutableStateFlow<User?> = MutableStateFlow(null)

  val allUsers = _allUsers.asStateFlow()

  /** Refreshes the data of the viewmodel */
  fun refresh() {
    refreshAllUsers()
  }

  /** Refreshes the data of all users */
  private fun refreshAllUsers() {
    viewModelScope.launch {
      _allUsers.value = repository.userTable.getAll(onError = { onError(it) })
    }
  }

  /**
   * Creates a user in the temporary user table that will add a user when the user connects for the
   * first time
   */
  fun createUser(tmpUser: TempUser) {
    viewModelScope.launch {
      repository.tempUserTable.set(tmpUser.email, tmpUser, onError = { onError(it) })
      SnackbarManager.showMessage("User created successfully")
      refreshAllUsers()
    }
  }

  /** Deletes a user from the database */
  fun deleteUser(user: User) {
    viewModelScope.launch {
      repository.userTable.delete(user.id, onError = { onError(it) })
      refreshAllUsers()
    }
  }

  /** Callback executed when an error occurs on database-related operations */
  private fun onError(e: Exception) {
    SnackbarManager.showMessage(e.message ?: "An unknown error occurred")
  }
}
