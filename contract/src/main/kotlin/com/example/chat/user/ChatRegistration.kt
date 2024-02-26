package com.example.chat.user

import com.example.chat.VALID_EMAIL
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Max
import org.hibernate.validator.constraints.Length
import org.springframework.validation.annotation.Validated

@Validated
data class ChatRegistration(
    @field:Length(min = 4, max = 64)
    val nickName: String,
    @field:Email(regexp = VALID_EMAIL)
    val email: String?,
    @field:Max(value = 256)
    val statusMessage: String?,
    val picture: String?
)
