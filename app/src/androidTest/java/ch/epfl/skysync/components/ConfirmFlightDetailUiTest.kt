package ch.epfl.skysync.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.mutableStateOf
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
import ch.epfl.skysync.models.flight.PlannedFlight
import ch.epfl.skysync.models.flight.Role
import ch.epfl.skysync.models.flight.RoleType
import ch.epfl.skysync.models.flight.Team
import ch.epfl.skysync.models.flight.Vehicle
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime

class ConfirmFlightDetailUiTest {
    @get:Rule
    val composeTestRule = createComposeRule()
    lateinit var navController: TestNavHostController
    private var dummyFlight =
        PlannedFlight(
            UNSET_ID,
            1,
            FlightType.FONDUE,
            Team(listOf()),
            null,
            null,
            LocalDate.now(),
            TimeSlot.AM,
            listOf()
        )
    private var dummyConfirmedFlight =
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
            )

    @Before
    fun setUpNavHost() {
        composeTestRule.setContent {
            ConfirmFlightDetail(
                originalFlight = dummyFlight,
                confirmedFlight = dummyConfirmedFlight,
                backClick = {},
                paddingValues = PaddingValues(0.dp)
            )
        }
    }

    @Test
    fun idIsDisplayed() {
        composeTestRule.onNodeWithTag("body").performScrollToNode(hasTestTag("ID"))
        composeTestRule.onNodeWithTag("ID").assertIsDisplayed()
    }
    @Test
    fun nbOfPaxIsDisplayed(){
        composeTestRule.onNodeWithTag("body").performScrollToNode(hasTestTag("Number Of Pax"))
        composeTestRule.onNodeWithTag("Number Of Pax").assertIsDisplayed()
    }
    @Test
    fun flightTypeIsDisplayed(){
        composeTestRule.onNodeWithTag("body").performScrollToNode(hasTestTag("Flight Type"))
        composeTestRule.onNodeWithTag("Flight Type").assertIsDisplayed()
    }
    @Test
    fun balloonIsDisplayed(){
        composeTestRule.onNodeWithTag("body").performScrollToNode(hasTestTag("Balloon"))
        composeTestRule.onNodeWithTag("Balloon").assertIsDisplayed()
    }
    @Test
    fun teamIsNotDisplayed(){
        composeTestRule.onNodeWithTag("body").performScrollToNode(hasTestTag("Team"))
        composeTestRule.onNodeWithTag("Team").assertIsDisplayed()
    }
    @Test
    fun teamIsDisplayed(){
        dummyConfirmedFlight = dummyConfirmedFlight.copy(team = Team(listOf(Role(RoleType.PILOT),Role(RoleType.CREW))))
        var indexPilot = 0
        var indexCrew = 0
        for(i in dummyConfirmedFlight.team.roles){
            if(i.roleType == RoleType.PILOT){
                composeTestRule.onNodeWithTag("body").performScrollToNode(hasTestTag(RoleType.PILOT.name + "$indexPilot"))
                composeTestRule.onNodeWithTag(RoleType.PILOT.name + "$indexPilot").assertIsDisplayed()
                indexPilot += 1
            }
            if(i.roleType == RoleType.CREW){
                composeTestRule.onNodeWithTag("body").performScrollToNode(hasTestTag(RoleType.CREW.name + "$indexCrew"))
                composeTestRule.onNodeWithTag(RoleType.PILOT.name + "$indexCrew").assertIsDisplayed()
                indexCrew += 1
            }
        }
    }

    @Test
    fun ballonIsDisplayed(){
        composeTestRule.onNodeWithTag("body").performScrollToNode(hasTestTag("Balloon"))
        composeTestRule.onNodeWithTag("Balloon").assertIsDisplayed()
    }
    @Test
    fun basketIsDisplayed(){
        composeTestRule.onNodeWithTag("body").performScrollToNode(hasTestTag("Basket"))
        composeTestRule.onNodeWithTag("Basket").assertIsDisplayed()
    }
    @Test
    fun dateIsDisplayed(){
        composeTestRule.onNodeWithTag("body").performScrollToNode(hasTestTag("Date"))
        composeTestRule.onNodeWithTag("Date").assertIsDisplayed()
    }
    @Test
    fun timeSlotIsDisplayed(){
        composeTestRule.onNodeWithTag("body").performScrollToNode(hasTestTag("Time Slot"))
        composeTestRule.onNodeWithTag("Time Slot").assertIsDisplayed()
    }
    @Test
    fun vehiclesIsDisplayed(){
        dummyConfirmedFlight = dummyConfirmedFlight.copy(vehicles = listOf(Vehicle("test1"),Vehicle("test2")))
        var index = 0
        for(i in dummyConfirmedFlight.vehicles){
            composeTestRule.onNodeWithTag("body").performScrollToNode(hasTestTag("Vehicle$index"))
            composeTestRule.onNodeWithTag("Vehicle$index").assertIsDisplayed()
            index += 1
        }
    }
    @Test
    fun vehiclesIsNotDisplayed(){
        dummyConfirmedFlight = dummyConfirmedFlight.copy(vehicles = listOf())
        composeTestRule.onNodeWithTag("body").performScrollToNode(hasTestTag("Vehicle"))
        composeTestRule.onNodeWithTag("Vehicle").assertIsDisplayed()
    }
    @Test
    fun remarksIsDisplayed(){
        dummyConfirmedFlight = dummyConfirmedFlight.copy(remarks = listOf("test1","test2"))
        var index = 0
        for(i in dummyConfirmedFlight.remarks){
            composeTestRule.onNodeWithTag("body").performScrollToNode(hasTestTag("Remark$index"))
            composeTestRule.onNodeWithTag("Remark$index").assertIsDisplayed()
            index += 1
        }
    }
    @Test
    fun remarksIsNotDisplayed(){
        dummyConfirmedFlight = dummyConfirmedFlight.copy(remarks = listOf())
        composeTestRule.onNodeWithTag("body").performScrollToNode(hasTestTag("Remark"))
        composeTestRule.onNodeWithTag("Remark").assertIsDisplayed()
    }
    @Test
    fun colorIsDisplayed(){
        composeTestRule.onNodeWithTag("body").performScrollToNode(hasTestTag("Color"))
        composeTestRule.onNodeWithTag("Color").assertIsDisplayed()
    }
    @Test
    fun meetupTimeTeamIsDisplayed(){
        composeTestRule.onNodeWithTag("body").performScrollToNode(hasTestTag("Meetup Time Team"))
        composeTestRule.onNodeWithTag("Meetup Time Team").assertIsDisplayed()
    }
    @Test
    fun departureTimeTeamIsDisplayed(){
        composeTestRule.onNodeWithTag("body").performScrollToNode(hasTestTag("Departure Time Team"))
        composeTestRule.onNodeWithTag("Departure Time Team").assertIsDisplayed()
    }
    @Test
    fun meetupTimePassengerIsDisplayed(){
        composeTestRule.onNodeWithTag("body").performScrollToNode(hasTestTag("Meetup Time Passenger"))
        composeTestRule.onNodeWithTag("Meetup Time Passenger").assertIsDisplayed()
    }
    @Test
    fun meetupLocationPassengerIsDisplayed(){
        composeTestRule.onNodeWithTag("body").performScrollToNode(hasTestTag("Meetup Location Passenger"))
        composeTestRule.onNodeWithTag("Meetup Location Passenger").assertIsDisplayed()
    }

}