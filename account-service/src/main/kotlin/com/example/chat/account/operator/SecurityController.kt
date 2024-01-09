package com.example.chat.account.operator

import com.example.chat.account.operator.config_map.ConfigMapService
import io.kubernetes.client.informer.ResourceEventHandler
import io.kubernetes.client.informer.SharedIndexInformer
import io.kubernetes.client.openapi.models.V1ConfigMap
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.util.concurrent.ArrayBlockingQueue

@Component
class SecurityController(
    internal val configMapService: ConfigMapService,
    internal val configMapInformer: SharedIndexInformer<V1ConfigMap>,
    internal val properties: OperatorProperties,
) {
    internal val log: Logger by lazy { LoggerFactory.getLogger(this::class.java) }
    fun create() {
        log.atInfo()
            .log { "Initializing ${this::class.simpleName} for config map with name [${properties.configMapName}] in namespace: ${properties.currentNamespace}" }

        configMapInformer.addEventHandler(
            object : ResourceEventHandler<V1ConfigMap> {
                override fun onAdd(configMap: V1ConfigMap) {
                    log.atDebug().log { "New config map received: $configMap" }
                    configMap
                        .takeIf(properties::checkTargetConfigMap)
                        .let { configMapService.handleChange(previous = null, next = it) }

                }

                override fun onUpdate(previous: V1ConfigMap, next: V1ConfigMap) {
                    log.atDebug().log { "Config map update received:\n$previous\n****\n$next" }
                    if (properties.checkTargetConfigMap(previous)) {
                        configMapService.handleChange(previous = previous, next = next)
                    }
                }

                override fun onDelete(previous: V1ConfigMap, deletedFinalStateUnknown: Boolean) {
                    log.atDebug().log { "Config map removed: $deletedFinalStateUnknown \n$previous\n****" }
                    if (properties.checkTargetConfigMap(previous)) {
                        configMapService.handleChange(previous = previous, next = null)
                    }
                }
            }
        )
    }

    fun run() {
        log.atInfo().log { "Starting security controller" }
        waitUntilSynced()
        configMapService.run()
    }

    fun stop() {
        log.atInfo().log { "Stopping security controller..." }
        while (configMapService.hasCompleted()) {
            log.atTrace().log { "Waiting until the work queue is empty" }
        }
        log.atDebug().log { "Security controller stopped" }
    }
}