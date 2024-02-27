package com.example.chat.config

import com.example.chat.security.common.UserRole
import jakarta.annotation.PostConstruct
import jakarta.validation.constraints.NotBlank
import org.keycloak.admin.client.Keycloak
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
    lateinit var realmName: String
    var readiness: ReadinessProperties = ReadinessProperties()
    var requiredRoles: List<UserRole> =
        listOf(
            UserRole.REGISTERED,
            UserRole.ANONYMOUS
        )

    class ReadinessProperties {
        var timeOut: Duration = Duration.ofMinutes(2)
        var pollingInterval: Duration = Duration.ofSeconds(20)
    }

    internal val log: Logger by lazy { LoggerFactory.getLogger(this::class.java) }
    internal lateinit var adminClient: Keycloak
    internal lateinit var realm: RealmResource
    internal lateinit var authzClientConfig: org.keycloak.authorization.client.Configuration

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
        realm = adminClient.realm(realmName)
        authzClientConfig = org.keycloak.authorization.client.Configuration(
                url,
                realmName,
                clientId,
                null,
                null
            )
    }

    @Bean
    fun users(): UsersResource =
        realm.users()

    @Bean
    fun groups(): GroupsResource =
        realm.groups()

    @Bean
    fun clients(): ClientsResource =
        realm.clients()

}