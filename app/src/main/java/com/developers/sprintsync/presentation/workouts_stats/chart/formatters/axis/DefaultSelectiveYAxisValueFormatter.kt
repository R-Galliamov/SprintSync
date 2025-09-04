package com.developers.sprintsync.presentation.workouts_stats.chart.formatters.axis

import com.developers.sprintsync.core.util.DistanceConverter
import com.developers.sprintsync.core.util.extension.approximatelyEquals
import com.developers.sprintsync.core.util.log.TimberAppLogger
import com.developers.sprintsync.presentation.components.formatter.CaloriesUiFormatter
import com.developers.sprintsync.presentation.components.formatter.DurationUiFormatter
import com.developers.sprintsync.presentation.components.formatter.DurationUiPattern
import com.developers.sprintsync.presentation.components.Metric
import com.developers.sprintsync.presentation.components.formatter.KilometerFormatter
import kotlin.math.abs

class DefaultSelectiveYAxisValueFormatter : SelectiveYAxisValueFormatter() {
    private var selectedValue: Float = Float.MIN_VALUE
    private var valuePosition: Float = Float.MIN_VALUE

    private var metric: Metric? = null

    private val tolerance get() = abs(selectedValue - valuePosition) * TOLERANCE_MULTIPLIER

    override fun highlightYAxisValue(
        metric: Metric,
        value: Float,
        yAxisMax: Float,
        yAxisLabelCount: Int,
    ) {
        this.metric = metric
        selectedValue = value
        valuePosition = calculateValuePosition(value, yAxisMax, yAxisLabelCount)
    }

    override fun getFormattedValue(value: Float): String {
        val metric = metric
        return if (metric != null &&
            value.approximatelyEquals(
                valuePosition,
                tolerance,
            )
        ) {
            when (metric) {
                Metric.DURATION -> DurationUiFormatter.format(selectedValue.toLong(), DurationUiPattern.MM_UNIT)
                Metric.DISTANCE -> KilometerFormatter(DistanceConverter(TimberAppLogger()), TimberAppLogger()).format(
                    selectedValue
                ).withUnit

                Metric.CALORIES -> CaloriesUiFormatter.format(selectedValue, CaloriesUiFormatter.Pattern.WITH_UNIT)
            }
        } else {
            EMPTY
        }
    }

    private fun calculateValuePosition(
        value: Float,
        yAxisMax: Float,
        yAxisLabelCount: Int,
    ): Float {
        val lag = yAxisMax / yAxisLabelCount.dec()
        var position = START_POSITION
        for (i in START_INDEX_VALUE until yAxisLabelCount) {
            if (position + lag > value) {
                break
            }
            position = lag * i
        }
        return position
    }

    companion object {
        private const val EMPTY = ""
        private const val START_POSITION = 0f
        private const val START_INDEX_VALUE = 0
        private const val TOLERANCE_MULTIPLIER = 0.5f
    }
}
