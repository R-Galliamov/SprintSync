package com.developers.sprintsync.statistics.util.converter

import com.developers.sprintsync.statistics.domain.chart.data.Metric
import com.developers.sprintsync.tracking.analytics.dataManager.calculator.CaloriesConverter
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
            Metric.CALORIES -> CaloriesConverter.kiloCaloriesToCalories(uiValue.toInt())
        }
}
