package ch.epfl.skysync.dataModels.flightModels

import ch.epfl.skysync.dataModels.userModels.Pilot
import ch.epfl.skysync.dataModels.userModels.User

enum class BalloonQualification {
    SMALL, MEDIUM, LARGE;

    fun canFlyThisBalloon(user: User): Boolean {
        if (user is Pilot) {
            return true
            }
        return false

    }
}