package com.example.z.model.requests

import kotlinx.serialization.Serializable

@Serializable
data class FriendRequest(
    val id: String,              // ID запроса
    val senderId: String,        // ID отправителя
    val senderLogin: String,     // Логин отправителя (добавьте это поле!)
    val receiverId: String,      // ID получателя
    val status: String           // "PENDING", "ACCEPTED", "REJECTED"
)
