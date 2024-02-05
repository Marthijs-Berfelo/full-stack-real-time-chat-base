package com.example.chat.account

import com.example.chat.security.common.UserRole
import org.keycloak.representations.idm.RoleRepresentation

fun UserRole.equalsRole(role: RoleRepresentation): Boolean =
    roleName.equals(role.name, true) &&
            description.equals(role.description, false)

fun RoleRepresentation.equalsRole(userRole: UserRole): Boolean =
    userRole.equalsRole(this)

fun UserRole.toRepresentation(): RoleRepresentation =
    RoleRepresentation(roleName, description, false)
