package ch.epfl.skysync.navigation

/** Define the paths used for the navigation in the app */
object Route {
  const val MAIN = "Main graph"
  const val FLIGHT = "Flight"
  const val ADD_FLIGHT = "Add Flight"
  const val MODIFY_FLIGHT = "Modify Flight"
  const val CONFIRM_FLIGHT = "Confirm Flight"
  const val ADMIN_USER_DETAILS = "User Details"
  const val REPORT = "Report"

  // The reports route will be merged when the viewModel is implemented
  const val CREW_REPORT = "Crew Report"
  const val PILOT_REPORT = "Pilot Report"

  const val USER = "User"
  const val ADD_USER = "Add User"

  const val STATS = "Stats"

  const val LOGIN = "Login"

  const val LOADING = "Loading"
  const val ADMIN = "Admin"
  const val ADMIN_HOME = "Admin Home"
  const val ADMIN_FLIGHT_DETAILS = "Admin Flight Details"
  const val ADMIN_CALENDAR = "Admin Calendar"
  const val ADMIN_CHAT = "Admin Chat"
  const val ADMIN_TEXT = "Admin Text"
  const val ADMIN_AVAILABILITY_CALENDAR = "Admin Availability Calendar"
  const val ADMIN_FLIGHT_CALENDAR = "Admin Flight Calendar"
  const val ADMIN_STATS = "Admin Stats"
  const val ADMIN_FLIGHT = "Admin Flight"

  const val CREW_PILOT = "Crew Pilot"
  const val CREW_CHAT = "Chat"
  const val CREW_FLIGHT_DETAILS = "Flight Details"
  const val CREW_CALENDAR = "Calendar"
  const val CREW_AVAILABILITY_CALENDAR = "Availability Calendar"
  const val CREW_FLIGHT_CALENDAR = "Flight Calendar"
  const val CREW_HOME = "Home"
  const val CREW_TEXT = "Text"
  const val LAUNCH_FLIGHT = "Launch Flight"
}
