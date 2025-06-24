package com.developers.sprintsync.presentation.workouts_stats.chart.formatters.axis

import com.developers.sprintsync.domain.workouts_plan.model.Metric
import com.github.mikephil.charting.formatter.ValueFormatter

abstract class SelectiveYAxisValueFormatter : ValueFormatter() {
    abstract fun highlightYAxisValue(
        metric: Metric,
        value: Float,
        yAxisMax: Float,
        yAxisLabelCount: Int,
    )
}
