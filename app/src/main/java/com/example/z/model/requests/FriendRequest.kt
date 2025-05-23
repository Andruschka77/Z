package com.example.z.model.requests

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class FriendRequest(
    @SerialName("sender_login")
    val senderLogin: String
)
