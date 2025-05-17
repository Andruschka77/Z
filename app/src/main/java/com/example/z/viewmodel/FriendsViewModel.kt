package com.example.z.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.z.model.FriendModel
import com.example.z.network.ApiService
import com.example.z.model.requests.FriendRequest
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class FriendsViewModel : ViewModel() {
    private val _friends = mutableStateOf<List<FriendModel>>(emptyList())
    val friends: List<FriendModel> get() = _friends.value

    private val _pendingRequests = mutableStateOf<List<FriendRequest>>(emptyList())
    val pendingRequests: List<FriendRequest> get() = _pendingRequests.value

    private val _errorMessage = mutableStateOf<String?>(null)

    private val _isLoading = mutableStateOf(false)
    val isLoading: Boolean get() = _isLoading.value

    fun loadFriends(token: String) {
        if (token.isBlank()) {
            _errorMessage.value = "Токен отсутствует"
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = ApiService.getFriends(token)
                if (response.success) {
                    val friends = Json.decodeFromString<List<FriendModel>>(response.message)
                    _friends.value = friends
                } else {
                    _errorMessage.value = response.message
                }
            } catch (e: Exception) {
                _errorMessage.value = "Ошибка: ${e.message?.take(20)}..."
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun sendFriendRequest(token: String, receiverLogin: String) {
        viewModelScope.launch {
            try {
                ApiService.sendFriendRequest(token, receiverLogin)
            } catch (e: Exception) {
                Log.e("Network", "Ошибка: ${e.message}")
            }
        }
    }
    fun respondToRequest(token: String, senderLogin: String, accept: Boolean) {
        viewModelScope.launch {
            try {
                val response = ApiService.respondToFriendRequest(token, senderLogin, accept)
                if (!response.success) {
                    _errorMessage.value = response.message
                } else {
                    loadFriends(token)
                }
            } catch (e: Exception) {
                _errorMessage.value = "Ошибка обработки запроса"
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun deleteFriend(token: String, friendLogin: String) {
        viewModelScope.launch {
            try {
                val response = ApiService.deleteFriend(token, friendLogin)
                if (response.success) {
                    _friends.value = _friends.value.filterNot { it.login == friendLogin }
                }
            } catch (e: Exception) {
                _errorMessage.value = "Ошибка удаления: ${e.message}"
            }
        }
    }
}