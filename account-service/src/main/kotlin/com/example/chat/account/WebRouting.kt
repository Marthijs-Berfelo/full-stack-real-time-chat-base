package com.example.chat.account

import com.example.chat.account.WebHandler.Companion.CHAT_USER_ID_PARAM
import com.example.chat.security.web.EnableWebSecurity
import com.example.chat.security.web.chatUserId
import com.example.chat.security.web.oauthPrincipal
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.function.server.ServerResponse.noContent
import org.springframework.web.reactive.function.server.ServerResponse.ok

@Component
class WebHandler(private val service: IdentityAdminService) {
    companion object {
        const val CHAT_USER_ID_PARAM = "chatUserId"
    }

    suspend fun registerAccount(request: ServerRequest): ServerResponse =
        request
            .awaitBody(AccountRegistration::class)
            .let { service.registerAccount(it) }
            .getOrThrow()
            .let { noContent().buildAndAwait() }

    suspend fun registerChatUser(request: ServerRequest): ServerResponse =
        request
            .awaitBody(ChatAccountRegistration::class)
            .let { service.registerChatUser(it) }
            .getOrThrow()
            .let { ok().bodyValueAndAwait(it) }

    @PreAuthorize("isAuthenticated()")
    suspend fun addChatUser(request: ServerRequest): ServerResponse =
        (request.oauthPrincipal() to request.pathVariable(CHAT_USER_ID_PARAM))
            .let { (principal, chatUserId) -> service.addChatUser(userId = principal.userId, chatUserId = chatUserId) }
            .getOrThrow()
            .let { noContent().buildAndAwait() }


    @PreAuthorize("isAuthenticated()")
    suspend fun removeChatUser(request: ServerRequest): ServerResponse =
        (request.oauthPrincipal() to request.chatUserId())
            .let { (principal, chatUserId) -> service.removeChatUser(userId = principal.userId, chatUserId = chatUserId) }
            .getOrThrow()
            .let { noContent().buildAndAwait() }

}

@Configuration
@EnableWebSecurity
class WebRoutingConfig {

    companion object {
        const val BASE_PATH = "/api/account"
        const val CHAT_PATH = "/chat"
        const val CHAT_USER_ID_PATH = "/{$CHAT_USER_ID_PARAM}"
    }

    @Bean
    fun accountRoutes(handler: WebHandler): RouterFunction<ServerResponse> =
        coRouter {
            POST(BASE_PATH, handler::registerAccount)
            BASE_PATH.nest {
                POST(CHAT_PATH, handler::registerChatUser)
                DELETE(CHAT_PATH, handler::removeChatUser)
                CHAT_PATH.nest {
                    PUT(CHAT_USER_ID_PATH, handler::addChatUser)
                }
            }
        }
}