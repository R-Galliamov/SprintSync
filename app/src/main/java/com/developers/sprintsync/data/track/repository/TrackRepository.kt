package com.developers.sprintsync.data.track.repository

import com.developers.sprintsync.domain.track.model.Track
import kotlinx.coroutines.flow.Flow

interface TrackRepository {
    val tracksFlow: Flow<List<Track>>

    suspend fun getTrackById(id: Int): Track

    suspend fun getLastTrack(): Track?

    suspend fun save(track: Track): Int

    suspend fun deleteTrackById(id: Int)
}
