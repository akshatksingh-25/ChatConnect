package com.example.chatconnect.data

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await


class UserRepository(
    private val auth : FirebaseAuth,
    private val firestore: FirebaseFirestore
){
    //SignUp
    suspend fun signUp(email : String, password :String, firstName:String, lastName : String) : Result<Boolean> =
        try{
            auth.createUserWithEmailAndPassword(email, password).await()
            val user = User(firstName,lastName,email)
            saveUserToFirestore(user)
            //add user to firestore
            Result.Success(true)
        }
        catch(e : Exception){
            Result.Error(e)
        }

    private suspend fun saveUserToFirestore(user: User){
        //firestore saves data as documents within a collection.
        // For each user document we will save them in a collection called users providing
        // their email as a key rather than letting firestore generate random characters.
        // With this email we can then manage each user better in the chatrooms.
        firestore.collection("users").document(user.email).set(user).await()
    }

    suspend fun login(email:String,password: String) : Result<Boolean> =
        try {
            auth.signInWithEmailAndPassword(email,password).await()
            Result.Success(true)
        }catch (e : Exception){
            Result.Error(e)
        }

    suspend fun getCurrentUser(): Result<User> = try {
        val uid = auth.currentUser?.email
        if (uid != null) {
            val userDocument = firestore.collection("users").document(uid).get().await()
            val user = userDocument.toObject(User::class.java)
            if (user != null) {
                Log.d("user2","$uid")
                Result.Success(user)
            } else {
                Result.Error(Exception("User data not found"))
            }
        } else {
            Result.Error(Exception("User not authenticated"))
        }
    } catch (e: Exception) {
        Result.Error(e)
    }


}