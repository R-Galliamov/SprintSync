package com.developers.sprintsync.presentation.workouts_statistics.chart.formatters.axis

import android.util.Log
import com.developers.sprintsync.core.util.extension.approximatelyEquals
import com.developers.sprintsync.core.util.track_formatter.CaloriesUiFormatter
import com.developers.sprintsync.core.util.track_formatter.DistanceUiFormatter
import com.developers.sprintsync.core.util.track_formatter.DistanceUiPattern
import com.developers.sprintsync.core.util.track_formatter.DurationUiFormatter
import com.developers.sprintsync.core.util.track_formatter.DurationUiPattern
import com.developers.sprintsync.domain.goal.model.Metric
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
        Log.d(TAG, "getFormattedValue: $value")
        val metric = metric
        return if (metric != null &&
            value.approximatelyEquals(
                valuePosition,
                tolerance,
            )
        ) {
            when (metric) {
                Metric.DURATION -> DurationUiFormatter.format(selectedValue.toLong(), DurationUiPattern.MM_UNIT)
                Metric.DISTANCE -> DistanceUiFormatter.format(selectedValue, DistanceUiPattern.WITH_UNIT)
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

        private const val TAG = "My stack: SelectiveYAxisValueForm"
    }
}
