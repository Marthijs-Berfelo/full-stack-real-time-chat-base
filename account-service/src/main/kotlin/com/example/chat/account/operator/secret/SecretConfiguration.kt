package com.example.chat.account.operator.secret

import com.example.chat.account.operator.OperatorProperties
import io.kubernetes.client.informer.SharedIndexInformer
import io.kubernetes.client.informer.SharedInformerFactory
import io.kubernetes.client.informer.cache.Lister
import io.kubernetes.client.openapi.apis.CoreV1Api
import io.kubernetes.client.openapi.models.V1Secret
import io.kubernetes.client.openapi.models.V1SecretList
import io.kubernetes.client.util.CallGeneratorParams
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SecretConfiguration {

    @Bean
    fun secretInformer(
        informerFactory: SharedInformerFactory,
        api: CoreV1Api,
        properties: OperatorProperties
    ): SharedIndexInformer<V1Secret> =
        informerFactory
            .sharedIndexInformerFor(
                { params: CallGeneratorParams ->
                    api.listNamespacedSecretCall(
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
                V1Secret::class.java,
                V1SecretList::class.java,
                properties.pollingInterval.toMillis()
                )

    @Bean
    fun secretLister(
        properties: OperatorProperties,
        secretInformer: SharedIndexInformer<V1Secret>
    ): Lister<V1Secret> =
        Lister(secretInformer.indexer, properties.currentNamespace)
}