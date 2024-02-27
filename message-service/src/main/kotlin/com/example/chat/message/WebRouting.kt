package com.example.chat.message

import com.example.chat.mesage.ChatMessage
import com.example.chat.mesage.ConversationRegistration
import com.example.chat.message.MessageRouterHandler.Companion.CONVERSATION_ID_PARAM
import com.example.chat.security.web.EnableWebSecurity
import com.example.chat.security.web.chatUserId
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.function.server.ServerResponse.noContent
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.server.ResponseStatusException

@Component
class MessageRouterHandler(private val service: MessageService) {

    companion object {
        const val CONVERSATION_ID_PARAM = "conversationId"
    }

    @PreAuthorize("isAuthenticated()")
    suspend fun initializeConversation(request: ServerRequest): ServerResponse =
        (request.chatUserId() to request.awaitBody(ConversationRegistration::class))
            .let { (chatUserId, registration) -> service.initializeConversation(chatUserId, registration.peerUserId) }
            .getOrThrow()
            .let { ok().bodyValueAndAwait(it) }

    @PreAuthorize("isAuthenticated()")
    suspend fun receiveMessage(request: ServerRequest): ServerResponse =
        request
            .authorizedMessage()
            .let { service.receive(it) }
            .getOrThrow()
            .let { ok().bodyValueAndAwait(it) }

    @PreAuthorize("isAuthenticated()")
    suspend fun getMessages(request: ServerRequest): ServerResponse =
        request
            .chatUserId()
            .let { service.getMessages(it) }
            .let { ok().bodyAndAwait(it) }

    @PreAuthorize("isAuthenticated()")
    suspend fun cleanUp(request: ServerRequest): ServerResponse =
        request
            .chatUserId()
            .let { service.cleanUp(it) }
            .getOrThrow()
            .let { noContent().buildAndAwait() }
}

@Configuration
@EnableWebSecurity
class WebRoutingConfig {
    companion object {
        private const val BASE_PATH = "/api"
        private const val CONVERSATIONS_PATH = "/conversations"
        private const val CONVERSATION_ID_PATH = "/{$CONVERSATION_ID_PARAM}"
        private const val MESSAGES_PATH = "/messages"
    }

    @Bean
    fun messageRoutes(handler: MessageRouterHandler): RouterFunction<ServerResponse> =
        coRouter {
            BASE_PATH.nest {
                POST(CONVERSATIONS_PATH, handler::initializeConversation)
                DELETE(CONVERSATIONS_PATH, handler::cleanUp)
                GET(MESSAGES_PATH, handler::getMessages)
                CONVERSATIONS_PATH.nest {
                    POST(CONVERSATION_ID_PATH, handler::receiveMessage)

                }
            }
        }
}

private suspend fun ServerRequest.authorizedMessage(): ChatMessage =
    (chatUserId() to awaitBody(ChatMessage::class))
        .authorize()
        .let(::validate)

private fun Pair<String, ChatMessage>.authorize(): ChatMessage =
    takeIf { (chatUserId, message) -> message.from == chatUserId }
        ?.second
        ?: throw ResponseStatusException(HttpStatus.FORBIDDEN)

private fun ServerRequest.validate(message: ChatMessage): ChatMessage =
    message
        .takeIf { pathVariable(CONVERSATION_ID_PARAM) == it.conversationId }
        ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Body value must match path variable for '$CONVERSATION_ID_PARAM'")