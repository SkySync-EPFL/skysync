package ch.epfl.skysync.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import ch.epfl.skysync.models.flight.ConfirmedFlight
import ch.epfl.skysync.models.flight.FlightColor
import ch.epfl.skysync.models.flight.Team
import ch.epfl.skysync.models.flight.Vehicle
import ch.epfl.skysync.ui.theme.Pink40
import ch.epfl.skysync.ui.theme.lightOrange

/**
 * Composable function for displaying a UI to confirm flight details.
 *
 * @param confirmedFlight The details of the confirmed flight.
 * @param backClick Lambda function to handle the click event for going back.
 * @param paddingValues The padding values to be applied to the layout.
 * @param okClick Lambda function to handle the click event for confirming the flight.
 */
@Composable
fun ConfirmFlightDetailUi(
    confirmedFlight: ConfirmedFlight,
    backClick: () -> Unit,
    paddingValues: PaddingValues,
    okClick: () -> Unit
) {
  Column(modifier = Modifier.fillMaxHeight().fillMaxWidth().padding(paddingValues)) {
    Column(modifier = Modifier.fillMaxHeight(0.87f)) {
      Header(backClick = backClick, title = "Confirmed Flight")
      ConfirmFlightDetailBody(confirmedFlight = confirmedFlight)
    }
    ConfirmedFlightDetailBottom(okClick = okClick)
  }
}

/**
 * Composable function for displaying the body of the confirmation UI with flight details.
 *
 * @param confirmedFlight The details of the confirmed flight to be displayed.
 */
@Composable
fun ConfirmFlightDetailBody(confirmedFlight: ConfirmedFlight) {
  Column(
      modifier =
          Modifier.padding(horizontal = 16.dp)
              .padding(PaddingValues(top = 16.dp))
              .verticalScroll(enabled = true, state = rememberScrollState())
              .testTag("body")
              .fillMaxHeight(0.8f)) {
        TitledText(padding = 16.dp, title = "ID", value = confirmedFlight.id)
        TitledText(
            padding = 16.dp, title = "Number Of Pax", value = "${confirmedFlight.nPassengers}")
        TitledText(padding = 16.dp, title = "Flight Type", value = confirmedFlight.flightType.name)
        TeamText(team = confirmedFlight.team, padding = 16.dp)
        TitledText(padding = 16.dp, title = "Balloon", value = confirmedFlight.balloon.name)
        TitledText(padding = 16.dp, title = "Basket", value = confirmedFlight.basket.name)
        TitledText(padding = 16.dp, title = "Date", value = confirmedFlight.date.toString())
        TitledText(
            padding = 16.dp, title = "Time Slot", value = confirmedFlight.timeSlot.toString())
        VehicleText(vehicle = confirmedFlight.vehicles, padding = 16.dp)
        if (confirmedFlight.remarks.isEmpty()) {
          EmptyListText("Remarks", 16.dp)
        } else {
          RemarkText(remarks = confirmedFlight.remarks, padding = 16.dp)
        }
        ShowColor("Flight Color", confirmedFlight.color, 16.dp)
        TitledText(
            padding = 16.dp,
            title = "Meetup Time Team",
            value = confirmedFlight.meetupTimeTeam.toString())
        TitledText(
            padding = 16.dp,
            title = "Departure Time Team",
            value = confirmedFlight.departureTimeTeam.toString())
        TitledText(
            padding = 16.dp,
            title = "Meetup Time Passenger",
            value = confirmedFlight.meetupTimePassenger.toString())
        HyperLinkText(
            padding = 16.dp,
            title = "Meetup Location Passenger",
            value = confirmedFlight.meetupLocationPassenger)
      }
}
/**
 * Composable function for displaying text in a list element.
 *
 * @param text The text to be displayed.
 * @param padding The padding to be applied to the text.
 * @param testTag The tag used for testing purposes.
 */
@Composable
fun ListElementText(text: String, padding: Dp, testTag: String) {
  Text(
      text = text,
      modifier = Modifier.fillMaxWidth().padding(horizontal = padding).testTag(testTag),
      color = Color.Black,
      style = MaterialTheme.typography.bodyLarge,
  )
  Spacer(modifier = Modifier.padding(4.dp))
}

@Composable
fun HyperLinkText(title: String, value: String, padding: Dp) {
  val uriHandler = LocalUriHandler.current
  val googleMapsLink = "https://www.google.com/maps/search/?api=1&query=${value.replace(" ", "+")}"
  val string = buildAnnotatedString {
    pushStringAnnotation(tag = "URL", annotation = googleMapsLink)
    withStyle(
        style =
            SpanStyle(
                color = MaterialTheme.colorScheme.primary,
                textDecoration = TextDecoration.Underline)) {
          append(value)
        }
    pop()
  }
  Text(
      modifier = Modifier.fillMaxWidth().padding(horizontal = padding).testTag(title + value),
      text = title,
      style = MaterialTheme.typography.headlineSmall,
      color = Color.Black)
  Spacer(modifier = Modifier.padding(4.dp))

  ClickableText(
      text = string,
      modifier = Modifier.padding(horizontal = padding.plus(4.dp)).testTag(title),
      style = MaterialTheme.typography.bodyLarge,
      onClick = { offset ->
        string.getStringAnnotations(tag = "URL", start = offset, end = offset).firstOrNull()?.let {
            stringAnnotation ->
          uriHandler.openUri(stringAnnotation.item)
        }
      })
  Spacer(modifier = Modifier.padding(12.dp))
}
/**
 * Composable function for displaying team details.
 *
 * @param team The team for which the details are to be displayed.
 * @param padding The padding to be applied to the text elements.
 */
@Composable
fun TeamText(team: Team, padding: Dp) {
  Text(
      text = "Team",
      modifier = Modifier.fillMaxWidth().padding(horizontal = padding),
      style = MaterialTheme.typography.headlineSmall,
  )
  var index = 0
  for (i in team.roles) {
    ListElementText(
        text =
            "${i.roleType.name} : ${i.assignedUser?.firstname ?: "First Name Not Assigned"} ${i.assignedUser?.lastname ?: "Last Name Not Assigned"}",
        padding = padding.plus(4.dp),
        testTag = "Team $index")
    index += 1
  }
  Spacer(modifier = Modifier.padding(12.dp))
}
/**
 * Composable function for displaying vehicle details.
 *
 * @param vehicle The list of vehicles for which details are to be displayed.
 * @param padding The padding to be applied to the text elements.
 */
@Composable
fun VehicleText(vehicle: List<Vehicle>, padding: Dp) {
  Text(
      text = "Vehicle",
      modifier = Modifier.fillMaxWidth().padding(horizontal = padding),
      color = Color.Black,
      style = MaterialTheme.typography.headlineSmall,
  )
  Spacer(modifier = Modifier.padding(4.dp))
  var index = 0
  for (i in vehicle) {
    ListElementText(i.name, padding.plus(4.dp), "Vehicle $index")
    index += 1
  }
  Spacer(modifier = Modifier.padding(12.dp))
}
/**
 * Composable function for displaying remarks.
 *
 * @param remarks The list of remarks to be displayed.
 * @param padding The padding to be applied to the text elements.
 */
@Composable
fun RemarkText(remarks: List<String>, padding: Dp) {
  Text(
      text = "Remarks",
      modifier = Modifier.fillMaxWidth().padding(horizontal = padding),
      color = Color.Black,
      style = MaterialTheme.typography.headlineSmall,
  )
  Spacer(modifier = Modifier.padding(4.dp))
  var index = 0
  for (i in remarks) {
    ListElementText(i, padding.plus(4.dp), "Remark $index")
    index += 1
  }
  Spacer(modifier = Modifier.padding(12.dp))
}
/**
 * Composable function for displaying a message when a list is empty.
 *
 * @param title The title or label indicating the type of empty list.
 * @param padding The padding to be applied to the text elements.
 */
@Composable
fun EmptyListText(title: String, padding: Dp) {
  Text(
      text = title,
      modifier = Modifier.fillMaxWidth().padding(horizontal = padding),
      color = Color.Black,
      style = MaterialTheme.typography.headlineSmall,
  )
  Spacer(modifier = Modifier.padding(4.dp))
  Text(
      text = "No $title to display",
      modifier = Modifier.fillMaxWidth().padding(horizontal = padding.plus(4.dp)).testTag(title),
      style = MaterialTheme.typography.bodyLarge,
  )
  Spacer(modifier = Modifier.padding(12.dp))
}
/**
 * Composable function for displaying a color representation.
 *
 * @param title The title or label indicating the purpose of the color representation.
 * @param flightColor The FlightColor enum representing the color to be displayed.
 * @param padding The padding to be applied to the color representation.
 */
@Composable
fun ShowColor(title: String, flightColor: FlightColor, padding: Dp) {

  Text(
      text = title,
      modifier = Modifier.fillMaxWidth().padding(horizontal = padding),
      color = Color.Black,
      style = MaterialTheme.typography.headlineSmall,
  )
  val color =
      when (flightColor) {
        FlightColor.RED -> Color.Red
        FlightColor.BLUE -> Color.Blue
        FlightColor.YELLOW -> Color.Yellow
        FlightColor.ORANGE -> lightOrange
        FlightColor.PINK -> Pink40
        FlightColor.NO_COLOR -> Color.White
      }
  Spacer(modifier = Modifier.padding(4.dp))
  Box(
      modifier =
          Modifier.fillMaxWidth()
              .height(75.dp)
              .padding(horizontal = padding)
              .padding(padding)
              .testTag(title)
              .background(color),
  )
  Spacer(modifier = Modifier.padding(12.dp))
}
/**
 * Composable function for displaying the bottom section of the confirmed flight details UI.
 *
 * @param okClick Lambda function to handle the click event for confirming the flight details.
 */
@Composable
fun ConfirmedFlightDetailBottom(okClick: () -> Unit) {
  Column(
      modifier = Modifier.padding(16.dp).fillMaxWidth().fillMaxHeight(),
  ) {
    Button(
        onClick = okClick,
        modifier = Modifier.fillMaxWidth().padding(16.dp).testTag("OK Button"),
        colors = ButtonDefaults.buttonColors(containerColor = lightOrange)) {
          Text(text = "OK", color = Color.White, overflow = TextOverflow.Clip)
        }
  }
}
