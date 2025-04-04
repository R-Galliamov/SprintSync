package com.developers.sprintsync.data.track.data_source

import com.developers.sprintsync.domain.track.model.Track
import kotlinx.coroutines.flow.Flow

interface TrackDataSource {
    val tracks: Flow<List<Track>>

    suspend fun getTrackById(id: Int): Track

    suspend fun getLastTrack(): Track?

    suspend fun saveTrack(track: Track): Int

    suspend fun deleteTrackById(id: Int)
}
