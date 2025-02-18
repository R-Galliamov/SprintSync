package com.developers.sprintsync.core.components.track.data.data_source

import com.developers.sprintsync.core.components.track.data.model.Track
import kotlinx.coroutines.flow.Flow

interface TrackDataSource {
    // CREATE
    val tracks: Flow<List<Track>>

    suspend fun saveTrack(track: Track): Int

    // READ
    fun getTrackById(id: Int): Track

    // DELETE
    suspend fun deleteTrackById(id: Int)
}
