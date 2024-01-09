package com.example.chat.account.operator

import io.kubernetes.client.openapi.models.V1ConfigMap

fun OperatorProperties.checkTargetConfigMap(configMap: V1ConfigMap): Boolean =
    configMapName == configMap.metadata?.name