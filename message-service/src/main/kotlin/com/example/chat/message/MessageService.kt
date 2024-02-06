package com.example.chat.message

import com.example.chat.mesage.ChatMessage
import com.example.chat.mesage.Conversation
import jakarta.validation.Valid
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.filter
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.validation.annotation.Validated

@Validated
@Service
class MessageService(
    internal val messageRepo: MessageRepository,
    internal val conversationRepo: ConversationRepository,
    private val publisher: SharedFlow<ChatMessage>
) {

    internal val log: Logger by lazy { LoggerFactory.getLogger(this::class.java) }

    suspend fun initializeConversation(userId: String, peerUserId: String): Result<Conversation> =
        runCatching {
            listOf(userId, peerUserId)
                .also { log.atInfo().log { "Initialize conversation between users: $it" } }
                .let {
                    findExistingConversation(userId, it)
                        ?: createConversation(it)
                }
                .toConversation()
        }
            .onFailure {
                log.atWarn().setCause(it)
                    .log { "Failed to initialize conversation between users: ${listOf(userId, peerUserId)}" }
            }

    suspend fun receive(@Valid message: ChatMessage): Result<ChatMessage> =
        runCatching {
            message
                .also { log.atInfo().log { "Receiving new message: $it" } }
                .toDocument()
                .let { messageRepo.save(it) }
                .toMessage()
                .also { log.atDebug().log { "Processed new message: $it" } }
        }
            .onFailure { log.atWarn().setCause(it).log { "Failed to receive message: $message" } }

    fun messages(userId: String): Flow<ChatMessage> =
        publisher
            .filter { it.to == userId }

    suspend fun cleanUp(userId: String): Result<Unit> =
        runCatching {
            conversationRepo
                .also { log.atInfo().log { "Cleaning up conversations for user: $userId" } }
                .getAllByUsersContains(userId)
                .let { removeUserReference(userId, it) }
                .also { removeMessages(it) }
                .let { conversationRepo.deleteAll(it) }
                .also { log.atDebug().log { "Conversations cleaned up for user: $userId" } }
        }
            .onFailure { log.atWarn().setCause(it).log { "Failed to clean up conversations for user: $userId" } }

}

