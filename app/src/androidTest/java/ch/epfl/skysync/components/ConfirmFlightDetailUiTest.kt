package ch.epfl.skysync.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.unit.dp
import androidx.navigation.testing.TestNavHostController
import ch.epfl.skysync.models.UNSET_ID
import ch.epfl.skysync.models.calendar.TimeSlot
import ch.epfl.skysync.models.flight.Balloon
import ch.epfl.skysync.models.flight.BalloonQualification
import ch.epfl.skysync.models.flight.Basket
import ch.epfl.skysync.models.flight.ConfirmedFlight
import ch.epfl.skysync.models.flight.FlightColor
import ch.epfl.skysync.models.flight.FlightType
import ch.epfl.skysync.models.flight.Role
import ch.epfl.skysync.models.flight.RoleType
import ch.epfl.skysync.models.flight.Team
import ch.epfl.skysync.models.flight.Vehicle
import java.time.LocalDate
import java.time.LocalTime
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ConfirmFlightDetailUiTest {
  @get:Rule val composeTestRule = createComposeRule()
  lateinit var navController: TestNavHostController
  private var dummyConfirmedFlight =
      mutableStateOf(
          ConfirmedFlight(
              UNSET_ID,
              1,
              Team(listOf()),
              FlightType.FONDUE,
              Balloon("Test Balloon", BalloonQualification.LARGE),
              Basket("Test Basket", true),
              LocalDate.now(),
              TimeSlot.AM,
              listOf(Vehicle("test")),
              listOf("test"),
              FlightColor.ORANGE,
              meetupTimeTeam = LocalTime.MIN,
              departureTimeTeam = LocalTime.NOON,
              meetupTimePassenger = LocalTime.MIDNIGHT,
              meetupLocationPassenger = "Test Location",
          ))

  @Before
  fun setUpNavHost() {
    composeTestRule.setContent {
      ConfirmFlightDetailUi(
          confirmedFlight = dummyConfirmedFlight.value,
          backClick = {},
          paddingValues = PaddingValues(0.dp),
          okClick = {},
      )
    }
  }

  @Test
  fun nbOfPaxIsDisplayed() {
    composeTestRule.onNodeWithTag("body").performScrollToNode(hasTestTag("Number Of Pax"))
    composeTestRule.onNodeWithTag("Number Of Pax").assertIsDisplayed()
  }

  @Test
  fun flightTypeIsDisplayed() {
    composeTestRule.onNodeWithTag("body").performScrollToNode(hasTestTag("Flight Type"))
    composeTestRule.onNodeWithTag("Flight Type").assertIsDisplayed()
  }

  @Test
  fun balloonIsDisplayed() {
    composeTestRule.onNodeWithTag("body").performScrollToNode(hasTestTag("Balloon"))
    composeTestRule.onNodeWithTag("Balloon").assertIsDisplayed()
  }

  @Test
  fun teamIsDisplayed() {
    dummyConfirmedFlight.value =
        dummyConfirmedFlight.value.copy(
            team = Team(listOf(Role(RoleType.PILOT), Role(RoleType.CREW))))
    var index = 0
    for (i in dummyConfirmedFlight.value.team.roles) {
      composeTestRule.onNodeWithTag("body").performScrollToNode(hasTestTag("Team $index"))
      composeTestRule.onNodeWithTag("Team $index").assertIsDisplayed()
      index += 1
    }
  }

  @Test
  fun ballonIsDisplayed() {
    composeTestRule.onNodeWithTag("body").performScrollToNode(hasTestTag("Balloon"))
    composeTestRule.onNodeWithTag("Balloon").assertIsDisplayed()
  }

  @Test
  fun basketIsDisplayed() {
    composeTestRule.onNodeWithTag("body").performScrollToNode(hasTestTag("Basket"))
    composeTestRule.onNodeWithTag("Basket").assertIsDisplayed()
  }

  @Test
  fun dateIsDisplayed() {
    composeTestRule.onNodeWithTag("body").performScrollToNode(hasTestTag("Date"))
    composeTestRule.onNodeWithTag("Date").assertIsDisplayed()
  }

  @Test
  fun timeSlotIsDisplayed() {
    composeTestRule.onNodeWithTag("body").performScrollToNode(hasTestTag("Time Slot"))
    composeTestRule.onNodeWithTag("Time Slot").assertIsDisplayed()
  }

  @Test
  fun vehiclesIsDisplayed() {
    dummyConfirmedFlight.value =
        dummyConfirmedFlight.value.copy(vehicles = listOf(Vehicle("test1"), Vehicle("test2")))
    var index = 0
    for (i in dummyConfirmedFlight.value.vehicles) {
      composeTestRule.onNodeWithTag("body").performScrollToNode(hasTestTag("Vehicle $index"))
      composeTestRule.onNodeWithTag("Vehicle $index").assertIsDisplayed()
      index += 1
    }
  }

  @Test
  fun remarksIsDisplayed() {
    dummyConfirmedFlight.value = dummyConfirmedFlight.value.copy(remarks = listOf("test1", "test2"))
    var index = 0
    for (i in dummyConfirmedFlight.value.remarks) {
      composeTestRule.onNodeWithTag("body").performScrollToNode(hasTestTag("Remark $index"))
      composeTestRule.onNodeWithTag("Remark $index").assertIsDisplayed()
      index += 1
    }
  }

  @Test
  fun remarksIsNotDisplayed() {
    dummyConfirmedFlight.value = dummyConfirmedFlight.value.copy(remarks = listOf())
    composeTestRule.onNodeWithTag("body").performScrollToNode(hasTestTag("Remarks"))
    composeTestRule.onNodeWithTag("Remarks").assertIsDisplayed()
  }

  @Test
  fun colorIsDisplayed() {
    composeTestRule.onNodeWithTag("body").performScrollToNode(hasTestTag("Flight Color"))
    composeTestRule.onNodeWithTag("Flight Color").assertIsDisplayed()
  }

  @Test
  fun meetupTimeTeamIsDisplayed() {
    composeTestRule.onNodeWithTag("body").performScrollToNode(hasTestTag("Meetup Time Team"))
    composeTestRule.onNodeWithTag("Meetup Time Team").assertIsDisplayed()
  }

  @Test
  fun departureTimeTeamIsDisplayed() {
    composeTestRule.onNodeWithTag("body").performScrollToNode(hasTestTag("Departure Time Team"))
    composeTestRule.onNodeWithTag("Departure Time Team").assertIsDisplayed()
  }

  @Test
  fun meetupTimePassengerIsDisplayed() {
    composeTestRule.onNodeWithTag("body").performScrollToNode(hasTestTag("Meetup Time Passenger"))
    composeTestRule.onNodeWithTag("Meetup Time Passenger").assertIsDisplayed()
  }

  @Test
  fun meetupLocationPassengerIsDisplayed() {
    composeTestRule
        .onNodeWithTag("body")
        .performScrollToNode(hasTestTag("Meetup Location Passenger"))
    composeTestRule.onNodeWithTag("Meetup Location Passenger").assertIsDisplayed()
  }

  @Test
  fun confirmButtonIsDisplayed() {
    composeTestRule.onNodeWithTag("OK Button").assertIsDisplayed()
  }

  @Test
  fun confirmButtonIsClickable() {
    composeTestRule.onNodeWithTag("OK Button").assertHasClickAction()
  }
}
