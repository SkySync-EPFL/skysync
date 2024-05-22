package ch.epfl.skysync.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ch.epfl.skysync.models.message.ChatMessage
import ch.epfl.skysync.models.message.MessageDateFormatter
import ch.epfl.skysync.models.message.MessageType
import ch.epfl.skysync.ui.theme.lightGray
import ch.epfl.skysync.ui.theme.lightOrange
import ch.epfl.skysync.ui.theme.veryLightGreen

/**
 * A composable function that displays a chat interface with messages and an input field.
 *
 * @param messages List of chat messages to display.
 * @param onSend Callback function to handle sending a new message.
 * @param paddingValues Padding values to apply around the chat interface.
 */
@Composable
fun ChatText(messages: List<ChatMessage>, onSend: (String) -> Unit, paddingValues: PaddingValues) {
  Column(modifier = Modifier.padding(paddingValues).imePadding()) {
    ChatTextBody(messages, modifier = Modifier.weight(1f))
    ChatInput(onSend)
  }
}
/**
 * Displays a list of chat messages in a scrollable column.
 *
 * @param messages List of chat messages to display.
 * @param modifier Modifier for styling and layout (default is Modifier).
 *
 * The LazyColumn displays each message using the ChatBubble composable. The list scrolls to the
 * latest message when a new one is added.
 */
@Composable
fun ChatTextBody(messages: List<ChatMessage>, modifier: Modifier = Modifier) {
  var scrollTo = true
  val lazyListState = rememberLazyListState()
  LazyColumn(modifier.testTag("ChatTextBody"), state = lazyListState) {
    items(messages.size) { index -> ChatBubble(message = messages[index], index = "$index") }
  }
  LaunchedEffect(messages) {
    if (messages.isNotEmpty() && scrollTo) {
      lazyListState.scrollToItem(messages.size - 1)
      scrollTo = false
    }
  }
}
/**
 * Composable function representing a chat bubble.
 *
 * @param message The message.
 * @param index The index of the chat bubble.
 */
@Composable
fun ChatBubble(message: ChatMessage, index: String) {
  val isMyMessage = message.messageType == MessageType.SENT
  val messageContent = message.message.content
  val time = MessageDateFormatter.format(message.message.date)
  val backgroundColor = if (isMyMessage) veryLightGreen else lightGray
  val contentColor = Color.Black
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
      modifier = Modifier.padding(12.dp).fillMaxWidth(),
      horizontalArrangement = if (isMyMessage) Arrangement.End else Arrangement.Start,
      verticalAlignment = Alignment.Bottom) {
        Column {
          if (!isMyMessage) {
            Text(
                text = message.message.user.name(),
                style = TextStyle(fontSize = 12.sp, color = Color.Gray),
                modifier = Modifier.testTag("ChatBubbleUser$index"))
          }
          Message(
              messageContent = messageContent,
              time = time,
              backgroundColor = backgroundColor,
              contentColor = contentColor,
              shape = shape,
              index = index,
          )
        }
      }
}

@Composable
fun Message(
    messageContent: String,
    time: String,
    backgroundColor: Color,
    contentColor: Color,
    shape: RoundedCornerShape,
    index: String
) {
  Column(modifier = Modifier.background(color = backgroundColor, shape = shape).padding(8.dp)) {
    Row {
      Text(
          text = messageContent,
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
      modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically) {
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            placeholder = { Text("Type a message") },
            shape = RoundedCornerShape(24.dp),
            colors =
                OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Gray, focusedLabelColor = lightGray),
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
              if (text.isNotEmpty()) {
                onSend(text)
                text = ""
                keyboardController?.hide()
              }
            },
            enabled = text.isNotEmpty(),
            modifier =
                Modifier.padding(start = 8.dp)
                    .background(if (text.isNotEmpty()) lightOrange else Color.Gray, CircleShape)
                    .testTag("SendButton")) {
              Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color.White)
            }
      }
}
