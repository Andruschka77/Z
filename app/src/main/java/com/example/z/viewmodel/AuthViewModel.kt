package com.example.z.viewmodel

import androidx.lifecycle.ViewModel
import com.example.z.model.requests.LoginRequest
import com.example.z.model.requests.UserRequest
import com.example.z.model.response.BaseResponse
import com.example.z.network.ApiService
import com.example.z.utils.TokenManager

class AuthViewModel : ViewModel() {
    suspend fun signUp(userRequest: UserRequest): BaseResponse =
        ApiService.signUp(userRequest = userRequest)

    suspend fun logIn(tokenManager: TokenManager, loginRequest: LoginRequest): Boolean {
        val response = ApiService.logIn(loginRequest = loginRequest)

        return if (response.success) {
            tokenManager.saveToken(response.message)
            true
        } else false
    }
}