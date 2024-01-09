package com.example.chat.account.config.model

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.validation.constraints.AssertTrue
import org.springframework.validation.annotation.Validated

@Validated
data class SecurityChange(
    val previous: SecurityConfiguration?,
    val next: SecurityConfiguration?,
    val type: ChangeType = previous.toChangeType(next)
) {
    @JsonIgnore
    @AssertTrue(message = "Security change must have at least a previous or a next")
    fun hasChanges(): Boolean =
        (previous != null || next != null) && type != ChangeType.INVALID
}