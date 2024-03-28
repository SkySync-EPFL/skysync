package ch.epfl.skysync.models.flight

enum class RoleType {
    PILOT,
    CREW,
    SERVICE_ON_BOARD,
    MAITRE_FONDUE,
    OXYGEN_MASTER,
    TRANSLATION,
    ON_BOARD // if no other more specific role applies
}