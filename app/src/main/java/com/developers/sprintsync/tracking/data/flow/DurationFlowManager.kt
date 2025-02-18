package com.developers.sprintsync.tracking.data.flow

import com.developers.sprintsync.tracking.data.provider.time.TrackingTimeProvider
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

class DurationFlowManager
    @Inject
    constructor(
        private val provider: TrackingTimeProvider,
    ) : FlowManager<Long>(provider.timeInMillisFlow()) {
        override fun start(
            externalScope: CoroutineScope,
            onCollect: suspend (Long) -> Unit,
        ) {
            super.start(externalScope) { duration ->
                provider.start()
                onCollect(duration)
            }
        }

        override fun stop() {
            super.stop()
            provider.pause()
        }

        fun clean() {
            super.stop()
            provider.reset()
        }
    }