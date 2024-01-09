package com.example.chat.account.operator.common

import com.example.chat.account.config.model.SecurityChange
import com.example.chat.account.config.model.SecurityConfiguration
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.kubernetes.client.common.KubernetesType
import io.kubernetes.client.openapi.ApiException
import io.kubernetes.client.openapi.models.V1ConfigMap
import io.kubernetes.client.util.generic.KubernetesApiResponse

fun <T : KubernetesType> KubernetesApiResponse<T>.evaluate(message: String): KubernetesApiResponse<T> =
    if (isSuccess) {
        this
    } else {
        throw ApiException(httpStatusCode, status.message ?: message)
    }

const val KEY_SEPARATOR = "::"

fun V1ConfigMap?.configuration(): SecurityConfiguration? =
    this?.data
        ?.let(jacksonObjectMapper()::writeValueAsString)
        ?.let(jacksonObjectMapper()::readValue)

fun SecurityChange.toKey(): String =
    "${this::class.simpleName}$KEY_SEPARATOR${jacksonObjectMapper().writeValueAsString(this)}"
