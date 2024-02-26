package com.example.chat.security.web

import com.example.chat.security.common.DefaultOAuth2Principal
import org.springframework.http.HttpStatus
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.awaitPrincipal
import org.springframework.web.server.ResponseStatusException

suspend fun ServerRequest.oauthPrincipal(): DefaultOAuth2Principal =
    awaitPrincipal()
        .let { it as BearerTokenAuthentication }
        .let { it.principal as DefaultOAuth2Principal }

suspend fun ServerRequest.chatUserId(): String =
    oauthPrincipal()
        .chatUserId
        ?: throw ResponseStatusException(HttpStatus.FORBIDDEN)