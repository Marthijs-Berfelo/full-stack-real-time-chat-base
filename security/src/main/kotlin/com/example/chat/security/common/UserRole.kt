package com.example.chat.security.common

enum class UserRole(val roleName: String, val description: String) {
    REGISTERED("REGISTERED", "Role for users with an account"),
    ANONYMOUS("ANONYMOUS", "Role for users without an account")
}