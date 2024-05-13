package ch.epfl.skysync.viewmodel

import androidx.compose.ui.test.junit4.createComposeRule
import ch.epfl.skysync.Repository
import ch.epfl.skysync.database.DatabaseSetup
import ch.epfl.skysync.database.FirestoreDatabase
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class UserManagementViewModelTest {
  @get:Rule var composeTestRule = createComposeRule()
  private lateinit var userManagementViewModel: UserManagementViewModel
  private val db = FirestoreDatabase(useEmulator = true)
  private val dbSetup = DatabaseSetup()
  private val repository: Repository = Repository(db)

  @Before
  fun setUp() = runTest {
    dbSetup.clearDatabase(db)
    dbSetup.fillDatabase(db)
    composeTestRule.setContent {
      userManagementViewModel = UserManagementViewModel.createViewModel(repository, "id-admin-1")
      userManagementViewModel.refresh()
    }
  }

  @Test
  fun loadsCorrectAdmin() = runTest {
    composeTestRule.waitUntil { userManagementViewModel.userId != null }
    assert(userManagementViewModel.userId == "id-admin-1")
  }

  @Test
  fun loadsCorrectUsers() = runTest {
    composeTestRule.waitUntil { userManagementViewModel.allUsers.value.isNotEmpty() }
    assert(userManagementViewModel.allUsers.value.size == 7)
  }

  @Test
  fun deleteCorrectUser() = runTest {
    composeTestRule.waitUntil { userManagementViewModel.allUsers.value.isNotEmpty() }
    val userToDelete = userManagementViewModel.allUsers.value[0]
    userManagementViewModel.deleteUser(userToDelete)
    composeTestRule.waitUntil { userManagementViewModel.allUsers.value.size == 6 }
    assert(userManagementViewModel.allUsers.value[0] != userToDelete)
  }
}
