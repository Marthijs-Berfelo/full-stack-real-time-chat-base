package com.example.chat.mesage

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.NotBlank
import org.springframework.validation.annotation.Validated
import java.time.Instant

@Validated
data class ChatMessage(
    @field:NotBlank
    val from: String,
    @field:NotBlank
    val to: String,
    @field:NotBlank
    val conversationId: String,
    val sendAt: Instant = Instant.now(),
    @field:Max(value = 256)
    @field:NotBlank
    val message: String
)
