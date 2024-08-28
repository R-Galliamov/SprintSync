package com.developers.sprintsync.tracking.dataStorage.repository.track

import com.developers.sprintsync.tracking.session.model.track.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TestRepository
@Inject
constructor() : TrackRepository {
    val mondayTimestamp1 =
        Calendar
            .getInstance()
            .apply {
                set(2024, Calendar.AUGUST, 5) // Assume this is a Monday
            }.timeInMillis

    val tuesdayTimestamp =
        Calendar
            .getInstance()
            .apply {
                set(2024, Calendar.AUGUST, 6)
            }.timeInMillis

    val wednesdayTimestamp1 =
        Calendar
            .getInstance()
            .apply {
                set(2024, Calendar.AUGUST, 7)
            }.timeInMillis

    val thursdayTimestamp1 =
        Calendar
            .getInstance()
            .apply {
                set(2024, Calendar.AUGUST, 8)
            }.timeInMillis

    val fridayTimestamp1 =
        Calendar
            .getInstance()
            .apply {
                set(2024, Calendar.AUGUST, 9)
            }.timeInMillis

    val saturdayTimestamp1 =
        Calendar
            .getInstance()
            .apply {
                set(2024, Calendar.AUGUST, 10)
            }.timeInMillis

    val sundayTimestamp1 =
        Calendar
            .getInstance()
            .apply {
                set(2024, Calendar.AUGUST, 11)
            }.timeInMillis

    val mondayTimestamp2 =
        Calendar
            .getInstance()
            .apply {
                set(2024, Calendar.AUGUST, 12) // Another Monday
            }.timeInMillis

    val mondayTimestamp3 =
        Calendar
            .getInstance()
            .apply {
                set(2024, Calendar.AUGUST, 19) // Another Monday
            }.timeInMillis

    val tracklist =
        listOf(
            Track(1, mondayTimestamp1, 3600000, 1000, 5.0f, 4.5f, 30000, listOf(), null),
            Track(2, mondayTimestamp1, 3600000, 1550, 5.0f, 4.5f, 30000, listOf(), null),
            //Track(2, tuesdayTimestamp, 3600000, 1550, 5.0f, 4.5f, 300, listOf(), null),
            Track(3, wednesdayTimestamp1, 3600000, 1000, 5.0f, 4.5f, 30000, listOf(), null),
            Track(4, thursdayTimestamp1, 3600000, 1000, 5.0f, 4.5f, 30000, listOf(), null),
            Track(5, fridayTimestamp1, 3600000, 1000, 5.0f, 4.5f, 30000, listOf(), null),
            Track(6, saturdayTimestamp1, 3600000, 1000, 5.0f, 4.5f, 30000, listOf(), null),
            Track(7, sundayTimestamp1, 3600000, 1000, 5.0f, 4.5f, 30000, listOf(), null),
            Track(8, mondayTimestamp2, 3600000, 1000, 5.0f, 4.5f, 30000, listOf(), null),
            // Track(6, mondayTimestamp3, 3600000, 1000, 5.0f, 4.5f, 300, listOf(), null),
        )

    init {
        CoroutineScope(Dispatchers.IO).launch {
            delay(10000)
            val currentTimestamp = System.currentTimeMillis()
            val track = Track(9, currentTimestamp, 2600000, 600, 5.0f, 4.5f, 20000, listOf(), null)
            saveTrack(track)
        }
    }

    private val _tracks = MutableStateFlow(tracklist)
    override val tracks: Flow<List<Track>> = _tracks.asStateFlow()

    override suspend fun saveTrack(track: Track) {
        _tracks.update {
            it + track
        }
    }

    override fun getTrackById(id: Int): Track = tracklist.first { it.id == id }

    override suspend fun deleteTrackById(id: Int) {
        _tracks.update { track ->
            track.filter { it.id != id }
        }
    }
}
