package ch.epfl.skysync.models.flight

import ch.epfl.skysync.models.user.Pilot
import ch.epfl.skysync.models.user.User

enum class BalloonQualification {
    SMALL, MEDIUM, LARGE;

    fun canFlyThisBalloon(user: User): Boolean {
        return user is Pilot
        //TODO: check that ballons Qualification is higher or equal than balloon
    }
}