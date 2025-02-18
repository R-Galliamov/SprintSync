package com.developers.sprintsync.tracking.data.processing.util.calculator.calories.met

sealed class VO2Calculator {
    protected abstract val multiplier: Float
    /* TODO: define coefficients.
Note: If you want to account for incline, you’d need to add the grade factor (0.9×speed×grade) to the VO2 equation.
Also, for walking or other speeds/activities, different coefficients apply.
     */

    fun calculateVO2(speedInMetersPerMinute: Float): Float {
        require(speedInMetersPerMinute >= 0) { "Speed must be non-negative." }
        return multiplier * speedInMetersPerMinute + BASE_VO2
    }

    companion object {
        private const val BASE_VO2 = 3.5f
    }

    data object LowActivityVO2Calculator : VO2Calculator() {
        override val multiplier: Float = 0.1f
    }

    data object HighActivityVO2Calculator : VO2Calculator() {
        override val multiplier: Float = 0.2f
    }
}