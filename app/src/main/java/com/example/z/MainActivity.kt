package com.example.z

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.z.ui.screen.*
import com.example.z.utils.Routes
import com.example.z.utils.TokenManager
import com.example.z.viewmodel.AuthViewModel
import com.example.z.viewmodel.FriendsViewModel
import com.example.z.viewmodel.MapViewModel
import com.yandex.mapkit.MapKitFactory
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.z.viewmodel.ThemeViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.z.model.FriendModel
import com.example.z.utils.ThemePreferences

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.initialize(this)
        val tokenManager = TokenManager(this)
        val themePreferences = ThemePreferences(this)

        setContent {
            ZApp(
                tokenManager = tokenManager,
                themePreferences = themePreferences
            )
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
fun ZApp(
    tokenManager: TokenManager,
    themePreferences: ThemePreferences
) {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val mapViewModel: MapViewModel = viewModel()
    val friendsViewModel: FriendsViewModel = viewModel()
    var selectedFriend by remember { mutableStateOf<FriendModel?>(null) }

    val themeViewModel: ThemeViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return ThemeViewModel(themePreferences) as T
            }
        }
    )

    val colorScheme = if (themeViewModel.isDarkTheme) {
        darkColorScheme()
    } else {
        lightColorScheme()
    }

    MaterialTheme(colorScheme = colorScheme) {
        NavHost(
            navController = navController,
            startDestination = if (tokenManager.getToken() != null) Routes.MAP_SCREEN else Routes.AUTH_SCREEN
        ) {
            composable(Routes.AUTH_SCREEN) {
                AuthScreen(
                    onRegisterClick = { navController.navigate(Routes.REGISTER_SCREEN) },
                    onLoginSuccess = { navController.navigate(Routes.MAP_SCREEN) },
                    tokenManager = tokenManager,
                    authViewModel = authViewModel
                )
            }

            composable(Routes.REGISTER_SCREEN) {
                RegisterScreen(
                    onRegisterSuccess = { navController.navigate(Routes.MAP_SCREEN) },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(Routes.MAP_SCREEN) {
                YandexMapWithLocationMarker(
                    viewModel = mapViewModel,
                    onSettingsClick = { navController.navigate(Routes.SETTINGS_SCREEN) },
                    onProfileClick = { navController.navigate(Routes.PROFILE_SCREEN) },
                    onFriendsClick = { navController.navigate(Routes.FRIENDS_SCREEN) }
                )
            }

            composable(Routes.PROFILE_SCREEN) {
                LaunchedEffect(Unit) {
                    tokenManager.getToken()?.let { authViewModel.loadProfileData(it) }
                }

                ProfileScreen(
                    onBackClick = { navController.popBackStack() },
                    onLogoutClick = {
                        authViewModel.logout(tokenManager)
                        navController.navigate(Routes.AUTH_SCREEN) {
                            popUpTo(Routes.MAP_SCREEN) { inclusive = true }
                        }
                    },
                    profileData = authViewModel.profileData.value
                )
            }

            composable(Routes.FRIENDS_SCREEN) {
                LaunchedEffect(Unit) {
                    tokenManager.getToken()?.let { friendsViewModel.loadFriends(it) }
                }

                FriendsScreen(
                    onBackClick = { navController.popBackStack() },
                    onFriendClick = { friend ->
                        navController.currentBackStackEntry?.savedStateHandle?.set("friend", friend)
                        navController.navigate("${Routes.FRIEND_PROFILE_SCREEN}/${friend.id}")
                    },
                    viewModel = friendsViewModel
                )
            }

            composable("${Routes.FRIEND_PROFILE_SCREEN}/{friendId}") { backStackEntry ->
                val friendId = backStackEntry.arguments?.getString("friendId")
                val friend = friendsViewModel.friends.find { it.id == friendId }
                    ?: return@composable

                FriendProfileScreen(
                    friend = friend,
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(Routes.SETTINGS_SCREEN) {
                SettingsScreen(
                    onBackClick = { navController.popBackStack() },
                    onThemeChange = { isDark ->
                        themeViewModel.toggleTheme(isDark)
                    }
                )
            }
        }
    }
}