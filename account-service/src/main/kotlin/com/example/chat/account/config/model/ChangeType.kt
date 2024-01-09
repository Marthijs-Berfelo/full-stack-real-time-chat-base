package com.example.chat.account.config.model

enum class ChangeType {
    CREATE,
    UPDATE,
    DELETE,
    INVALID
}

fun Any?.toChangeType(next: Any? = null): ChangeType =
    when {
        next?.let { this == null } ?: false -> ChangeType.CREATE
        next?.let { false } ?: (this != null) -> ChangeType.DELETE
        this?.let { next } != null -> ChangeType.UPDATE
        else -> ChangeType.INVALID
    }