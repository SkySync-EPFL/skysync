package ch.epfl.skysync.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ch.epfl.skysync.models.message.GroupDetails
import ch.epfl.skysync.models.message.MessageDateFormatter
import ch.epfl.skysync.ui.theme.Purple40
import ch.epfl.skysync.ui.theme.lightGray
import ch.epfl.skysync.ui.theme.lightOrange

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
    groupList: List<GroupDetails>,
    onClick: (GroupDetails) -> Unit,
    onDelete: (GroupDetails) -> Unit,
    paddingValues: PaddingValues,
    isAdmin: Boolean
) {
  var searchQuery by remember { mutableStateOf("") }
  var activeGroupId by remember { mutableStateOf<String?>(null) }
  Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp)) {
    GroupChatTopBar(isAdmin = isAdmin, paddingValues = paddingValues)
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
    val filteredGroups = groupList.filter { it.name.contains(searchQuery, ignoreCase = true) }
    Spacer(modifier = Modifier.fillMaxHeight(0.05f))
    if (filteredGroups.isEmpty() && searchQuery.isEmpty()) {
      LoadingComponent(isLoading = true, onRefresh = { /*TODO*/}) {}
    } else {
      GroupChatBody(
          groupList = filteredGroups,
          onClick = onClick,
          onDelete = onDelete,
          activeGroupId = activeGroupId,
          onSetActiveGroupId = { activeGroupId = it },
          isAdmin)
    }
  }
}
/** Composable function to display the top bar of the group chat UI. */
@Composable
fun GroupChatTopBar(isAdmin: Boolean, paddingValues: PaddingValues) {
  val color = if (isAdmin) lightOrange else Purple40
  Text(
      text = "Messages",
      style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
      modifier =
          Modifier.background(
                  color = color, shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
              .fillMaxWidth()
              .padding(
                  top = paddingValues.calculateTopPadding() + 16.dp,
                  start = 16.dp,
                  end = 16.dp,
                  bottom = 16.dp),
      color = Color.White,
      textAlign = TextAlign.Center)
}

/**
 * Composable function to display a group card in the group chat UI.
 *
 * @param groupDetails The details of the group
 * @param onClick Callback triggered when the group card is clicked.
 * @param testTag A tag used for testing purposes.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GroupCard(
    groupDetails: GroupDetails,
    onClick: (GroupDetails) -> Unit,
    onDelete: (GroupDetails) -> Unit,
    testTag: String,
    isActive: Boolean,
    onActivate: () -> Unit,
    isAdmin: Boolean
) {
  val time = groupDetails.lastMessage?.let { MessageDateFormatter.format(it.date) } ?: ""
  Card(
      modifier =
          Modifier.combinedClickable(
                  onClick = { onClick(groupDetails) }, onLongClick = { onActivate() })
              .fillMaxWidth()
              .padding(vertical = 4.dp)
              .testTag("GroupCard$testTag"),
      shape = RoundedCornerShape(8.dp),
      colors = CardDefaults.cardColors(containerColor = lightGray)) {
        Row(modifier = Modifier.padding(8.dp)) {
          Box(
              modifier =
                  Modifier.size(40.dp).clip(CircleShape).background(groupDetails.color.toColor()!!),
              contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Group Icon",
                    tint = Color.White)
              }
          Spacer(modifier = Modifier.width(8.dp))
          Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween) {
                  Text(
                      text = groupDetails.name,
                      fontSize = 16.sp,
                      fontWeight = FontWeight.Bold,
                      color = Color.Black)
                  Text(
                      text = time,
                      color = Color.Gray,
                      style = MaterialTheme.typography.bodyMedium,
                  )
                }
            Text(
                text = groupDetails.lastMessage?.content ?: "",
                color = Color.Black,
                style = MaterialTheme.typography.bodyMedium)
          }
        }
        if (isActive && isAdmin) {
          Button(
              onClick = { onDelete(groupDetails) },
              modifier = Modifier.fillMaxWidth().padding(8.dp).testTag("DeleteButton$testTag")) {
                Text("Delete Group")
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
fun GroupChatBody(
    groupList: List<GroupDetails>,
    onClick: (GroupDetails) -> Unit,
    onDelete: (GroupDetails) -> Unit,
    activeGroupId: String?,
    onSetActiveGroupId: (String?) -> Unit,
    isAdmin: Boolean
) {
  LazyColumn(modifier = Modifier.testTag("GroupChatBody")) {
    items(groupList.size) { index ->
      val group = groupList[index]
      GroupCard(
          groupDetails = groupList[index],
          onClick = onClick,
          onDelete = {
            onDelete(group)
            onSetActiveGroupId(null)
          },
          testTag = "$index",
          isActive = activeGroupId == group.id,
          onActivate = { onSetActiveGroupId(group.id) },
          isAdmin)
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
