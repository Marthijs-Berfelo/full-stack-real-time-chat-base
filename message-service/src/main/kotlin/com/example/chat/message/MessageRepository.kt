package com.example.chat.message

import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface MessageRepository : CoroutineCrudRepository<MessageDocument, String> {

    fun findAllByConversationId(conversationId: String): Flow<MessageDocument>

    suspend fun deleteAllByConversationIdIn(conversationIds: Flow<String>)

}