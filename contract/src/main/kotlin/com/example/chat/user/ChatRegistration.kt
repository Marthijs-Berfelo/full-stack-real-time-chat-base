package com.example.chat.user

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.NotBlank
import org.springframework.validation.annotation.Validated

@Validated
data class ChatRegistration(
    @field:NotBlank
    val userId: String,
    @field:NotBlank
    @field:Max(value = 64)
    val nickName: String,
    @field:Max(value = 256)
    val statusMessage: String?,
    val picture: String?
)
