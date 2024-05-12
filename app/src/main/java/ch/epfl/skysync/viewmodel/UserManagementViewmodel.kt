package ch.epfl.skysync.viewmodel

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import ch.epfl.skysync.Repository
import ch.epfl.skysync.components.SnackbarManager
import ch.epfl.skysync.database.UserRole
import ch.epfl.skysync.models.flight.BalloonQualification
import ch.epfl.skysync.models.user.TempUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserManagementViewmodel(
    val repository: Repository,
    val userId: String?,
) : ViewModel() {
  companion object {
    @Composable
    fun createViewModel(
        repository: Repository,
        userId: String?,
    ): UserManagementViewmodel {
      return viewModel<UserManagementViewmodel>(
          factory =
              object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                  return UserManagementViewmodel(repository, userId) as T
                }
              })
    }
  }

  private val userTable = repository.userTable
  private val tempUserTable = repository.tempUserTable

  private val _emails: MutableStateFlow<List<String>> = MutableStateFlow(emptyList())
  val emails = _emails.asStateFlow()

  fun refresh() {
    refreshAvailableEmails()
  }

  fun refreshAvailableEmails() =
      viewModelScope.launch {
        _emails.value = emptyList()
        tempUserTable.getAll(onError = { onError(it) }).forEach() { u -> _emails.value += u.email }
      }

  fun addTempUser(
      email: String,
      userRole: UserRole,
      firstname: String,
      lastname: String,
      balloonQualification: BalloonQualification?
  ) =
      viewModelScope.launch {
        val index = _emails.value.indexOf(email)
        if (index == -1) {
          tempUserTable.set(
              email,
              TempUser(email, userRole, firstname, lastname, balloonQualification),
              onError = { onError(it) })
        } else {
          // tempUserTable.
        }
      }

  fun getTempUser(email: String): TempUser? {
    var tempUser: TempUser? = null
    viewModelScope.launch {
      tempUserTable.getAll(onError = { onError(it) }).forEach() { u ->
        if (u.email == email) {
          tempUser = u
        }
      }
    }
    return tempUser
  }

  private fun onError(e: Exception) {
    SnackbarManager.showMessage(e.message ?: "An unknown error occurred")
  }
}
