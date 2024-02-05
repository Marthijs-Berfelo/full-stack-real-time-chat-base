package com.example.chat.account

data class AuthTokens(
    val accessToken: String,
    val expiresIn: Long,
    val refreshToken: String,
    val refreshExpiresIn: Long,
    val tokenType: String,
    val notBefore: Int,
    val scope: String,
    val sessionState: String
)
