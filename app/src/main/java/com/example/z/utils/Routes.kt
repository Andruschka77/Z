package com.example.z.utils

object Routes {
    // Основные экраны
    const val AUTH_SCREEN = "auth_screen"
    const val REGISTER_SCREEN = "register_screen"
    const val MAP_SCREEN = "map_screen"
    const val PROFILE_SCREEN = "profile_screen"
    const val FRIENDS_SCREEN = "friends_screen"
    const val FRIEND_PROFILE_SCREEN = "friend_profile" // Базовый маршрут
    const val FRIEND_PROFILE_WITH_ID = "friend_profile/{friendId}" // Параметризованный
    const val MESSAGES_SCREEN = "messages_screen"
    const val SETTINGS_SCREEN = "settings_screen"

    // Функция для создания пути с параметром
    fun getFriendProfileRoute(friendId: String) = "friend_profile/$friendId"
}