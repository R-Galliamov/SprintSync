package com.developers.sprintsync.tracking.data.provider.time

import com.developers.sprintsync.tracking.data.provider.time.stopwatch.Stopwatch
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

class GetDurationFlowUseCase
    @Inject
    constructor(
        private val trackingTimeProvider: TrackingTimeProvider,
    ) {
        operator fun invoke() = trackingTimeProvider.timeInMillisFlow()
    }

@Singleton
class TrackingTimeProvider
    @Inject
    constructor(
        private val stopwatch: Stopwatch,
    ) {
        fun timeInMillisFlow(): Flow<Long> = stopwatch.timeMillisState

        fun getCurrentDuration() = stopwatch.timeMillisState.value

        fun start() = stopwatch.start()

        fun pause() = stopwatch.pause()

        fun reset() {
            stopwatch.reset()
        }
    }
