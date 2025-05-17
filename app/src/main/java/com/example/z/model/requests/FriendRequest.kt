package com.example.z.model.requests

import kotlinx.serialization.Serializable

@Serializable
data class FriendRequest(
    val senderLogin: String
)
