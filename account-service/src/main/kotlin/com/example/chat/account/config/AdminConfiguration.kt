package com.example.chat.account.config

import jakarta.annotation.PostConstruct
import jakarta.validation.constraints.NotBlank
import org.keycloak.admin.client.Keycloak
import org.keycloak.admin.client.resource.ClientScopesResource
import org.keycloak.admin.client.resource.ClientsResource
import org.keycloak.admin.client.resource.GroupsResource
import org.keycloak.admin.client.resource.RealmResource
import org.keycloak.admin.client.resource.UsersResource
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.validation.annotation.Validated
import java.time.Duration

@Validated
@Configuration
@ConfigurationProperties("admin.client")
class AdminConfiguration {
    lateinit var url: String
    @field:NotBlank
    lateinit var mainRealm: String
    @field:NotBlank
    lateinit var user: String
    @field:NotBlank
    lateinit var pass: String
    @field:NotBlank
    lateinit var clientId: String
    @field:NotBlank
    lateinit var realm: String
    var readiness: ReadinessProperties = ReadinessProperties()

    class ReadinessProperties {
        var timeOut: Duration = Duration.ofMinutes(2)
        var pollingInterval: Duration = Duration.ofSeconds(20)
    }

    internal val log: Logger by lazy { LoggerFactory.getLogger(this::class.java) }
    internal lateinit var adminClient: Keycloak

    @PostConstruct
    fun init() {
        adminClient = Keycloak.getInstance(
            url,
            mainRealm,
            user,
            pass,
            clientId
        )
        waitUntilClientReady()
    }

    @Bean
    fun realm(): RealmResource =
        adminClient.realm(realm)

    @Bean
    fun users(): UsersResource =
        realm().users()

    @Bean
    fun groups(): GroupsResource =
        realm().groups()

    @Bean
    fun clients(): ClientsResource =
        realm().clients()

    fun foo(): ClientScopesResource =
        realm().clientScopes()
}