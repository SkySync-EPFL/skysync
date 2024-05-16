package ch.epfl.skysync.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
import androidx.navigation.NavHostController
import ch.epfl.skysync.Repository
import ch.epfl.skysync.database.DatabaseSetup
import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.screens.admin.AddUserScreen
import ch.epfl.skysync.viewmodel.UserManagementViewModel
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class AddUserTest {
  @get:Rule val composeTestRule = createComposeRule()
  val navController: NavHostController = mockk("NavController", relaxed = true)
  private lateinit var userManagementViewModel: UserManagementViewModel
  private val db = FirestoreDatabase(useEmulator = true)
  private val dbSetup = DatabaseSetup()
  private val repository: Repository = Repository(db)

  @Before
  fun setUp() = runTest {
    dbSetup.clearDatabase(db)
    dbSetup.fillDatabase(db)
    composeTestRule.setContent {
      userManagementViewModel =
          UserManagementViewModel.createViewModel(repository, dbSetup.admin1.id)
      AddUserScreen(navController, userManagementViewModel)
    }
  }

  @Test
  fun isFirstNameFieldDisplayed() {
    composeTestRule
        .onNodeWithTag("Add User Lazy Column")
        .performScrollToNode(hasTestTag("First Name"))
    composeTestRule.onNodeWithTag("First Name").assertIsDisplayed()
  }

  @Test
  fun isLastNameFieldDisplayed() {
    composeTestRule
        .onNodeWithTag("Add User Lazy Column")
        .performScrollToNode(hasTestTag("Last Name"))
    composeTestRule.onNodeWithTag("Last Name").assertIsDisplayed()
  }

  @Test
  fun isEmailFieldDisplayed() {
    composeTestRule.onNodeWithTag("Add User Lazy Column").performScrollToNode(hasTestTag("E-mail"))
    composeTestRule.onNodeWithTag("E-mail").assertIsDisplayed()
  }

  @Test
  fun isRoleFieldDisplayed() {
    composeTestRule
        .onNodeWithTag("Add User Lazy Column")
        .performScrollToNode(hasTestTag("Role Menu"))
    composeTestRule.onNodeWithTag("Role Menu").assertIsDisplayed()
  }

  @Test
  fun isBalloonQualificationFieldDisplayed() {
    composeTestRule.onNodeWithTag("Balloon Qualification Menu").assertDoesNotExist()
    composeTestRule
        .onNodeWithTag("Add User Lazy Column")
        .performScrollToNode(hasTestTag("Role Menu"))
    composeTestRule.onNodeWithTag("Role Menu").performClick()
    composeTestRule.onNodeWithText("Pilot", substring = true, ignoreCase = true).performClick()
    composeTestRule
        .onNodeWithTag("Add User Lazy Column")
        .performScrollToNode(hasTestTag("Balloon Qualification Menu"))
    composeTestRule.onNodeWithTag("Balloon Qualification Menu").assertIsDisplayed()
  }

  @Test
  fun isButtonClickable() {
    composeTestRule.onNodeWithTag("Add User Button").performClick()
  }

  @Test
  fun doesErrorWorksCorrectly() {
    composeTestRule.onNodeWithTag("Add User Button").performClick()

    composeTestRule
        .onNodeWithTag("Add User Lazy Column")
        .performScrollToNode(hasTestTag("Role Menu"))
    composeTestRule.onNodeWithText("Select a role", true).assertIsDisplayed()
    composeTestRule.onNodeWithTag("Role Menu").performClick()
    composeTestRule.onNodeWithText("Pilot", substring = true, ignoreCase = true).performClick()

    composeTestRule
        .onNodeWithTag("Add User Lazy Column")
        .performScrollToNode(hasTestTag("First Name"))
    composeTestRule.onNodeWithText("Enter a first name").assertIsDisplayed()
    composeTestRule.onNodeWithTag("First Name").performTextInput("John")

    composeTestRule
        .onNodeWithTag("Add User Lazy Column")
        .performScrollToNode(hasTestTag("Last Name"))
    composeTestRule.onNodeWithText("Enter a last name").assertIsDisplayed()
    composeTestRule.onNodeWithTag("Last Name").performTextInput("Doe")

    composeTestRule.onNodeWithTag("Add User Lazy Column").performScrollToNode(hasTestTag("E-mail"))
    composeTestRule.onNodeWithText("Invalid email").assertIsDisplayed()
    composeTestRule.onNodeWithTag("E-mail").performTextInput("test@gmail.com")

    composeTestRule.onNodeWithTag("Add User Button").performClick()
    composeTestRule
        .onNodeWithTag("Add User Lazy Column")
        .performScrollToNode(hasTestTag("Balloon Qualification Menu"))
    composeTestRule.onNodeWithText("Select a balloon type").assertIsDisplayed()
    composeTestRule.onNodeWithTag("Balloon Qualification Menu").performClick()
    composeTestRule.onNodeWithTag("Balloon Qualification 0").performClick()

    composeTestRule.onNodeWithTag("Add User Button").performClick()

    composeTestRule
        .onNodeWithTag("Add User Lazy Column")
        .performScrollToNode(hasTestTag("Balloon Qualification Menu"))
    composeTestRule.onNodeWithText("Select a balloon type").assertIsNotDisplayed()

    composeTestRule.onNodeWithTag("Add User Lazy Column").performScrollToNode(hasTestTag("E-mail"))
    composeTestRule.onNodeWithText("Invalid email").assertIsNotDisplayed()

    composeTestRule
        .onNodeWithTag("Add User Lazy Column")
        .performScrollToNode(hasTestTag("Last Name"))
    composeTestRule.onNodeWithText("Enter a last name").assertIsNotDisplayed()

    composeTestRule
        .onNodeWithTag("Add User Lazy Column")
        .performScrollToNode(hasTestTag("First Name"))
    composeTestRule.onNodeWithText("Enter a first name").assertIsNotDisplayed()

    composeTestRule
        .onNodeWithTag("Add User Lazy Column")
        .performScrollToNode(hasTestTag("Role Menu"))
    composeTestRule.onNodeWithText("Select a role", true).assertIsNotDisplayed()
  }
}
