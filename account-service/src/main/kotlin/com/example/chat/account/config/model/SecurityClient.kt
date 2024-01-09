package com.example.chat.account.config.model

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.validation.Valid
import jakarta.validation.constraints.AssertTrue
import jakarta.validation.constraints.NotBlank
import org.springframework.validation.annotation.Validated

@Validated
data class SecurityClient(
    @field:NotBlank
    val id: String,
    @field:NotBlank
    val name: String,
    val description: String?,
    val public: Boolean = false,
    @field:Valid
    val serviceAccountRoles: List<@NotBlank String>? = null,
    @field:Valid
    val loginRedirectUris: List<@NotBlank String>? = null,
    @field:Valid
    val logoutRedirectUris: List<@NotBlank String>? = null,
    @field:Valid
    val webOrigins: List<@NotBlank String>? = null
) {

    @JsonIgnore
    @AssertTrue(message = "All security client login redirects must have a matching web origin")
    fun hasValidLoginRedirects(): Boolean =
        loginRedirectUris
            ?.all { redirectUri ->
                webOrigins?.any { redirectUri.startsWith(it) } ?: false
            }
            ?: true

    @JsonIgnore
    @AssertTrue(message = "All security client logout redirects must have a matching web origin")
    fun hasValidLogoutRedirects(): Boolean =
        logoutRedirectUris
            ?.all { redirectUri ->
                webOrigins?.any { redirectUri.startsWith(it) } ?: false
            }
            ?: true
}