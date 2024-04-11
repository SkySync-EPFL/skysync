package ch.epfl.skysync.database

/**
 * Represent the role of the user
 *
 * It is the authentication role, as such, it is used to define the permissions of the user. It is
 * not the same as models.flight.RoleType which define the role of a user on a particular flight.
 */
enum class UserRole {
  ADMIN,
  CREW,
  PILOT,
}
