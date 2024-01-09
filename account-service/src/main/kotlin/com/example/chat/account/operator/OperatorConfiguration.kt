package com.example.chat.account.operator

import io.kubernetes.client.informer.SharedInformerFactory
import io.kubernetes.client.openapi.ApiClient
import io.kubernetes.client.openapi.apis.CoreV1Api
import io.kubernetes.client.util.ClientBuilder
import jakarta.validation.Validator
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

@Configuration
class OperatorConfiguration {

    @Bean
    @Primary
    fun apiClient(): ApiClient =
        ClientBuilder
            .standard()
            .setVerifyingSsl(false)
            .build()

    @Bean
    @ConfigurationProperties("operator")
    fun properties(validator: Validator): OperatorProperties =
        OperatorProperties()
            .let {
                validator.validate(it)
                it
            }

    @Bean
    fun coreApi(apiClient: ApiClient): CoreV1Api = CoreV1Api(apiClient)

    @Bean
    fun informerFactory(apiClient: ApiClient): SharedInformerFactory =
        SharedInformerFactory(apiClient)
}