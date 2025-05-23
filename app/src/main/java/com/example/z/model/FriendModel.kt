package com.example.z.model

import kotlinx.serialization.Serializable

@Serializable
data class FriendModel(
    val id: String,
    val login: String,
    val firstName: String,
    val lastName: String,
    val coordinates: String
)
