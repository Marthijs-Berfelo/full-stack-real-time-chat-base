package com.example.chat.message

import com.example.chat.mesage.ChatMessage
import com.example.chat.mesage.Conversation
import jakarta.validation.Valid
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.validation.annotation.Validated
import java.time.Instant

@Validated
@Service
class MessageService(
    internal val messageRepo: MessageRepository,
    internal val conversationRepo: ConversationRepository,
    private val publisher: SharedFlow<ChatMessage>
) {

    internal val log: Logger by lazy { LoggerFactory.getLogger(this::class.java) }

    suspend fun initializeConversation(chatUserId: String, peerUserId: String): Result<Conversation> =
        runCatching {
            listOf(chatUserId, peerUserId)
                .also { log.atInfo().log { "Initialize conversation between users: $it" } }
                .let {
                    findExistingConversation(chatUserId, it)
                        ?: createConversation(it)
                }
                .toConversation()
        }
            .onFailure {
                log.atWarn().setCause(it)
                    .log { "Failed to initialize conversation between users: ${listOf(chatUserId, peerUserId)}" }
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

    fun getMessages(chatUserId: String, sentAfter: Instant? = null): Flow<ChatMessage> =
        findMessages(chatUserId, sentAfter)
            .map(MessageDocument::toMessage)

    fun messageUpdates(chatUserId: String): Flow<ChatMessage> =
        publisher
            .filter { it.to == chatUserId }

    suspend fun cleanUp(chatUserId: String): Result<Unit> =
        runCatching {
            conversationRepo
                .also { log.atInfo().log { "Cleaning up conversations for user: $chatUserId" } }
                .getAllByUsersContains(chatUserId)
                .let { removeUserReference(chatUserId, it) }
                .also { removeMessages(it) }
                .let { conversationRepo.deleteAll(it) }
                .also { log.atDebug().log { "Conversations cleaned up for user: $chatUserId" } }
        }
            .onFailure { log.atWarn().setCause(it).log { "Failed to clean up conversations for user: $chatUserId" } }

}

