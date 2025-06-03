package com.developers.sprintsync.data.statistics

import com.developers.sprintsync.core.util.timestamp.TimestampUtils
import com.developers.sprintsync.domain.statistics.model.WorkoutsStats
import com.developers.sprintsync.domain.track.model.Track
import javax.inject.Inject

class WorkoutsStatsCreator @Inject constructor() {

    fun create(tracks: List<Track>): WorkoutsStats {
        val totalWorkouts = tracks.size
        val totalWorkoutDays = calculateWorkoutDays(tracks)
        val totalDistanceMeters = tracks.sumOf { it.distanceMeters.toDouble() }.toFloat()
        val totalDurationMillis = tracks.sumOf { it.durationMillis }
        val longestDistanceMeters = tracks.maxOf { it.distanceMeters }
        val longestDurationMillis = tracks.maxOf { it.durationMillis }
        val avgPaceMPKm = tracks.map { it.avgPace }.average().toFloat()
        val peakPaceMPKm = tracks.minOf { it.avgPace }
        val totalCalories = tracks.sumOf { it.calories.toDouble() }.toFloat()

        return WorkoutsStats(
            totalWorkouts = totalWorkouts,
            totalWorkoutDays = totalWorkoutDays,
            totalDistanceMeters = totalDistanceMeters,
            totalDurationMillis = totalDurationMillis,
            longestDistanceMeters = longestDistanceMeters,
            longestDurationMillis = longestDurationMillis,
            avgPaceMPKm = avgPaceMPKm,
            peakPaceMPKm = peakPaceMPKm,
            totalCalories = totalCalories,
        )
    }


    private fun calculateWorkoutDays(tracks: List<Track>): Int =
        tracks.map { TimestampUtils.getStartOfDayTimestamp(it.timestamp) }.distinct().size

    private fun calculateWorkoutStreak(tracks: List<Track>): Int {
        val workoutDaysTimestamps =
            tracks.map { TimestampUtils.getStartOfDayTimestamp(it.timestamp) }.distinct().sorted()

        var nextDayTimestamp = TimestampUtils.addDaysToTimestamp(workoutDaysTimestamps.first(), 1)
        var maxStreak = 1
        workoutDaysTimestamps.forEach {
            if (it == nextDayTimestamp) maxStreak++
            nextDayTimestamp = TimestampUtils.addDaysToTimestamp(it, 1)
        }

        return maxStreak
    }
}
