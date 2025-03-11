package com.example.chatconnect

sealed class Screen(val route : String){
    object LoginScreen : Screen("loginscreen")
    object SignUpScreen : Screen("signupscreen")
    object ChatRoomsScreen : Screen("chatroomsscreen")
    object ChatScreen : Screen("chatscreen")
}