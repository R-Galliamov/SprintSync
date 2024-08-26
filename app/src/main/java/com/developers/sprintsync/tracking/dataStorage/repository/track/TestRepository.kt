package com.developers.sprintsync.tracking.dataStorage.repository.track

import com.developers.sprintsync.tracking.session.model.track.Track
import com.developers.sprintsync.user.model.DailyGoal
import com.developers.sprintsync.user.model.chart.chartData.Metric
import kotlinx.coroutines.flow.flowOf
import java.util.Calendar
import javax.inject.Inject

class TestRepository
    @Inject
    constructor() : TrackRepository {
        val mondayTimestamp1 =
            Calendar
                .getInstance()
                .apply {
                    set(2024, Calendar.AUGUST, 5) // Assume this is a Monday
                }.timeInMillis

        val wendsdayTimestamp1 =
            Calendar
                .getInstance()
                .apply {
                    set(2024, Calendar.AUGUST, 7)
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
                Track(1, mondayTimestamp1, 3600000, 1000, 5.0f, 4.5f, 300, listOf(), null),
                Track(2, mondayTimestamp1, 3600000, 1550, 5.0f, 4.5f, 300, listOf(), null),
                Track(3, wendsdayTimestamp1, 3600000, 1000, 5.0f, 4.5f, 300, listOf(), null),
                Track(4, sundayTimestamp1, 3600000, 1000, 5.0f, 4.5f, 300, listOf(), null),
                Track(5, mondayTimestamp2, 3600000, 1000, 5.0f, 4.5f, 300, listOf(), null),
                Track(6, mondayTimestamp3, 3600000, 1000, 5.0f, 4.5f, 300, listOf(), null),
            )

        val goals =
            listOf(
                DailyGoal(mondayTimestamp1, Metric.DISTANCE, 1000f),
                DailyGoal(mondayTimestamp1 + 1, Metric.DISTANCE, 2000f),
                DailyGoal(wendsdayTimestamp1, Metric.DISTANCE, 2000f),
                DailyGoal(mondayTimestamp2, Metric.DISTANCE, 3000f),
                DailyGoal(mondayTimestamp3, Metric.DISTANCE, 4000f),
            )

        override val tracks = flowOf(tracklist)

        override suspend fun saveTrack(track: Track) {
            TODO("Not yet implemented")
        }

        override fun getTrackById(id: Int): Track {
            TODO("Not yet implemented")
        }

        override suspend fun deleteTrackById(id: Int) {
            TODO("Not yet implemented")
        }
    }
