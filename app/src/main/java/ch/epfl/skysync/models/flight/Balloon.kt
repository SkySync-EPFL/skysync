package ch.epfl.skysync.models.flight

data class Balloon(val name:String, val qualification: BalloonQualification)


val EXAMPLE_BALLOON = Balloon("QQP", BalloonQualification.LARGE)
