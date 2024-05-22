package ch.epfl.skysync.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ch.epfl.skysync.database.DateUtility
import ch.epfl.skysync.database.FlightStatus
import ch.epfl.skysync.models.calendar.TimeSlot
import ch.epfl.skysync.models.flight.Balloon
import ch.epfl.skysync.models.flight.BalloonQualification
import ch.epfl.skysync.models.flight.Basket
import ch.epfl.skysync.models.flight.ConfirmedFlight
import ch.epfl.skysync.models.flight.FinishedFlight
import ch.epfl.skysync.models.flight.Flight
import ch.epfl.skysync.models.flight.FlightColor
import ch.epfl.skysync.models.flight.FlightType
import ch.epfl.skysync.models.flight.Role
import ch.epfl.skysync.models.flight.RoleType
import ch.epfl.skysync.models.flight.Team
import ch.epfl.skysync.models.flight.Vehicle
import ch.epfl.skysync.models.flight.flightColorOptions
import ch.epfl.skysync.models.location.LocationPoint
import java.net.URLEncoder
import java.time.Instant
import java.time.LocalDate
import java.util.Date

/**
 * Composable function for displaying flight details in a UI.
 *
 * @param flight The flight object containing details to be displayed. If null, a loading component
 *   is shown.
 * @param padding PaddingValues that specify the padding for the layout.
 */
@Composable
fun FlightDetails(flight: Flight?, padding: PaddingValues) {
  if (flight == null) {
    LoadingComponent(isLoading = true, onRefresh = {}) {}
  } else {
    val cardColor = Color.White
    val defaultPadding = 16.dp
    Column(modifier = Modifier.padding(padding)) {
      LazyColumn(
          modifier = Modifier.padding(8.dp).weight(1f).testTag("FlightDetailLazyColumn"),
          verticalArrangement = Arrangement.spacedBy(defaultPadding)) {
            item { GlobalFlightMetricsDetails(flight = flight, cardColor) }
            item { FlightTeamMembersDetails(flight = flight, padding = defaultPadding, cardColor) }
            when (flight) {
              is ConfirmedFlight -> {
                item {
                  ConfirmFlightTimes(
                      confirmedFlight = flight, padding = defaultPadding, cardColor = cardColor)
                }
                item {
                  ConfirmFlightLocation(
                      confirmedFlight = flight, padding = defaultPadding, cardColor = cardColor)
                }
                item {
                  ConfirmFlightRemarks(
                      confirmedFlight = flight, padding = defaultPadding, cardColor = cardColor)
                }
              }
              is FinishedFlight -> {
                item {
                  FinishedFlightTimes(
                      finishedFlight = flight, padding = defaultPadding, cardColor = cardColor)
                }
                item {
                  FinishedFlightLocation(
                      finishedFlight = flight, padding = defaultPadding, cardColor = cardColor)
                }
              }
            }
          }
    }
  }
}

/**
 * Composable function for displaying global flight metrics details in a card.
 *
 * @param flight The flight object containing details to be displayed.
 * @param cardColor The color to be used for the card background.
 */
@Composable
fun GlobalFlightMetricsDetails(flight: Flight, cardColor: Color) {
  var flightStatus = FlightStatus.PLANNED
  when (flight) {
    is ConfirmedFlight -> {
      flightStatus = FlightStatus.CONFIRMED
    }
    is FinishedFlight -> {
      flightStatus = FlightStatus.FINISHED
    }
  }
  val metrics =
      mapOf(
          "Flight status" to flightStatus.toString(),
          "Day of flight" to DateUtility.localDateToString(flight.date),
          "Time slot" to flight.timeSlot.toString(),
          "Number of Passengers" to "${flight.nPassengers}",
          "Flight type" to flight.flightType.name,
          "Balloon" to (flight.balloon?.name ?: "Unset"),
          "Basket" to (flight.basket?.name ?: "Unset"))

  Card(colors = CardDefaults.cardColors(cardColor)) {
    metrics.forEach { (metric, value) -> DisplaySingleMetric(metric = metric, value = value) }
    DisplayListOfMetrics(
        metric = "Vehicles", values = flight.vehicles.map { vehicle -> vehicle.name })
  }
}

@Composable
fun FlightTeamMembersDetails(
    flight: Flight,
    padding: Dp,
    cardColor: Color,
    flightColor: @Composable () -> Unit = { DisplayFlightColor(flight, padding) }
) {
  Card(colors = CardDefaults.cardColors(cardColor)) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      HeaderTitle(title = "Team", padding = padding, color = Color.Black)
      flightColor()
    }
    flight.team.roles.forEach { role ->
      var values = listOf("Unset")
      if (role.assignedUser != null) {
        values = listOf(role.assignedUser!!.firstname, role.assignedUser!!.lastname)
      }
      DisplayListOfMetrics(metric = role.roleType.description, values = values)
    }
  }
}

/**
 * Composable function for displaying the color information of a confirmed flight.
 *
 * @param flight The flight object containing details to be displayed.
 * @param padding The padding value to be applied around the content.
 */
@Composable
fun DisplayFlightColor(flight: Flight, padding: Dp) {
  when (flight) {
    is ConfirmedFlight -> {
      SmallTitle(
          title = "COLOR ${flight.color}",
          padding = padding,
          color = flightColorOptions.getOrDefault(flight.color, Color.Gray))
    }
    is FinishedFlight -> {
      SmallTitle(
          title = "COLOR ${flight.color}",
          padding = padding,
          color = flightColorOptions.getOrDefault(flight.color, Color.Gray))
    }
  }
}

/**
 * Composable function for displaying the meetup times for a confirmed flight.
 *
 * @param confirmedFlight The confirmed flight object containing details to be displayed.
 * @param padding The padding value to be applied around the content.
 * @param cardColor The color to be used for the card background.
 */
@Composable
fun ConfirmFlightTimes(confirmedFlight: ConfirmedFlight, padding: Dp, cardColor: Color) {
  val times =
      mapOf(
          "Team meet up time" to confirmedFlight.meetupTimeTeam,
          "Team departure time" to confirmedFlight.departureTimeTeam,
          "Passengers meet up time" to confirmedFlight.meetupTimePassenger)

  Card(colors = CardDefaults.cardColors(cardColor)) {
    LargeTitle(title = "Meet up times", padding = padding, color = Color.Black)
    times.forEach { (label, time) ->
      DisplaySingleMetric(metric = label, value = DateUtility.localTimeToString(time))
    }
  }
}

/**
 * Composable function for displaying the takeoff and landing times for a finished flight.
 *
 * @param finishedFlight The finished flight object containing details to be displayed.
 * @param padding The padding value to be applied around the content.
 * @param cardColor The color to be used for the card background.
 */
@Composable
fun FinishedFlightTimes(finishedFlight: FinishedFlight, padding: Dp, cardColor: Color) {
  val times =
      mapOf(
          "Takeoff time" to finishedFlight.takeOffTime,
          "Landing time" to finishedFlight.landingTime)

  Card(colors = CardDefaults.cardColors(cardColor)) {
    LargeTitle(title = "Operational times", padding = padding, color = Color.Black)
    times.forEach { (label, time) ->
      DisplaySingleMetric(
          metric = label, value = DateUtility.localTimeToString(DateUtility.dateToLocalTime(time)))
    }
    DisplaySingleMetric("Flight Duration", (finishedFlight.flightTime / 1000.0).toString() + " sec")
  }
}

/**
 * Composable function for displaying the meetup location of a confirmed flight.
 *
 * @param confirmedFlight The confirmed flight object containing details to be displayed.
 * @param padding The padding value to be applied around the content.
 * @param cardColor The color to be used for the card background.
 */
@Composable
fun ConfirmFlightLocation(confirmedFlight: ConfirmedFlight, padding: Dp, cardColor: Color) {
  Card(colors = CardDefaults.cardColors(cardColor), modifier = Modifier.fillMaxWidth()) {
    LargeTitle(title = "Passengers meet up location", padding = padding, color = Color.Black)
    HyperLinkText(location = confirmedFlight.meetupLocationPassenger, padding = padding)
  }
}

/**
 * Composable function for displaying the takeoff and landing location of a finished flight.
 *
 * @param finishedFlight The finished flight object containing details to be displayed.
 * @param padding The padding value to be applied around the content.
 * @param cardColor The color to be used for the card background.
 */
@Composable
fun FinishedFlightLocation(finishedFlight: FinishedFlight, padding: Dp, cardColor: Color) {
  Card(colors = CardDefaults.cardColors(cardColor), modifier = Modifier.fillMaxWidth()) {
    LargeTitle(title = "Takeoff location", padding = padding, color = Color.Black)
    HyperLinkText(location = finishedFlight.takeOffLocation.name, padding = padding)
  }
  Card(colors = CardDefaults.cardColors(cardColor), modifier = Modifier.fillMaxWidth()) {
    LargeTitle(title = "Landing location", padding = padding, color = Color.Black)
    HyperLinkText(location = finishedFlight.landingLocation.name, padding = padding)
  }
}

/**
 * Composable function for displaying a location as a hyperlink.
 *
 * @param location The location string to be displayed as a hyperlink.
 * @param padding The padding value to be applied around the content.
 */
@Composable
fun HyperLinkText(location: String, padding: Dp) {
  val uriHandler = LocalUriHandler.current
  val encodedValue = URLEncoder.encode(location, "UTF-8")
  val googleMapsLink = "https://www.google.com/maps/search/?api=1&query=$encodedValue"
  val string = buildAnnotatedString {
    pushStringAnnotation(tag = "URL", annotation = googleMapsLink)
    withStyle(
        style =
            SpanStyle(
                color = MaterialTheme.colorScheme.primary,
                textDecoration = TextDecoration.Underline)) {
          append(location)
        }
    pop()
  }
  ClickableText(
      text = string,
      modifier = Modifier.padding(padding).testTag(location),
      style = MaterialTheme.typography.bodyLarge,
      onClick = { offset ->
        string.getStringAnnotations(tag = "URL", start = offset, end = offset).firstOrNull()?.let {
            stringAnnotation ->
          uriHandler.openUri(stringAnnotation.item)
        }
      })
}

/**
 * Composable function for displaying the remarks of a confirmed flight.
 *
 * @param confirmedFlight The confirmed flight object containing details to be displayed.
 * @param padding The padding value to be applied around the content.
 * @param cardColor The color to be used for the card background.
 */
@Composable
fun ConfirmFlightRemarks(confirmedFlight: ConfirmedFlight, padding: Dp, cardColor: Color) {
  Card(colors = CardDefaults.cardColors(cardColor), modifier = Modifier.fillMaxWidth()) {
    LargeTitle(title = "Remarks", padding = padding, color = Color.Black)
    if (confirmedFlight.remarks.isEmpty()) {
      Text(text = "There are no remarks", modifier = Modifier.padding(padding))
    }
    confirmedFlight.remarks.forEach { r -> Text(modifier = Modifier.padding(padding), text = r) }
  }
}

/**
 * Displays a list of metrics with corresponding values in a row format.
 *
 * @param metric The name of the metric.
 * @param values The list of values corresponding to the metric.
 */
@Composable
fun DisplayListOfMetrics(metric: String, values: List<String>) {
  Row(modifier = Modifier.padding(8.dp)) {
    Text(
        text = metric,
        modifier = Modifier.fillMaxWidth().weight(1f),
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Left)
    Column(modifier = Modifier.fillMaxWidth().weight(1f)) {
      values.forEach { value ->
        Text(
            text = value,
            modifier = Modifier.fillMaxWidth().padding(1.dp).testTag("Metric$metric$value"),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center)
      }
    }
  }
  Divider(modifier = Modifier.padding(start = 20.dp), color = Color.LightGray, thickness = 1.dp)
}

/**
 * Displays a metric with corresponding values in a row format.
 *
 * @param metric The name of the metric.
 * @param value The value corresponding to the metric.
 */
@Composable
fun DisplaySingleMetric(metric: String, value: String) {
  DisplayListOfMetrics(metric, listOf(value))
}

/**
 * Composable function for displaying the bottom section of the confirmed flight details UI.
 *
 * @param okClick Lambda function to handle the click event for confirming the flight details.
 */
@Composable
fun ConfirmedFlightDetailBottom(okClick: () -> Unit, deleteClick: () -> Unit, isAdmin: Boolean) {
  BottomAppBar {
    if (isAdmin) {
      Button(
          onClick = deleteClick,
          modifier =
              Modifier.fillMaxHeight().fillMaxWidth(0.5f).padding(16.dp).testTag("OK Button")) {
            Text(text = "Delete", color = Color.White, overflow = TextOverflow.Clip)
          }
    }
    Button(
        onClick = okClick, modifier = Modifier.fillMaxSize().padding(16.dp).testTag("OK Button")) {
          Text(text = "OK", color = Color.White, overflow = TextOverflow.Clip)
        }
  }
}

/**
 * Composable function for displaying the bottom section of the confirmed flight details UI.
 *
 * @param reportClick Lambda function to handle the click event for confirming the flight details.
 */
@Composable
fun FinishedFlightDetailBottom(reportClick: () -> Unit, flightTraceClick: () -> Unit) {
  BottomAppBar {
    Button(
        onClick = reportClick,
        modifier = Modifier.fillMaxHeight().fillMaxWidth(0.5f).padding(16.dp).testTag("Report")) {
          Text(text = "Report", color = Color.White, overflow = TextOverflow.Clip)
        }
    Button(
        onClick = flightTraceClick,
        modifier = Modifier.fillMaxSize().padding(16.dp).testTag("Flight Trace")) {
          Text(text = "Flight Trace", color = Color.White, overflow = TextOverflow.Clip)
        }
  }
}

@Preview
@Composable
fun FlightDetailsPreview() {
  val finishedFlight =
      FinishedFlight(
          "1234",
          3,
          Team(listOf(Role(RoleType.CREW), Role(RoleType.CREW))),
          FlightType.DISCOVERY,
          Balloon("Balloon Name", BalloonQualification.LARGE, "Ballon Name"),
          Basket("Basket Name", true, "1234"),
          LocalDate.now().plusDays(3),
          TimeSlot.PM,
          listOf(
              Vehicle("Peugeot 308", "1234"),
          ),
          color = FlightColor.RED,
          takeOffTime = Date.from(Instant.now()),
          takeOffLocation = LocationPoint(21, 46.0, 6.0, "Vernier"),
          landingTime = Date.from(Instant.now()),
          landingLocation = LocationPoint(21, 46.2, 6.1, "Vernier"),
          flightTime = 2000000)

  FlightDetails(finishedFlight, PaddingValues(0.dp))
}
