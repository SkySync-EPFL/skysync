package ch.epfl.skysync.models.calendar

import java.time.LocalDate
import java.time.temporal.ChronoUnit

/**
 * represents a calendar where each slot uniquely defined by its date and timeslot contains a
 * CalendarViewable object. Each slot can have exactly one object.
 *
 * @property cells: the Collection that contains the cells
 * @property size: the curent number of slots
 *
 * (mutable class)
 */
abstract class CalendarModel<T : CalendarViewable> {
  protected val cells: MutableList<T> = mutableListOf()
  var size: Int = cells.size
    private set

  /**
   * fills the calendar with some init values of T
   *
   * @param from: the first date for which an entry in the calendar is initialized
   * @param to: the last date (inclusive)
   */
  abstract fun initForRange(
      from: LocalDate,
      to: LocalDate,
  )

  /**
   * creates the slots for the given interval and inits each slot with the given constructor lambda
   *
   * @param from first slot to be generated (inclusive)
   * @param to last slot to be generated (inclusive)
   * @param constructor: constructs a new instance for T as a function of the slot coordinates
   */
  protected fun initForRangeSuper(
      from: LocalDate,
      to: LocalDate,
      constructor: (LocalDate, TimeSlot) -> T
  ) {
    val numberOfDays = from.until(to, ChronoUnit.DAYS)
    var currentDate = from
    for (i in 0..numberOfDays) {
      for (timeSlot in TimeSlot.entries) {
        cells.add(constructor(currentDate, timeSlot))
        updateSize()
      }
      currentDate = currentDate.plusDays(1)
    }
  }

  /** updates the size */
  private fun updateSize() {
    size = cells.size
  }

  /**
   * adds the given slots by checking that there are not duplicate slots
   *
   * @param toAdd the slots to add
   * @exception IllegalArgumentException: if multiple slots have the same coordinate (date,
   *   timeSlot)
   */
  fun addCells(toAdd: List<T>) {
    for (t in toAdd) {
      if (cells.any { it.date == t.date && it.timeSlot == t.timeSlot }) {
        throw IllegalArgumentException("Cannot add cells for the same date and time slot twice")
      }
      cells.add(t)
      updateSize()
    }
  }

  /**
   * updates the slot by the given coordinates with the output of produceNewValue
   *
   * @param date the date coordinate of the slot
   * @param timeSlot the timeSlot coordinate of the slot
   * @param produceNewValue computes a new value as function of the coordinates and the old value
   */
  fun setByDate(
      date: LocalDate,
      timeSlot: TimeSlot,
      produceNewValue: (LocalDate, TimeSlot, oldValue: T) -> T
  ) {
    val oldValue = removeByDate(date, timeSlot)
    if (oldValue != null) {
      val newValue = produceNewValue(date, timeSlot, oldValue)
      cells.add(newValue)
    }
  }

  /**
   * @param date the date of the cell to remove
   * @param timeSlot the timeSlot of the cell to remove
   * @return the removed value if it was found, null otherwise
   */
  fun removeByDate(date: LocalDate, timeSlot: TimeSlot): T? {

    val oldValue = getByDate(date, timeSlot)
    if (oldValue != null) {
      cells.remove(oldValue)
      updateSize()
    }
    return oldValue
  }

  /**
   * @param date date coordinate of slot
   * @param timeSlot timeSlot coordinate of slot
   * @return slot for given coordinates if found, else null
   */
  fun getByDate(date: LocalDate, timeSlot: TimeSlot): T? {
    return cells.firstOrNull { it.date == date && it.timeSlot == timeSlot }
  }
}
