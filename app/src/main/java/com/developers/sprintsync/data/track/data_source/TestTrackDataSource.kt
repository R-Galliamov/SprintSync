package com.developers.sprintsync.data.track.data_source

import com.developers.sprintsync.domain.track.model.Segment
import com.developers.sprintsync.domain.track.model.Track
import com.developers.sprintsync.tracking.data.model.Latitude
import com.developers.sprintsync.tracking.data.model.LocationModel
import com.developers.sprintsync.tracking.data.model.Longitude
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import java.util.Calendar
import java.util.GregorianCalendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TestTrackDataSource
    @Inject
    constructor() : TrackDataSource {
        private val tracksList = mutableListOf<Track>()

        init {
            val febStart =
                GregorianCalendar(2025, Calendar.FEBRUARY, 23).apply { set(Calendar.HOUR_OF_DAY, 12) }.timeInMillis
            (0..5).forEach { dayOffset ->
                val timestamp = febStart + dayOffset * 24 * 60 * 60 * 1000L
                tracksList.add(createTrack(dayOffset + 1, timestamp))
            }

            val marStart = GregorianCalendar(2025, Calendar.MARCH, 1).apply { set(Calendar.HOUR_OF_DAY, 12) }.timeInMillis
            (0..13).forEach { dayOffset ->
                val timestamp = marStart + dayOffset * 24 * 60 * 60 * 1000L
                tracksList.add(createTrack(dayOffset + 7, timestamp))
            }
        }

        private fun createTrack(
            id: Int,
            timestamp: Long,
        ): Track {
            val durationMillis = 3600000L
            val distanceMeters = 5000f + id * 100f
            val avgPace = 300f + id * 5f
            val bestPace = 250f + id * 4f
            val calories = 300f + id * 10f
            val segments =
                listOf(
                    Segment.Active(
                        id = id.toLong(),
                        startLocation = LocationModel(Latitude(40.7128 + id * 0.01), Longitude(-74.0060 + id * 0.01)),
                        startTime = timestamp,
                        endLocation =
                            LocationModel(
                                Latitude(40.7128 + (id + 1) * 0.01),
                                Longitude(-74.0060 + (id + 1) * 0.01),
                            ),
                        endTime = timestamp + durationMillis,
                        durationMillis = durationMillis,
                        distanceMeters = distanceMeters,
                        pace = avgPace,
                        calories = calories,
                    ),
                )
            return Track(id, timestamp, durationMillis, distanceMeters, avgPace, bestPace, calories, segments)
        }

        override val tracks: Flow<List<Track>>
            get() = flowOf(tracksList.toList())

        override suspend fun getTrackById(id: Int): Track =
            tracksList.find { it.id == id } ?: throw NoSuchElementException("Track with id $id not found")

        override suspend fun getLastTrack(): Track? = tracksList.maxByOrNull { it.timestamp }

        override suspend fun saveTrack(track: Track): Int {
            val id = track.id
            if (tracksList.any { it.id == id }) {
                tracksList.removeIf { it.id == id }
            }
            tracksList.add(track)
            return id
        }

        override suspend fun deleteTrackById(id: Int) {
            if (!tracksList.removeIf { it.id == id }) {
                throw NoSuchElementException("Track with id $id not found")
            }
        }
    }
