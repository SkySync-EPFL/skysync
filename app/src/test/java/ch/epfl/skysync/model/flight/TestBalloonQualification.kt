package ch.epfl.skysync.model.flight


import ch.epfl.skysync.models.flight.BalloonQualification

import org.junit.Before
import org.junit.Assert.*
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class TestBalloonQualification {
  @Test
  fun `greater equal compares correctly`() {
    assertTrue(BalloonQualification.LARGE.greaterEqual(BalloonQualification.LARGE))
    assertTrue(BalloonQualification.LARGE.greaterEqual(BalloonQualification.MEDIUM))
    assertTrue(BalloonQualification.LARGE.greaterEqual(BalloonQualification.SMALL))
    assertTrue(BalloonQualification.MEDIUM.greaterEqual(BalloonQualification.SMALL))
    assertFalse(BalloonQualification.MEDIUM.greaterEqual(BalloonQualification.LARGE))

  }







}
