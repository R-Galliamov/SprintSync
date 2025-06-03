package com.developers.sprintsync.data.track.service.processing.calculator.calories.met

sealed class VO2Calculator {
    protected abstract val factor: Float

    fun calculateVO2(speedInMetersPerMinute: Float): Float {
        require(speedInMetersPerMinute >= 0) { "Speed must be non-negative." }
        return factor * speedInMetersPerMinute + BASE_VO2
    }

    companion object {
        private const val BASE_VO2 = 3.5f
    }

    data object LowActivityVO2Calculator : VO2Calculator() {
        override val factor: Float = 0.1f
    }

    data object HighActivityVO2Calculator : VO2Calculator() {
        override val factor: Float = 0.2f
    }
}