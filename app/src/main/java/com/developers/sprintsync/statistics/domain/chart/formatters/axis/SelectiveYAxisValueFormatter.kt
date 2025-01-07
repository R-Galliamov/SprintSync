package com.developers.sprintsync.statistics.domain.chart.formatters.axis

import com.developers.sprintsync.core.components.goal.data.model.Metric
import com.github.mikephil.charting.formatter.ValueFormatter

abstract class SelectiveYAxisValueFormatter : ValueFormatter() {
    abstract fun highlightYAxisValue(
        metric: Metric,
        value: Float,
        yAxisMax: Float,
        yAxisLabelCount: Int,
    )
}
