package com.example.chat.config

import org.keycloak.admin.client.Keycloak
import org.keycloak.representations.info.ServerInfoRepresentation
import org.slf4j.Logger
import java.time.Instant
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

fun Keycloak.info(log: Logger): ServerInfoRepresentation =
    serverInfo()
        .info
        .also { log.atInfo().log { "Connected to Keycloak version: ${it.systemInfo.version}" } }

fun AdminConfiguration.waitUntilClientReady() {
    var ready = false
    val start = Instant.now()
    while (!ready && start.plus(readiness.timeOut).isAfter(Instant.now())) {
        runCatching { adminClient.info(log) }
            .onFailure {
                log.atWarn()
                    .setCause(it)
                    .log { "Keycloak not ready" }
                readiness
                    .pollingInterval
                    .seconds
                    .let(TimeUnit.SECONDS::sleep)
            }
            .onSuccess {
                log.atInfo().log { "Keycloak ready" }
                ready = true
            }
    }
    if (!ready) throw TimeoutException("Client could not connect to Keycloak at [ $url ] within: ${readiness.timeOut}")
}