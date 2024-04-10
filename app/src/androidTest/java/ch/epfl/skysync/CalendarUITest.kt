//package ch.epfl.skysync
//import ch.epfl.skysync.screens.showCalendarAvailabilities
//
//
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.test.junit4.createComposeRule
//import androidx.compose.ui.test.onNodeWithText
//import androidx.compose.ui.test.performClick
//import androidx.navigation.compose.ComposeNavigator
//import androidx.navigation.compose.NavHost
//import androidx.navigation.testing.TestNavHostController
//import androidx.test.ext.junit.runners.AndroidJUnit4
//import ch.epfl.skysync.navigation.Route
//import ch.epfl.skysync.navigation.homeGraph
//import ch.epfl.skysync.viewmodel.UserViewModel
//import junit.framework.TestCase
//import org.junit.Assert
//import org.junit.Before
//import org.junit.Rule
//import org.junit.Test
//import org.junit.runner.RunWith
//import java.time.LocalDate
//
//@RunWith(AndroidJUnit4::class)
//class CalendarUITest {
//
//    //required to test UI components
//    @get:Rule val composeTestRule = createComposeRule()
//    lateinit var date
//    lateinit var viewModel
//    @Before
//    fun testSetup() {
//        //here ChatScreen refers to the class in app/src/main/java/ch/epfl/skysync
//        date = LocalDate.of(2003,3,3)
//        viewModel = UserViewModel.createViewModel(firebaseUser = null)
//        composeTestRule.setContent { showCalendarAvailabilities(date,viewModel) }
//    }
//
//    @Test
//    fun ButtonIsCorrectlyDisplayedAndhasClickAction() {
//
//        composeTestRule.onNodeWithText("Next Week").performClick()
//
//    }
//
//}