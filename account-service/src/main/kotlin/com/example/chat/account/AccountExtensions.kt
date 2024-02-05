package com.example.chat.account

import com.example.chat.security.common.SecurityProperties
import org.keycloak.representations.idm.UserRepresentation


internal const val VERIFY_EMAIL_ACTION = "VERIFY_EMAIL"
internal const val RESET_PASSWORD_ACTION = "UPDATE_PASSWORD"
internal const val CONFIGURE_MFA_ACTION = "CONFIGURE_TOTP"

fun AccountRegistration.toRepresentation(
    tokenAttributes: SecurityProperties.TokenAttributeProperties
): UserRepresentation =
    let {
        UserRepresentation()
            .apply {
                username = it.email
                email = it.email
                firstName = it.firstName
                lastName = it.lastName
                isEnabled = true
                attributes =
                    mapOf(
                        tokenAttributes.middleName.tokenKey to listOfNotNull(it.middleName),
                        tokenAttributes.nickName.tokenKey to listOf(it.nickName)
                    )
                requiredActions = listOf(VERIFY_EMAIL_ACTION)
            }
    }

fun ChatRegistration.toRepresentation(
    tokenAttributes: SecurityProperties.TokenAttributeProperties
): UserRepresentation =
    let {
        UserRepresentation()
            .apply {
                username = it.username
                if (it.email != null) {
                    email = it.email
                }
                isEnabled = true
                attributes = mapOf(
                    tokenAttributes.chatUserId.tokenKey to listOf(it.chatUserId)
                )
            }
    }