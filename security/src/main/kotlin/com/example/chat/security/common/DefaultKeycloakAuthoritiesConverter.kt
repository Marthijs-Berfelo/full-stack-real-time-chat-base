package com.example.chat.security.common

import jakarta.annotation.PostConstruct
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.convert.converter.Converter
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.stereotype.Component

@Component
class DefaultKeycloakAuthoritiesConverter(authProperties: SecurityProperties) :
    KeycloakAuthoritiesConverter<SecurityProperties.TokenAttributeProperties> {

    override val log: Logger by lazy { LoggerFactory.getLogger(this::class.java) }
    override val props = authProperties.tokenAttributes
    override val defaultAuthenticationConverter: Converter<Jwt, AbstractAuthenticationToken> =
        JwtAuthenticationConverter()

    override lateinit var decoder: ReactiveJwtDecoder

    @PostConstruct
    fun init() {
        log.atInfo().log { "Initialized: ${this::class.simpleName}" }
    }

    override fun principal(source: Jwt, roles: List<GrantedAuthority>): OAuth2Principal =
        DefaultOAuth2Principal(
            userId = source.subject,
            username = source.getClaimAsString(props.userName.tokenKey) ?: source.subject,
            firstName = source.claim(props.firstName.tokenKey),
            middleName = source.claim(props.middleName.tokenKey),
            lastName = source.claim(props.lastName.tokenKey),
            nickName = source.claim(props.nickName.tokenKey),
            email = source.claim(props.email.tokenKey),
            chatUserId = source.claim(props.chatUserId.tokenKey),
            authorities = roles,
            attributes = source.claims
        )
}
