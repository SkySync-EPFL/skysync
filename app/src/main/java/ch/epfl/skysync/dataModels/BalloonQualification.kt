package ch.epfl.skysync.dataModels

enum class BalloonQualification {
    SMALL, MEDIUM, LARGE;

    fun canFlyThisBalloon(user: User): Boolean {
        if (user is Pilot) {
            return true
            }
        return false

    }
}