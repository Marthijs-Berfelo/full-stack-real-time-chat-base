package com.example.chat.account.operator.config_map

import com.example.chat.account.IdentityAdminService
import com.example.chat.account.config.AdminConfiguration
import com.example.chat.account.operator.common.ResourceService
import com.example.chat.account.config.model.SecurityChange
import com.example.chat.account.operator.common.configuration
import io.kubernetes.client.openapi.models.V1ConfigMap
import jakarta.validation.Validator
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.concurrent.ArrayBlockingQueue

@Service
class ConfigMapService(
    internal val validator: Validator,
    internal val adminService: IdentityAdminService,
    internal val adminProperties: AdminConfiguration,
    ) : ResourceService<SecurityChange> {

    override val log: Logger by lazy { LoggerFactory.getLogger(this::class.java) }

    override val workQueue = ArrayBlockingQueue<SecurityChange>(1024)
    fun handleChange(previous: V1ConfigMap?, next: V1ConfigMap?) = runCatching {
        SecurityChange(
            previous = previous.configuration(),
            next = next.configuration()
        )
            .let(::validate)
            .let(::managedRealm)
            ?.also { log.atDebug().log { "Queueing security change $it to the worker queue" } }
            ?.let(workQueue::add)
    }
        .onFailure { log.atWarn().setCause(it).log { "Failed to process config map change [ FROM: ${previous?.metadata} | TO: ${next?.metadata} ]" } }
        .onSuccess { log.atInfo().log { "Processed config map change" } }

    override fun reconcile(securityChange: SecurityChange): Result<Unit> = runCatching {
        updateRealm(securityChange)
        updateClientScopes(securityChange)
        updateRoles(securityChange)
        updateClients(securityChange)
    }
}