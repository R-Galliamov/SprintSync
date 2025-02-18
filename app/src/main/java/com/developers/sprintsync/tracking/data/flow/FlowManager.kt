package com.developers.sprintsync.tracking.data.flow

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

open class FlowManager<T>(
    val flow: Flow<T>,
) {
    private var job: Job? = null

    open fun start(
        externalScope: CoroutineScope,
        onCollect: suspend (T) -> Unit,
    ) {
        stop()
        job =
            externalScope.launch {
                flow.collectLatest { onCollect(it) }
            }
    }

    open fun stop() {
        job?.cancel()
        job = null
    }
}