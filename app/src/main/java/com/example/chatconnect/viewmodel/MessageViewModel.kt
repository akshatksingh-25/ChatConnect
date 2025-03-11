package com.example.chatconnect.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatconnect.Injection
import com.example.chatconnect.data.Message
import com.example.chatconnect.data.MessageRepository
import com.example.chatconnect.data.Result.*
import com.example.chatconnect.data.User
import com.example.chatconnect.data.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MessageViewModel : ViewModel() {
    private val messageRepository : MessageRepository
    private val userRepository : UserRepository
    init{
        messageRepository = MessageRepository(Injection.instance())
        userRepository = UserRepository(
            FirebaseAuth.getInstance(),
            Injection.instance()
        )
        loadCurrentUser()
    }

    private val _messages = MutableLiveData<List<Message>>()
    val messages : LiveData<List<Message>> get() = _messages

    private val _roomId = MutableLiveData<String>()
    private val _currentUser = MutableLiveData<User>()
    val currentUser : LiveData<User> get() = _currentUser

    //loadCurrentUser to identify the user on every accounts during a chat.
    private fun loadCurrentUser(){
        viewModelScope.launch {
            when (val result = userRepository.getCurrentUser()) {
                is Success -> {
                    _currentUser.value = result.data
                }
                is Error -> {

                }
            }
        }
    }

    //loadMessages to retrieve messages within a room.
    fun loadMessages(){
        viewModelScope.launch {
            if(_roomId != null){
                messageRepository.getChatMessages(_roomId.value.toString())
                    .collect{
                        _messages.value = it
                    }
            }
        }
    }

    //sendMessage to send a message to a room by the current user. This all displays the first name of the user
    fun sendMessage(text : String){
        if (_currentUser.value != null){
            val message = Message(
                senderFirstName = _currentUser.value!!.firstName,
                senderId = _currentUser.value!!.email,
                text = text
            )
            viewModelScope.launch {
                when (messageRepository.sendMessage(_roomId.value.toString(),message)){
                    is Success -> Unit
                    is Error -> {

                    }
                }
            }
        }
    }

    //Finally the setRoomId for providing the id for the current room information and display the messages.
    fun setRoomId(roomId: String) {
        _roomId.value = roomId
        loadMessages()
    }
}