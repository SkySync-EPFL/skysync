package ch.epfl.skysync.screens.flightDetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ch.epfl.skysync.components.LoadingComponent
import ch.epfl.skysync.models.flight.Flight
import ch.epfl.skysync.models.flight.Team
import ch.epfl.skysync.models.flight.Vehicle
import ch.epfl.skysync.ui.theme.lightOrange

/**
 * FlightDetailUi is a Composable function that displays the UI for flight details. It consists of a
 * header, body, and bottom section with action buttons.
 *
 * @param backClick Callback function invoked when the back button is clicked.
 * @param deleteClick Callback function invoked when the delete button is clicked.
 * @param editClick Callback function invoked when the edit button is clicked.
 * @param confirmClick Callback function invoked when the confirm button is clicked.
 * @param padding PaddingValues to apply to the content.
 * @param flight The flight details to display.
 */
@Composable
fun FlightDetailUi(
    backClick: () -> Unit,
    deleteClick: (flightId: String) -> Unit,
    editClick: (flightId: String) -> Unit,
    confirmClick: (flightId: String) -> Unit,
    padding: PaddingValues,
    flight: Flight?
) {
  Column(
      modifier = Modifier
          .fillMaxSize()
          .background(Color.White),
  ) {
    FlightDetailHead(BackClick = backClick)
    if (flight == null) {
      LoadingComponent(isLoading = true, onRefresh = {}) {

      }
    } else {
      Box(modifier = Modifier
          .fillMaxHeight()
          .padding(padding)) {
        FlightdetailBody(flight, padding)
        FlightDetailBottom(flight.id, deleteClick, editClick, confirmClick)
      }
    }
  }
}

/**
 * FlightDetailHead is a Composable function that displays the header of the flight detail screen.
 *
 * @param BackClick Callback function invoked when the back button is clicked.
 */
@Composable
fun FlightDetailHead(BackClick: () -> Unit) {
  Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
  ) {
    IconButton(onClick = BackClick, modifier = Modifier.fillMaxWidth(0.2f)) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = lightOrange)
        Text(text = "Back", color = lightOrange)
      }
    }
    Column(
        modifier = Modifier.fillMaxWidth(0.75f),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
          Text(
              text = "Flight Detail",
              color = Color.Black,
              fontSize = 30.sp,
          )
        }
  }
}
/**
 * FlightdetailBody is a Composable function that displays the body of the flight detail screen.
 *
 * @param flight The flight details to display.
 * @param padding PaddingValues to apply to the content.
 */
@Composable
fun FlightdetailBody(flight: Flight, padding: PaddingValues) {
  Column(modifier = Modifier
      .fillMaxWidth()
      .fillMaxHeight(0.9f)
      .padding(padding)) {
    Spacer(modifier = Modifier.fillMaxHeight(0.05f))
    Row() {
      Column(
          modifier = Modifier.fillMaxWidth(0.7f),
          verticalArrangement = Arrangement.Center,
          horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = flight.flightType.name,
                color = Color.Black,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold)
            Text(
                text = flight.nPassengers.toString() + " Pax",
                fontSize = 20.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold)
          }
      Column(
          verticalArrangement = Arrangement.Center,
          horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                flight.date.toString(),
                fontSize = 15.sp,
                color = Color.Black,
            )
            Text(
                flight.timeSlot.name,
                color = Color.Black,
                fontSize = 15.sp,
            )
          }
    }
    Row {
      Column(
          modifier = Modifier.fillMaxWidth(0.5f),
          verticalArrangement = Arrangement.Center,
          horizontalAlignment = Alignment.CenterHorizontally) {
            TextBar(textLeft = "Balloon", textRight = flight.balloon?.name ?: "None")
          }
      Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.Center,
          horizontalAlignment = Alignment.CenterHorizontally) {
            TextBar(textLeft = "Basket", textRight = flight.basket?.name ?: "None")
          }
    }
    ScrollableBoxWithButton("Team") { TeamRolesList(team = flight.team) }

    ScrollableBoxWithButton("Vehicles") { VehicleListText(vehicle = flight.vehicles) }
  }
}
/**
 * FlightDetailBottom is a Composable function that displays the bottom section of the flight detail
 * screen.
 *
 * @param DeleteClick Callback function invoked when the delete button is clicked.
 * @param EditClick Callback function invoked when the edit button is clicked.
 * @param ConfirmClick Callback function invoked when the confirm button is clicked.
 * @param padding PaddingValues to apply to the content.
 */
@Composable
fun FlightDetailBottom(
    flightId: String,
    DeleteClick: (flightId: String) -> Unit,
    EditClick: (flightId: String) -> Unit,
    ConfirmClick: (flightId: String) -> Unit,
) {
  Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically) {
          ClickButton(
              text = "Delete",
              onClick = { DeleteClick(flightId) },
              modifier = Modifier.fillMaxWidth(0.3f),
              color = Color.Red)
          ClickButton(
              text = "Edit",
              onClick = { EditClick(flightId) },
              modifier = Modifier.fillMaxWidth(3 / 7f),
              color = Color.Yellow)
          ClickButton(
              text = "Confirm",
              onClick = { ConfirmClick(flightId) },
              modifier = Modifier.fillMaxWidth(0.7f),
              color = Color.Green)
        }
  }
}
/**
 * Composable function to create a custom clickable button.
 *
 * @param text The text to be displayed on the button.
 * @param onClick The lambda function to be executed when the button is clicked.
 * @param modifier The modifier for the button layout.
 * @param color The color for the button background.
 */
@Composable
fun ClickButton(text: String, onClick: () -> Unit, modifier: Modifier, color: Color) {
  Button(
      onClick = onClick,
      modifier = modifier,
      colors = ButtonDefaults.buttonColors(containerColor = color)) {
        Text(text = text, color = Color.Black, overflow = TextOverflow.Clip)
      }
}
/**
 * TextBar is a Composable function that displays a row with two text elements separated by a
 * divider.
 *
 * @param textLeft The text to display on the left side.
 * @param textRight The text to display on the right side.
 */
@Composable
fun TextBar(textLeft: String, textRight: String) {
  Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween) {
        Column(
            modifier = Modifier.fillMaxWidth(0.5f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
              Text(text = textLeft, color = Color.Black, fontSize = 15.sp)
            }
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                  text = textRight,
                  fontSize = 15.sp,
                  color = Color.Black,
                  modifier = Modifier.testTag(textLeft + textRight))
            }
      }
  Spacer(modifier = Modifier.height(8.dp))
  Divider(color = Color.Black, thickness = 1.dp)
  Spacer(modifier = Modifier.height(8.dp))
}
/**
 * TeamRolesList is a Composable function that displays a list of team roles.
 *
 * @param team The team containing the roles to display.
 */
@Composable
fun TeamRolesList(team: Team) {
  if (team.roles.isEmpty()) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
          Text(text = "No team member", color = Color.Black)
        }
  } else {
    LazyColumn(modifier = Modifier
        .testTag("TeamList")
        .fillMaxHeight(0.5f)) {
      itemsIndexed(team.roles) { index, role ->
        val firstname = role.assignedUser?.firstname ?: ""
        val lastname = role.assignedUser?.lastname ?: ""
        val name = "$firstname $lastname"
        TextBar(textLeft = "Member $index: ${role.roleType.name}", textRight = name)
      }
    }
  }
}
/**
 * VehicleListText is a Composable function that displays a list of vehicles.
 *
 * @param vehicle The list of vehicles to display.
 */
@Composable
fun VehicleListText(vehicle: List<Vehicle>) {
  if (vehicle.indices.isEmpty()) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {
          Text(
              text = "No vehicle",
              color = Color.Black,
          )
        }
  } else {
    LazyColumn(modifier = Modifier.testTag("VehicleList")) {
      itemsIndexed(vehicle) { index, car ->
        TextBar(textLeft = "Vehicle $index", textRight = car.name)
      }
    }
  }
}
/**
 * ScrollableBoxWithButton is a Composable function that displays a button that, when clicked,
 * toggles the visibility of a content area.
 *
 * @param name The text to display on the button.
 * @param content The content to be displayed when the button is clicked.
 */
@Composable
fun ScrollableBoxWithButton(name: String, content: @Composable () -> Unit) {
  var expanded by remember { mutableStateOf(false) }

  Column {
    Button(
        onClick = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)) {
          Text(text = name, color = Color.White)
        }

    if (expanded) {
      content()
    }
  }
}
