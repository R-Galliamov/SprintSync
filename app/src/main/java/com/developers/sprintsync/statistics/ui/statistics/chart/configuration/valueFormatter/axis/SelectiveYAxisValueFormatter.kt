package com.developers.sprintsync.statistics.ui.statistics.chart.configuration.valueFormatter.axis

import com.developers.sprintsync.statistics.model.chart.chartData.Metric
import com.github.mikephil.charting.formatter.ValueFormatter

abstract class SelectiveYAxisValueFormatter : ValueFormatter() {
    abstract fun selectYAxisValue(
        metric: Metric,
        value: Float,
        yAxisMax: Float,
        yAxisLabelCount: Int,
    )
}
