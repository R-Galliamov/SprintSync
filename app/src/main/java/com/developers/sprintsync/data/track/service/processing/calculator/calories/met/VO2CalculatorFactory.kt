package com.developers.sprintsync.data.track.service.processing.calculator.calories.met

/**
 * Factory class for creating VO2 calculators based on speed.
 *
 * This class provides a method to get the appropriate VO2 calculator
 * based on the provided speed in meters per minute. It uses a predefined
 * mapping of speed ranges to specific VO2 calculator implementations.
 */
class VO2CalculatorFactory {
    private val calculatorMapping: List<Pair<ClosedFloatingPointRange<Float>, VO2Calculator>> =
        listOf(
            MIN_SPEED..SPEED_THRESHOLD to VO2Calculator.LowActivityVO2Calculator,
            SPEED_THRESHOLD..Float.MAX_VALUE to VO2Calculator.HighActivityVO2Calculator,
        )

    /**
     * Retrieves the appropriate VO2 calculator based on the provided speed.
     *
     * This function searches through a mapping of speed ranges to VO2 calculators
     * [calculatorMapping] and returns the calculator associated with the first
     * range that contains the given `speedInMetersPerMinute`.
     *
     * @param speedMPM The speed in meters per minute for which to find a VO2 calculator.
     * @return The [VO2Calculator] instance that corresponds to the given speed.
     * @throws NoSuchElementException if no calculator is found for the given speed
     * (i.e., the speed does not fall within any of the defined ranges in [calculatorMapping]).
     */
    fun getCalculator(speedMPM: Float): VO2Calculator =
        calculatorMapping.first { (range, _) -> speedMPM in range }.second

    companion object {
        private const val MIN_SPEED = 0f
        private const val SPEED_THRESHOLD = 134.12f // 134.12 Meters per minute
    }
}
