package com.example.chat.account

import io.micrometer.tracing.SpanName
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.function.server.ServerResponse.ok

@Configuration
class RouterConfig {

    @Bean
    fun routes(handler: HelloWorldHandler): RouterFunction<ServerResponse> =
        coRouter {
            GET("hello", handler::hello)
        }


}

@Component
class HelloWorldHandler {

    private val log: Logger by lazy { LoggerFactory.getLogger(this::class.java) }

    suspend fun hello(request: ServerRequest): ServerResponse =
        "Hello, world!"
            .also { msg -> log.atInfo().log { msg } }
            .let { ok().bodyValueAndAwait(it) }
}
