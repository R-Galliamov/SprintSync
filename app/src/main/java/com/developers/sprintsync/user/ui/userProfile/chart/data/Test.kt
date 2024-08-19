package com.developers.sprintsync.user.ui.userProfile.chart.data

import com.developers.sprintsync.tracking.session.model.track.Track
import kotlinx.coroutines.flow.flowOf
import java.util.Calendar


/*
data class Track(
    val id: Int,
    val timestamp: Long,
    val durationMillis: Long,
    val distanceMeters: Int,
    val avgPace: Float,
    val bestPace: Float,
    val calories: Int,
    val segments: Segments,
    val mapPreview: Bitmap?,
)
 */

class TrackTestContainer {
    val track1 =
        Track(
            id = 0,
            timestamp = 1724025601000,
            durationMillis = 0,
            distanceMeters = 1000,
            avgPace = 0f,
            bestPace = 0f,
            calories = 0,
            segments = emptyList(),
            mapPreview = null,
        )

    val track2 =
        Track(
            id = 0,
            timestamp = 1724112001000,
            durationMillis = 0,
            distanceMeters = 1000,
            avgPace = 0f,
            bestPace = 0f,
            calories = 0,
            segments = emptyList(),
            mapPreview = null,
        )

    val track3 =
        Track(
            id = 0,
            timestamp = 1724198401000,
            durationMillis = 0,
            distanceMeters = 1000,
            avgPace = 0f,
            bestPace = 0f,
            calories = 0,
            segments = emptyList(),
            mapPreview = null,
        )

    val track4 =
        Track(
            id = 0,
            timestamp = 1724284801000,
            durationMillis = 0,
            distanceMeters = 1000,
            avgPace = 0f,
            bestPace = 0f,
            calories = 0,
            segments = emptyList(),
            mapPreview = null,
        )

    val track5 =
        Track(
            id = 0,
            timestamp = 1724371201000,
            durationMillis = 0,
            distanceMeters = 1000,
            avgPace = 0f,
            bestPace = 0f,
            calories = 0,
            segments = emptyList(),
            mapPreview = null,
        )

    val track6 =
        Track(
            id = 0,
            timestamp = 1724457601000,
            durationMillis = 0,
            distanceMeters = 1000,
            avgPace = 0f,
            bestPace = 0f,
            calories = 0,
            segments = emptyList(),
            mapPreview = null,
        )

    val track7 =
        Track(
            id = 0,
            timestamp = 1724544001000,
            durationMillis = 0,
            distanceMeters = 1000,
            avgPace = 0f,
            bestPace = 0f,
            calories = 0,
            segments = emptyList(),
            mapPreview = null,
        )

    val mondayTimestamp1 =
        Calendar
            .getInstance()
            .apply {
                set(2024, Calendar.AUGUST, 5) // Assume this is a Monday
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

    val tracklist2 =
        listOf(
            Track(1, mondayTimestamp1, 3600000, 1000, 5.0f, 4.5f, 300, listOf(), null),
            // Track(2, mondayTimestamp2, 3600000, 1000, 5.0f, 4.5f, 300, listOf(), null),
            // Track(3, mondayTimestamp3, 3600000, 1000, 5.0f, 4.5f, 300, listOf(), null),
        )

    val trackList = listOf(track1, track2, track3, track4, track5, track6, track7)

    val tracks = flowOf(tracklist2)
}