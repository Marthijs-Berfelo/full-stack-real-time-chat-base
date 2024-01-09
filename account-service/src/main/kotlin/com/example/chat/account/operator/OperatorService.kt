package com.example.chat.account.operator

import io.kubernetes.client.informer.SharedInformerFactory
import jakarta.annotation.PreDestroy
import org.springframework.boot.ApplicationRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OperatorService(
    private val controller: SecurityController,
    private val informerFactory: SharedInformerFactory
) {

    @Bean
    fun start(): ApplicationRunner = ApplicationRunner {
        controller.create()
        informerFactory.startAllRegisteredInformers()
        controller.run()
    }

    @PreDestroy
    fun destroy() {
        informerFactory.stopAllRegisteredInformers()
        controller.stop()
    }
}