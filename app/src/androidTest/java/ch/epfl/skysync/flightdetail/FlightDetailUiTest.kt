package ch.epfl.skysync.flightdetail

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import ch.epfl.skysync.database.DatabaseSetup
import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.database.tables.FlightTable
import ch.epfl.skysync.navigation.Route
import ch.epfl.skysync.components.FlightDetailUi
import io.mockk.confirmVerified
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FlightDetailUiTest {
  @get:Rule val composeTestRule = createComposeRule()

  @get:Rule val mockkRule = MockKRule(this)
  // Relaxed mocks methods have a default implementation returning values
  @RelaxedMockK lateinit var navController: NavHostController
  private val db = FirestoreDatabase(useEmulator = true)
  private val dbs = DatabaseSetup()
  private val flightTable = FlightTable(db)

  @Before
  fun setUpNavHost() = runTest {
    dbs.clearDatabase(db)
    dbs.fillDatabase(db)

    composeTestRule.setContent {
      FlightDetailUi(
          backClick = { navController.popBackStack() },
          deleteClick = { navController.navigate(Route.HOME) },
          editClick = { navController.navigate(Route.MODIFY_FLIGHT + "/${dbs.flight1.id}") },
          confirmClick = { navController.navigate(Route.CONFIRM_FLIGHT + "/${dbs.flight1.id}") },
          padding = PaddingValues(0.dp),
          flight = dbs.flight1,
      )
    }
  }

  @Test
  fun backButtonWorks() {
    composeTestRule.onNodeWithText("Back").performClick()
    verify { navController.popBackStack() }
    confirmVerified(navController)
  }

  @Test
  fun deleteButtonWorksWhenDismiss() = runTest {
    composeTestRule.onNodeWithTag("AlertDialog").assertIsNotDisplayed()
    composeTestRule.onNodeWithText("Delete").performClick()
    composeTestRule.onNodeWithTag("AlertDialog").assertIsDisplayed()
    composeTestRule.onNodeWithTag("AlertDialogDismiss").performClick()
    composeTestRule.onNodeWithTag("AlertDialog").assertIsNotDisplayed()

    val getFlight = flightTable.get(dbs.flight1.id, onError = { Assert.assertNull(it) })

    Assert.assertEquals(dbs.flight1, getFlight)
  }

  @Test
  fun deleteButtonWorksWhenConfirm() = runTest {
    composeTestRule.onNodeWithTag("AlertDialog").assertIsNotDisplayed()
    composeTestRule.onNodeWithText("Delete").performClick()
    composeTestRule.onNodeWithTag("AlertDialog").assertIsDisplayed()
    composeTestRule.onNodeWithTag("AlertDialogConfirm").performClick()

    verify { navController.navigate(Route.HOME) }
    confirmVerified(navController)
  }

  @Test
  fun editButtonWorks() {
    composeTestRule.onNodeWithText("Edit").performClick()
    verify { navController.navigate(Route.MODIFY_FLIGHT + "/${dbs.flight1.id}") }
    confirmVerified(navController)
  }

  @Test
  fun confirmButtonWorks() {
    composeTestRule.onNodeWithText("Confirm").performClick()
    verify { navController.navigate(Route.CONFIRM_FLIGHT + "/${dbs.flight1.id}") }
    confirmVerified(navController)
  }

  @Test
  fun numberOfPaxValueIsDisplayed() {
    val expected = dbs.flight1.nPassengers.toString()
    composeTestRule.onNodeWithText("$expected Pax").assertIsDisplayed()
  }

  @Test
  fun flightTypeValueIsDisplayed() {
    val expected = dbs.flight1.flightType.name
    composeTestRule.onNodeWithText(expected).assertIsDisplayed()
  }

  @Test
  fun dateValueIsDisplayed() {
    val expected = dbs.flight1.date.toString()
    composeTestRule.onNodeWithText(expected).assertIsDisplayed()
  }

  @Test
  fun balloonAndValueIsDisplayed() {
    composeTestRule.onNodeWithText("Balloon").assertIsDisplayed()
    val expected = dbs.flight1.balloon?.name ?: "None"
    composeTestRule.onNodeWithTag("Balloon$expected").assertIsDisplayed()
  }

  @Test
  fun basketAndValueIsDisplayed() {
    composeTestRule.onNodeWithText("Basket").assertIsDisplayed()
    val expected = dbs.flight1.basket?.name ?: "None"
    composeTestRule.onNodeWithTag("Basket$expected").assertIsDisplayed()
  }

  @Test
  fun timeSlotIsDisplayed() {
    composeTestRule.onNodeWithText(dbs.flight1.timeSlot.name).assertIsDisplayed()
  }

  @Test
  fun teamButtonWorks() {
    composeTestRule.onNodeWithText("Team").performClick()
    for (index in dbs.flight1.team.roles.indices) {
      val role = dbs.flight1.team.roles[index]
      composeTestRule
          .onNodeWithTag("TeamList")
          .performScrollToNode(hasText("Member $index: ${role.roleType.name}"))
          .assertIsDisplayed()
      val firstname = role.assignedUser?.firstname ?: ""
      val lastname = role.assignedUser?.lastname ?: ""
      val name = "$firstname $lastname"
      composeTestRule.onNodeWithText(name).assertIsDisplayed()
    }
  }

  @Test
  fun vehiclesAfterTeamIsDisplayed() {
    composeTestRule.onNodeWithText("Team").performClick()
    composeTestRule.onNodeWithText("Vehicles").assertIsDisplayed()
  }

  @Test
  fun vehiclesAndValuesAreDisplayed() {
    composeTestRule.onNodeWithText("Vehicles").performClick()
    for (index in dbs.flight1.vehicles.indices) {
      composeTestRule
          .onNodeWithTag("VehicleList")
          .performScrollToNode(hasText("Vehicle $index"))
          .assertIsDisplayed()

      composeTestRule
          .onNodeWithTag("Vehicle $index" + dbs.flight1.vehicles[index].name)
          .assertIsDisplayed()
    }
  }

  @Test
  fun vehiclesValueAreDisplayed() {
    composeTestRule.onNodeWithText("Vehicles").performClick()
    for (index in dbs.flight1.vehicles.indices) {
      composeTestRule
          .onNodeWithTag("VehicleList")
          .performScrollToNode(hasTestTag("Vehicle $index" + dbs.flight1.vehicles[index].name))
          .assertIsDisplayed()
    }
  }
}
