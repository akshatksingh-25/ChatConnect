package com.example.chatconnect.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await

class RoomRepository(private val firestore: FirebaseFirestore) {

    suspend fun createRoom(name : String) : Result<Unit> =
        try{
            val room = Room(name = name)
            firestore.collection("rooms").add(room).await()
            Result.Success(Unit)
        }
        catch (e:Exception){
            Result.Error(e)
        }

    // The createRoom function will get the name of the room and save it to its own
    // collection called room. The getRooms function will return a list of Room by
    // querying the rooms collection then map the documents into a list including the id
    // for each object This id will enable us to identify each rooms during chats.

    suspend fun getRooms() : Result<List<Room>> =
        try{
            val querySnapShot = firestore.collection("rooms").get().await()
            val rooms = querySnapShot.documents.map {
                document ->
                document.toObject(Room::class.java)!!.copy(id = document.id)
            }
            Result.Success(rooms)
        }
        catch (e:Exception){
            Result.Error(e)
        }
}