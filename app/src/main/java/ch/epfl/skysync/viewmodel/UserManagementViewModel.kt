package ch.epfl.skysync.viewmodel

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import ch.epfl.skysync.Repository
import ch.epfl.skysync.components.SnackbarManager
import ch.epfl.skysync.models.flight.RoleType
import ch.epfl.skysync.models.user.TempUser
import ch.epfl.skysync.models.user.User
import kotlin.coroutines.cancellation.CancellationException
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
  private val _filteredUsers: MutableStateFlow<List<User>> = MutableStateFlow(emptyList())

  val allUsers = _allUsers.asStateFlow()
  val filteredUsers = _filteredUsers.asStateFlow()

  /** Refreshes the users */
  fun refresh() = viewModelScope.launch { refreshAllUsers() }

  /** Refreshes the users, resets filtered users */
  private suspend fun refreshAllUsers() {
    _allUsers.value = repository.userTable.getAll(onError = { onError(it) })
    _filteredUsers.value = _allUsers.value
  }

  /**
   * Creates a user in the temporary user table that will add a user when the user connects for the
   * first time
   */
  fun createUser(tmpUser: TempUser) =
      viewModelScope.launch {
        repository.tempUserTable.set(tmpUser.email, tmpUser, onError = { onError(it) })
        SnackbarManager.showMessage("User created successfully")
        refreshAllUsers()
      }

  /** Deletes a user from the database */
  fun deleteUser(user: User) =
      viewModelScope.launch {
        repository.userTable.delete(user.id, onError = { onError(it) })
        refreshAllUsers()
      }

  /**
   * Filters the list of users based on a search query and a role.
   *
   * The function updates the value of `_filteredUsers` by filtering `_allUsers` according to the
   * following criteria:
   * - If `query` is not empty, the user's full name (concatenation of `firstname` and `lastname`)
   *   should contain the query string, ignoring case.
   * - If `selectedRole` is not null, the user should have a role that matches `selectedRole`.
   *
   * @param query A string to search for in the user's full name. If empty, this criterion is
   *   ignored.
   * @param selectedRole An optional role to filter users by. If null, this criterion is ignored.
   */
  fun filterByQueryAndRole(query: String, selectedRole: RoleType?) {
    _filteredUsers.value =
        _allUsers.value.filter {
          (query.isEmpty() || it.name().contains(query, ignoreCase = true)) &&
              (selectedRole == null || it.roleTypes.contains(selectedRole))
        }
  }

  /** Callback executed when an error occurs on database-related operations */
  private fun onError(e: Exception) {
    if (e !is CancellationException) {
      SnackbarManager.showMessage(e.message ?: "An unknown error occurred")
    }
  }
}
