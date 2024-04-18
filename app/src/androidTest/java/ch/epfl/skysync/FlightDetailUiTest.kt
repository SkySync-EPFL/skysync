package ch.epfl.skysync

import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.unit.dp
import androidx.navigation.testing.TestNavHostController
import ch.epfl.skysync.models.UNSET_ID
import ch.epfl.skysync.models.calendar.TimeSlot
import ch.epfl.skysync.models.flight.FlightType
import ch.epfl.skysync.models.flight.PlannedFlight
import ch.epfl.skysync.models.flight.Role
import ch.epfl.skysync.models.flight.RoleType
import ch.epfl.skysync.models.flight.Team
import ch.epfl.skysync.models.flight.Vehicle
import ch.epfl.skysync.screens.flightDetail.FlightDetailUi
import java.time.LocalDate
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FlightDetailUiTest {
  @get:Rule val composeTestRule = createComposeRule()
  val pilot = Role(RoleType.PILOT, null)
  val crew = Role(RoleType.CREW, null)
  val car = Vehicle("Car")
  lateinit var navController: TestNavHostController
  var dummyFlight =
      mutableStateOf(
          PlannedFlight(
              UNSET_ID,
              1,
              FlightType.FONDUE,
              Team(listOf()),
              null,
              null,
              LocalDate.now(),
              TimeSlot.AM,
              listOf(
                  car,
                  car,
                  car,
                  car,
                  car,
                  car,
                  car,
                  car,
                  car,
                  car,
                  car,
                  car,
                  car,
                  car,
                  car,
                  car,
                  car)))

  @Before
  fun setUpNavHost() {
    composeTestRule.setContent {
      FlightDetailUi(
          backClick = {},
          deleteClick = {},
          editClick = {},
          confirmClick = {},
          padding = PaddingValues(0.dp),
          flight = dummyFlight.value)
    }
  }

  @Test
  fun BackButtonIsDisplayed() {
    composeTestRule.onNodeWithText("Back").assertIsDisplayed()
  }

  @Test
  fun BackButtonIsClickable() {
    composeTestRule.onNodeWithText("Back").assertHasClickAction()
  }

  @Test
  fun DeleteButtonIsDisplayed() {
    composeTestRule.onNodeWithText("Delete").assertIsDisplayed()
  }

  @Test
  fun DeletButtonIsClickable() {
    composeTestRule.onNodeWithText("Delete").assertHasClickAction()
  }

  @Test
  fun EditButtonIsDisplayed() {
    composeTestRule.onNodeWithText("Edit").assertIsDisplayed()
  }

  @Test
  fun EditButtonIsClickable() {
    composeTestRule.onNodeWithText("Edit").assertHasClickAction()
  }

  @Test
  fun ConfirmButtonIsDisplayed() {
    composeTestRule.onNodeWithText("Confirm").assertIsDisplayed()
  }

  @Test
  fun ConfirmButtonIsClickable() {
    composeTestRule.onNodeWithText("Confirm").assertHasClickAction()
  }

  @Test
  fun NumberOfPaxValueIsDisplayed() {
    val expected = dummyFlight.value.nPassengers.toString()
    composeTestRule.onNodeWithText("$expected Pax").assertIsDisplayed()
  }

  @Test
  fun FlightTypeValueIsDisplayed() {
    val expected = dummyFlight.value.flightType.name
    composeTestRule.onNodeWithText(expected).assertIsDisplayed()
  }

  @Test
  fun DateValueIsDisplayed() {
    val expected = dummyFlight.value.date.toString()
    composeTestRule.onNodeWithText(expected).assertIsDisplayed()
  }

  @Test
  fun BalloonIsDisplayed() {
    composeTestRule.onNodeWithText("Balloon").assertIsDisplayed()
  }

  @Test
  fun BalloonValueIsDisplayed() {
    val expected = dummyFlight.value.balloon?.name ?: "None"
    composeTestRule.onNodeWithTag("Balloon$expected").assertIsDisplayed()
  }

  @Test
  fun BasketIsDisplayed() {
    composeTestRule.onNodeWithText("Basket").assertIsDisplayed()
  }

  @Test
  fun BasketValueIsDisplayed() {
    val expected = dummyFlight.value.basket?.name ?: "None"
    composeTestRule.onNodeWithTag("Basket$expected").assertIsDisplayed()
  }

  @Test
  fun TimeSlotIsDisplayed() {
    composeTestRule.onNodeWithText(dummyFlight.value.timeSlot.name).assertIsDisplayed()
  }

  @Test
  fun TeamButtonIsDisplayed() {
    composeTestRule.onNodeWithText("Team").assertIsDisplayed()
  }

  @Test
  fun TeamButtonIsClickable() {
    composeTestRule.onNodeWithText("Team").assertHasClickAction()
  }

  @Test
  fun TeamIsDisplayed() {
    composeTestRule.onNodeWithText("Team").performClick()
    composeTestRule.onNodeWithText("No team member").assertIsDisplayed()
  }

  @Test
  fun VehiclesButtonIsDisplayed() {
    composeTestRule.onNodeWithText("Vehicles").assertIsDisplayed()
  }

  @Test
  fun VehiclesButtonIsClickable() {
    composeTestRule.onNodeWithText("Vehicles").assertHasClickAction()
  }

  @Test
  fun VehiclesAfterTeamIsDisplayed() {
    composeTestRule.onNodeWithText("Team").performClick()
    composeTestRule.onNodeWithText("Vehicles").assertIsDisplayed()
  }

  @Test
  fun VehiclesAreDisplayed() {
    composeTestRule.onNodeWithText("Vehicles").performClick()
    for (index in dummyFlight.value.vehicles.indices) {
      composeTestRule
          .onNodeWithTag("VehicleList")
          .performScrollToNode(hasText("Vehicle $index"))
          .assertIsDisplayed()
    }
  }

  @Test
  fun VehiclesValueAreDisplayed() {
    composeTestRule.onNodeWithText("Vehicles").performClick()
    for (index in dummyFlight.value.vehicles.indices) {
      composeTestRule
          .onNodeWithTag("VehicleList")
          .performScrollToNode(
              hasTestTag("Vehicle $index" + dummyFlight.value.vehicles[index].name))
          .assertIsDisplayed()
    }
  }

  @Test
  fun testNoVehiclesButManyTeamMembers() {
    val teamMembers = List(100) { Role(RoleType.CREW, null) }
    val flightWithNoVehiclesButManyTeamMembers =
        PlannedFlight(
            UNSET_ID,
            1,
            FlightType.FONDUE,
            Team(teamMembers),
            null,
            null,
            LocalDate.now(),
            TimeSlot.AM,
            listOf())

    // Change the dummyFlight variable before running the test
    dummyFlight.value = flightWithNoVehiclesButManyTeamMembers
    composeTestRule.waitForIdle()
    composeTestRule.onNodeWithText("Vehicles").performClick()
    composeTestRule.onNodeWithText("No vehicle").assertIsDisplayed()
    composeTestRule.onNodeWithText("Team").performClick()
    for (index in teamMembers.indices) {
      val firstname = dummyFlight.value.team.roles[index].assignedUser?.firstname ?: ""
      val lastname = dummyFlight.value.team.roles[index].assignedUser?.lastname ?: ""
      val name = "$firstname $lastname"
      Log.d(
          "TeamMember",
          "Member $index: ${dummyFlight.value.team.roles[index].roleType.name}" + name)
      composeTestRule
          .onNodeWithTag("TeamList")
          .performScrollToNode(
              hasText("Member $index: ${dummyFlight.value.team.roles[index].roleType.name}"))
          .assertIsDisplayed()
    }
  }
}
