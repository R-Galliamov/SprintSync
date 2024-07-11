package com.developers.sprintsync.tracking.data.repository

import com.developers.sprintsync.tracking.data.model.track.Track
import kotlinx.coroutines.flow.Flow

interface TrackRepository {
    val tracks: Flow<List<Track>>

    // CREATE
    suspend fun saveTrack(track: Track)

    // READ
    fun getTrackById(id: Int): Track

    // DELETE
    suspend fun deleteTrackById(id: Int)
}
