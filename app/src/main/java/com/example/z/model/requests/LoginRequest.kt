package com.example.z.model.requests
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest (
    val email: String,
    val password: String
)

