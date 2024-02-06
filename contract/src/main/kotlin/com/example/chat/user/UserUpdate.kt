package com.example.chat.user

import java.time.Instant

data class UserUpdate(
    val type: UpdateType,
    val user: ChatUser,
    val changedAt: Instant
)

enum class UpdateType {
    CHANGED,
    DELETED
}
