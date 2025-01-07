package com.developers.sprintsync.update_goals.presentation.util

import com.developers.sprintsync.core.components.goal.data.model.Metric
import com.developers.sprintsync.core.components.track.domain.metrics_converter.CaloriesConverter
import com.developers.sprintsync.core.components.track.domain.metrics_converter.DistanceConverter
import com.developers.sprintsync.core.components.track.domain.metrics_converter.DurationConverter

object MetricInputConverter {
    fun convertInputToMetricValue(
        metric: Metric,
        uiValue: String,
    ): Float =
        when (metric) {
            Metric.DISTANCE ->
                DistanceConverter
                    .kilometersToMeters(
                        uiValue.toFloat(),
                    ).toFloat()

            Metric.DURATION -> DurationConverter.minutesToMillis(uiValue.toLong()).toFloat()
            Metric.CALORIES -> CaloriesConverter.kiloCaloriesToCalories(uiValue.toInt())
        }
}
