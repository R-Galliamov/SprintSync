package com.developers.sprintsync.tracking.repository

import com.developers.sprintsync.tracking.model.Track
import kotlinx.coroutines.flow.Flow

interface TrackRepository {
    val tracks: Flow<List<Track>>

    // CREATE
    suspend fun saveTrack(track: Track)

    // READ
    fun getAllTracks(): Flow<List<Track>>

    // DELETE
    suspend fun deleteTrackById(id: Int)
}
