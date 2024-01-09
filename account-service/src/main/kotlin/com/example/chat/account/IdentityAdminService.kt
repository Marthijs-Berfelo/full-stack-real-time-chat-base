package com.example.chat.account

import com.example.chat.account.config.AdminConfiguration
import com.example.chat.security.common.SecurityProperties
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class IdentityAdminService(
    adminProperties: AdminConfiguration,
    internal val securityProperties: SecurityProperties
) {
    internal val log: Logger by lazy { LoggerFactory.getLogger(this::class.java) }
    internal val client = adminProperties.adminClient
    internal val realmName = adminProperties.realm


    @PostConstruct
    fun init() {
        ensureRealmExists()
            .ensureClientScopeExists(this)
            .ensureAccountClientExists(this)
    }
}