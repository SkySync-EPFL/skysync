package ch.epfl.skysync.models.flight

data class Vehicle(val name:String, val id: String = "")

val EXAMPLE_VEHICLE = Vehicle("sprinter 1")