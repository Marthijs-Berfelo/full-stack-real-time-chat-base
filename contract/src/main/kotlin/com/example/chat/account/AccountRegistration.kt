package com.example.chat.account

import com.example.chat.VALID_EMAIL
import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.validation.constraints.AssertTrue
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.NotBlank
import org.hibernate.validator.constraints.Length
import org.springframework.validation.annotation.Validated

@Validated
data class AccountRegistration(
    @field:Max(value = 50)
    val firstName: String,
    @field:Max(value = 50)
    val lastName: String,
    @field:NotBlank
    val middleName: String?,
    @field:Email(regexp = VALID_EMAIL)
    val email: String,
    @field:Length(min = 4, max = 64)
    val nickName: String?,
    @field:Length(min = 4, max = 256)
    val password: String,
    val passwordConfirmation: String
) {

    @JsonIgnore
    @AssertTrue(message = "Password confirmation and password must match")
    fun hasMatchingPasswordConfirmation(): Boolean =
        password == passwordConfirmation

}