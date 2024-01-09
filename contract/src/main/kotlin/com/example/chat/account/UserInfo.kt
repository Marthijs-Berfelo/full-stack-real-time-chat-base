package com.example.chat.account

data class UserInfo(
    val userId: String,
    val username: String,
    val firstName: String?,
    val middleName: String?,
    val lastName: String?,
    val nickName: String?,
    val email: String?,
    val authorities: List<String>
)