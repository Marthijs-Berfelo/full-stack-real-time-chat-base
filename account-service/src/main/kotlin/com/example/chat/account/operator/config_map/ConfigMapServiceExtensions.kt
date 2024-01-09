package com.example.chat.account.operator.config_map

import com.example.chat.account.config.model.*
import com.example.chat.account.ensureRealmExists
import com.example.chat.account.ensureDefaultClientScopes
import com.example.chat.account.existingOrNewClientScope
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

internal fun ConfigMapService.validate(change: SecurityChange): SecurityChange =
    validator
        .validate(change)
        .takeIf { it.isNotEmpty() }
        ?.sortedBy { it.propertyPath.toString() }
        ?.joinToString { "${it.propertyPath}: ${it.message}" }
        ?.let { throw ResponseStatusException(HttpStatus.BAD_REQUEST, it) }
        ?: change


private fun SecurityChange.isForRealm(realm: String): Boolean =
    previous?.realm?.name?.equals(realm) ?: true || next?.realm?.name?.equals(realm) ?: true

internal fun ConfigMapService.managedRealm(change: SecurityChange): SecurityChange? =
    change
        .takeIf { it.isForRealm(adminProperties.realm) }
        ?.also { log.atDebug().log { "Change found for managed realm: ${adminProperties.realm}" } }
        ?: log.atInfo().log { "Change skipped, not for managed realm: ${adminProperties.realm}" }
            .let { null }

internal fun ConfigMapService.updateRealm(change: SecurityChange) {
    when {
        change.isNew() || change.isUpdate() -> adminService.ensureRealmExists()
        change.isRemoved() -> log.atWarn().log { "Security configuration removed, realm not removed" }
    }
}

internal fun ConfigMapService.updateClientScopes(change: SecurityChange) {
    log.atWarn().log { "Updating roles not implemented" }
//    val realm = adminService.ensureRealmExists()
//    realm.updateScopes(adminService, change.scopesToRemove())
//    realm.createScopes(adminService, change.scopesToCreate())
//    realm.updateScopes(adminService, change.scopesToUpdate())
}

internal fun ConfigMapService.updateRoles(change: SecurityChange) = log.atWarn().log { "Updating roles not implemented" }
internal fun ConfigMapService.updateClients(change: SecurityChange) = log.atWarn().log { "Updating clients not implemented" }