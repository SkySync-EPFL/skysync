package ch.epfl.skysync

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.testing.TestNavHostController
import ch.epfl.skysync.components.confirmationScreen
import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.models.calendar.TimeSlot
import ch.epfl.skysync.models.flight.Balloon
import ch.epfl.skysync.models.flight.BalloonQualification
import ch.epfl.skysync.models.flight.Basket
import ch.epfl.skysync.models.flight.FlightType
import ch.epfl.skysync.models.flight.PlannedFlight
import ch.epfl.skysync.models.flight.Role
import ch.epfl.skysync.models.flight.RoleType
import ch.epfl.skysync.models.flight.Team
import ch.epfl.skysync.models.flight.Vehicle
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ConfirmFlightUITest {
  @get:Rule val composeTestRule = createComposeRule()
  lateinit var navController: TestNavHostController
  private var planedFlight =
      mutableStateOf(
          PlannedFlight(
              "1234",
              3,
              FlightType.DISCOVERY,
              Team(listOf(Role(RoleType.CREW))),
              Balloon("Ballon Name", BalloonQualification.LARGE, "Ballon Name"),
              Basket("Basket Name", true, "1234"),
              LocalDate.now().plusDays(3),
              TimeSlot.PM,
              listOf(Vehicle("Peugeot 308", "1234"))))

  @Before
  fun setUpNavHost() {
    composeTestRule.setContent {
      val repository = Repository(FirestoreDatabase(useEmulator = true))
      navController = TestNavHostController(LocalContext.current)
      navController.navigatorProvider.addNavigator(ComposeNavigator())
      confirmationScreen(plannedFlight = planedFlight.value, navController = navController) {}
    }
  }
  // test of info to verify by a user
  @Test
  fun verifyTitle() {
    val id = planedFlight.value.id
    composeTestRule.onNodeWithText("Confirmation of Flight $id").assertIsDisplayed()
  }

  @Test
  fun verifyNbPassengers() {
    val passengerNb = planedFlight.value.nPassengers
    composeTestRule.onNodeWithText(passengerNb.toString()).assertIsDisplayed()
  }

  @Test
  fun verifyFlightType() {
    val flightType = planedFlight.value.flightType
    composeTestRule.onNodeWithText(flightType.name).assertIsDisplayed()
  }

  @Test
  fun verifyRoles() {
    val rolesLists = planedFlight.value.team.roles
    rolesLists.forEach() { (role) -> composeTestRule.onNodeWithText(role.name).assertIsDisplayed() }
  }

  @Test
  fun verifyBalloon() {
    val balloon = planedFlight.value.balloon
    if (balloon != null) {
      composeTestRule.onNodeWithText(balloon.name).assertIsDisplayed()
    }
  }

  @Test
  fun verifyBasket() {
    val basket = planedFlight.value.basket
    if (basket != null) {
      composeTestRule.onNodeWithText(basket.name).assertIsDisplayed()
    }
  }

  @Test
  fun verifyNullValues() {
    planedFlight.value =
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
    val date = planedFlight.value.date
    val timeSlot = planedFlight.value.timeSlot
    composeTestRule
        .onNodeWithText(
            (date.dayOfMonth.toString() + " " + date.month.toString() + " $timeSlot").lowercase())
        .assertIsDisplayed()
  }

  @Test
  fun verifyVehicles() {
    val vehicles = planedFlight.value.vehicles
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
}
