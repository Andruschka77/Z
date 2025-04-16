package com.example.z.network

import com.example.z.model.requests.LoginRequest
import com.example.z.model.requests.ProfileRequest
import com.example.z.model.requests.UserRequest
import com.example.z.model.response.BaseResponse
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

object ApiService {
    //private const val BASE_URL = "http://192.168.112.226:8080/api/v1/"
    private const val BASE_URL = "http://10.0.2.2:8080/api/v1/"

    suspend fun signUp(userRequest: UserRequest): BaseResponse {
        return KtorClient.client.post("${BASE_URL}signup") {
            contentType(ContentType.Application.Json)
            setBody(userRequest)
        }.body()
    }

    suspend fun logIn(loginRequest: LoginRequest): BaseResponse {
        return KtorClient.client.post("${BASE_URL}login") {
            contentType(ContentType.Application.Json)
            setBody(loginRequest)
        }.body()
    }

    suspend fun getProfile(token: String): BaseResponse {
        return KtorClient.client.get("${BASE_URL}profile") {
            header("Authorization", "Bearer $token")
        }.body()
    }

    suspend fun updateProfile(token: String, profile: ProfileRequest): BaseResponse {
        return KtorClient.client.post("${BASE_URL}update-profile") {
            header("Authorization", "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(profile)
        }.body()
    }

}