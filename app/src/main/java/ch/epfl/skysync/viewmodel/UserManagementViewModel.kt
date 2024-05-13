package ch.epfl.skysync.viewmodel

import android.util.Log
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
  val selectedUser = _selectedUser.asStateFlow()

  fun refresh() {
    refreshAllUsers()
    refreshSelectedUser()
  }

  private fun refreshAllUsers() {
    viewModelScope.launch {
      _allUsers.value = repository.userTable.getAll(onError = { onError(it) })
      Log.d("UserManagementViewModel", "All users loaded")
    }
  }

  private fun refreshSelectedUser() {
    viewModelScope.launch {
      _selectedUser.value = repository.userTable.get(userId!!, onError = { onError(it) })
      Log.d("UserManagementViewModel", "Selected user loaded")
    }
  }

  fun addUser(tmpUser: TempUser) {
    viewModelScope.launch {
      repository.tempUserTable.set(tmpUser.email, tmpUser, onError = { onError(it) })
      refreshAllUsers()
      Log.d("UserManagementViewModel", "User added")
    }
  }

  fun deleteUser(user: User) {
    viewModelScope.launch {
      repository.userTable.delete(user.id, onError = { onError(it) })
      refreshAllUsers()
      Log.d("UserManagementViewModel", "User deleted")
    }
  }

  /** Callback executed when an error occurs on database-related operations */
  private fun onError(e: Exception) {
    SnackbarManager.showMessage(e.message ?: "An unknown error occurred")
  }
}
