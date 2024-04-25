package ch.epfl.skysync.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ch.epfl.skysync.ui.theme.lightGray
import ch.epfl.skysync.ui.theme.lightOrange

data class GroupDetail(
    val groupName: String,
    val groupImage: ImageVector?,
    val lastMessage: String,
    val lastMessageTime: String
)
/**
 * Composable function to display a group chat UI.
 *
 * @param groupList A list of quadruples representing group data containing group name, image, last
 *   message, and last message time.
 * @param onClick Callback triggered when a group is clicked.
 * @param paddingValues Padding values for the column.
 */
@Composable
fun GroupChat(
    groupList: List<GroupDetail>,
    onClick: (String) -> Unit,
    paddingValues: PaddingValues
) {
  var searchQuery by remember { mutableStateOf("") }
  Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
    GroupChatTopBar()
    Spacer(modifier = Modifier.fillMaxHeight(0.02f))

    OutlinedTextField(
        value = searchQuery,
        onValueChange = { searchQuery = it },
        label = { Text("Search") },
        colors =
            OutlinedTextFieldDefaults.colors(
                focusedBorderColor = lightOrange, focusedLabelColor = lightOrange),
        modifier = Modifier.fillMaxWidth().testTag("Search"),
        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done))
    val filteredGroups = groupList.filter { it.groupName.contains(searchQuery, ignoreCase = true) }
    Spacer(modifier = Modifier.fillMaxHeight(0.05f))
    GroupChatBody(groupList = filteredGroups, onClick = onClick)
  }
}
/** Composable function to display the top bar of the group chat UI. */
@Composable
fun GroupChatTopBar() {
  Column {
    Box(
        modifier = Modifier.fillMaxWidth().fillMaxHeight(0.05f),
        contentAlignment = Alignment.Center,
    ) {
      Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.Center) {
            Text(
                text = "Messages",
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
            )
          }
    }
  }
}
/**
 * Composable function to display a group card in the group chat UI.
 *
 * @param group The name of the group.
 * @param onClick Callback triggered when the group card is clicked.
 * @param groupImage The image associated with the group. Can be null if no image is available.
 * @param lastMsg The last message in the group.
 * @param lastMsgTime The time of the last message.
 * @param testTag A tag used for testing purposes.
 */
@Composable
fun GroupCard(
    group: String,
    onClick: () -> Unit,
    groupImage: ImageVector?,
    lastMsg: String,
    lastMsgTime: String,
    testTag: String
) {

  Card(
      modifier = Modifier.clickable(onClick = onClick).fillMaxWidth().testTag(testTag),
      shape = RectangleShape,
      colors =
          CardDefaults.cardColors(
              containerColor = lightGray,
          )) {
        Row {
          if (groupImage != null) {
            Box(modifier = Modifier.fillMaxWidth(0.125f).size(50.dp)) {
              Image(imageVector = groupImage, contentDescription = "Group Image")
            }
          } else {
            Box(
                modifier =
                    Modifier.fillMaxWidth(0.125f)
                        .size(50.dp)
                        .background(color = Color.LightGray)) {}
          }
          Spacer(modifier = Modifier.size(10.dp))
          Column(
              modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.SpaceBetween) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween) {
                      Text(
                          text = group,
                          fontSize = 16.sp,
                          fontWeight = FontWeight.Bold,
                          color = Color.Black)
                      Text(
                          text = lastMsgTime,
                          color = Color.Gray,
                          style = MaterialTheme.typography.bodyMedium,
                      )
                    }
                Text(
                    text = lastMsg,
                    color = Color.Black,
                    style = MaterialTheme.typography.bodyMedium)
              }
        }
      }
}
/**
 * Composable function to display the body of the group chat UI.
 *
 * @param Groups List of quadruples representing group data containing group name, image, last
 *   message, and last message time.
 * @param onClick Callback triggered when a group card is clicked.
 */
@Composable
fun GroupChatBody(groupList: List<GroupDetail>, onClick: (String) -> Unit) {
  LazyColumn(modifier = Modifier.testTag("GroupChatBody")) {
    items(groupList.size) { index ->
      GroupCard(
          group = groupList[index].groupName,
          onClick = { onClick(groupList[index].groupName) },
          groupImage = groupList[index].groupImage,
          lastMsg = groupList[index].lastMessage,
          lastMsgTime = groupList[index].lastMessageTime,
          testTag = "GroupCard$index")
      Spacer(modifier = Modifier.size(1.dp))
    }
  }
}

// @Composable
// @Preview
// fun GroupChatPreview() {
//    val image: ImageVector? = null
//    val groups = listOf(Quadruple("Group 1", image, "Last message", "Last message time"),
//        Quadruple("Group 2",image, "Last message", "Last message time"),
//        Quadruple("Group 3",image,"Last message", "Last message time")
//    )
//    GroupChat(GroupsImageLastmsgLastmsgtime = groups, onClick = {}, paddingValues =
// PaddingValues(0.dp))
// }
