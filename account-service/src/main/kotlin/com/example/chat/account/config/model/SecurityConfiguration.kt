package com.example.chat.account.config.model

import jakarta.validation.Valid
import org.springframework.validation.annotation.Validated

@Validated
data class SecurityConfiguration(
    @field:Valid
    val realm: SecurityRealm
)
