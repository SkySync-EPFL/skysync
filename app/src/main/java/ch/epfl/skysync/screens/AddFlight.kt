package ch.epfl.skysync.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import ch.epfl.skysync.models.calendar.TimeSlot
import ch.epfl.skysync.models.flight.Balloon
import ch.epfl.skysync.models.flight.Basket
import ch.epfl.skysync.models.flight.FlightType
import ch.epfl.skysync.models.flight.Vehicle
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFlightScreen(navController: NavHostController) {
  Scaffold(
      modifier = Modifier.fillMaxSize(),
      topBar = {
        TopAppBar(
            title = { Text("Add Flight") },
            navigationIcon = {
              IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
              }
            })
      }) { padding ->
        Column(
            modifier = Modifier.padding(padding), verticalArrangement = Arrangement.SpaceBetween) {
              val defaultPadding = 16.dp

              var nbPassenger by remember { mutableStateOf("") }
              OutlinedTextField(
                  value = nbPassenger,
                  onValueChange = { value -> nbPassenger = value.filter { it.isDigit() } },
                  placeholder = { Text("Number of passengers") },
                  singleLine = true,
                  keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                  modifier = Modifier.fillMaxWidth().padding(defaultPadding))

              var openDatePicker by remember { mutableStateOf(false) }
              var date by remember { mutableStateOf(LocalDate.now()) }
              if (openDatePicker) {
                val datePickerState =
                    rememberDatePickerState(
                        initialSelectedDateMillis = java.util.Calendar.getInstance().timeInMillis)

                DatePickerDialog(
                    onDismissRequest = {},
                    confirmButton = {
                      TextButton(
                          onClick = {
                            openDatePicker = false
                            java.util.Calendar.getInstance().apply {
                              timeInMillis = datePickerState.selectedDateMillis!!
                              date =
                                  Instant.ofEpochMilli(timeInMillis)
                                      .atZone(ZoneId.of("GMT"))
                                      .toLocalDate()
                            }
                          }) {
                            Text("OK")
                          }
                    },
                    dismissButton = {
                      TextButton(onClick = { openDatePicker = false }) { Text("Cancel") }
                    }) {
                      DatePicker(state = datePickerState)
                    }
              }

              OutlinedTextField(
                  modifier =
                      Modifier.fillMaxWidth().padding(defaultPadding).clickable {
                        openDatePicker = true
                      },
                  enabled = false,
                  value =
                      String.format("%02d/%02d/%04d", date.dayOfMonth, date.monthValue, date.year),
                  onValueChange = {})

              var flightType: FlightType? by remember { mutableStateOf(null) }
              var expandedFlightTypeMenu by remember { mutableStateOf(false) }
              ExposedDropdownMenuBox(
                  modifier =
                      Modifier.fillMaxWidth().padding(defaultPadding).clickable {
                        expandedFlightTypeMenu = true
                      },
                  expanded = expandedFlightTypeMenu,
                  onExpandedChange = { expandedFlightTypeMenu = !expandedFlightTypeMenu }) {
                    OutlinedTextField(
                        value = flightType?.name ?: "Flight Type",
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        readOnly = true,
                        onValueChange = {},
                        trailingIcon = {
                          ExposedDropdownMenuDefaults.TrailingIcon(
                              expanded = expandedFlightTypeMenu)
                        })

                    DropdownMenu(
                        expanded = expandedFlightTypeMenu,
                        onDismissRequest = { expandedFlightTypeMenu = false }) {
                          FlightType.all_flights.forEach { item ->
                            DropdownMenuItem(
                                modifier = Modifier.fillMaxWidth().padding(defaultPadding),
                                onClick = {
                                  flightType = item
                                  expandedFlightTypeMenu = false
                                },
                                text = { Text(item.name) })
                          }
                        }
                  }

              var vehicles by remember { mutableStateOf(emptyList<Vehicle>()) }
              var expandedVehicleMenu by remember { mutableStateOf(false) }
              var vehicleQuery by remember { mutableStateOf("") }
              var vehicle: Vehicle? by remember { mutableStateOf(null) }
              val allVehicles = listOf<Vehicle>(Vehicle("Car"), Vehicle("Bus"), Vehicle("Bike"))
              val focusRequester = remember { FocusRequester() }
              Box(Modifier.fillMaxWidth()) {
                Column {
                  OutlinedTextField(
                      value = vehicleQuery,
                      onValueChange = {
                        vehicleQuery = it
                        vehicle = null
                      },
                      placeholder = { Text("Vehicles") },
                      modifier =
                          Modifier.padding(horizontal = defaultPadding)
                              .fillMaxWidth()
                              .focusRequester(focusRequester)
                              .onFocusChanged { expandedVehicleMenu = it.isFocused },
                      singleLine = true,
                      keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                      keyboardActions = KeyboardActions(onDone = { expandedVehicleMenu = false }))
                  if (expandedVehicleMenu) {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)) {
                          items(allVehicles) { item ->
                            if (item.name.contains(vehicleQuery, ignoreCase = true)) {
                              ListItem(
                                  modifier =
                                      Modifier.clickable {
                                        vehicle = item
                                        vehicleQuery = item.name
                                        expandedVehicleMenu = false
                                        focusRequester.freeFocus()
                                      },
                                  colors = ListItemDefaults.colors(Color.Transparent),
                                  headlineContent = { Text(text = item.name) })
                            }
                          }
                        }
                  }
                }
              }
              var timeSlot: TimeSlot by remember { mutableStateOf(TimeSlot.AM) }
              var expandedTimeSlot by remember { mutableStateOf(false) }
              Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    placeholder = { Text("Time Slot") },
                    modifier =
                        Modifier.fillMaxWidth().padding(defaultPadding).clickable {
                          expandedTimeSlot = true
                        },
                    enabled = false,
                    value = timeSlot.toString(),
                    onValueChange = {})
                DropdownMenu(
                    expanded = expandedTimeSlot, onDismissRequest = { expandedTimeSlot = false }) {
                      TimeSlot.entries.forEach { item ->
                        DropdownMenuItem(
                            modifier = Modifier.fillMaxWidth().padding(defaultPadding),
                            onClick = {
                              timeSlot = item
                              expandedTimeSlot = false
                            },
                            text = { Text(item.name) })
                      }
                    }
              }

              var balloon: Balloon? by remember { mutableStateOf(null) }
              var basket: Basket? by remember { mutableStateOf(null) }
            }
      }
}

@Composable
@Preview
fun AddFlightScreenPreview() {
  val navController = rememberNavController()
  AddFlightScreen(navController = navController)
}
