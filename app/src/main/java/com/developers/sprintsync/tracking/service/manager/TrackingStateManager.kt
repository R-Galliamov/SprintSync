package com.developers.sprintsync.tracking.service.manager

import com.developers.sprintsync.tracking.component.model.TrackState
import com.developers.sprintsync.tracking.component.model.TrackingStatus
import com.developers.sprintsync.tracking.data.model.TimedLocation
import kotlinx.coroutines.flow.Flow

abstract class TrackingStateManager {
    abstract val trackingStateFlow: Flow<TrackState>

    abstract fun updateLocationDuration(data: TimedLocation)

    abstract fun updateTrackingStatus(status: TrackingStatus)

    abstract fun resetState()
}
