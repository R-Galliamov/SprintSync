package com.developers.sprintsync.tracking.service.provider.time

import com.developers.sprintsync.tracking.service.provider.time.stopwatch.Stopwatch
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TimeProvider
    @Inject
    constructor(
        private val stopwatch: Stopwatch,
    ) {
        fun timeInMillisFlow(): Flow<Long> = stopwatch.timeMillisState

        fun updateStopwatchState(isActive: Boolean) {
            when (isActive) {
                true -> stopwatch.start()
                false -> stopwatch.pause()
            }
        }

        fun reset() {
            stopwatch.reset()
        }
    }
