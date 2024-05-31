package ch.epfl.skysync.screens.admin

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ch.epfl.skysync.components.CustomTopAppBar
import ch.epfl.skysync.components.forms.TitledDropDownMenu
import ch.epfl.skysync.components.forms.TitledInputTextField
import ch.epfl.skysync.database.UserRole
import ch.epfl.skysync.models.flight.BalloonQualification
import ch.epfl.skysync.models.user.TempUser
import ch.epfl.skysync.ui.theme.lightOrange
import ch.epfl.skysync.util.hasError
import ch.epfl.skysync.util.inputNonNullValidation
import ch.epfl.skysync.util.textInputValidation
import ch.epfl.skysync.util.validateEmail
import ch.epfl.skysync.viewmodel.UserManagementViewModel

/** screen to add a user */
@Composable
fun AddUserScreen(navController: NavController, viewModel: UserManagementViewModel) {
  val title = "Add User"
  Scaffold(
      modifier = Modifier.fillMaxSize(),
      topBar = { CustomTopAppBar(navController = navController, title = title) }) { padding ->
        val defaultPadding = 16.dp

        var roleValue: UserRole? by remember { mutableStateOf(null) }
        var roleError by remember { mutableStateOf(false) }

        var firstNameValue by remember { mutableStateOf("") }
        var firstNameError by remember { mutableStateOf(false) }

        var lastNameValue by remember { mutableStateOf("") }
        var lastNameError by remember { mutableStateOf(false) }

        var emailValue by remember { mutableStateOf("") }
        var emailError by remember { mutableStateOf(false) }

        var balloonQualificationValue: BalloonQualification? by remember { mutableStateOf(null) }
        var balloonQualificationError by remember { mutableStateOf(false) }

        var isError by remember { mutableStateOf(false) }

        Column(Modifier.fillMaxSize().padding(padding)) {
          LazyColumn(modifier = Modifier.fillMaxSize().weight(1f).testTag("$title Lazy Column")) {
            item {
              TitledDropDownMenu(
                  defaultPadding = defaultPadding,
                  title = "Role",
                  value = roleValue,
                  onclickMenu = { roleValue = it },
                  items = UserRole.entries,
                  showString = { it?.name?.lowercase() ?: "" },
                  isError = roleError,
                  messageError = if (roleError) "Select a role" else "")
            }
            item {
              TitledInputTextField(
                  padding = defaultPadding,
                  title = "First Name",
                  value = firstNameValue,
                  onValueChange = { firstNameValue = it },
                  isError = firstNameError,
                  messageError = if (firstNameError) "Enter a first name" else "")
            }
            item {
              TitledInputTextField(
                  padding = defaultPadding,
                  title = "Last Name",
                  value = lastNameValue,
                  onValueChange = { lastNameValue = it },
                  isError = lastNameError,
                  messageError = if (lastNameError) "Enter a last name" else "")
            }
            item {
              TitledInputTextField(
                  padding = defaultPadding,
                  title = "E-mail",
                  value = emailValue,
                  onValueChange = { emailValue = it },
                  isError = emailError,
                  messageError = if (emailError) "Invalid email" else "",
                  keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email))
            }
            if (roleValue === UserRole.PILOT) {
              item {
                TitledDropDownMenu(
                    defaultPadding = defaultPadding,
                    title = "Balloon Qualification",
                    value = balloonQualificationValue,
                    onclickMenu = { balloonQualificationValue = it },
                    items = BalloonQualification.entries,
                    isError = balloonQualificationError,
                    messageError = if (balloonQualificationError) "Select a balloon type" else "",
                    showString = { it?.name?.lowercase() ?: "Choose a balloon qualification" })
              }
            }
          }
          Button(
              onClick = {
                emailError = !validateEmail(emailValue)
                firstNameError = textInputValidation(firstNameValue)
                lastNameError = textInputValidation(lastNameValue)
                roleError = !inputNonNullValidation(roleValue)
                balloonQualificationError =
                    if (roleValue === UserRole.PILOT)
                        !inputNonNullValidation(balloonQualificationValue)
                    else false
                isError =
                    hasError(
                        roleError,
                        firstNameError,
                        lastNameError,
                        emailError,
                        balloonQualificationError)
                if (!isError) {
                  val tmpUser =
                      TempUser(
                          email = emailValue,
                          firstname = firstNameValue,
                          lastname = lastNameValue,
                          userRole = roleValue!!,
                          balloonQualification = balloonQualificationValue)

                  viewModel.createUser(tmpUser)
                  navController.popBackStack()
                }
              },
              modifier = Modifier.fillMaxWidth().padding(defaultPadding).testTag("$title Button"),
              colors = ButtonDefaults.buttonColors(containerColor = lightOrange)) {
                Text(title)
              }
        }
      }
}
