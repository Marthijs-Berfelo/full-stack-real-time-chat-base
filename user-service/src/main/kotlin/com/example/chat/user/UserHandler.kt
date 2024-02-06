package com.example.chat.user

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.event.EventListener
import org.springframework.data.mongodb.core.mapping.event.AfterDeleteEvent
import org.springframework.data.mongodb.core.mapping.event.AfterSaveEvent
import org.springframework.stereotype.Component

@Component
class UserHandler {

    private val log: Logger by lazy { LoggerFactory.getLogger(this::class.java) }
    private val updatePublisher = MutableSharedFlow<UserUpdate>()

    @EventListener
    fun handleUserSaved(event: AfterSaveEvent<UserDocument>) {
        event
            .toUserUpdate()
            .let(updatePublisher::tryEmit)
            .let { log.atDebug().log { "User change ($it) saved and published" } }
    }

    @EventListener
    fun handleUserDeleted(event: AfterDeleteEvent<UserDocument>) {
        event
            .toUserUpdate()
            .let(updatePublisher::tryEmit)
            .let { log.atDebug().log { "User removal ($it) saved and published" } }
    }

    @Bean
    fun publisher(): SharedFlow<UserUpdate> =
        updatePublisher.asSharedFlow()
}