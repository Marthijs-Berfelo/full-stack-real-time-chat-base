package com.example.chat.security.common

import jakarta.validation.constraints.NotBlank
import org.springframework.validation.annotation.Validated

@Validated
class TokenMapperProperties(
    @NotBlank
    var name: String,
    @NotBlank
    var tokenKey: String,
    @NotBlank
    var mapperName: String,
    @NotBlank
    var protocol: String = "openid-connect",
    var properties: Map<String, String> = emptyMap()
) {
    fun mapperConfig(): Map<String, String> =
        properties
            .plus("access.token.claim" to "true")
            .plus("id.token.claim" to "true")
            .plus("userinfo.token.claim" to "true")
}