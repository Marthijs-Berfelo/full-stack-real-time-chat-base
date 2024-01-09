package com.example.chat.account.operator.common

import org.slf4j.Logger
import java.util.concurrent.ArrayBlockingQueue

interface ResourceService<T> {
    val workQueue: ArrayBlockingQueue<T>
    val log: Logger

    fun run() {
        while (true) {
            runCatching {
                workQueue
                    .take()
                    .also { log.atDebug().log { "Processing: $it" } }
                    .let(::reconcile)
                    .getOrThrow()
            }
                .onFailure { log.atWarn().setCause(it).log { "Error processing resource" } }
        }
    }

    fun hasCompleted(): Boolean =
        workQueue.isEmpty()
    fun reconcile(securityChange: T): Result<Unit>
}