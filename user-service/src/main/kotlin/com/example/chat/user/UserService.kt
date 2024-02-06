package com.example.chat.user

import jakarta.validation.Valid
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.map
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.validation.annotation.Validated
import org.springframework.web.server.ResponseStatusException

@Validated
@Service
class UserService(
    private val repo: UserRepository,
    private val publisher: SharedFlow<UserUpdate>
) {
    private val log: Logger by lazy { LoggerFactory.getLogger(this::class.java) }

    suspend fun register(@Valid registration: ChatRegistration): Result<ChatUser> =
        runCatching {
            registration
                .also { log.atInfo().log { "Register user: $it" } }
                .toDocument()
                .let { repo.save(it) }
                .toUser()
                .also { log.atDebug().log { "User registered: $it" } }
        }
            .onFailure { log.atWarn().setCause(it).log { "Failed to register user: $registration" } }

    suspend fun checkNickNameUnique(nickName: String): Result<Unit> =
        runCatching {
            nickName
                .also { log.atInfo().log { "Checking if nickname is already in use: $it" } }
                .let { repo.findByNickName(it) }
                ?.also { log.atDebug().log { "Found user with nickname: $it" } }
                ?.let { throw ResponseStatusException(HttpStatus.CONFLICT, "Nick name ($nickName) already used") }
                ?: log.atDebug().log { "No user found with nick name: $nickName" }
        }
            .onFailure { log.atWarn().setCause(it).log { "User name check resulted in failure: ${it.message}" } }

    suspend fun getUsers(): Flow<ChatUser> =
        repo
            .also { log.atInfo().log { "Get all users" } }
            .findAll()
            .map(UserDocument::toUser)

    suspend fun userUpdates(): Flow<UserUpdate> =
        publisher

    suspend fun remove(chatUserId: String): Result<Unit> =
        runCatching {
            chatUserId
                .also { log.atInfo().log { "Removing user: $it" } }
                .let { repo.findById(it) }
                ?.let { repo.delete(it) }
                ?: log.atDebug().log { "Removed user: $chatUserId" }
        }
            .onFailure { log.atWarn().setCause(it).log { "Failed to remove user: $chatUserId" } }
}