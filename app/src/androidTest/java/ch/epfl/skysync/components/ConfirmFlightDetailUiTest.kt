package ch.epfl.skysync.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
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
import ch.epfl.skysync.models.flight.Team
import ch.epfl.skysync.components.ConfirmFlightDetailUi
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime

class ConfirmFlightDetailUi {
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
                listOf(),
                listOf(),
                FlightColor.ORANGE,
                meetupTimeTeam = LocalTime.MIN,
                departureTimeTeam = LocalTime.NOON,
                meetupTimePassenger = LocalTime.MIDNIGHT,
                meetupLocationPassenger = "Test Location",
            )
        )

    @Before
    fun setUpNavHost() {
        composeTestRule.setContent {
            ConfirmFlightDetail(
                originalFlight = dummyFlight,
                confirmedFlight = dummyConfirmedFlight.value,
                backClick = {},
                paddingValues = PaddingValues(0.dp)
            )
        }
    }

    @Test
    fun idIsDisplayed() {
        composeTestRule.onNodeWithTag()
    }

}