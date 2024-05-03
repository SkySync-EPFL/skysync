package ch.epfl.skysync.database.tables

import androidx.test.ext.junit.runners.AndroidJUnit4
import ch.epfl.skysync.database.DatabaseSetup
import ch.epfl.skysync.database.FirestoreDatabase
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TempUserTableUnitTest {
  private val db = FirestoreDatabase(useEmulator = true)
  private val dbs = DatabaseSetup()
  private val tempUserTable = TempUserTable(db)

  @Before
  fun testSetup() = runTest {
    dbs.clearDatabase(db)
    dbs.fillDatabase(db)
  }

  @Test
  fun getTest() = runTest {
    val user = tempUserTable.get(dbs.tempUser.email, onError = { assertNull(it) })
    if (user != null) {
      assertEquals(dbs.tempUser.email, user.email)
    }
  }

  @Test
  fun deleteTest() = runTest {
    tempUserTable.delete(dbs.tempUser.email, onError = { assertNull(it) })
    val user = tempUserTable.get(dbs.tempUser.email, onError = { assertNull(it) })
    assertEquals(null, user)
  }
}
