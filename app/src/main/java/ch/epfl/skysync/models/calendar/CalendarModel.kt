package ch.epfl.skysync.models.calendar

import java.time.LocalDate
import java.time.temporal.ChronoUnit

abstract class CalendarModel<T: CalendarViewable>{
    protected val cells: MutableList<T> = mutableListOf()
    var size: Int = cells.size
        private set

    /**
     * fills the calendar with some init values of T
     * @param from: the first date for which an entry in the calendar is initialized
     * @param to: the last date (inclusive)
     */
    abstract fun initForRange(from: LocalDate,
                              to: LocalDate,)

    /**
     * @param from inclusive
     * @param to inclusive
     */
    protected fun initForRangeSuper(from: LocalDate,
                                    to: LocalDate,
                                    constructor: (LocalDate, TimeSlot) -> T) {
        val numberOfDays = from.until(to, ChronoUnit.DAYS)
        var currentDate = from
        for (i in 0.. numberOfDays) {
            for (timeSlot in TimeSlot.entries) {
                cells.add(constructor(currentDate, timeSlot))
                updateSize()
            }
            currentDate = currentDate.plusDays(1)
        }

    }
    private fun updateSize(){
        size = cells.size
    }

    fun addCells(toAdd: List<T>) {
        for (t in toAdd) {
            if (cells.any {
                it.date == t.date && it.timeSlot == t.timeSlot
            }
                )
            {
                throw IllegalArgumentException("Cannot add cells for the same date and time slot twice")
            }
            cells.add(t)
            updateSize()
        }
    }
    fun setByDate(date: LocalDate,
                  timeSlot: TimeSlot,
                  produceNewValue: (LocalDate, TimeSlot, oldValue: T) -> T) {
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

    fun getByDate(date: LocalDate, timeSlot: TimeSlot): T? {
        return cells.firstOrNull { it.date == date && it.timeSlot == timeSlot }
    }
}
