package ch.epfl.skysync.viewmodel

import androidx.compose.ui.test.junit4.createComposeRule
import ch.epfl.skysync.Repository
import ch.epfl.skysync.database.DatabaseSetup
import ch.epfl.skysync.database.FirestoreDatabase
import ch.epfl.skysync.database.UserRole
import ch.epfl.skysync.database.tables.BalloonTable
import ch.epfl.skysync.database.tables.BasketTable
import ch.epfl.skysync.database.tables.FlightTable
import ch.epfl.skysync.database.tables.FlightTypeTable
import ch.epfl.skysync.database.tables.VehicleTable
import ch.epfl.skysync.models.user.TempUser
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class UserManagementViewmodelTest {

  private val db = FirestoreDatabase(useEmulator = true)
  private val dbSetup = DatabaseSetup()
  private val flightTable = FlightTable(db)
  private val basketTable = BasketTable(db)
  private val balloonTable = BalloonTable(db)
  private val flightTypeTable = FlightTypeTable(db)
  private val vehicleTable = VehicleTable(db)
  private val repository = Repository(db)

  // adding this rule should set the test dispatcher and should
  // enable us to use advanceUntilIdle(), but it seems advanceUntilIdle
  // cancel the coroutine instead of waiting for it to finish
  // instead use the .join() for the moment
  // @ExperimentalCoroutinesApi @get:Rule var mainCoroutineRule = MainCoroutineRule()

  @get:Rule val composeTestRule = createComposeRule()
  lateinit var viewModelAdmin: UserManagementViewmodel

  @Before
  fun setUp() = runTest {
    dbSetup.clearDatabase(db)
    dbSetup.fillDatabase(db)
  }

  @Test
  fun setTempUser() {
    composeTestRule.setContent {
      viewModelAdmin =
          UserManagementViewmodel.createViewModel(repository = repository, userId = "id-admin-1")
    }
    val firstname = "John"
    val lastname = "Deer"
    val userRole = UserRole.ADMIN
    val email = "john.deer@gmail.com"
    val testTempUser =
        TempUser(firstname = firstname, lastname = lastname, userRole = userRole, email = email)
    viewModelAdmin.addTempUser(email, userRole, firstname, lastname, null)
    val vmTempUser = viewModelAdmin.getTempUser(email)
    Assert.assertNotNull(vmTempUser)
    if (vmTempUser != null) {
      Assert.assertEquals(firstname, vmTempUser.firstname)
      Assert.assertEquals(lastname, vmTempUser.lastname)
      Assert.assertEquals(userRole, vmTempUser.userRole)
      Assert.assertEquals(email, vmTempUser.email)
    }
  }
}
