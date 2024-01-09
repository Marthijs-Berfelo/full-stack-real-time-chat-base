package com.example.chat.user

data class ChatUser(
    val id: String,
    val nickName: String,
    val statusMessage: String?,
    val picture: String?
)
