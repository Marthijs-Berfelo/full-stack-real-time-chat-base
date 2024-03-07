package com.example.chat.message

import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import java.time.Instant

interface MessageRepository : CoroutineCrudRepository<MessageDocument, String> {

    fun findAllByConversationId(conversationId: String): Flow<MessageDocument>

    fun findAllByToOrderBySentAtAsc(to: String): Flow<MessageDocument>
    fun findAllByToAndSentAtGreaterThanOrderBySentAtAsc(to: String, sentAfter: Instant): Flow<MessageDocument>

    suspend fun deleteAllByConversationIdIn(conversationIds: Flow<String>)

}