//package ch.epfl.skysync.viewmodel
//
//import androidx.compose.ui.test.junit4.createComposeRule
//import ch.epfl.skysync.models.calendar.AvailabilityStatus
//import ch.epfl.skysync.models.calendar.TimeSlot
//import java.time.LocalDate
//import org.junit.Assert
//import org.junit.Before
//import org.junit.Rule
//import org.junit.Test
//
//class generalViewModelTest {
//
//  @get:Rule val composeTestRule = createComposeRule()
//  lateinit var viewModel: CalendarViewModel
//
//  @Before
//  fun setUp() {
//    composeTestRule.setContent { viewModel = CalendarViewModel.createViewModel(firebaseUser = null) }
//  }
//
//  @Test
//  fun correctlyInitsCalendar() {
//    val someDate = LocalDate.of(2012, 12, 12)
//    val someTimeSlot = TimeSlot.AM
//    val nonExistingDate =
//        viewModel.user.value.availabilities.getAvailabilityStatus(someDate, someTimeSlot)
//    Assert.assertEquals(nonExistingDate, AvailabilityStatus.UNDEFINED)
//    Assert.assertEquals(viewModel.user.value.availabilities.getSize(), 0)
//  }
//
//  @Test
//  fun correctlyMaintainsCalendar() {
//    val someDate = LocalDate.of(2012, 12, 12)
//    val someTimeSlot = TimeSlot.AM
//    val newAvailabilityStatus = AvailabilityStatus.OK
//    viewModel.user.value.availabilities.setAvailabilityByDate(
//        someDate, someTimeSlot, newAvailabilityStatus)
//    val foundAvailability =
//        viewModel.user.value.availabilities.getAvailabilityStatus(someDate, someTimeSlot)
//
//    Assert.assertEquals(foundAvailability, newAvailabilityStatus)
//  }
//}
