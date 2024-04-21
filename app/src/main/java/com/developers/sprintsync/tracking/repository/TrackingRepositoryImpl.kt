package com.developers.sprintsync.tracking.repository

import com.developers.sprintsync.tracking.model.Track
import com.developers.sprintsync.tracking.model.TrackerState
import com.developers.sprintsync.tracking.model.TrackingSession
import com.developers.sprintsync.tracking.service.tracker.Tracker
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrackingRepositoryImpl
    @Inject
    constructor(private val tracker: Tracker) :
    TrackingRepository {
        override val trackerState: StateFlow<TrackerState>
            get() = tracker.state
        override val data: StateFlow<TrackingSession>
            get() = tracker.data

        override fun startTracking() {
            tracker.start()
        }

        override fun pauseTracking() {
            tracker.pause()
        }

        override fun finishTracking() {
            tracker.finish()
        }

        override fun saveTrack(track: Track) {
        }
    }
