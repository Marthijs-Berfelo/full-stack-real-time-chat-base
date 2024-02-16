package com.example.chat.security.web

import com.example.chat.security.common.AuthoritiesConverter
import com.example.chat.security.common.SecurityConfig
import com.example.chat.security.common.SecurityProperties
import org.slf4j.Logger
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter
import org.springframework.security.web.server.SecurityWebFilterChain

interface WebSecurityConfig : SecurityConfig {
    val log: Logger
    val properties: SecurityProperties

    fun init()

    fun securityWebFilterChain(
        http: ServerHttpSecurity,
        converter: AuthoritiesConverter
    ): SecurityWebFilterChain

    fun configure(
        http: ServerHttpSecurity,
        converter: AuthoritiesConverter
    ): SecurityWebFilterChain =
        http
            .apply {
                cors { it.disable() }
                    .csrf { it.disable() }
                    .authorizeExchange {
                        it.pathMatchers(
                            *(properties.unsecuredResources
                                .also { paths -> log.atDebug().log { "Unsecured paths: $paths" } }
                                .toTypedArray()
                                    )
                        )
                            .permitAll()
                            .anyExchange()
                            .authenticated()
                    }
                    .oauth2ResourceServer {
                        it.jwt { jwtSpec ->
                            jwtSpec
                                .jwtAuthenticationConverter(
                                    ReactiveJwtAuthenticationConverterAdapter(converter)
                                )
                        }
                    }
            }
            .build()

}