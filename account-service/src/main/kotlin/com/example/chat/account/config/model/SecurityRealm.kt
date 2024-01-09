package com.example.chat.account.config.model

import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import org.springframework.validation.annotation.Validated

@Validated
data class SecurityRealm(
    @field:NotBlank
    val name: String,
    @field:Valid
    val scopes: List<SecurityClientScope>? = null,
    val roles: List<@NotBlank String>? = null,
    @field:Valid
    val clients: List<SecurityClient>? = null
)