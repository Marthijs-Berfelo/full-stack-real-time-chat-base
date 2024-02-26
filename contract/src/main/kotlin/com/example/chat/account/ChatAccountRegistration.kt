package com.example.chat.account

import com.example.chat.VALID_EMAIL
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import org.hibernate.validator.constraints.Length
import org.springframework.validation.annotation.Validated

@Validated
data class ChatAccountRegistration(
    @field:Length(min = 4, max = 64)
    val nickName: String,
    @field: NotBlank
    val chatUserId: String,
    @field:Email(regexp = VALID_EMAIL)
    val email: String? = null
)
