package ch.epfl.skysync.dataModels.flightModels

data class Balloon(val name:String, val qualification: BalloonQualification)


val EXAMPLE_BALLOON = Balloon("QQP", BalloonQualification.LARGE)
