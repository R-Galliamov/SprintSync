package com.developers.sprintsync.data.track.service.processing.calculator.calories.met

/**
 * Sealed class for calculating VO2 (oxygen consumption) based on activity level and speed.
 */
sealed class VO2Calculator {
    /**
     * Factor used in VO2 calculation, specific to activity level.
     */
    protected abstract val factor: Float

    /**
     * Calculates VO2 consumption based on speed.
     * @param speedMPM Speed in meters per minute.
     * @return VO2 value in mL/kg/min.
     * @throws IllegalArgumentException if speedMPM is negative.
     */
    fun calculateVO2(speedMPM: Float): Float {
        require(speedMPM >= 0) { "Speed must be non-negative." }
        return factor * speedMPM + Constants.BASE_VO2
    }

    /**
     * VO2 calculator for low-intensity activities (e.g., walking).
     */
    data object LowActivityVO2Calculator : VO2Calculator() {
        override val factor: Float = Constants.LOW_FACTOR
    }

    /**
     * VO2 calculator for high-intensity activities (e.g., running).
     */
    data object HighActivityVO2Calculator : VO2Calculator() {
        override val factor: Float = Constants.HIGH_FACTOR
    }

    protected object Constants {
        const val BASE_VO2 = 3.5f
        const val LOW_FACTOR = 0.1f
        const val HIGH_FACTOR = 0.2f
    }
}