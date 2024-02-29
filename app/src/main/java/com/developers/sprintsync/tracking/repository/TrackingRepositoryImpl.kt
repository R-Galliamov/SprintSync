package com.developers.sprintsync.tracking.repository

import com.developers.sprintsync.service.tracker.Tracker
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrackingRepositoryImpl
    @Inject
    constructor(private val tracker: Tracker) :
    TrackingRepository {
        override val isTracking = tracker.isActive

        override val testFlow: Flow<Int> = tracker.testFlow

        override val track = tracker.trackFLow

        override val timeMillis: Flow<Long> = tracker.timeInMillisFlow

        override fun startTracking() {
            tracker.start()
        }

        override fun stopTracking() {
            tracker.stop()
        }

        override fun finishTracking() {
            tracker.reset()
        }
    }
