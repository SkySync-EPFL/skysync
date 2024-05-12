package ch.epfl.skysync.viewmodel

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import ch.epfl.skysync.Repository
import ch.epfl.skysync.components.SnackbarManager
import ch.epfl.skysync.database.UserRole
import ch.epfl.skysync.models.calendar.TimeSlot
import ch.epfl.skysync.models.flight.BalloonQualification
import ch.epfl.skysync.models.flight.Basket
import ch.epfl.skysync.models.flight.Flight
import ch.epfl.skysync.models.flight.FlightType
import ch.epfl.skysync.models.flight.Vehicle
import ch.epfl.skysync.models.user.TempUser
import ch.epfl.skysync.models.user.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class UserManagementViewmodel (
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

    private val _tempUsers: MutableStateFlow<List<TempUser>> = MutableStateFlow(emptyList())
    private val _emails: MutableStateFlow<List<String>> = MutableStateFlow(emptyList())



    val tempUsers = _tempUsers.asStateFlow()
    val emails = _emails.asStateFlow()

    fun refresh() {
        refreshAvailableEmails()
        refreshAvailableUsers()
    }
    fun refreshAvailableUsers() =
        viewModelScope.launch {
                _tempUsers.value = repository.tempUserTable.getAll(onError = { onError(it) })
            }
    fun refreshAvailableEmails() =
        viewModelScope.launch {
            _emails.value = emptyList()
            _tempUsers.value.forEach(){u ->
                _emails.value += u.email
            }
        }
    fun addTempUser(email : String, userRole: UserRole, firstname: String, lastname: String, balloonQualification: BalloonQualification?) =
        viewModelScope.launch {
            val index = _emails.value.indexOf(email)
            if(index == -1){
                TempUser(email,userRole,firstname,lastname,balloonQualification)
            }
            else{
                //todo [see how to handle already assigned emails]
            }
            refreshAvailableUsers()
            refreshAvailableEmails()
        }



    private fun onError(e: Exception) {
        SnackbarManager.showMessage(e.message ?: "An unknown error occurred")
    }
}