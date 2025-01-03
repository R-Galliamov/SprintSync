package com.developers.sprintsync.statistics.domain.chart.formatters.axis

import android.util.Log
import com.developers.sprintsync.global.util.extension.approximatelyEquals
import com.developers.sprintsync.statistics.domain.chart.data.Metric
import com.developers.sprintsync.tracking.analytics.dataManager.formatter.indicator.CaloriesFormatter
import com.developers.sprintsync.tracking.analytics.dataManager.formatter.indicator.DistanceFormatter
import com.developers.sprintsync.tracking.analytics.dataManager.formatter.indicator.DurationFormatter
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
                Metric.DURATION -> DurationFormatter.formatToMm(selectedValue.toLong(), true)

                Metric.DISTANCE -> DistanceFormatter.metersToPresentableKilometers(selectedValue.toInt(), true)
                Metric.CALORIES -> CaloriesFormatter.formatCalories(selectedValue.toInt(), true)
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
