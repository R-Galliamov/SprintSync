package com.developers.sprintsync.core.components.track.data.repository

import com.developers.sprintsync.core.components.track.data.model.Track
import kotlinx.coroutines.flow.Flow

interface TrackRepository {
    // CREATE
    val tracks: Flow<List<Track>>

    suspend fun saveTrack(track: Track)

    // READ
    fun getTrackById(id: Int): Track

    // DELETE
    suspend fun deleteTrackById(id: Int)
}
