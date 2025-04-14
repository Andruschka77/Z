package com.example.z

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.yandex.mapkit.MapKitFactory
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.z.ui.screen.AuthScreen
import com.example.z.ui.screen.FriendsScreen
import com.example.z.ui.screen.MessagesScreen
import com.example.z.ui.screen.ProfileScreen
import com.example.z.ui.screen.RegisterScreen
import com.example.z.ui.screen.SettingsScreen
import com.example.z.ui.screen.YandexMapWithLocationMarker
import com.example.z.utils.Routes
import com.example.z.utils.TokenManager
import com.example.z.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {
    private lateinit var tokenManager: TokenManager
    private lateinit var authViewModel: AuthViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tokenManager = TokenManager(this)
        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        setContent {
            Navigation(tokenManager = tokenManager, authViewModel = authViewModel)
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
fun Navigation(tokenManager: TokenManager, authViewModel: AuthViewModel) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = if (tokenManager.getToken() != null) Routes.YANDEXMAPWITHLOCATIONMARKER else Routes.AUTH_SCREEN
    ) {
        composable(Routes.AUTH_SCREEN) {
            AuthScreen(
                onRegisterClick = { navController.navigate(Routes.REGISTER_SCREEN) },
                onLoginSuccess = { navController.navigate(Routes.YANDEXMAPWITHLOCATIONMARKER) },
                tokenManager = tokenManager,
                authViewModel = authViewModel
            )
        }
        composable(Routes.REGISTER_SCREEN) {
            RegisterScreen(
                onRegisterSuccess = { navController.navigate(Routes.YANDEXMAPWITHLOCATIONMARKER) },
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(Routes.YANDEXMAPWITHLOCATIONMARKER) {
            YandexMapWithLocationMarker(
                onSettingsClick = { navController.navigate(Routes.SETTINGS_SCREEN) },
                onProfileClick = { navController.navigate(Routes.PROFILE_SCREEN) },
                onFriendsClick = { navController.navigate(Routes.FRIENDS_SCREEN) },
                onMessagesClick = { navController.navigate(Routes.MESSAGES_SCREEN) },
                onLogoutClick = {
                    tokenManager.clearToken()
                    navController.navigate(Routes.AUTH_SCREEN) {
                        popUpTo(Routes.AUTH_SCREEN) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.PROFILE_SCREEN) {
            ProfileScreen(onBackClick = { navController.popBackStack() })
        }
        composable(Routes.FRIENDS_SCREEN) {
            FriendsScreen(onBackClick = { navController.popBackStack() })
        }
        composable(Routes.MESSAGES_SCREEN) {
            MessagesScreen(onBackClick = { navController.popBackStack() })
        }
        composable(Routes.SETTINGS_SCREEN) {
            SettingsScreen(onBackClick = { navController.popBackStack() })
        }
    }
}