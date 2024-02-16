package com.example.chat.security.web

import com.example.chat.security.common.AuthoritiesConverter
import com.example.chat.security.common.SecurityProperties
import jakarta.annotation.PostConstruct
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter
import org.springframework.security.web.server.SecurityWebFilterChain

@Configuration
@ConditionalOnMissingBean(WebSecurityConfig::class)
class DisabledWebSecurityConfig(override val properties: SecurityProperties) : WebSecurityConfig {

    override val log: Logger by lazy { LoggerFactory.getLogger(this::class.java) }

    @PostConstruct
    override fun init() {
        log.atInfo().log { "Initialized: ${this::class.simpleName}" }
    }

    override fun securityWebFilterChain(
        http: ServerHttpSecurity,
        converter: AuthoritiesConverter
    ): SecurityWebFilterChain  =
        http.also { security ->
            security
                .csrf { it.disable() }
                .cors { it.disable() }
                .requestCache { it.disable() }
                .authorizeExchange { it.anyExchange().permitAll() }
                .oauth2Client { }
                .oauth2ResourceServer {
                    it.jwt { jwtSpec ->
                        jwtSpec.jwtAuthenticationConverter(
                            ReactiveJwtAuthenticationConverterAdapter(converter)
                        )
                    }
                }
        }
            .build()
}