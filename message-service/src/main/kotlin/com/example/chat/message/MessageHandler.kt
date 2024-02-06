package com.example.chat.message

import com.example.chat.mesage.ChatMessage
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.bson.Document
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.data.mongodb.core.mapping.event.AfterSaveCallback
import org.springframework.stereotype.Component

@Component
class MessageHandler : AfterSaveCallback<MessageDocument> {

    private val log: Logger by lazy { LoggerFactory.getLogger(this::class.java) }
    private val messagePublisher = MutableSharedFlow<ChatMessage>()

    override fun onAfterSave(entity: MessageDocument, document: Document, collection: String): MessageDocument =
        entity
            .also {
                it
                    .toMessage()
                    .let(messagePublisher::tryEmit)
                    .also { result -> log.atDebug().log { "New message ($it) saved and published: $result" } }
            }

    @Bean
    fun publisher(): SharedFlow<ChatMessage> =
        messagePublisher.asSharedFlow()
}