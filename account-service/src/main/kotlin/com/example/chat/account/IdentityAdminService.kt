package com.example.chat.account

import com.example.chat.config.AdminConfiguration
import com.example.chat.common.generatePassword
import com.example.chat.security.common.SecurityProperties
import com.example.chat.security.common.UserRole
import jakarta.validation.Valid
import org.keycloak.admin.client.resource.UsersResource
import org.keycloak.authorization.client.AuthzClient
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.validation.annotation.Validated
import javax.annotation.PostConstruct

@Validated
@Service
class IdentityAdminService(
    adminProperties: AdminConfiguration,
    internal val securityProperties: SecurityProperties,
    internal val usersResource: UsersResource,
) {
    internal val log: Logger by lazy { LoggerFactory.getLogger(this::class.java) }
    internal val client = adminProperties.adminClient
    internal val realmName = adminProperties.realmName
    internal val requiredRoles = adminProperties.requiredRoles
    internal val authClientConfig = adminProperties.authzClientConfig
    internal lateinit var authClient: AuthzClient


    @PostConstruct
    fun init() {
        ensureRealmExists()
            .ensureClientScopeExists(this)
            .ensureRolesExist(this)
            .ensureAccountClientExists(this)
        authClient = AuthzClient.create(authClientConfig)
    }

    fun registerAccount(@Valid registration: AccountRegistration): Result<Unit> =
        runCatching {
            registration
                .toRepresentation(securityProperties.tokenAttributes)
                .let(::createUser)
                .let { ensureUserInRole(it, UserRole.REGISTERED) }
                .let { registerPassword(it, registration) }
                .getOrThrow()
        }

    fun registerChatUser(@Valid registration: ChatAccountRegistration): Result<AuthTokens> =
        runCatching {
            registration
                .toRepresentation(securityProperties.tokenAttributes)
                .let(::createUser)
                .let { ensureUserInRole(it, UserRole.ANONYMOUS) }
                .let { it to generatePassword(16) }
                .let { (userId, password) ->
                    setUserPassword(userId = userId, password = password)
                        .getOrThrow()
                        .let { password }
                }
                .let { login(username = registration.nickName, password = it) }
                .getOrThrow()
        }

    fun addChatUser(userId: String, chatUserId: String): Result<Unit> =
        runCatching {
            usersResource[userId]
                .let { userResource ->
                    userResource
                        .toRepresentation()
                        .apply {
                            if (attributes == null) {
                                attributes = mapOf(securityProperties.tokenAttributes.chatUserId.tokenKey to listOf(chatUserId))
                            } else
                                attributes[securityProperties.tokenAttributes.chatUserId.tokenKey] = listOf(chatUserId)
                        }
                        .let { userResource.update(it) }
                }
        }

    fun removeChatUser(userId: String, chatUserId: String): Result<Unit> =
        runCatching {
            usersResource[userId]
                .also { log.atInfo().log { "Removing chat user $chatUserId for user: $userId" } }
                .let { user ->
                    if (user.toRepresentation().isRegisteredUser()) {
                        user.toRepresentation()
                            .apply {
                                attributes
                                    ?.remove(securityProperties.tokenAttributes.chatUserId.tokenKey)
                            }
                            .let { user.update(it) }
                            .also { log.atDebug().log { "Removed chat user $chatUserId for registered user: $userId" } }
                    } else if (user.toRepresentation().isAnonymousUser()) {
                        user.remove()
                            .also { log.atDebug().log { "Removed anonymous user: $chatUserId" } }
                    } else {
                        log.atWarn().log { "Unable to remove chat user $chatUserId for user $userId! No valid role assigned" }
                    }
                }
        }

}

