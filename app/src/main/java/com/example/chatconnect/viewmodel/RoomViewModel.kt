package com.example.chatconnect.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatconnect.Injection
import com.example.chatconnect.data.Result.*
import com.example.chatconnect.data.Room
import com.example.chatconnect.data.RoomRepository
import kotlinx.coroutines.launch

// First we need a state for the room list. Using LiveData we create a mutable state for updating the
// list and an immutable one for observing through the UI. Next is to create and initialize the
// RoomRepository within the init block. Using viewmodelscope we launch the createRoom function
// with a room name. In the loadRoom function we also launch viewmodelscope and get the rooms list
// if data fetching is successful.


class RoomViewModel : ViewModel() {
    private val _rooms = MutableLiveData<List<Room>>()
    val rooms : LiveData<List<Room>> get() = _rooms
    private val roomRepository : RoomRepository

    init {
        roomRepository = RoomRepository(Injection.instance())
        loadRooms()
    }

    fun createRoom(name:String){
        viewModelScope.launch {
            roomRepository.createRoom(name)
//            when (val result = roomRepository.createRoom(name)) {
//                is Success -> {
//                    // Room created, now reload the rooms to update the UI
//                    loadRooms()
//                }
//                is Error -> {
//                    // Handle error
//                }
//            }
        }
    }

    fun loadRooms(){
        viewModelScope.launch {
            when (val result = roomRepository.getRooms()){
                is Success ->{
                    _rooms.value = result.data
                }
                is Error ->{

                }
            }
        }
    }
}