package com.example.chat.account.config.model

import com.example.chat.security.common.TokenMapperProperties
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import org.springframework.validation.annotation.Validated

@Validated
data class SecurityClientScope(
    @field:NotBlank
    val name: String,
    val description: String?,
    val default: Boolean = true,
    val mappers: List<@Valid TokenMapperProperties>? = null
)