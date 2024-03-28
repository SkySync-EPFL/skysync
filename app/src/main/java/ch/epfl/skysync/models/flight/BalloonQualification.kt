package ch.epfl.skysync.models.flight

import ch.epfl.skysync.models.user.Pilot
import ch.epfl.skysync.models.user.User

enum class BalloonQualification {
    SMALL, MEDIUM, LARGE;

    fun canFlyThisBalloon(user: User): Boolean {
        if (user is Pilot) {
            return true
            }
        return false

    }
}