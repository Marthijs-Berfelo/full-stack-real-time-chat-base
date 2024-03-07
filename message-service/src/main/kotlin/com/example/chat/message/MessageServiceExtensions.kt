package com.example.chat.message

import com.example.chat.mesage.Conversation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.time.Instant

internal fun MessageService.findMessages(chatUserId: String, sentAfter: Instant? = null): Flow<MessageDocument> =
    sentAfter
        ?.let { messageRepo.findAllByToAndSentAtGreaterThanOrderBySentAtAsc(chatUserId, it) }
        ?: messageRepo.findAllByToOrderBySentAtAsc(chatUserId)

internal suspend fun MessageService.findExistingConversation(userId: String, users: List<String>): ConversationDocument? =
    conversationRepo.getAllByUsersContains(userId)
        .firstOrNull { it.users.containsAll(users) }
        ?.also { log.atDebug().log { "Found conversation $it" } }

internal suspend fun MessageService.createConversation(users: List<String>): ConversationDocument =
    Conversation(id = "", users = users)
        .also { log.atDebug().log { "Creating conversation: $it" } }
        .toDocument()
        .let { conversationRepo.save(it) }

internal fun MessageService.removeUserReference(
    userId: String,
    conversations: Flow<ConversationDocument>
): Flow<ConversationDocument> =
    runCatching {
        conversations
            .also { log.atDebug().log { "Removing user $userId from conversations" } }
            .filter { it.users.contains(userId) }
            .map { it.removeUser(userId) }
            .let { conversationRepo.saveAll(it) }
            .let { conversations }
            .filter { it.users.size == 1 }
    }
        .onFailure { log.atWarn().setCause(it).log { "Failed to remove user: $userId from conversations" } }
        .getOrThrow()

internal suspend fun MessageService.removeMessages(
    conversations: Flow<ConversationDocument>
): Unit =
    runCatching {
        conversations
            .also { log.atDebug().log { "Removing messages for orphaned conversations" } }
            .map { it.id }
            .let { messageRepo.deleteAllByConversationIdIn(it) }
    }
        .onFailure { log.atWarn().setCause(it).log { "Failed to remove messages for orphaned conversations" } }
        .getOrThrow()