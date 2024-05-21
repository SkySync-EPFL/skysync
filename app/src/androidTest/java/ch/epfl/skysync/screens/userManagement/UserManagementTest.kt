package ch.epfl.skysync.screens.userManagement

import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
import androidx.navigation.compose.rememberNavController
import ch.epfl.skysync.Repository
import ch.epfl.skysync.components.ConnectivityStatus
import ch.epfl.skysync.database.DatabaseSetup
import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.models.flight.RoleType
import ch.epfl.skysync.screens.admin.RoleFilter
import ch.epfl.skysync.screens.admin.SearchBar
import ch.epfl.skysync.screens.admin.UserCard
import ch.epfl.skysync.screens.admin.UserManagementScreen
import ch.epfl.skysync.viewmodel.UserManagementViewModel
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class UserManagementTest {
  @get:Rule var composeTestRule = createComposeRule()
  private lateinit var userManagementViewModel: UserManagementViewModel
  private val db = FirestoreDatabase(useEmulator = true)
  private val dbSetup = DatabaseSetup()
  private val repository: Repository = Repository(db)

  @Before
  fun setUp() = runTest {
    dbSetup.clearDatabase(db)
    dbSetup.fillDatabase(db)
  }

  @Test
  fun userCardDisplaysCorrectly() {

    composeTestRule.setContent { UserCard(user = dbSetup.pilot1, onUserClick = {}) }

    composeTestRule
        .onNodeWithText("${dbSetup.pilot1.firstname} ${dbSetup.pilot1.lastname}")
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithText(dbSetup.pilot1.roleTypes.joinToString { it.name })
        .assertIsDisplayed()
  }

  @Test
  fun userCardClickable() {
    var clickedId = ""

    composeTestRule.setContent { UserCard(user = dbSetup.pilot1, onUserClick = { clickedId = it }) }
    composeTestRule
        .onNodeWithText("${dbSetup.pilot1.firstname} ${dbSetup.pilot1.lastname}")
        .performClick()
    assertEquals(dbSetup.pilot1.id, clickedId)
  }

  @Test
  fun searchBarUpdatesQuery() {
    var currentQuery = ""

    composeTestRule.setContent {
      SearchBar(query = currentQuery, onQueryChanged = { currentQuery = it })
    }

    composeTestRule.onNodeWithText("Search users").performTextInput(dbSetup.pilot1.firstname)
    assertEquals(dbSetup.pilot1.firstname, currentQuery)
  }

  @Test
  fun roleFilterDisplaysRoles() {
    composeTestRule.setContent { RoleFilter(onRoleSelected = {}, roles = RoleType.entries) }

    composeTestRule.onNodeWithText("Filter by role").performClick()
    composeTestRule.onNodeWithText("PILOT").assertIsDisplayed()
  }

  @Test
  fun userManagementScreenDisplaysUsers() {
    composeTestRule.setContent {
      userManagementViewModel =
          UserManagementViewModel.createViewModel(repository, dbSetup.admin1.id)
      userManagementViewModel.refresh()
      val context = LocalContext.current
      val connectivityStatus = remember { ConnectivityStatus(context) }
      UserManagementScreen(rememberNavController(), userManagementViewModel, connectivityStatus)
    }

    // Assuming your mockUsers are visible to the test
    composeTestRule
        .onNodeWithTag("UserManagementLazyColumn")
        .performScrollToNode(hasText("${dbSetup.pilot1.firstname} ${dbSetup.pilot1.lastname}"))
    composeTestRule
        .onNodeWithText("${dbSetup.pilot1.firstname} ${dbSetup.pilot1.lastname}")
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithTag("UserManagementLazyColumn")
        .performScrollToNode(hasText("${dbSetup.crew1.firstname} ${dbSetup.crew1.lastname}"))

    composeTestRule
        .onNodeWithText("${dbSetup.crew1.firstname} ${dbSetup.crew1.lastname}")
        .assertIsDisplayed()
  }
}
