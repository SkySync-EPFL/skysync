package ch.epfl.skysync.components

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.testing.TestNavHostController
import ch.epfl.skysync.database.DatabaseSetup
import ch.epfl.skysync.database.FirestoreDatabase
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FlightCardTest {
  @get:Rule val composeTestRule = createComposeRule()
  lateinit var navController: TestNavHostController
  private val db = FirestoreDatabase(useEmulator = true)
  private val dbs = DatabaseSetup()

  @Before
  fun setUpNavHost() = runTest {
    dbs.clearDatabase(db)
    dbs.fillDatabase(db)
  }

  @Test
  fun flightCardDisplaysCorrectData() {
    val flight = dbs.flight1

    // Set the content to the FlightCard composable
    composeTestRule.setContent { FlightCard(flight = flight, onFlightClick = {}) }
    composeTestRule.onNodeWithText("Wed\nAug 14").assertExists()
  }
}
