package com.example.userbrowserapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.userbrowserapp.domain.model.UserModel
import com.example.userbrowserapp.presentation.dashboard.DashboardScreen
import com.example.userbrowserapp.presentation.detailed.DetailedScreen
import com.example.userbrowserapp.ui.theme.UserBrowserAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UserBrowserAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    UserBrowserApp()
                }
            }
        }
    }
}

@Composable
fun UserBrowserApp() {
    val navController = rememberNavController()
    var selectedUser by remember { mutableStateOf<UserModel?>(null) }
    var refreshKey by remember { mutableStateOf(0) }
    
    NavHost(
        navController = navController,
        startDestination = "dashboard"
    ) {
        composable("dashboard") {
            DashboardScreen(
                onUserClick = { user ->
                    selectedUser = user
                    navController.navigate("detailed")
                },
                refreshKey = refreshKey
            )
        }
        
        composable("detailed") {
            val user = selectedUser
            if (user != null) {
                DetailedScreen(
                    user = user,
                    onBackClick = { 
                        refreshKey++ // Force dashboard refresh
                        navController.popBackStack() 
                    }
                )
            } else {
                // Handle case where no user is selected
                navController.popBackStack()
            }
        }
    }
}