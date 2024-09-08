package com.developers.sprintsync.user.util.converter

import com.developers.sprintsync.tracking.analytics.dataManager.calculator.CaloriesCalculator
import com.developers.sprintsync.tracking.analytics.dataManager.calculator.DistanceCalculator
import com.developers.sprintsync.tracking.analytics.dataManager.calculator.DurationCalculator
import com.developers.sprintsync.user.model.chart.chartData.Metric

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
