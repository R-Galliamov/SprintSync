package com.developers.sprintsync.tracking.repository

import com.developers.sprintsync.tracking.service.provider.Tracker
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrackingRepositoryImpl
    @Inject
    constructor(private val tracker: Tracker) :
    TrackingRepository {
        override val isTracking = tracker.isActive
        override val track = tracker.trackFlow
        override val timeMillis: Flow<Long> = tracker.timeInMillisFlow

        override fun startTracking() {
            tracker.start()
        }

        override fun stopTracking() {
            tracker.stop()
        }

        override fun finishTracking() {
            TODO("Not yet implemented")
        }
    }
