package com.example.chat.security.common

import org.slf4j.Logger
import org.springframework.core.convert.converter.Converter
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.core.OAuth2AccessToken
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication

interface KeycloakAuthoritiesConverter<ATTRIBUTES : TokenAttributes> : AuthoritiesConverter {
    val log: Logger
    val props: ATTRIBUTES
    val defaultAuthenticationConverter: Converter<Jwt, AbstractAuthenticationToken>

    fun principal(source: Jwt, roles: List<GrantedAuthority>): OAuth2Principal

    override fun convert(source: Jwt): AbstractAuthenticationToken? =
        defaultAuthenticationConverter
            .convert(source)
            ?.authorities
            ?.plus(extractRoles(source))
            ?.let {
                BearerTokenAuthentication(
                    principal(source, it),
                    OAuth2AccessToken(
                        OAuth2AccessToken.TokenType.BEARER,
                        source.tokenValue,
                        source.issuedAt,
                        source.expiresAt
                    ),
                    it
                )
            }

    private fun extractRoles(jwt: Jwt): MutableCollection<GrantedAuthority> =
        jwt
            .also { log.atDebug().log { "Starting extraction of authorizations: ${it.claims}" } }
            .extractRoles()
            .also { log.atDebug().log { "Authorities: $it" } }
            .cast()

}