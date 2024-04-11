package ch.epfl.skysync.models.calendar

import java.time.LocalDate

/**
 * represents a calendar where each slot uniquely defined by its date and timeslot contains a
 * CalendarViewable object. Each slot can have exactly one object.
 *
 * @property cells: the Collection that contains the cells
 *
 * (mutable class)
 */
abstract class CalendarModel<T : CalendarViewable> {
  protected val cells: MutableList<T> = mutableListOf()

  /** @return number of entries in calendar */
  fun getSize(): Int {
    return cells.size
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
    }
  }

  /**
   * updates the slot (if present) or creates new slot by the given coordinates with the output of
   * produceNewValue
   *
   * @param date the date coordinate of the slot
   * @param timeSlot the timeSlot coordinate of the slot
   * @param produceNewValue computes a new value as function of the coordinates and the old value
   */
  protected fun setByDate(
      date: LocalDate,
      timeSlot: TimeSlot,
      produceNewValue: (LocalDate, TimeSlot, oldValue: T?) -> T
  ) {
    val oldValue = removeByDate(date, timeSlot)
    val newValue = produceNewValue(date, timeSlot, oldValue)
    cells.add(newValue)
  }

  /**
   * @param date the date of the cell to remove
   * @param timeSlot the timeSlot of the cell to remove
   * @return the removed value if it was found, null otherwise
   */
  protected fun removeByDate(date: LocalDate, timeSlot: TimeSlot): T? {

    val oldValue = getByDate(date, timeSlot)
    if (oldValue != null) {
      cells.remove(oldValue)
    }
    return oldValue
  }

  /**
   * @param date date coordinate of slot
   * @param timeSlot timeSlot coordinate of slot
   * @return slot for given coordinates if found, else null
   */
  protected fun getByDate(date: LocalDate, timeSlot: TimeSlot): T? {
    return cells.firstOrNull { it.date == date && it.timeSlot == timeSlot }
  }
}
