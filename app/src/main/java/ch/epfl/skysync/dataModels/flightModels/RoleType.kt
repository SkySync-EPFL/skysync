package ch.epfl.skysync.dataModels.flightModels

enum class RoleType {
    PILOT,
    CREW,
    SERVICE_ON_BOARD,
    MAITRE_FONDUE,
    OXYGEN_MASTER,
    TRANSLATION,
    ON_BOARD // if no other more specific role applies
}