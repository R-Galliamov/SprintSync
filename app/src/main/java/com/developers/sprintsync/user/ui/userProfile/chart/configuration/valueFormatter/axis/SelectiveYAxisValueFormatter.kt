package com.developers.sprintsync.user.ui.userProfile.chart.configuration.valueFormatter.axis

import com.developers.sprintsync.user.model.chart.chartData.Metric
import com.github.mikephil.charting.formatter.ValueFormatter

abstract class SelectiveYAxisValueFormatter : ValueFormatter() {
    abstract fun selectYAxisValue(
        metric: Metric,
        value: Float,
        yAxisMax: Float,
        yAxisLabelCount: Int,
    )
}
