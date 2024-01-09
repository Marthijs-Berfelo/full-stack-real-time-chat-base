package com.example.chat.mesage

data class Conversation(
    val id: String,
    val users: List<String>,
    val messages: List<ChatMessage>,
)
