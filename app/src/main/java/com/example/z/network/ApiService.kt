package com.example.z.network

import android.util.Log
import com.example.z.model.requests.LoginRequest
import com.example.z.model.requests.UserRequest
import com.example.z.model.response.BaseResponse
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

object ApiService {
    private const val BASE_URL = "http://192.168.112.226:8080/api/v1/"

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
}