package com.developers.sprintsync.statistics.util.converter

import com.developers.sprintsync.statistics.model.chart.chartData.Metric
import com.developers.sprintsync.tracking.analytics.dataManager.calculator.CaloriesCalculator
import com.developers.sprintsync.tracking.analytics.dataManager.calculator.DistanceCalculator
import com.developers.sprintsync.tracking.analytics.dataManager.calculator.DurationCalculator

object MetricInputConverter {
    fun convertInputToMetricValue(
        metric: Metric,
        uiValue: String,
    ): Float =
        when (metric) {
            Metric.DISTANCE ->
                DistanceCalculator
                    .kilometersToMeters(
                        uiValue.toFloat(),
                    ).toFloat()

            Metric.DURATION -> DurationCalculator.minutesToMillis(uiValue.toLong()).toFloat()
            Metric.CALORIES -> CaloriesCalculator.kiloCaloriesToCalories(uiValue.toInt())
        }
}
