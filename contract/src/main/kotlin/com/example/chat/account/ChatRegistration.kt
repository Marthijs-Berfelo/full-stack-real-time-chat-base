package com.example.chat.account

data class ChatRegistration(
    val username: String,
    val chatUserId: String,
    val email: String? = null
)
