package com.example.chat.message

import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository

interface ConversationRepository : CoroutineCrudRepository<ConversationDocument, String> {

    fun getAllByUsersContains(userId :String): Flow<ConversationDocument>

}