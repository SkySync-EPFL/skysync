package ch.epfl.skysync.models.flight

/**
 * Enum class representing the different role types.
 *
 * The roles are:
 * - ADMIN
 * - PILOT
 * - CREW
 * - SERVICE_ON_BOARD
 * - MAITRE_FONDUE
 * - OXYGEN_MASTER
 * - TRANSLATION
 * - ON_BOARD used if no other more specific role applies
 *
 * @property description The description of the role type.
 */
enum class RoleType(val description: String) {
  ADMIN("Admin"),
  PILOT("Pilot"),
  CREW("Crew"),
  SERVICE_ON_BOARD("Service on board"),
  MAITRE_FONDUE("Maitre Fondue"),
  OXYGEN_MASTER("Oxygen Master"),
  TRANSLATION("Translation"),
  ON_BOARD("Crew On Board")
}
