package com.developers.sprintsync.data.track.service.processing.calculator.calories.met

import com.developers.sprintsync.data.track.service.processing.calculator.calories.met.PhysiologyConstants.RESTING_VO2


/**
 * Physiological constants for human physiology and exercise calculations.
 */
object PhysiologyConstants {
    const val RESTING_VO2 = 3.5f          // mL·kg⁻¹·min⁻¹
    const val ML_TO_KCAL_DIVISOR = 200f   // 1000 / (5 kcal·L⁻¹ × 3.5)
    const val MEDIATE_SPEED_MPM = 150f // Meters per minute
}

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
        return factor * speedMPM + RESTING_VO2
    }

    /**
     * VO2 calculator for low-intensity activities (e.g., walking).
     */
    data object LowActivityVO2Calculator : VO2Calculator() {
        override val factor: Float = Factors.LOW_FACTOR
    }

    /**
     * VO2 calculator for high-intensity activities (e.g., running).
     */
    data object HighActivityVO2Calculator : VO2Calculator() {
        override val factor: Float = Factors.HIGH_FACTOR
    }

    object Factors {
        const val LOW_FACTOR = 0.1f
        const val HIGH_FACTOR = 0.2f
    }
}