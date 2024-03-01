package com.developers.sprintsync.tracking.repository

import com.developers.sprintsync.tracking.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface TrackingRepository {
    val isTracking: StateFlow<Boolean>

    val track: Flow<Track>

    val timeMillis: Flow<Long>

    fun startTracking()

    fun stopTracking()

    fun finishTracking()
}
