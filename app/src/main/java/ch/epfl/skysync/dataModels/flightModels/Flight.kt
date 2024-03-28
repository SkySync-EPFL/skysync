package ch.epfl.skysync.dataModels.flightModels

import java.time.LocalDate

interface Flight{
    val id: Int
    val n_passengers: Int
    val team: Team
    val flightType: FlightType
    val balloon: Balloon? // might not yet be defined on flight creation
    val basket: Basket? // might not yet be defined on flight creation
    val date: LocalDate
    val isMorningFlight: Boolean
    val vehicles: List<Vehicle>
}