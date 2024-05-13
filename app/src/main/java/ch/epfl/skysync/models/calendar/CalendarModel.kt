package ch.epfl.skysync.models.calendar

import java.time.LocalDate
import java.util.SortedSet

/**
 * represents a calendar where each slot uniquely defined by its date and timeslot contains a
 * CalendarViewable object. Each slot can have exactly one object.
 *
 * @property cells: the Collection that contains the cells
 *
 * (mutable class)
 */
abstract class CalendarModel<T : CalendarViewable>(
  val cells: List<T> = emptyList(),
) {


  /**
   * returns a new CalendarModel with the given cells
   */
  abstract fun constructor(cells: List<T>): CalendarModel<T>

  /** @return number of entries in calendar */
  fun getSize(): Int {
    return cells.size
  }


  protected fun add(t: T, ): CalendarModel<T> {
    return constructor(cells + t)
  }

  /**
   * adds the given slots by checking that there are not duplicate slots
   *
   * @param elementsToAdd the slots to add
   */
  protected fun addCells(elementsToAdd: List<T>): CalendarModel<T> {
    val combinedCells = cells + elementsToAdd
    return constructor(combinedCells)
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
  ) : CalendarModel<T> {
    val oldValue = getByDate(date, timeSlot)
    val newValue = produceNewValue(date, timeSlot, oldValue)
    val newCells = if (oldValue != null) {
      cells - oldValue + newValue
    }
    else{
      cells + newValue
    }
    return constructor(newCells)
  }

  /**
   * @param date the date of the cell to remove
   * @param timeSlot the timeSlot of the cell to remove
   * @return the removed value if it was found, null otherwise
   */
  protected fun removeByDate(date: LocalDate, timeSlot: TimeSlot): CalendarModel<T> {

    val oldValue = getByDate(date, timeSlot)
    return if (oldValue != null) {
      constructor(cells - oldValue)
    }
    else {
      this
    }
  }

  /**
   * @param date date coordinate of slot
   * @param timeSlot timeSlot coordinate of slot
   * @return slot for given coordinates if found, else null
   */
  protected fun getByDate(date: LocalDate, timeSlot: TimeSlot): T? {
    return cells.firstOrNull { it.date == date && it.timeSlot == timeSlot }
  }

  /** Return a set of the cells, sorted by date and time slot */
  private fun getSortedSetOfCells(): SortedSet<T> {
    return cells.toSortedSet(
        Comparator<T> { a, b -> a.date.compareTo(b.date) }.thenBy { it.timeSlot })
  }

  override fun hashCode(): Int {
    return getSortedSetOfCells().hashCode()
  }

  override fun equals(other: Any?): Boolean {
    if (other == null) return false
    if (other::class != this::class) return false
    // we do not take into account the order in which the cells have been added
    // when performing equality check
    return (other as CalendarModel<*>).getSortedSetOfCells() == this.getSortedSetOfCells()
  }
}
