package com.example.chat.user

import com.example.chat.security.web.EnableWebSecurity
import com.example.chat.security.web.oauthPrincipal
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
class UserRouterHandler(private val service: UserService) {

    companion object {
        const val NICK_NAME_PARAM = "nick-name"
    }

    suspend fun registerUser(request: ServerRequest): ServerResponse =
        request
            .awaitBody(ChatRegistration::class)
            .let { service.register(it) }
            .getOrThrow()
            .let { ok().bodyValueAndAwait(it) }

    suspend fun checkNickNameUnique(request: ServerRequest): ServerResponse =
        request
            .queryParamOrNull(NICK_NAME_PARAM)
            ?.let { service.checkNickNameUnique(it) }
            ?.getOrThrow()
            ?.let { noContent().buildAndAwait() }
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "No value found for query parameter: $NICK_NAME_PARAM")

    @PreAuthorize("isAuthenticated()")
    suspend fun getUsers(request: ServerRequest): ServerResponse =
        service
            .getUsers()
            .let { ok().bodyAndAwait(it) }

    @PreAuthorize("isAuthenticated()")
    suspend fun deleteUser(request: ServerRequest): ServerResponse =
        request.oauthPrincipal()
            .chatUserId
            ?.let { service.remove(it) }
            ?.getOrThrow()
            .let { noContent().buildAndAwait() }

}

@Configuration
@EnableWebSecurity
class WebRoutingConfig {
    companion object {
        private const val BASE_PATH = "/api/users"
        private const val VALIDATION_PATH = "/validate"
    }

    @Bean
    fun userRoutes(handler: UserRouterHandler): RouterFunction<ServerResponse> =
        coRouter {
            GET(BASE_PATH, handler::getUsers)
            POST(BASE_PATH, handler::registerUser)
            DELETE(BASE_PATH, handler::deleteUser)
            BASE_PATH.nest {
                GET(VALIDATION_PATH, handler::checkNickNameUnique)
            }
        }
}