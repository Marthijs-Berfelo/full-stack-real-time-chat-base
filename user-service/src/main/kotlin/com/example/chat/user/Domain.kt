package com.example.chat.user

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.event.AfterDeleteEvent
import org.springframework.data.mongodb.core.mapping.event.AfterSaveEvent
import java.time.Instant

@Document("user")
data class UserDocument(
    @Id
    val id: String = "",
    val nickName: String,
    val email: String?,
    val statusMessage: String?,
    val picture: String?
)

fun ChatRegistration.toDocument(): UserDocument =
    UserDocument(
        nickName = nickName,
        email = email,
        statusMessage = statusMessage,
        picture = picture
    )

fun UserDocument.toUser(): ChatUser =
    ChatUser(
        id = id,
        nickName = nickName,
        statusMessage = statusMessage,
        picture = picture
    )

fun AfterSaveEvent<UserDocument>.toUserUpdate(): UserUpdate =
    source
        .toUser()
        .let {
            UserUpdate(
                type = UpdateType.CHANGED,
                changedAt = Instant.ofEpochMilli(timestamp),
                user = it
            )
        }

fun AfterDeleteEvent<UserDocument>.toUserUpdate(): UserUpdate =
    source
        .toJson()
        .let { jacksonObjectMapper().readValue<UserDocument>(it) }
        .toUser()
        .let {
            UserUpdate(
                type = UpdateType.CHANGED,
                changedAt = Instant.ofEpochMilli(timestamp),
                user = it
            )
        }