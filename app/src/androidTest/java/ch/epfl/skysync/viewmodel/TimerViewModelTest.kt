package ch.epfl.skysync.viewmodel

import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class TimerViewModelTest {

  @get:Rule val composeTestRule = createComposeRule()
  lateinit var timerViewModel: TimerViewModel

  @Before
  fun setUp() {
    composeTestRule.setContent {
      timerViewModel = TimerViewModel.createViewModel()
      val countString by timerViewModel.counter.collectAsStateWithLifecycle()
      Text(countString)
    }
  }

  @Test
  fun isZeroBeforeStart() = runTest {
    val countString = timerViewModel.counter.value
    assertTrue(countString == "00:00:00")
  }

  @Test
  fun testStartFunction() {
    timerViewModel.start()
    composeTestRule.waitUntil(timeoutMillis = 1500) {
      val countString = timerViewModel.counter.value
      countString == "00:00:01"
    }
    val isRunning = timerViewModel.isRunning.value
    assertTrue(isRunning)
    val currentCount = timerViewModel.counter.value
    assertTrue(currentCount == "00:00:01")
  }

  @Test
  fun testStopFunction() = runTest {
    timerViewModel.start()
    composeTestRule.waitUntil(timeoutMillis = 2500) {
      val countString = timerViewModel.counter.value
      countString == "00:00:02"
    }
    timerViewModel.stop()
    val isRunning = timerViewModel.isRunning.value
    assertFalse(isRunning)
    val currentCount = timerViewModel.counter.value
    assertTrue(currentCount == "00:00:02")
  }
}
