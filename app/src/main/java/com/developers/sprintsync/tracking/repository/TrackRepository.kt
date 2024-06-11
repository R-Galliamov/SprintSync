package com.developers.sprintsync.tracking.repository

import com.developers.sprintsync.tracking.model.Track
import kotlinx.coroutines.flow.Flow

interface TrackRepository {
    suspend fun saveTrack(track: Track)

    fun getAllTracks(): Flow<List<Track>>
}
