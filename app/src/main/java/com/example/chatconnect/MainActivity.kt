package com.example.chatconnect

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.chatconnect.screen.ChatRoomListScreen
import com.example.chatconnect.screen.ChatScreen
import com.example.chatconnect.screen.LoginScreen
import com.example.chatconnect.screen.SignUpScreen
import com.example.chatconnect.ui.theme.ChatConnectTheme
import com.example.chatconnect.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val authViewModel : AuthViewModel = viewModel()
            ChatConnectTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavigationGraph(navController = navController,authViewModel = authViewModel)
                }
            }
        }
    }
}




@Composable
fun NavigationGraph(
    navController : NavHostController,
    authViewModel: AuthViewModel
){
    NavHost(
        navController = navController,
        startDestination = Screen.SignUpScreen.route
    ){

        composable(Screen.SignUpScreen.route){
            SignUpScreen (
                authViewModel = authViewModel,
                onNavigateToLogin = {
                    navController.navigate(Screen.LoginScreen.route)
                }
            )
        }

        composable(Screen.LoginScreen.route){
            LoginScreen(
                authViewModel = authViewModel,
                onNavigateToSignUp = {
                    navController.navigate(Screen.SignUpScreen.route)
                },
                onSignInSuccess = {
                    navController.navigate(Screen.ChatRoomsScreen.route)
                }
            )
        }

        composable(Screen.ChatRoomsScreen.route){
            ChatRoomListScreen(
                onJoinClicked = {
                    navController.navigate("${Screen.ChatScreen.route}/${it.id}")
                }
            )
        }

        composable("${Screen.ChatScreen.route}/{roomId}"){
            val roomId : String = it.arguments?.getString("roomId") ?: ""
            ChatScreen(roomId = roomId)
        }
    }
}
