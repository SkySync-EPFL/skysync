package ch.epfl.skysync.screens

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
import androidx.navigation.NavHostController
import ch.epfl.skysync.database.DatabaseSetup
import ch.epfl.skysync.database.DateUtility
import ch.epfl.skysync.models.flight.FlightColor
import ch.epfl.skysync.navigation.Route
import ch.epfl.skysync.screens.admin.Confirmation
import ch.epfl.skysync.utils.inputTimePicker
import io.mockk.confirmVerified
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import io.mockk.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ConfirmFlightScreenTest {
  @get:Rule val composeTestRule = createComposeRule()
  @get:Rule val mockkRule = MockKRule(this)
  @RelaxedMockK lateinit var navController: NavHostController

  private val dbs = DatabaseSetup()
  private var plannedFlight = dbs.flight1

  @Before
  fun setUpNavHost() {
    composeTestRule.setContent {
      Confirmation(navController = navController, plannedFlight) {
        navController.navigate(Route.ADMIN_HOME)
      }
    }
  }

  @Test
  fun backButtonWorks() {
    composeTestRule.onNodeWithTag("BackButton").performClick()
    verify { navController.popBackStack() }
    confirmVerified(navController)
  }

  @Test
  fun plannedFlightInformationDisplayed() {
    val metrics =
        mapOf(
            "Day of flight" to DateUtility.localDateToString(plannedFlight.date),
            "Time slot" to plannedFlight.timeSlot.toString(),
            "Number of Passengers" to "${plannedFlight.nPassengers}",
            "Flight type" to plannedFlight.flightType.name,
            "Balloon" to (plannedFlight.balloon?.name ?: "Unset"),
            "Basket" to (plannedFlight.basket?.name ?: "Unset"))
    metrics.forEach { (text, metric) ->
      composeTestRule.onNodeWithText(text).assertIsDisplayed()
      composeTestRule.onNodeWithText(metric).assertIsDisplayed()
    }
    composeTestRule.onNodeWithText("Vehicles").assertIsDisplayed()
    plannedFlight.vehicles.forEach { v ->
      composeTestRule.onNodeWithText(v.name).assertIsDisplayed()
    }
  }

  @Test
  fun teamIsCorrectlyDisplayed() {
    composeTestRule
        .onNodeWithTag("ConfirmationScreenLazyColumn")
        .performScrollToNode(hasText("Team"))
    plannedFlight.team.roles.forEach { r ->
      r.assignedUser?.let {
        composeTestRule.onNodeWithText(it.firstname).assertIsDisplayed()
        composeTestRule.onNodeWithText(it.lastname).assertIsDisplayed()
      }
    }
  }

  @Test
  fun canChooseTeamColor() {
    composeTestRule
        .onNodeWithTag("ConfirmationScreenLazyColumn")
        .performScrollToNode(hasText("Team"))
    composeTestRule.onNodeWithText("Select team color").performClick()
    composeTestRule.onNodeWithText(FlightColor.RED.toString()).performClick()
    composeTestRule.onNodeWithText(FlightColor.RED.toString()).assertIsDisplayed()
    composeTestRule.onNodeWithText(FlightColor.RED.toString())
  }

  private fun addRemark(remark: String) {
    for (i in 0..1) {
      composeTestRule
          .onNodeWithTag("ConfirmationScreenLazyColumn")
          .performScrollToNode(hasText("Remarks"))
      composeTestRule.onNodeWithText("Add remark").performClick()
      composeTestRule.onNodeWithTag("").performTextInput(remark)

      val tag = if (i == 0) "AlertDialogDismiss" else "AlertDialogConfirm"
      composeTestRule.onNodeWithTag(tag).performClick()
    }
  }

  @Test
  fun canAddAndDeleteRemarks() {
    val remark1 = "This flight will be legendary"
    val remark2 = "This flight will be written in history"
    val remarks = listOf(remark1, remark2)
    remarks.forEach { addRemark(it) }
    remarks.forEach {
      composeTestRule.onNodeWithTag("ConfirmationScreenLazyColumn").performScrollToNode(hasText(it))
      composeTestRule.onNodeWithText(it).assertIsDisplayed()
    }

    composeTestRule
        .onNodeWithTag("ConfirmationScreenLazyColumn")
        .performScrollToNode(hasText(remark1))

    composeTestRule.onNodeWithTag("DeleteRemark$remark1").performClick()
    composeTestRule.onNodeWithText(remark1).assertIsNotDisplayed()
    composeTestRule.onNodeWithText(remark2).assertIsDisplayed()
  }

  @Test
  fun addTimesMeetupLocationAndConfirm() {
    composeTestRule
        .onNodeWithTag("ConfirmationScreenLazyColumn")
        .performScrollToNode(hasText("Passengers meet up time"))
    composeTestRule.onNodeWithText("Confirm").assertIsNotEnabled()

    val setTime = composeTestRule.onAllNodesWithTag("TimePickerButton")
    for (i in 0 until setTime.fetchSemanticsNodes().size) {
      setTime[i].performClick()
      inputTimePicker(composeTestRule = composeTestRule, hour = 18, minute = 34)
    }
    composeTestRule
        .onNodeWithTag("ConfirmationScreenLazyColumn")
        .performScrollToNode(hasText("Meet up Location"))
    composeTestRule.onNodeWithTag("Meet up Location").performTextInput("Ecublens")
    composeTestRule.onNodeWithText("Confirm").performClick()
    composeTestRule.onNodeWithTag("AlertDialogConfirm").performClick()
    verify { navController.navigate(Route.ADMIN_HOME) }
    confirmVerified(navController)
  }
}
