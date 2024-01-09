package com.example.chat.account.operator.config_map

import com.example.chat.account.operator.OperatorProperties
import io.kubernetes.client.informer.SharedIndexInformer
import io.kubernetes.client.informer.SharedInformer
import io.kubernetes.client.informer.SharedInformerFactory
import io.kubernetes.client.informer.cache.Lister
import io.kubernetes.client.openapi.ApiClient
import io.kubernetes.client.openapi.apis.CoreV1Api
import io.kubernetes.client.openapi.models.V1ConfigMap
import io.kubernetes.client.openapi.models.V1ConfigMapList
import io.kubernetes.client.util.CallGeneratorParams
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ConfigMapConfiguration {

    @Bean
    fun configMapInformer(
        informerFactory: SharedInformerFactory,
        api: CoreV1Api,
        properties: OperatorProperties
    ): SharedIndexInformer<V1ConfigMap> =
        informerFactory
            .sharedIndexInformerFor(
                { params: CallGeneratorParams ->
                    api.listNamespacedConfigMapCall(
                        properties.currentNamespace,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        params.resourceVersion,
                        null,
                        params.timeoutSeconds,
                        params.watch,
                        null
                    )
                },
                V1ConfigMap::class.java,
                V1ConfigMapList::class.java,
                properties.pollingInterval.toMillis()
            )

    @Bean
    fun configMapLister(
        properties: OperatorProperties,
        configMapInformer: SharedIndexInformer<V1ConfigMap>
    ): Lister<V1ConfigMap> =
        Lister(configMapInformer.indexer, properties.currentNamespace)
}