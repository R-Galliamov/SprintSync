package com.developers.sprintsync.update_goals.presentation.util

import com.developers.sprintsync.core.components.goal.data.model.Metric
import com.developers.sprintsync.core.components.track.domain.metrics_converter.KilometersToMetersConverter
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
