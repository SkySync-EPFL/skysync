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
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ch.epfl.skysync.ui.theme.lightOrange

data class Quadruple<A, B, C, D>(val first: A, val second: B?, val third: C, val fourth: D)
@Composable
fun GroupChat(GroupsImageLastmsgLastmsgtime: List<Quadruple<String,ImageVector,String,String>>, onClick: (String) -> Unit,paddingValues: PaddingValues){
    var searchQuery by remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ){
        GroupChatTopBar()
        Spacer(modifier = Modifier.fillMaxHeight(0.02f))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = lightOrange,
                focusedLabelColor = lightOrange
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("Search"))
        val filteredGroups = GroupsImageLastmsgLastmsgtime.filter { it.first.contains(searchQuery, ignoreCase = true) }
        Spacer(modifier = Modifier.fillMaxHeight(0.05f))
        GroupChatBody(Groups = filteredGroups, onClick = onClick)
    }
}

@Composable
@Preview
fun GroupChatPreview() {
    val image: ImageVector? = null
    val groups = listOf(Quadruple("Group 1", image, "Last message", "Last message time"),
        Quadruple("Group 2",image, "zebi", "Last message time"),
        Quadruple("Group 3",image,"Last message", "Last message time")
    )
  GroupChat(GroupsImageLastmsgLastmsgtime = groups, onClick = {}, paddingValues = PaddingValues(0.dp))
}
@Composable
fun GroupChatTopBar() {
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.05f),
            contentAlignment = Alignment.Center,
        )
        {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Messages",
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                )
            }
        }
    }
}

@Composable
fun GroupCard(group: String, onClick : () -> Unit, groupImage: ImageVector?, lastMsg: String, lastMsgTime: String, testTag: String){
    Card(
        modifier = Modifier
            .clickable(onClick = onClick)
            .fillMaxWidth()
            .testTag(testTag),
        shape = RectangleShape,
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
        )
    ) {
        Row {
            if(groupImage != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.125f)
                        .size(50.dp)
                ){
                    Image(imageVector = groupImage, contentDescription = "Group Image")
                }
            }
            else{
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.125f)
                        .size(50.dp)
                        .background(color = Color.LightGray)
                ){
                }
            }
            Spacer(modifier =Modifier.size(10.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.SpaceBetween
            ){
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    Text(
                        text = group,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = lastMsgTime,
                        color = Color.Gray
                    )
                }
                Text(
                    text = lastMsg
                )
            }
        }
    }
}
@Composable
fun GroupChatBody(Groups: List<Quadruple<String,ImageVector,String,String>>, onClick: (String) -> Unit){
    LazyColumn(
        modifier = Modifier.testTag("GroupChatBody")
    ){
        items(Groups.size) { index ->
            GroupCard(
                group = Groups[index].first,
                onClick = {onClick(Groups[index].first)},
                groupImage = Groups[index].second,
                lastMsg = Groups[index].third,
                lastMsgTime = Groups[index].fourth,
                testTag = "GroupCard$index"
            )
            Spacer(modifier = Modifier.size(1.dp))
        }
    }

}