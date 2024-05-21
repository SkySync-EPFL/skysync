package ch.epfl.skysync.screens.admin

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import ch.epfl.skysync.models.flight.RoleType
import ch.epfl.skysync.models.user.User
import ch.epfl.skysync.navigation.AdminBottomBar
import ch.epfl.skysync.navigation.Route
import ch.epfl.skysync.ui.theme.lightGray
import ch.epfl.skysync.ui.theme.lightOrange
import ch.epfl.skysync.ui.theme.lightTurquoise
import ch.epfl.skysync.viewmodel.UserManagementViewModel

// Composable function to display a card for a User object.
@Composable
fun UserCard(user: User, onUserClick: (String) -> Unit) {
  Card(
      modifier =
      Modifier
          .fillMaxWidth()
          .padding(vertical = 1.dp)
          .clickable { onUserClick(user.id) }
          .testTag("userCard"),
      elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
  ) {
    Surface(
        modifier =
        Modifier
            .fillMaxWidth()
            .border(
                border = BorderStroke(1.dp, Color.Black),
                shape =
                RoundedCornerShape(
                    topStart = 12.dp,
                    topEnd = 12.dp,
                    bottomEnd = 12.dp,
                    bottomStart = 12.dp
                )
            ),
        color = lightGray) {
          Row(
              modifier = Modifier
                  .fillMaxWidth()
                  .padding(16.dp),
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
fun TopBarTitle(paddingValues: PaddingValues) {
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)){
        Text(
            text = "Users",
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            modifier =
            Modifier
                .background(
                    color = lightOrange,
                    shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                )
                .fillMaxWidth()
                .padding(
                    top = paddingValues.calculateTopPadding() + 16.dp,
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 16.dp
                ),
            color = Color.White,
            textAlign = TextAlign.Center
        )
    }
}

// Composable function for the search bar.
@Composable
fun SearchBar(query: String, onQueryChanged: (String) -> Unit) {
  OutlinedTextField(
      value = query,
      onValueChange = onQueryChanged,
      modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 8.dp),
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
fun RoleFilter(onRoleSelected: (RoleType?) -> Unit, roles: List<RoleType>, count: Int) {
  var expanded by remember { mutableStateOf(false) } // State to manage dropdown expansion.
  var displayText by remember { mutableStateOf("Filter by role") }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Button(
                onClick = { expanded = true },
                colors =
                ButtonDefaults.buttonColors(
                    containerColor = lightOrange,
                    contentColor = Color.Black
                )
            ) {
                Text(text = displayText)
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Expand or collapse menu"
                )
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
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(color = lightTurquoise, shape = CircleShape)
                .padding(8.dp)) {
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.titleLarge,
            )
        }
    }
}

// Main screen composable integrating all components. List of users to later be replaced with
// UserViewModel
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun UserManagementScreen(
    navController: NavHostController,
    userManagementViewModel: UserManagementViewModel
) {
  var searchQuery by remember { mutableStateOf("") }
  var selectedRole by remember { mutableStateOf<RoleType?>(null) }
  val roles = RoleType.entries
  val users = userManagementViewModel.allUsers.collectAsStateWithLifecycle()
  // Filter users based on search query and selected role.
  val filteredUsers =
      users.value.filter {
        (searchQuery.isEmpty() ||
            "${it.firstname} ${it.lastname}".contains(searchQuery, ignoreCase = true)) &&
            (selectedRole == null || it.roleTypes.contains(selectedRole))
      }

  Scaffold(
      modifier = Modifier.fillMaxSize(),
      bottomBar = { AdminBottomBar(navController) },
      floatingActionButton = {
        Surface(
            modifier = Modifier.size(56.dp),
            shape = CircleShape,
            color = Color.Transparent,
            border = BorderStroke(2.dp, Color.Black)) {
              FloatingActionButton(
                  modifier = Modifier.fillMaxSize(1f),
                  onClick = { navController.navigate(Route.ADD_USER) },
                  containerColor = Color.White) {
                    Icon(imageVector = Icons.Filled.Add, contentDescription = "Add User")
                  }
            }
      },
      floatingActionButtonPosition = FabPosition.Center) { padding ->
      Column(modifier = Modifier.fillMaxSize()) {
          TopBarTitle(padding)
          SearchBar(query = searchQuery, onQueryChanged = { searchQuery = it })
          RoleFilter(
              onRoleSelected = { selectedRole = it },
              roles = roles,
              count = filteredUsers.size
          )
          Box(
              modifier = Modifier
                  .fillMaxSize()
                  .padding(padding)
          ) {
              if (filteredUsers.isEmpty()) {
                  // Display a message when no users are found
                  Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                      Text("No such user exists", style = MaterialTheme.typography.titleLarge)
                  }
              } else {
                  LazyColumn(
                      modifier =
                      Modifier
                          .align(Alignment.TopCenter)
                          .padding(horizontal = 16.dp)
                          .testTag("UserManagementLazyColumn")
                  ) {
                      items(filteredUsers) { user ->
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
