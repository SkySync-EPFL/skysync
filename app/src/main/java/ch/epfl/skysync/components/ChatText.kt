package ch.epfl.skysync.components

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ch.epfl.skysync.ui.theme.lightOrange

/**
 * Composable function representing a chat screen.
 *
 * @param groupName Name of the group or chat.
 * @param ListofPairSenderImagePairMsgTime List of chat messages with sender information, message
 *   content, and timestamp.
 * @param backClick Callback function to be invoked when the back button is clicked.
 * @param sendClick Callback function to be invoked when a message is sent.
 * @param paddingValues Padding values to be applied to the chat screen.
 */
@Composable
fun ChatText(
    groupName: String,
    ListofPairSenderImagePairMsgTime: List<Pair<Pair<String, ImageVector?>, Pair<String, String>>>,
    backClick: () -> Unit,
    sendClick: (String) -> Unit,
    paddingValues: PaddingValues
) {
  Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
    Header(BackClick = backClick, title = groupName)
    Spacer(modifier = Modifier.size(1.dp))
    ChatTextBody(ListofPairSenderImagePairMsgTime)
    ChatInput(sendClick)
  }
}
/**
 * Composable function representing the body of a chat screen, displaying chat bubbles.
 *
 * @param ListofPairSenderImagePairMsgTime List of chat messages with sender information, message
 *   content, and timestamp.
 */
@Composable
fun ChatTextBody(
    ListofPairSenderImagePairMsgTime: List<Pair<Pair<String, ImageVector?>, Pair<String, String>>>
) {
  val lazyListState = rememberLazyListState()
  LazyColumn(Modifier.fillMaxHeight(0.875f).testTag("ChatTextBody"), state = lazyListState) {
    items(ListofPairSenderImagePairMsgTime.size) { index ->
      ChatBubble(
          sender = ListofPairSenderImagePairMsgTime[index].first.first,
          image = ListofPairSenderImagePairMsgTime[index].first.second,
          message = ListofPairSenderImagePairMsgTime[index].second.first,
          time = ListofPairSenderImagePairMsgTime[index].second.second,
          index = "$index")
    }
  }
  LaunchedEffect(Unit) { lazyListState.scrollToItem(ListofPairSenderImagePairMsgTime.size - 1) }
}
/**
 * Composable function representing a chat bubble.
 *
 * @param sender The sender of the message.
 * @param image The image associated with the sender.
 * @param message The message content.
 * @param time The timestamp of the message.
 * @param index The index of the chat bubble.
 */
@Composable
fun ChatBubble(sender: String, image: ImageVector?, message: String, time: String, index: String) {
  var isMyMessage = false
  if (sender == "me") {
    isMyMessage = true
  }
  val backgroundColor = if (isMyMessage) Color(0xFFDCF8C6) else Color.White
  val contentColor = if (isMyMessage) Color.Black else Color.Black
  val shape =
      when (isMyMessage) {
        false -> {
          RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp, bottomEnd = 8.dp, bottomStart = 0.dp)
        }
        true -> {
          RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp, bottomEnd = 0.dp, bottomStart = 8.dp)
        }
      }
  Row(
      modifier = Modifier.padding(8.dp).fillMaxWidth(),
      horizontalArrangement = if (isMyMessage) Arrangement.End else Arrangement.Start,
      verticalAlignment = Alignment.Bottom) {
        if (!isMyMessage) {
          if (image != null) {
            Box(modifier = Modifier.fillMaxWidth(0.125f).size(30.dp)) {
              Image(imageVector = image, contentDescription = "Image of Sender")
            }
          } else {
            Box(
                modifier =
                    Modifier.fillMaxWidth(0.075f)
                        .size(30.dp)
                        .background(color = Color.LightGray, shape = CircleShape)) {}
          }
          Spacer(modifier = Modifier.size(2.dp))
        }
        Column(
            modifier = Modifier.background(color = backgroundColor, shape = shape).padding(8.dp)) {
              Row {
                Text(
                    text = message,
                    color = contentColor,
                    modifier = Modifier.padding(bottom = 2.dp).testTag("ChatBubbleMessage$index"))
                Spacer(modifier = Modifier.size(4.dp))
                Text(
                    text = time,
                    color = Color.Gray,
                    fontSize = 9.sp,
                    modifier = Modifier.align(Alignment.Bottom).testTag("ChatBubbleTime$index"))
              }
            }
      }
}
/**
 * Composable function representing an input field for typing and sending messages.
 *
 * @param onSend Callback function to be invoked when a message is sent. It takes the message as a
 *   parameter.
 */
@Composable
fun ChatInput(onSend: (String) -> Unit) {
  var text by remember { mutableStateOf("") }
  val keyboardController = LocalSoftwareKeyboardController.current
  Row(
      modifier = Modifier.fillMaxWidth().padding(16.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically) {
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Type a message") },
            colors =
                OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.DarkGray, focusedLabelColor = Color.DarkGray),
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Send),
            keyboardActions =
                KeyboardActions(
                    onSend = {
                      onSend(text)
                      text = ""
                      keyboardController?.hide()
                    }),
            modifier = Modifier.weight(1f).testTag("ChatInput"))

        IconButton(
            onClick = {
              onSend(text)
              text = ""
            },
            modifier =
                Modifier.padding(start = 8.dp)
                    .background(lightOrange, CircleShape)
                    .testTag("SendButton")) {
              Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color.White)
            }
      }
}
