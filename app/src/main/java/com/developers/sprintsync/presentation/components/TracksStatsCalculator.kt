package com.developers.sprintsync.presentation.components

import com.developers.sprintsync.core.util.timestamp.TimestampUtils
import com.developers.sprintsync.domain.track.model.Track

/**
 * Calculates aggregated statistics for a list of tracks.
 * TODO replace with model
 */
class TracksStatsCalculator(
    tracks: List<Track>,
) {
    val totalWorkouts = tracks.size
    val workoutDays = calculateWorkoutDays(tracks)
    val maxWorkoutStreak = calculateWorkoutStreak(tracks)
    val totalDistanceMeters = tracks.sumOf { it.distanceMeters.toDouble() }.toFloat()
    val totalDurationMillis = tracks.sumOf { it.durationMillis }
    val longestDistanceMeters = tracks.maxOf { it.distanceMeters }
    val longestDurationMillis = tracks.maxOf { it.durationMillis }
    val averagePace = tracks.map { it.avgPace }.average().toFloat()
    val bestPace = tracks.minOf { it.avgPace }
    val totalCaloriesBurned = tracks.sumOf { it.calories.toDouble() }.toFloat()

    private fun calculateWorkoutDays(tracks: List<Track>): Int =
        tracks.map { TimestampUtils.getStartOfDayTimestamp(it.timestamp) }.distinct().size

    private fun calculateWorkoutStreak(tracks: List<Track>): Int {
        val workoutDaysTimestamps = tracks.map { TimestampUtils.getStartOfDayTimestamp(it.timestamp) }.distinct().sorted()

        var nextDayTimestamp = TimestampUtils.addDaysToTimestamp(workoutDaysTimestamps.first(), 1)
        var maxStreak = 1
        workoutDaysTimestamps.forEach {
            if (it == nextDayTimestamp) maxStreak++
            nextDayTimestamp = TimestampUtils.addDaysToTimestamp(it, 1)
        }

        return maxStreak
    }
}
