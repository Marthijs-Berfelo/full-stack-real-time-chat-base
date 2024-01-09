package com.example.chat.security.common

import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder
import org.springframework.security.oauth2.server.resource.authentication.JwtReactiveAuthenticationManager
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter

interface SecurityConfig {

    fun authenticationManager(
        converter: AuthoritiesConverter,
        jwtDecoder: ReactiveJwtDecoder,
    ): ReactiveAuthenticationManager =
        JwtReactiveAuthenticationManager(jwtDecoder)
            .apply {
                setJwtAuthenticationConverter {
                    ReactiveJwtAuthenticationConverterAdapter(
                        converter.apply { decoder = jwtDecoder }
                    ).convert(it)
                }
            }
}