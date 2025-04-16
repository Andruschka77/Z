package com.example.z.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.z.model.requests.LoginRequest
import com.example.z.model.requests.ProfileRequest
import com.example.z.model.requests.UserRequest
import com.example.z.model.response.BaseResponse
import com.example.z.utils.TokenManager
import androidx.compose.runtime.State
import com.example.z.network.ApiService
import kotlinx.serialization.json.Json

class AuthViewModel : ViewModel() {
    private val _profileData = mutableStateOf<ProfileRequest?>(null)
    val profileData: State<ProfileRequest?> get() = _profileData

    suspend fun signUp(userRequest: UserRequest): BaseResponse =
        ApiService.signUp(userRequest = userRequest)

    suspend fun logIn(tokenManager: TokenManager, loginRequest: LoginRequest): Boolean {
        val response = ApiService.logIn(loginRequest = loginRequest)

        return if (response.success) {
            tokenManager.saveToken(response.message)
            true
        } else false
    }

    suspend fun loadProfileData(token: String) {
        try {
            val response = ApiService.getProfile(token)
            if (response.success) {
                _profileData.value = Json.decodeFromString<ProfileRequest>(response.message)
            }
        } catch (e: Exception) {
            Log.e("AuthViewModel", "Ошибка загрузки профиля", e)
        }
    }

    fun logout(tokenManager: TokenManager) {
        tokenManager.clearToken()
        _profileData.value = null
    }
}