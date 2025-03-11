package com.example.chatconnect.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chatconnect.Injection
import com.example.chatconnect.data.Result
import com.example.chatconnect.data.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch


// We need to initialize the userRepository which is dependent on both the FirebaseAuth
// and FirestoreDatabase. For the auth instance we can call it directly within the class
// without having to provide a global access to it because this is the only class we would need it.
// For the Firestore we will need to create an object class which can make it accessible to other
// classes that will be dependent on it.
class AuthViewModel : ViewModel() {
    private val userRepository : UserRepository

    init {
        userRepository = UserRepository(
            FirebaseAuth.getInstance(),
            Injection.instance()
        )
    }

    private val _authResult = MutableLiveData<Result<Boolean>>()
    val authResult : LiveData<Result<Boolean>> get() = _authResult

    fun signUp(email: String, password: String, firstName: String, lastName: String) {
        viewModelScope.launch {
            _authResult.value = userRepository.signUp(email, password, firstName, lastName)
        }
    }

    fun login(email: String,password: String){
        viewModelScope.launch {
            _authResult.value = userRepository.login(email, password)
        }
    }
}