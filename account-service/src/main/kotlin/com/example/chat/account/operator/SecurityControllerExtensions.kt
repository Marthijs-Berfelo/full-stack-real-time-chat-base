package com.example.chat.account.operator

internal fun SecurityController.waitUntilSynced() {
    log.atDebug().log { "Waiting for synchronisation..." }
    while (!configMapInformer.hasSynced()) {
        log.atTrace().log { "Awaiting synchronisation for config maps: ${configMapInformer.hasSynced()}" }
    }
    log.atInfo().log { "Synchronisation started with interval: ${properties.pollingInterval}" }
}
