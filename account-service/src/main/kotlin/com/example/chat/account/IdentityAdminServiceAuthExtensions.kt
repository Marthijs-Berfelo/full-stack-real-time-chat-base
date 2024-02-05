package com.example.chat.account

import org.keycloak.representations.idm.authorization.AuthorizationResponse

internal fun IdentityAdminService.login(username: String, password: String): Result<AuthTokens> =
    runCatching {
        usersResource
            .search(username)
            .first()
            .let { authClient.authorization(username, password) }
            .authorize()
            .toTokens()
    }

private fun AuthorizationResponse.toTokens(): AuthTokens =
    AuthTokens(
        accessToken = token,
        tokenType = tokenType,
        refreshToken = refreshToken,
        expiresIn = expiresIn,
        notBefore = notBeforePolicy,
        scope = scope,
        sessionState = sessionState,
        refreshExpiresIn = refreshExpiresIn
    )