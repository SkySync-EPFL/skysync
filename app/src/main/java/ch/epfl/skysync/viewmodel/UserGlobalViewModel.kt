package ch.epfl.skysync.viewmodel

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import ch.epfl.skysync.Repository
import ch.epfl.skysync.components.SnackbarManager
import ch.epfl.skysync.models.user.User
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the user global state
 *
 * @param repository The app repository
 * @return The user global view model
 */
class UserGlobalViewModel(
    repository: Repository,
) : ViewModel() {
  companion object {
    @Composable
    fun createViewModel(repository: Repository): UserGlobalViewModel {
      return viewModel<UserGlobalViewModel>(
          factory =
              object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                  return UserGlobalViewModel(repository) as T
                }
              })
    }
  }

  private val userTable = repository.userTable
  private val tempUserTable = repository.tempUserTable

  private val _user: MutableStateFlow<User?> = MutableStateFlow(null)
  private val _isLoading = MutableStateFlow(false)

  val user = _user.asStateFlow()
  val isLoading = _isLoading.asStateFlow()

  /**
   * Loads a user one of the following ways:
   * - If the user already exists, fetch it from user table
   * - If the user has just been created by an admin and it is the first time the user connects,
   *   fetch it from the temp user table
   * - If the user doesn't exists, connect with a default admin account for testing purposes
   *
   * @param uid The Firebase authentication uid of the user
   * @param email The email address of the user
   */
  fun loadUser(uid: String, email: String) =
      viewModelScope.launch {
        _isLoading.value = true

        // Case 1: User exists, fetch it from user table
        val user = userTable.get(uid, onError = { onError(it) })
        if (user != null) {
          _user.value = user
          _isLoading.value = false
          return@launch
        }

        // Case 2: First connection since user creation, it is in the temp user table
        val tempUser = tempUserTable.get(email, onError = { onError(it) })
        if (tempUser != null) {
          val newUser = tempUser.toUserSchema(uid).toModel()
          userTable.set(uid, newUser, onError = { onError(it) })
          tempUserTable.delete(email)
          _user.value = newUser
          _isLoading.value = false
          return@launch
        }

        // Case 3: User doesn't exists, connect with default admin account
        val defaultUser = userTable.get("default-user", onError = { onError(it) })
        if (defaultUser != null) {
          _user.value = defaultUser
          SnackbarManager.showMessage("Authentication with default Admin user")
        } else {
          onError(Exception("Default user not found."))
        }
        _isLoading.value = false
      }

  /**
   * Callback executed when an error occurs on database-related operations
   *
   * @param e The exception that occurred
   */
  private fun onError(e: Exception) {
    if (e !is CancellationException) {
      SnackbarManager.showMessage(e.message ?: "An unknown error occurred")
    }
  }
}
