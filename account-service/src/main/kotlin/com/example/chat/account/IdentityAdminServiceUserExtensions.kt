package com.example.chat.account

import com.example.chat.security.common.UserRole
import jakarta.ws.rs.core.Response
import org.keycloak.representations.idm.CredentialRepresentation
import org.keycloak.representations.idm.RoleRepresentation
import org.keycloak.representations.idm.UserRepresentation
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

internal fun IdentityAdminService.createUser(user: UserRepresentation): String =
    runCatching {
        usersResource
            .also { log.atInfo().log { "Creating user: ${user.email}" } }
            .create(user)
            .let(::checkSuccess)
            .location
            .path
            .split("/")
            .last()
            .also {
                usersResource[it]
                    ?.executeActionsEmail(user.requiredActions)
            }
            .also { log.atDebug().log { "Created user (${user.email}) with id: $it" } }
}
        .onFailure { log.atWarn().setCause(it).log { "Failed to create user: ${user.email}" } }
        .getOrThrow()

internal fun IdentityAdminService.registerPassword(userId: String, registration: AccountRegistration): Result<Unit> =
    setUserPassword(userId = userId, password = registration.password)

internal fun IdentityAdminService.setUserPassword(userId: String, password: String): Result<Unit> =
    runCatching {
        CredentialRepresentation()
            .also { log.atInfo().log { "Setting password for user: $userId" } }
            .apply {
                type = CredentialRepresentation.PASSWORD
                isTemporary = false
                value = password
            }
            .let { usersResource[userId].resetPassword(it) }
            .also { log.atDebug().log { "Password reset for user: $userId" } }
    }
        .onFailure { log.atWarn().setCause(it).log { "Failed to reset password for user: $userId" } }

internal fun IdentityAdminService.ensureUserInRole(userId: String, role: UserRole): String =
    runCatching {
        userId
            .also { log.atInfo().log { "Ensuring user $it has role: ${role.roleName}" } }
            .also {
                usersResource[it]
                    ?.roles()
                    ?.realmLevel()
                    ?.also { realmRoles -> realmRoles
                        .listEffective()
                        .takeUnless(MutableList<RoleRepresentation>::isEmpty)
                        ?.let { roles -> realmRoles.remove(roles) }
                        ?.also { log.atDebug().log { "Removed assigned roles from user: $userId" } }
                    }
                    ?.add(listOf(role.toRepresentation()))
            }
    }
        .onSuccess { log.atDebug().log { "User $it has role: ${role.roleName}" } }
        .onFailure { log.atWarn().setCause(it).log { "Failed to add role ${role.roleName} to user $userId" } }
        .getOrThrow()

internal fun UserRepresentation.isAnonymousUser(): Boolean =
    realmRoles.contains(UserRole.ANONYMOUS.roleName)

internal fun UserRepresentation.isRegisteredUser(): Boolean =
    realmRoles.contains(UserRole.REGISTERED.roleName)

private fun checkSuccess(response: Response) =
    if (response.statusInfo.family == Response.Status.Family.SUCCESSFUL)
        response
    else
        throw ResponseStatusException(HttpStatus.valueOf(response.status), response.statusInfo.reasonPhrase)
