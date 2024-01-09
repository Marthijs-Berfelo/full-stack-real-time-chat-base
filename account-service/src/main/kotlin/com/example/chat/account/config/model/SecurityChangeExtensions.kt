package com.example.chat.account.config.model

fun SecurityChange.isNew(): Boolean =
    next?.let { previous == null } ?: false

fun SecurityChange.isUpdate(): Boolean =
    next?.let { previous != null } ?: false

fun SecurityChange.isRemoved(): Boolean =
    next?.let { false } ?: (previous != null)


fun SecurityChange.scopesToCreate(): List<SecurityClientScope>? =
    next?.realm?.scopes?.minus((previous?.realm?.scopes ?: emptyList()).toSet())

fun SecurityChange.scopesToUpdate(): List<SecurityClientScope>? =
    next?.realm?.scopes?.filter { scope -> previous?.realm?.scopes?.any { it.name == scope.name } ?: false }

fun SecurityChange.scopesToRemove(): List<SecurityClientScope>? =
    previous?.realm?.scopes?.filter { scope -> next?.realm?.scopes?.none { it.name == scope.name } ?: false }