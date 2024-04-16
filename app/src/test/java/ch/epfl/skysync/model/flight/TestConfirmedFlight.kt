package ch.epfl.skysync.model.flight

import ch.epfl.skysync.models.calendar.AvailabilityCalendar
import ch.epfl.skysync.models.calendar.FlightGroupCalendar
import ch.epfl.skysync.models.calendar.TimeSlot
import ch.epfl.skysync.models.flight.Balloon
import ch.epfl.skysync.models.flight.BalloonQualification
import ch.epfl.skysync.models.flight.Basket
import ch.epfl.skysync.models.flight.FlightColor
import ch.epfl.skysync.models.flight.FlightType
import ch.epfl.skysync.models.flight.PlannedFlight
import ch.epfl.skysync.models.flight.Role
import ch.epfl.skysync.models.flight.RoleType
import ch.epfl.skysync.models.flight.Team
import ch.epfl.skysync.models.flight.Vehicle
import ch.epfl.skysync.models.user.Crew
import ch.epfl.skysync.models.user.User
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class TestConfirmedFlight {

  lateinit var user: User

  @Before
  fun setUp() {
    user = Crew(
        id = "1",
        firstname = "Paul",
        lastname = "Panzer",
        availabilities = AvailabilityCalendar(),
        assignedFlights = FlightGroupCalendar()
    )
  }

  @Test
  fun `throw error if tries to confirm flight that is not ready`() {
    val plannedFlight =
      PlannedFlight(
        id = "1",
        nPassengers = 2,
        flightType = FlightType.DISCOVERY,
        balloon = Balloon("QQP", BalloonQualification.LARGE, ""),
        basket = Basket("basket 1", hasDoor = false),
        date = LocalDate.of(2024, 4, 1),
        timeSlot = TimeSlot.AM,
        vehicles = listOf(Vehicle("sprinter 1"))
      )

    val meetUpTimeTeam = LocalTime.of(10, 10)
    val departureTimeTeam = LocalTime.of(10, 30)
    val meetUpTimePassenger = LocalTime.of(10, 40)
    val meetUpLocation = "EPFL"
    val remarks = listOf("remark 1")
    val color = FlightColor.BLUE
    assertThrows(
      IllegalStateException::class.java)
    {
      plannedFlight.confirmFlight(
        meetupTimeTeam = meetUpTimeTeam,
        departureTimeTeam = departureTimeTeam,
        meetupTimePassenger = meetUpTimePassenger,
        meetupLocationPassenger = meetUpLocation,
        remarks = remarks,
        color = color
      )
    }
  }

  @Test
  fun `confirmed flight is correctly created from plannedFlight`() {
    val completeTeam = Team(
      listOf(
        Role(RoleType.PILOT, user),
      )
    )
    val plannedFlight =
      PlannedFlight(
        id = "1",
        nPassengers = 2,
        flightType = FlightType.DISCOVERY,
        team = completeTeam,
        balloon = Balloon("QQP", BalloonQualification.LARGE, ""),
        basket = Basket("basket 1", hasDoor = false),
        date = LocalDate.of(2024, 4, 1),
        timeSlot = TimeSlot.AM,
        vehicles = listOf(Vehicle("sprinter 1"))
      )

    val meetUpTimeTeam = LocalTime.of(10, 10)
    val departureTimeTeam = LocalTime.of(10, 30)
    val meetUpTimePassenger = LocalTime.of(10, 40)
    val meetUpLocation = "EPFL"
    val remarks = listOf("remark 1")
    val color = FlightColor.BLUE
    val confirmedFlight = plannedFlight.confirmFlight(
      meetupTimeTeam = meetUpTimeTeam,
      departureTimeTeam = departureTimeTeam,
      meetupTimePassenger = meetUpTimePassenger,
      meetupLocationPassenger = meetUpLocation,
      remarks = remarks,
      color = color
    )
    assertEquals(plannedFlight.id, confirmedFlight.id)
    assertEquals(plannedFlight.nPassengers, confirmedFlight.nPassengers)
    assertEquals(plannedFlight.flightType, confirmedFlight.flightType)
    assertEquals(plannedFlight.balloon, confirmedFlight.balloon)
    assertEquals(plannedFlight.basket, confirmedFlight.basket)
    assertEquals(plannedFlight.date, confirmedFlight.date)
    assertEquals(plannedFlight.timeSlot, confirmedFlight.timeSlot)
    assertEquals(plannedFlight.vehicles, confirmedFlight.vehicles)
    assertEquals(plannedFlight.team, confirmedFlight.team)
    assertEquals(meetUpTimeTeam, confirmedFlight.meetupTimeTeam)
    assertEquals(departureTimeTeam, confirmedFlight.departureTimeTeam)
    assertEquals(meetUpTimePassenger, confirmedFlight.meetupTimePassenger)
    assertEquals(meetUpLocation, confirmedFlight.meetupLocationPassenger)
    assertEquals(remarks, confirmedFlight.remarks)
    assertEquals(color, confirmedFlight.color)
  }


}
