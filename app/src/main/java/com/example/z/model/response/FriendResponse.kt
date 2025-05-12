package com.example.z.model.response

import com.example.z.model.FriendModel
import com.example.z.model.requests.FriendRequest
import kotlinx.serialization.Serializable

@Serializable
data class FriendResponse(
    val success: Boolean,
    val message: String,
    val friends: List<FriendModel> = emptyList(),
    val pendingRequests: List<FriendRequest> = emptyList()
)
