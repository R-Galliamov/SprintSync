package com.developers.sprintsync.core.tracking_service.data.processing.util.calculator.calories.met

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class CaloriesCalculatingChainTest {
    private val vO2CalculatorFactory = VO2CalculatorFactory()

    @ParameterizedTest
    @MethodSource("provideTestCases")
    fun `test VO2CalculatorFactory returns correct calculator`(
        speedInMetersPerMinute: Float,
        expectedVO2CalculatorFactoryType: Class<out VO2Calculator>,
        expectedVO2: Float,
        expectedMET: Float,
    ) {
        val actualFactoryType = vO2CalculatorFactory.getCalculator(speedInMetersPerMinute)
        Assertions.assertEquals(
            expectedVO2CalculatorFactoryType,
            actualFactoryType::class.java,
            "Incorrect factory type for speed $speedInMetersPerMinute",
        )
    }

    @ParameterizedTest
    @MethodSource("provideTestCases")
    fun `test VO2 calculations with tolerance`(
        speedInMetersPerMinute: Float,
        expectedVO2CalculatorFactoryType: Class<out VO2Calculator>,
        expectedVO2: Float,
        expectedMET: Float,
    ) {
        val actualVO2 =
            vO2CalculatorFactory
                .getCalculator(speedInMetersPerMinute)
                .calculateVO2(speedInMetersPerMinute)
        assertWithTolerance(expectedVO2, actualVO2)
    }

    @ParameterizedTest
    @MethodSource("provideTestCases")
    fun `test MET calculations with tolerance`(
        speedInMetersPerMinute: Float,
        expectedVO2CalculatorFactoryType: Class<out VO2Calculator>,
        expectedVO2: Float,
        expectedMET: Float,
    ) {
        val metCalculator = METCalculator(VO2CalculatorFactory())
        val actualMET = metCalculator.calculateMET(speedInMetersPerMinute)
        assertWithTolerance(expectedMET, actualMET)
    }

    private fun assertWithTolerance(
        expected: Float,
        actual: Float,
    ) {
        val tolerance = ERROR_COEFFICIENT * expected
        Assertions.assertEquals(expected, actual, tolerance)
    }

    companion object {
        @JvmStatic
        fun provideTestCases(): List<Arguments> =
            listOf(
                Arguments.of(45.6f, VO2Calculator.LowActivityVO2Calculator::class.java, 8.06f, 2.3f),
                Arguments.of(53.6f, VO2Calculator.LowActivityVO2Calculator::class.java, 8.86f, 2.5f),
                Arguments.of(67f, VO2Calculator.LowActivityVO2Calculator::class.java, 10.2f, 2.9f),
                Arguments.of(80.4f, VO2Calculator.LowActivityVO2Calculator::class.java, 11.54f, 3.3f),
                Arguments.of(91.2f, VO2Calculator.LowActivityVO2Calculator::class.java, 12.62f, 3.6f),
                Arguments.of(100.5f, VO2Calculator.LowActivityVO2Calculator::class.java, 13.55f, 3.9f),
                Arguments.of(135f, VO2Calculator.HighActivityVO2Calculator::class.java, 30.5f, 8.7f),
                Arguments.of(161f, VO2Calculator.HighActivityVO2Calculator::class.java, 35.7f, 10.2f),
                Arguments.of(188f, VO2Calculator.HighActivityVO2Calculator::class.java, 41.1f, 11.7f),
                Arguments.of(201f, VO2Calculator.HighActivityVO2Calculator::class.java, 43.7f, 12.5f),
                Arguments.of(214f, VO2Calculator.HighActivityVO2Calculator::class.java, 46.3f, 13.3f),
                Arguments.of(241f, VO2Calculator.HighActivityVO2Calculator::class.java, 51.7f, 14.8f),
                Arguments.of(268f, VO2Calculator.HighActivityVO2Calculator::class.java, 57.1f, 16.3f),
            )

        private const val ERROR_COEFFICIENT = 0.05f
    }
}
