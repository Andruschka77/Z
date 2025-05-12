package com.example.z.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.z.network.ApiService
import com.example.z.model.requests.Friend
import com.example.z.model.requests.FriendRequest
import kotlinx.coroutines.launch

class FriendsViewModel : ViewModel() {
    private val _friends = mutableStateOf<List<Friend>>(emptyList())
    val friends: List<Friend> get() = _friends.value

    private val _pendingRequests = mutableStateOf<List<FriendRequest>>(emptyList())
    val pendingRequests: List<FriendRequest> get() = _pendingRequests.value

    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: String? get() = _errorMessage.value

    fun loadFriends(token: String) {
        viewModelScope.launch {
            try {
                val response = ApiService.getFriends(token)
                if (response.success) {
                    _friends.value = response.friends
                    _pendingRequests.value = response.pendingRequests
                } else {
                    _errorMessage.value = response.message
                }
            } catch (e: Exception) {
                _errorMessage.value = "Ошибка загрузки друзей"
            }
        }
    }

    fun sendFriendRequest(token: String, receiverLogin: String) {
        viewModelScope.launch {
            try {
                val response = ApiService.sendFriendRequest(token, receiverLogin)
                if (!response.success) {
                    _errorMessage.value = response.message
                }
            } catch (e: Exception) {
                _errorMessage.value = "Ошибка отправки запроса"
            }
        }
    }

    fun respondToRequest(token: String, requestId: String, accept: Boolean) {
        viewModelScope.launch {
            try {
                val response = ApiService.respondToFriendRequest(token, requestId, accept)
                if (!response.success) {
                    _errorMessage.value = response.message
                } else {
                    loadFriends(token) // Обновляем список после ответа
                }
            } catch (e: Exception) {
                _errorMessage.value = "Ошибка обработки запроса"
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}