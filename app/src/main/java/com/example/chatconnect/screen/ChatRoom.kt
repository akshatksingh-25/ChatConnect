package com.example.chatconnect.screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.chatconnect.R
import com.example.chatconnect.data.Message
import com.example.chatconnect.viewmodel.MessageViewModel
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ChatScreen(
    roomId: String,
    messageViewModel: MessageViewModel = viewModel()
){

    val messages by messageViewModel.messages.observeAsState(emptyList())
    messageViewModel.setRoomId(roomId = roomId)

    val text = remember {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f)
        ){
            items(messages){
                message ->
                ChatMessageItem(
                    message = message.copy(
                        isSentByCurrentUser = message.senderId == messageViewModel.currentUser.value?.email
                    )
                )
            }
        }

        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ){
            OutlinedTextField(
                value = text.value,
                onValueChange = { text.value = it },
                textStyle = TextStyle.Default.copy(fontSize = 16.sp),
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
            )
            
            IconButton(
                onClick = {
                    //Send the message when icon is clicked
                    if(text.value.isNotEmpty()){
                        messageViewModel.sendMessage(text.value)
                        text.value=""
                    }
                    messageViewModel.loadMessages()
                }) {
                Icon(imageVector = Icons.Default.Send, contentDescription = "Send")
            }
        }
    }
}


//@Preview(showBackground = true)
//@Composable
//fun ChatRoomsPreview(){
//    chatRoom(roomId = "")
//}


//Let's create the ChatItem for each message. Before that we would like each message to show the time it was created.
// This will require some helper functions to create a readable format for the users from the timestamp.
// First is to format the day, followed by the time then using the isSameDay we display the exact time day.
// Finally the formatTimeStamp changes the long time value from milli seconds and display on the screen.


@RequiresApi(Build.VERSION_CODES.O)
private fun formatTimeStamp( timeStamp:Long ) : String{
    val messageDateTime =
        LocalDateTime.ofInstant(Instant.ofEpochMilli(timeStamp),
    ZoneId.systemDefault())
    val now = LocalDateTime.now()

    return when {
        isSameDay(messageDateTime,now) ->
            "today ${formatTime(messageDateTime)}"
        isSameDay(messageDateTime.plusDays(1),now) ->
            "yesterday ${formatTime(messageDateTime)}"
        else ->
            formatDate(messageDateTime)
    }
}


@RequiresApi(Build.VERSION_CODES.O)
private fun isSameDay(dateTime1 : LocalDateTime, dateTime2 : LocalDateTime) : Boolean{
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    return dateTime1.format(formatter) == dateTime2.format(formatter)
}

@RequiresApi(Build.VERSION_CODES.O)
private fun formatTime(dateTime: LocalDateTime): String {
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    return formatter.format(dateTime)
}

@RequiresApi(Build.VERSION_CODES.O)
private fun formatDate(dateTime: LocalDateTime): String {
    val formatter = DateTimeFormatter.ofPattern("MMM d, yyyy")
    return formatter.format(dateTime)
}


//Now we will create the Message object to use for setting up the room item.
// The RoomItem consists of a Text for the message, a Text for the first name and another for the time.


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ChatMessageItem(message : Message){
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalAlignment = if (message.isSentByCurrentUser )
                                    Alignment.End
                              else
                                    Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .background(
                    if (message.isSentByCurrentUser) {
                        colorResource(id = R.color.purple_700)
                    } else {
                        Color.Gray
                    },
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(8.dp)
        ){
            Text(
                text = message.text,
                color = Color.White,
                style = TextStyle(fontSize = 16.sp)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = message.senderFirstName,
            style = TextStyle(
                fontSize = 12.sp,
                color = Color.Gray
            )
        )

        Text(
            text = formatTimeStamp(message.timestamp), // Replace with actual timestamp logic
            style = TextStyle(
                fontSize = 12.sp,
                color = Color.Gray
            )
        )
    }
}