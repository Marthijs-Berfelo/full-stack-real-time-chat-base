package com.example.chat.account.config

import com.example.chat.security.common.AuthoritiesConverter
import com.example.chat.security.common.DefaultKeycloakAuthoritiesConverter
import com.example.chat.security.common.SecurityConfig
import com.example.chat.security.common.SecurityProperties
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter
import org.springframework.security.web.server.SecurityWebFilterChain

@Configuration
@Import(
    value = [
        SecurityProperties::class,
        DefaultKeycloakAuthoritiesConverter::class,
    ]
)
class WebSecurityConfiguration(
    private val properties: SecurityProperties
) : SecurityConfig {
    private val log: Logger by lazy { LoggerFactory.getLogger(this::class.java) }

    @Bean
    fun securityWebFilterChain(
        http: ServerHttpSecurity,
        converter: AuthoritiesConverter
    ): SecurityWebFilterChain =
        http.apply {
            csrf { it.disable() }
                .requestCache { it.disable() }
                .authorizeExchange {
                    it.pathMatchers(
                        *(properties
                                    .unsecuredResources
                            .let { unsecuredResources ->
                                if (unsecuredResources.contains("/actuator")) {
                                    unsecuredResources
                                } else {
                                    unsecuredResources
                                        .plus("/actuator")
                                        .plus("/actuator/**")
                                }
                            }
                            .also { log.atDebug().log { "Unsecured paths: ${it.joinToString()}" } }
                            .toTypedArray()
                                )
                    ).permitAll()
                        .anyExchange()
                        .authenticated()
                }
        }
            .oauth2ResourceServer {
                it.jwt { jwtSpec ->
                    jwtSpec
                        .jwtAuthenticationConverter(
                            ReactiveJwtAuthenticationConverterAdapter(
                                converter
                            )
                        )
                }
            }
            .build()
}