package com.developers.sprintsync.core.components.track.data.repository

import com.developers.sprintsync.core.util.test.TestTimestamps
import com.developers.sprintsync.core.components.track.data.model.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrackRepositoryTest
    @Inject
    constructor() : TrackRepository {
        private val tt = TestTimestamps

        val trackList =
            listOf(
                Track(1, tt.currentTimestamp, 3600000, 10, 5.0f, 4.5f, 30000, listOf(), null),
                // Track(2, mondayTimestamp1, 3600000, 1550, 5.0f, 4.5f, 30000, listOf(), null),
                // //Track(2, tuesdayTimestamp, 3600000, 1550, 5.0f, 4.5f, 300, listOf(), null),
                // Track(3, wednesdayTimestamp1, 3600000, 1000, 5.0f, 4.5f, 30000, listOf(), null),
                // Track(4, thursdayTimestamp1, 3600000, 1000, 5.0f, 4.5f, 30000, listOf(), null),
                // Track(5, fridayTimestamp1, 3600000, 1000, 5.0f, 4.5f, 30000, listOf(), null),
                // Track(6, saturdayTimestamp1, 3600000, 1000, 5.0f, 4.5f, 30000, listOf(), null),
                // Track(7, sundayTimestamp1, 3600000, 1000, 5.0f, 4.5f, 30000, listOf(), null),
                // Track(8, mondayTimestamp2, 3600000, 1000, 5.0f, 4.5f, 30000, listOf(), null),
                // Track(6, mondayTimestamp3, 3600000, 1000, 5.0f, 4.5f, 300, listOf(), null),
            )

        init {
            CoroutineScope(Dispatchers.IO).launch {
                delay(10000)
                val currentTimestamp = System.currentTimeMillis()
                val track = Track(9, currentTimestamp, 2600000, 600, 5.0f, 4.5f, 20000, listOf(), null)
                // saveTrack(track)
            }
        }

        private val _tracks = MutableStateFlow(trackList)
        override val tracks: Flow<List<Track>> = _tracks.asStateFlow()

        override suspend fun saveTrack(track: Track) {
            _tracks.update {
                it + track
            }
        }

        override fun getTrackById(id: Int): Track = trackList.first { it.id == id }

        override suspend fun deleteTrackById(id: Int) {
            _tracks.update { track ->
                track.filter { it.id != id }
            }
        }
    }
