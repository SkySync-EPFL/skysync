package ch.epfl.skysync.screens.userManagement

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.compose.rememberNavController
import ch.epfl.skysync.models.calendar.AvailabilityCalendar
import ch.epfl.skysync.models.calendar.FlightGroupCalendar
import ch.epfl.skysync.models.flight.RoleType
import ch.epfl.skysync.models.user.User
import ch.epfl.skysync.screens.RoleFilter
import ch.epfl.skysync.screens.SearchBar
import ch.epfl.skysync.screens.UserCard
import ch.epfl.skysync.screens.UserManagementScreen
import junit.framework.TestCase.assertEquals
import org.junit.Rule
import org.junit.Test

class UserManagementTest {
  @get:Rule val composeTestRule = createComposeRule()

  private val mockUsers =
      listOf(
          object : User {
            override val id = "1"
            override val firstname = "Jean"
            override val lastname = "Michel"
            override val availabilities: AvailabilityCalendar = AvailabilityCalendar()
            override val assignedFlights: FlightGroupCalendar = FlightGroupCalendar()
            override val roleTypes = setOf(RoleType.PILOT)

            override fun addRoleType(roleType: RoleType) = this
          },
      )

  @Test
  fun userCardDisplaysCorrectly() {

    composeTestRule.setContent { UserCard(user = mockUsers[0], onUserClick = {}) }

    composeTestRule.onNodeWithText("Jean Michel").assertIsDisplayed()
    composeTestRule.onNodeWithText("PILOT").assertIsDisplayed()
  }

  @Test
  fun userCardClickable() {
    var clickedId = ""

    composeTestRule.setContent { UserCard(user = mockUsers[0], onUserClick = { clickedId = it }) }

    composeTestRule.onNodeWithText("Jean Michel").performClick()
    assertEquals("1", clickedId)
  }

  @Test
  fun searchBarUpdatesQuery() {
    var currentQuery = ""

    composeTestRule.setContent {
      SearchBar(query = currentQuery, onQueryChanged = { currentQuery = it })
    }

    composeTestRule.onNodeWithText("Search users").performTextInput("Jean")
    assertEquals("Jean", currentQuery)
  }

  @Test
  fun roleFilterDisplaysRoles() {
    composeTestRule.setContent {
      RoleFilter(onRoleSelected = {}, roles = RoleType.values().toList())
    }

    composeTestRule.onNodeWithText("Filter by role").performClick()
    composeTestRule.onNodeWithText("PILOT").assertIsDisplayed()
  }

  @Test
  fun userManagementScreenDisplaysUsers() {
    composeTestRule.setContent { UserManagementScreen(navController = rememberNavController()) }

    // Assuming your mockUsers are visible to the test
    composeTestRule.onNodeWithText("Jean Michel").assertIsDisplayed()
    composeTestRule.onNodeWithText("Jean Kevin").assertIsDisplayed()
  }
}
