package ch.epfl.skysync.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import ch.epfl.skysync.models.calendar.AvailabilityCalendar
import ch.epfl.skysync.models.calendar.FlightGroupCalendar
import ch.epfl.skysync.models.flight.RoleType
import ch.epfl.skysync.models.user.User
import ch.epfl.skysync.navigation.BottomBar
import ch.epfl.skysync.ui.theme.lightGray
import ch.epfl.skysync.ui.theme.lightOrange

// Mock data for the list of users
private val mockUsers =
    listOf(
        object : User {
          override val id = "1"
          override val firstname = "Jean"
          override val lastname = "Michel"
          override val availabilities: AvailabilityCalendar = AvailabilityCalendar()
          override val assignedFlights: FlightGroupCalendar = FlightGroupCalendar()
          override val roleTypes = setOf(RoleType.PILOT)

          override fun addRoleType(roleType: RoleType) = this
        },
        object : User {
          override val id = "2"
          override val firstname = "Jean"
          override val lastname = "Kevin"
          override val availabilities: AvailabilityCalendar = AvailabilityCalendar()
          override val assignedFlights: FlightGroupCalendar = FlightGroupCalendar()
          override val roleTypes = setOf(RoleType.CREW)

          override fun addRoleType(roleType: RoleType) = this
        },
        object : User {
          override val id = "3"
          override val firstname = "Jean"
          override val lastname = "Edouard"
          override val availabilities: AvailabilityCalendar = AvailabilityCalendar()
          override val assignedFlights: FlightGroupCalendar = FlightGroupCalendar()
          override val roleTypes = setOf(RoleType.MAITRE_FONDUE)

          override fun addRoleType(roleType: RoleType) = this
        },
        object : User {
          override val id = "4"
          override val firstname = "Jean"
          override val lastname = "Carre"
          override val availabilities: AvailabilityCalendar = AvailabilityCalendar()
          override val assignedFlights: FlightGroupCalendar = FlightGroupCalendar()
          override val roleTypes = setOf(RoleType.SERVICE_ON_BOARD)

          override fun addRoleType(roleType: RoleType) = this
        },
        // ... add more mock users
    )

// Composable function to display a card for a User object.
@Composable
fun UserCard(user: User, onUserClick: (String) -> Unit) {
  Card(
      modifier =
          Modifier.fillMaxWidth()
              .padding(vertical = 8.dp)
              .clickable { onUserClick(user.id) }
              .testTag("userCard"),
      elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
  ) {
    Surface(modifier = Modifier.fillMaxWidth(), color = lightGray) {
      Row(
          modifier = Modifier.fillMaxWidth().padding(16.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                "${user.firstname} ${user.lastname}",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyLarge)
            Text(user.roleTypes.joinToString { it.name })
          }
    }
  }
}

// Composable function to display the top bar title with user count.
@Composable
fun TopBarTitle(userCount: Int) {
  Row(verticalAlignment = Alignment.CenterVertically) {
    Text(
        text = "Users",
        style = MaterialTheme.typography.headlineLarge,
        modifier = Modifier.padding(end = 8.dp))
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.background(color = lightOrange, shape = CircleShape).padding(8.dp)) {
          Text(
              text = userCount.toString(),
              style = MaterialTheme.typography.titleLarge,
          )
        }
  }
}

// Composable function for the search bar.
@Composable
fun SearchBar(query: String, onQueryChanged: (String) -> Unit) {
  OutlinedTextField(
      value = query,
      onValueChange = onQueryChanged,
      modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
      placeholder = { Text("Search users") },
      singleLine = true,
      trailingIcon = {
        if (query.isNotEmpty()) {
          IconButton(onClick = { onQueryChanged("") }) {
            Icon(imageVector = Icons.Filled.Clear, contentDescription = "Clear search query")
          }
        }
      })
}

// Composable function to filter users by role.
@Composable
fun RoleFilter(onRoleSelected: (RoleType?) -> Unit, roles: List<RoleType>) {
  var expanded by remember { mutableStateOf(false) } // State to manage dropdown expansion.
  var displayText by remember { mutableStateOf("Filter by role") }

  Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
    Button(
        onClick = { expanded = true },
        colors =
            ButtonDefaults.buttonColors(containerColor = lightOrange, contentColor = Color.Black)) {
          Text(text = displayText)
          Icon(
              imageVector = Icons.Default.ArrowDropDown,
              contentDescription = "Expand or collapse menu")
        }

    // Dropdown menu for selecting roles.
    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
      DropdownMenuItem(
          onClick = {
            displayText = "Filter by role"
            onRoleSelected(null)
            expanded = false
          },
          text = { Text("All Roles") })
      roles.forEach { role ->
        DropdownMenuItem(
            onClick = {
              displayText = role.name
              onRoleSelected(role)
              expanded = false
            },
            text = { Text(role.name) })
      }
    }
  }
}

// Main screen composable integrating all components.
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun UserManagementScreen(navController: NavHostController) {
  var searchQuery by remember { mutableStateOf("") }
  var selectedRole by remember { mutableStateOf<RoleType?>(null) }
  val roles = RoleType.entries

  // Filter users based on search query and selected role.
  val filteredUsers =
      mockUsers.filter {
        (searchQuery.isEmpty() ||
            "${it.firstname} ${it.lastname}".contains(searchQuery, ignoreCase = true)) &&
            (selectedRole == null || it.roleTypes.contains(selectedRole))
      }

  Scaffold(
      modifier = Modifier.fillMaxSize(),
      topBar = {
        Column {
          TopAppBar(
              title = { TopBarTitle(userCount = filteredUsers.size) },
          )
          SearchBar(query = searchQuery, onQueryChanged = { searchQuery = it })
          RoleFilter(
              onRoleSelected = { selectedRole = it },
              roles = roles,
          )
        }
      },
      bottomBar = { BottomBar(navController) },
      floatingActionButton = {
        FloatingActionButton(
            onClick = { navController.navigate("addUser") }, containerColor = lightOrange) {
              Icon(imageVector = Icons.Filled.Add, contentDescription = "Add User")
            }
      },
      floatingActionButtonPosition = FabPosition.Center) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
          if (filteredUsers.isEmpty()) {
            // Display a message when no users are found
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
              Text("No such user exists", style = MaterialTheme.typography.titleLarge)
            }
          } else {
            LazyColumn(modifier = Modifier.align(Alignment.TopCenter).padding(horizontal = 16.dp)) {
              items(filteredUsers) { user ->
                UserCard(user) { Log.d("UserManagementScreen", "Navigating to UserDetail with id") }
              }
            }
          }
        }
      }
}

// Preview of UserManagementScreen
@Preview(showBackground = true)
@Composable
fun UserManagementScreenPreview() {
  val navController = rememberNavController()
  UserManagementScreen(navController = navController)
}
