package com.developers.sprintsync.tracking.service.manager

import com.developers.sprintsync.tracking.model.session.TrackerState
import com.developers.sprintsync.tracking.model.session.TrackingSession
import com.developers.sprintsync.tracking.service.tracker.Tracker
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrackingSessionManagerImpl
    @Inject
    constructor(
        val tracker: Tracker,
    ) : TrackingSessionManager {
        override val trackerState: StateFlow<TrackerState>
            get() = tracker.state
        override val data: StateFlow<TrackingSession>
            get() = tracker.data

        override fun startUpdatingLocation() {
            tracker.startUpdatingLocation()
        }

        override fun stopUpdatingLocation() {
            tracker.stopUpdatingLocation()
        }

        override fun startTracking() {
            tracker.start()
        }

        override fun pauseTracking() {
            tracker.pause()
        }

        override fun finishTracking() {
            tracker.finish()
        }

        override fun isTrackValid(): Boolean = tracker.isTrackValid()
    }
