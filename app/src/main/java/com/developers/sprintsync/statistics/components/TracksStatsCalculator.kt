package com.developers.sprintsync.statistics.components

import com.developers.sprintsync.core.components.time.utils.TimeUtils
import com.developers.sprintsync.core.components.track.data.model.Track

class TracksStatsCalculator(
    tracks: List<Track>,
) {
    val numberOfWorkouts = tracks.size
    val workoutDays = calculateWorkoutDays(tracks)
    val maxWorkoutStreak = calculateWorkoutStreak(tracks)
    val totalDistanceMeters = tracks.sumOf { it.distanceMeters }
    val totalDurationMillis = tracks.sumOf { it.durationMillis }
    val longestDistanceMeters = tracks.maxOf { it.distanceMeters }
    val longestDurationMillis = tracks.maxOf { it.durationMillis }
    val averagePace = tracks.map { it.avgPace }.average().toFloat()
    val bestPace = tracks.minOf { it.avgPace }
    val totalCaloriesBurned = tracks.sumOf { it.calories }

    private fun calculateWorkoutDays(tracks: List<Track>): Int = tracks.map { TimeUtils.getStartOfDayTimestamp(it.timestamp) }.distinct().size

    private fun calculateWorkoutStreak(tracks: List<Track>): Int {
        val workoutDaysTimestamps = tracks.map { TimeUtils.getStartOfDayTimestamp(it.timestamp) }.distinct().sorted()

        var nextDayTimestamp = TimeUtils.addDaysToTimestamp(workoutDaysTimestamps.first(), 1)
        var maxStreak = 1
        workoutDaysTimestamps.forEach {
            if (it == nextDayTimestamp) maxStreak++
            nextDayTimestamp = TimeUtils.addDaysToTimestamp(it, 1)
        }

        return maxStreak
    }
}
