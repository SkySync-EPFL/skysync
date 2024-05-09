package ch.epfl.skysync.components

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.navigation.NavHostController
import ch.epfl.skysync.database.DatabaseSetup
import ch.epfl.skysync.models.calendar.TimeSlot
import ch.epfl.skysync.models.flight.FlightType
import ch.epfl.skysync.models.flight.PlannedFlight
import ch.epfl.skysync.models.flight.Role
import ch.epfl.skysync.models.flight.RoleType
import ch.epfl.skysync.models.flight.Team
import ch.epfl.skysync.models.flight.Vehicle
import ch.epfl.skysync.navigation.Route
import io.mockk.confirmVerified
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import io.mockk.verify
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ConfirmFlightUITest {
  @get:Rule val composeTestRule = createComposeRule()
  @get:Rule val mockkRule = MockKRule(this)
  @RelaxedMockK lateinit var navController: NavHostController

  private val dbs = DatabaseSetup()

  private var plannedFlight = mutableStateOf(dbs.flight1)

  @Before
  fun setUpNavHost() {
    composeTestRule.setContent {
      confirmation(plannedFlight.value) { navController.navigate(Route.MAIN) }
    }
  }
  // test of info to verify by a user
  @Test
  fun verifyTitle() {
    val id = plannedFlight.value.id
    composeTestRule.onNodeWithText("Confirmation of Flight $id").assertIsDisplayed()
  }

  @Test
  fun verifyNbPassengers() {
    val passengerNb = plannedFlight.value.nPassengers
    composeTestRule.onNodeWithText(passengerNb.toString()).assertIsDisplayed()
  }

  @Test
  fun verifyFlightType() {
    val flightType = plannedFlight.value.flightType
    composeTestRule.onNodeWithText(flightType.name).assertIsDisplayed()
  }

  @Test
  fun verifyRoles() {
    val rolesLists = plannedFlight.value.team.roles
    rolesLists.forEach() { (role) -> composeTestRule.onNodeWithText(role.name).assertIsDisplayed() }
  }

  @Test
  fun verifyBalloon() {
    val balloon = plannedFlight.value.balloon
    if (balloon != null) {
      composeTestRule.onNodeWithText(balloon.name).assertIsDisplayed()
    }
  }

  @Test
  fun verifyBasket() {
    val basket = plannedFlight.value.basket
    if (basket != null) {
      composeTestRule.onNodeWithText(basket.name).assertIsDisplayed()
    }
  }

  @Test
  fun verifyNullValues() {
    plannedFlight.value =
        PlannedFlight(
            "1234",
            3,
            FlightType.DISCOVERY,
            Team(listOf(Role(RoleType.CREW))),
            null,
            null,
            LocalDate.now().plusDays(3),
            TimeSlot.PM,
            listOf(Vehicle("Peugeot 308", "1234")))
    composeTestRule.onNodeWithText("Basket").assertIsNotDisplayed()
    composeTestRule.onNodeWithText("Balloon").assertIsNotDisplayed()
  }

  @Test
  fun verifyDateAndTimeSlotShown() {
    val date = plannedFlight.value.date
    val timeSlot = plannedFlight.value.timeSlot
    composeTestRule
        .onNodeWithText(
            (date.dayOfMonth.toString() + " " + date.month.toString() + " $timeSlot").lowercase())
        .assertIsDisplayed()
  }

  @Test
  fun verifyVehicles() {
    val vehicles = plannedFlight.value.vehicles
    vehicles.forEach() { (vehicle) -> composeTestRule.onNodeWithText(vehicle).assertIsDisplayed() }
  }
  // test of info to enter by user
  @Test
  fun canColorsBeChoosen() {
    composeTestRule.onNodeWithText("Select Option").performClick()
    composeTestRule.onNodeWithText("RED").performClick()
    composeTestRule.onNodeWithText("RED").assertIsDisplayed()
  }

  @Test
  fun verifyGoodColorChoosing() {
    composeTestRule.onNodeWithText("Select Option").performClick()
    composeTestRule.onNodeWithText("RED").performClick()
    composeTestRule.onNodeWithText("BLUE").assertIsNotDisplayed()
  }

  @Test
  fun verifyTimeSettingForMeetUpTime() {
    val wantedTimeSet = LocalTime.of(21, 25)
    val wantedHour = wantedTimeSet.hour
    val wantedMinute = wantedTimeSet.minute
    val tag = "MeetUp"
    composeTestRule.onNodeWithTag("LazyList").performScrollToNode(hasText("Confirm"))
    composeTestRule.onNodeWithTag(tag + "/Hours").performTextClearance()
    composeTestRule.onNodeWithTag(tag + "/Hours").performTextInput(wantedHour.toString())
    composeTestRule.onNodeWithTag(tag + "/Minutes").performTextClearance()
    composeTestRule.onNodeWithTag(tag + "/Minutes").performTextInput(wantedMinute.toString())
    composeTestRule.onNodeWithTag(tag + "/SetTime").performClick()
    composeTestRule
        .onNodeWithText(
            "Selected Time: ${wantedTimeSet.format(DateTimeFormatter.ofPattern("HH:mm"))}")
        .assertIsDisplayed()
  }

  @Test
  fun verifyTimeSettingForDepartureTime() {
    val wantedTimeSet = LocalTime.of(23, 22)
    val wantedHour = wantedTimeSet.hour
    val wantedMinute = wantedTimeSet.minute
    val tag = "Departure"
    composeTestRule.onNodeWithTag("LazyList").performScrollToNode(hasText("Confirm"))
    composeTestRule.onNodeWithTag(tag + "/Hours").performTextClearance()
    composeTestRule.onNodeWithTag(tag + "/Hours").performTextInput(wantedHour.toString())
    composeTestRule.onNodeWithTag(tag + "/Minutes").performTextClearance()
    composeTestRule.onNodeWithTag(tag + "/Minutes").performTextInput(wantedMinute.toString())
    composeTestRule.onNodeWithTag(tag + "/SetTime").performClick()
    composeTestRule
        .onNodeWithText(
            "Selected Time: ${wantedTimeSet.format(DateTimeFormatter.ofPattern("HH:mm"))}")
        .assertIsDisplayed()
  }

  @Test
  fun verifyTimeSettingForPassengersMeetUpTimeWithNonproperValues() {
    val tag = "MeetUp pass."
    composeTestRule.onNodeWithTag(tag + "/Hours").performTextClearance()
    composeTestRule.onNodeWithTag(tag + "/Hours").performTextInput("This isn't a number")
    composeTestRule.onNodeWithTag(tag + "/Minutes").performTextClearance()
    composeTestRule.onNodeWithTag(tag + "/Minutes").performTextInput("this neither")
    composeTestRule.onNodeWithTag(tag + "/SetTime").performClick()
  }

  @Test
  fun verifyLocationAdding() {
    val wantedLocation = "VernierZoo"
    composeTestRule.onNodeWithText("Enter MeetUp Location").performTextInput(wantedLocation)
    composeTestRule.onNodeWithText("Add MeetUp Location").performClick()
    composeTestRule.onNodeWithText(wantedLocation).assertIsDisplayed()
  }

  @Test
  fun verifyRemarkAdding() {
    val wantedRemark = "We're on the capital"
    composeTestRule.onNodeWithText("Enter Remark").performTextInput(wantedRemark)
    composeTestRule.onNodeWithText("Add Remark").performClick()
    composeTestRule.onNodeWithText(wantedRemark).assertIsDisplayed()
  }

  @Test
  fun confirmButtonWorksWhenDismiss() = runTest {
    composeTestRule.onNodeWithTag("AlertDialog").assertIsNotDisplayed()
    composeTestRule.onNodeWithTag("LazyList").performScrollToNode(hasText("Confirm"))
    composeTestRule.onNodeWithText("Confirm").performClick()
    composeTestRule.onNodeWithTag("AlertDialog").assertIsDisplayed()
    composeTestRule.onNodeWithTag("AlertDialogDismiss").performClick()
    composeTestRule.onNodeWithTag("AlertDialog").assertIsNotDisplayed()

    // Test that navigate was not called (the route is not meaningful)
    verify(exactly = 0) { navController.navigate(Route.MAIN) }
  }

  @Test
  fun confirmButtonWorksWhenConfirm() = runTest {
    composeTestRule.onNodeWithTag("AlertDialog").assertIsNotDisplayed()
    composeTestRule.onNodeWithTag("LazyList").performScrollToNode(hasText("Confirm"))
    composeTestRule.onNodeWithText("Confirm").performClick()
    composeTestRule.onNodeWithTag("AlertDialog").assertIsDisplayed()
    composeTestRule.onNodeWithTag("AlertDialogConfirm").performClick()

    verify { navController.navigate(Route.MAIN) }
    confirmVerified(navController)
  }
}
