package com.example.z.ui

import kotlinx.serialization.Serializable

@Serializable
data class FriendRequest(
    val id: String,              // ID запроса
    val senderId: String,        // ID отправителя
    val senderLogin: String,     // Логин отправителя (добавьте это поле!)
    val receiverId: String,      // ID получателя
    val status: String           // "PENDING", "ACCEPTED", "REJECTED"
)

@Serializable
data class FriendResponse(
    val success: Boolean,
    val message: String,
    val friends: List<Friend> = emptyList(),
    val pendingRequests: List<FriendRequest> = emptyList()
)

@Serializable
data class Friend(
    val id: String,
    val login: String,
    val firstName: String,
    val lastName: String,
    val email: String
)