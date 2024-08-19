package com.developers.sprintsync.tracking.dataStorage.repository.track

import com.developers.sprintsync.tracking.session.model.track.Track
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
