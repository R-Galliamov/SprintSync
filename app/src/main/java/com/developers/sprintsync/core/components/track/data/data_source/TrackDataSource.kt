package com.developers.sprintsync.core.components.track.data.data_source

import com.developers.sprintsync.core.components.track.data.model.Track
import kotlinx.coroutines.flow.Flow

interface TrackDataSource {
    val tracks: Flow<List<Track>>

    suspend fun getTrackById(id: Int): Track

    suspend fun getLastTrack(): Track?

    suspend fun saveTrack(track: Track): Int

    suspend fun deleteTrackById(id: Int)
}
