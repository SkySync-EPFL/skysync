package ch.epfl.skysync.screens.admin

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import ch.epfl.skysync.components.ConnectivityStatus
import ch.epfl.skysync.components.LoadingComponent
import ch.epfl.skysync.components.TopBanner
import ch.epfl.skysync.models.flight.RoleType
import ch.epfl.skysync.models.user.User
import ch.epfl.skysync.navigation.AdminBottomBar
import ch.epfl.skysync.navigation.Route
import ch.epfl.skysync.ui.theme.lightGray
import ch.epfl.skysync.ui.theme.lightOrange
import ch.epfl.skysync.ui.theme.lightTurquoise
import ch.epfl.skysync.viewmodel.UserManagementViewModel

/**
 * Composable function that displays a user card.
 *
 * @param user The user
 * @param onUserClick The action to perform when the user is clicked
 */
@Composable
fun UserCard(user: User, onUserClick: (String) -> Unit) {
  Card(
      modifier =
          Modifier.fillMaxWidth()
              .padding(vertical = 1.dp)
              .clickable { onUserClick(user.id) }
              .border(
                  border = BorderStroke(1.dp, Color.Black),
                  shape =
                      RoundedCornerShape(
                          topStart = 12.dp, topEnd = 12.dp, bottomEnd = 12.dp, bottomStart = 12.dp))
              .testTag("userCard"),
      colors = CardDefaults.cardColors(lightGray),
      elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
  ) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween) {
          Text(
              user.name(), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
          Text(user.displayRoleName())
        }
  }
}

/**
 * Composable function that displays a search bar.
 *
 * @param query The search query
 * @param onQueryChanged The action to perform when the query is changed
 */
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

/**
 * Displays the filter along with the count of the results
 *
 * @param onRoleSelected The action to perform when a role is selected
 * @param roles The list of roles
 * @param count The number of users
 */
@Composable
fun RoleFilterAndCount(onRoleSelected: (RoleType?) -> Unit, roles: List<RoleType>, count: Int) {
  Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
  ) {
    RoleFilter(onRoleSelected, roles)
    CountDisplay(count)
  }
}
/**
 * Composable function that displays a role filter.
 *
 * @param onRoleSelected The action to perform when a role is selected
 * @param roles The list of roles
 * @param count The number of users
 */
@Composable
fun RoleFilter(onRoleSelected: (RoleType?) -> Unit, roles: List<RoleType>) {
  var expanded by remember { mutableStateOf(false) } // State to manage dropdown expansion.
  var displayText by remember { mutableStateOf("Filter by role") }
  Column(modifier = Modifier.padding(16.dp)) {
    Button(
        modifier = Modifier.testTag("UserManagementRoleFilterButton"),
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
            modifier = Modifier.testTag("RoleTag${role.description}"),
            onClick = {
              displayText = role.description
              onRoleSelected(role)
              expanded = false
            },
            text = { Text(role.description) })
      }
    }
  }
}

/** displays a given count in a circle */
@Composable
fun CountDisplay(count: Int) {
  Text(
      text = String.format("results: %2d", count),
      style = MaterialTheme.typography.titleLarge,
      textAlign = TextAlign.End,
      modifier = Modifier.background(color = lightTurquoise, shape = CircleShape).padding(8.dp))
}

/**
 * Composable function that displays the user management screen.
 *
 * @param navController The navigation controller
 * @param userManagementViewModel The view model
 * @param connectivityStatus The connectivity status
 */
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun UserManagementScreen(
    navController: NavHostController,
    userManagementViewModel: UserManagementViewModel,
    connectivityStatus: ConnectivityStatus
) {
  var searchQuery by remember { mutableStateOf("") }
  var selectedRole by remember { mutableStateOf<RoleType?>(null) }
  val roles = RoleType.entries
  val filteredUsers = userManagementViewModel.filteredUsers.collectAsStateWithLifecycle()
  val defaultPadding = 16.dp
  // Filter users based on search query and selected role.

  Scaffold(
      modifier = Modifier.fillMaxSize(),
      bottomBar = { AdminBottomBar(navController) },
      floatingActionButton = {
        if (connectivityStatus.isOnline()) {
          FloatingActionButton(
              containerColor = lightOrange,
              onClick = { navController.navigate(Route.ADD_USER) },
              elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 1.dp),
          ) {
            Icon(imageVector = Icons.Filled.Add, contentDescription = "Add User")
          }
        }
      },
  ) { padding ->
    Column(modifier = Modifier.padding(defaultPadding)) {
      TopBanner("Users", lightOrange, padding)
      SearchBar(
          query = searchQuery,
          onQueryChanged = {
            searchQuery = it
            userManagementViewModel.filterByQueryAndRole(searchQuery, selectedRole)
          })
      RoleFilterAndCount(
          onRoleSelected = {
            selectedRole = it
            userManagementViewModel.filterByQueryAndRole(searchQuery, selectedRole)
          },
          roles = roles,
          count = filteredUsers.value.size)

      if (filteredUsers.value.isEmpty() && searchQuery.isEmpty() && selectedRole == null) {
        LoadingComponent(isLoading = true) {}
      } else if (filteredUsers.value.isEmpty()) {
        // Display a message when no users are found
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
          Text("No such user exists", style = MaterialTheme.typography.titleLarge)
        }
      } else {
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
          LazyColumn(
              modifier =
                  Modifier.padding(horizontal = 16.dp)
                      .testTag("UserManagementLazyColumn")
                      .fillMaxSize()) {
                items(filteredUsers.value) { user ->
                  UserCard(user) {
                    navController.navigate(Route.ADMIN_USER_DETAILS + "/${user.id}")
                  }
                  Spacer(modifier = Modifier.height(8.dp))
                }
              }
        }
      }
    }
  }
}
