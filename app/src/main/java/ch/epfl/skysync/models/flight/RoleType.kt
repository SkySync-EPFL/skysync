package ch.epfl.skysync.models.flight

enum class RoleType(val description: String) {
  PILOT("Pilot"),
  CREW("Crew"),
  SERVICE_ON_BOARD("Service on board"),
  MAITRE_FONDUE ("Maitre Fondue"),
  OXYGEN_MASTER("Oxygen Master"),
  TRANSLATION("Translation"),
  ON_BOARD("Crew On Board"); // if no other more specific role applies

}
