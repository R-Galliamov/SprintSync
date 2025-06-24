package com.developers.sprintsync.presentation.goals_settings

import com.developers.sprintsync.domain.workouts_plan.model.Metric
import com.developers.sprintsync.core.util.KilometersToMetersConverter
import com.developers.sprintsync.core.util.time.TimeConverter
import javax.inject.Inject

class MetricInputConverter
    @Inject
    constructor() {
        fun convertInputToMetricValue(
            metric: Metric,
            uiValue: String,
        ): Float =
            when (metric) {
                Metric.DISTANCE -> KilometersToMetersConverter.convert(uiValue.toFloat())
                Metric.DURATION ->
                    TimeConverter
                        .convertToMillis(uiValue.toFloat(), TimeConverter.TimeUnit.MINUTES)
                        .toFloat()

                Metric.CALORIES -> uiValue.toFloat()
            }
    }
