package com.example.chat.message

import com.example.chat.mesage.ChatMessage
import com.example.chat.mesage.Conversation
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document(collection = "message")
data class MessageDocument(
    @Id
    val id: String = "",
    val from: String,
    val to: String,
    val conversationId: String,
    val sentAt: Instant,
    val message: String
)

@Document(collection = "conversation")
data class ConversationDocument(
    @Id
    val id: String = "",
    val users: List<String>
)

fun ChatMessage.toDocument(): MessageDocument =
    MessageDocument(
        id = id,
        from = from,
        to = to,
        conversationId = conversationId,
        sentAt = sentAt,
        message = message
    )

fun MessageDocument.toMessage(): ChatMessage =
    ChatMessage(
        id = id,
        from = from,
        to = to,
        conversationId = conversationId,
        sentAt = sentAt,
        message = message
    )

fun MessageDocument.toMessage(id: String): ChatMessage =
    ChatMessage(
        id = id,
        from = from,
        to = to,
        conversationId = conversationId,
        sentAt = sentAt,
        message = message
    )

fun Conversation.toDocument(): ConversationDocument =
    ConversationDocument(
        id = id,
        users = users
    )

fun ConversationDocument.toConversation(messages: List<ChatMessage> = emptyList()) : Conversation =
    Conversation(
        id = id,
        users = users,
        messages = messages
    )

fun ConversationDocument.removeUser(userId: String) =
    copy(users = users.filterNot { it == userId })
