package com.example.chat.security.web

import com.example.chat.security.common.AuthoritiesConverter
import com.example.chat.security.common.SecurityProperties
import jakarta.annotation.PostConstruct
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain

@Primary
@Configuration
class DefaultWebSecurityConfig(override val properties: SecurityProperties) : WebSecurityConfig {
    override val log: Logger by lazy { LoggerFactory.getLogger(this::class.java) }

    @PostConstruct
    override fun init() {
        log.atInfo().log { "Initialized: ${this::class.simpleName}" }
    }

    override fun securityWebFilterChain(
        http: ServerHttpSecurity,
        converter: AuthoritiesConverter
    ): SecurityWebFilterChain = configure(http, converter)
}