package com.example.z

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.yandex.mapkit.MapKitFactory
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppNavigation()
        }

    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
    }

    override fun onStop() {
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val mapViewModel: MapViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "map"
    ) {
        composable("map") {
            YandexMapWithLocationMarker(
                onProfileClick = { navController.navigate("profile") },
                onFriendsClick = { navController.navigate("friends") },
                onMessagesClick = { navController.navigate("messages") },
                onSettingsClick = { navController.navigate("settings") },
                viewModel = mapViewModel
            )
        }

        composable("profile") {
            ProfileScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("friends") {
            FriendsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("messages") {
            MessagesScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        composable("settings") {
            SettingsScreen(
                onBackClick = { navController.popBackStack() },
            )
        }
    }
}