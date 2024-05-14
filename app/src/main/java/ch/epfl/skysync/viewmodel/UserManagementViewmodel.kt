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
import ch.epfl.skysync.models.user.User
import com.google.firebase.firestore.Filter
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

  fun addTempUser(
      email: String,
      userRole: UserRole,
      firstname: String,
      lastname: String,
      balloonQualification: BalloonQualification?) =
      viewModelScope.launch {
          var tempUser: TempUser? = null
          var user : User? = null
          tempUserTable.getAll(onError = { onError(it) }).forEach() { t ->
              if (t.email == email) {
                  tempUser = t
              }
          }
          userTable.getAll(onError = { onError(it) }).forEach() { u ->
              if (u.email == email) {
                  user = u
              }
          }
          if (tempUser !=null){
              tempUserTable.queryDelete(Filter.notEqualTo("email",email),onError = { onError(it) })
          }
          if(user != null){
              userTable.delete(user!!.id,onError = { onError(it) })
          }
          tempUser = TempUser(email, userRole, firstname, lastname, balloonQualification)
          tempUserTable.set(email, tempUser!!,onError = { onError(it) })
        }

  private fun onError(e: Exception) {
    SnackbarManager.showMessage(e.message ?: "An unknown error occurred")
  }
}
