package ch.epfl.skysync.models.flight

enum class FlightStatus {
    IN_PLANNING, //still missing some information
    READY_FOR_CONFIRMATION, //has all the information needed to be confirmed
    CONFIRMED, // has been confirmed
    IN_PROGRESS, // is currently happening (flight day)
    MISSING_REPORT, //landed but missing the report
    COMPLETED,
}