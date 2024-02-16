package com.example.chat.security.web

import com.example.chat.security.common.DefaultKeycloakAuthoritiesConverter
import com.example.chat.security.common.SecurityProperties
import org.springframework.context.annotation.Import
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity


/**
 * Add this annotation to a {@code Configuration} class to have Web Security
 * support added. User's can then create one or more {@link ServerHttpSecurity}
 * {@code Bean} instances.
 *
 * @author Marthijs Berfelo
 * @see EnableWebFluxSecurity
 * @see EnableReactiveMethodSecurity
 */
@MustBeDocumented
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Import(
    value = [
        SecurityProperties::class,
        DefaultKeycloakAuthoritiesConverter::class,
        DefaultWebSecurityConfig::class
    ]
)
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
annotation class EnableWebSecurity
