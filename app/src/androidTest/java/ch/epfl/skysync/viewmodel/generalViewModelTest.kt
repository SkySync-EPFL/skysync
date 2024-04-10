package ch.epfl.skysync.viewmodel
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.NavHost
import androidx.navigation.testing.TestNavHostController
import ch.epfl.skysync.models.calendar.AvailabilityStatus
import ch.epfl.skysync.models.calendar.TimeSlot
import ch.epfl.skysync.navigation.Route
import ch.epfl.skysync.navigation.homeGraph
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

class generalViewModelTest {

    @get:Rule val composeTestRule = createComposeRule()
    lateinit var viewModel: UserViewModel


    @Before
    fun setUp() {
        composeTestRule.setContent {
//            navController = TestNavHostController(LocalContext.current)
//            navController.navigatorProvider.addNavigator(ComposeNavigator())
//            NavHost(navController = navController, startDestination = Route.MAIN) {
//                homeGraph(navController)
//            }
            viewModel = UserViewModel.createViewModel(firebaseUser = null)
        }
    }

    @Test
    fun correctlyInitsCalendar() {
        val someDate = LocalDate.of(2012, 12, 12)
        val someTimeSlot = TimeSlot.AM
        val nonExistingDate = viewModel.user.value.availabilities.getAvailabilityStatus(
            someDate,
            someTimeSlot
        )
        Assert.assertNull(nonExistingDate)
    }
    @Test
    fun correctlyMaintainsCalendar() {
        val someDate = LocalDate.of(2012, 12, 12)
        val someTimeSlot = TimeSlot.AM
        val newAvailabilityStatus = AvailabilityStatus.OK
        viewModel.user.value.availabilities.setAvailabilityByDate(
            someDate,
            someTimeSlot,
            newAvailabilityStatus
        )
        val foundAvailability = viewModel
            .user
            .value
            .availabilities
            .getAvailabilityStatus(
                someDate,
                someTimeSlot
            )

        Assert.assertEquals(foundAvailability, newAvailabilityStatus)
    }
}
