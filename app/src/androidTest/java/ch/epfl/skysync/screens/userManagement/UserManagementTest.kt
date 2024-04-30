package ch.epfl.skysync.screens.userManagement

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.compose.rememberNavController
import ch.epfl.skysync.database.DatabaseSetup
import ch.epfl.skysync.models.flight.RoleType
import ch.epfl.skysync.screens.RoleFilter
import ch.epfl.skysync.screens.SearchBar
import ch.epfl.skysync.screens.UserCard
import ch.epfl.skysync.screens.UserManagementScreen
import junit.framework.TestCase.assertEquals
import org.junit.Rule
import org.junit.Test

class UserManagementTest {
  @get:Rule val composeTestRule = createComposeRule()

  private val dbs = DatabaseSetup()

  private val users = listOf(dbs.pilot1, dbs.crew1)

  @Test
  fun userCardDisplaysCorrectly() {

    composeTestRule.setContent { UserCard(user = users[0], onUserClick = {}) }

    composeTestRule
        .onNodeWithText("${dbs.pilot1.firstname} ${dbs.pilot1.lastname}")
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithText(dbs.pilot1.roleTypes.joinToString { it.name })
        .assertIsDisplayed()
  }

  @Test
  fun userCardClickable() {
    var clickedId = ""

    composeTestRule.setContent { UserCard(user = users[0], onUserClick = { clickedId = it }) }

    composeTestRule.onNodeWithText("${dbs.pilot1.firstname} ${dbs.pilot1.lastname}").performClick()
    assertEquals(dbs.pilot1.id, clickedId)
  }

  @Test
  fun searchBarUpdatesQuery() {
    var currentQuery = ""

    composeTestRule.setContent {
      SearchBar(query = currentQuery, onQueryChanged = { currentQuery = it })
    }

    composeTestRule.onNodeWithText("Search users").performTextInput(dbs.pilot1.firstname)
    assertEquals(dbs.pilot1.firstname, currentQuery)
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
      UserManagementScreen(navController = rememberNavController(), users)
    }

    // Assuming your mockUsers are visible to the test
    composeTestRule
        .onNodeWithText("${dbs.pilot1.firstname} ${dbs.pilot1.lastname}")
        .assertIsDisplayed()
    composeTestRule
        .onNodeWithText("${dbs.crew1.firstname} ${dbs.crew1.lastname}")
        .assertIsDisplayed()
  }
}
