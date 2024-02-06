package com.example.chat.user

import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface UserRepository : CoroutineCrudRepository<UserDocument, String> {
    suspend fun findByNickName(nickName: String): UserDocument?


}