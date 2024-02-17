package com.developers.sprintsync.util.calculator

import java.util.concurrent.TimeUnit

class PaceCalculator {

    private var lastTimeMillis: Long = 0
    private var lastDistanceMeters: Int = 0


    //TODO add check of preferred pace presentation
    fun getCurrentPaceInMinPerKm(currentTimeMillis: Long, currentDistanceMeters: Int): Float {
        val elapsedTimeMillis = calculateElapsedTime(currentTimeMillis)
        val distanceCovered = calculateDistanceCovered(currentDistanceMeters)
        updateLastValues(currentTimeMillis, currentDistanceMeters)
        return calculatePaceMinPerKm(elapsedTimeMillis, distanceCovered)
    }

    private fun calculateElapsedTime(currentTimeMillis: Long): Long {
        val elapsedTimeMillis = currentTimeMillis - lastTimeMillis
        require(elapsedTimeMillis >= 0) { "Time must be non-negative" }
        return elapsedTimeMillis
    }

    private fun calculateDistanceCovered(currentDistanceMeters: Int): Int {
        val distanceCovered = currentDistanceMeters - lastDistanceMeters
        require(distanceCovered >= 0) { "Distance must be non-negative" }
        return distanceCovered
    }

    private fun updateLastValues(currentTimeInMillis: Long, currentDistanceMeters: Int) {
        lastTimeMillis = currentTimeInMillis
        lastDistanceMeters = currentDistanceMeters
    }

    private fun calculatePaceMinPerKm(elapsedTimeMillis: Long, distanceCoveredMeters: Int): Float {
        //Log.i("My stack indicators", "Time is: $elapsedTimeMillis")
        //Log.i("My stack indicators", "Distance is: $distanceCoveredMeters")
        require(distanceCoveredMeters >= 0) { "Distance must be non-negative" }
        if (distanceCoveredMeters == 0) return -1F
        val minutes = TimeUnit.MILLISECONDS.toMinutes(elapsedTimeMillis)
        val kilometers = distanceCoveredMeters / 1000.0f
        return (minutes / kilometers)
    }
}