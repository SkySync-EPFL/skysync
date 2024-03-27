package ch.epfl.skysync.dataModels

import java.time.LocalDate

data class Availability(
    val id: String,
    val status: AvailabilityStatus,
    override val timeSlot: TimeSlot,
    override val date: LocalDate,
) : CalendarViewable{
    fun setStatus(status: AvailabilityStatus): Availability {
        if (status == this.status) return this
        return Availability(id, status, timeSlot, date)
    }
}

enum class AvailabilityStatus {
    OK,
    MAYBE,
    NO
}


interface CalendarViewable {
    val date: LocalDate
    val timeSlot: TimeSlot
}



abstract class CalendarModel<T: CalendarViewable>{
    val cells: MutableList<T> = mutableListOf()
    fun init_from_to(from: LocalDate, to: LocalDate) {
        //todo
    }

    fun addCells(toAdd: List<T>) {
        cells.addAll(toAdd)
    }

    fun setByDate(date: LocalDate,
                  timeSlot: TimeSlot,
                  produceNewValue: (LocalDate, TimeSlot, oldValue: T) -> T) {
        val oldValue = removeByDate(date, timeSlot)
        val newValue = produceNewValue(date, timeSlot, oldValue)
        cells.add(newValue)

    }
    fun removeByDate(date: LocalDate, timeSlot: TimeSlot): T {
        //todo: remove correct one
        return cells.removeFirst()
    }

    fun getByDate(date: LocalDate, timeSlot: TimeSlot): T? {
        return cells.firstOrNull { it.date == date && it.timeSlot == timeSlot }
    }
}
