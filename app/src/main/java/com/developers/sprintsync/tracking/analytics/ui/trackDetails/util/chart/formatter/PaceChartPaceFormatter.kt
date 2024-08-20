package com.developers.sprintsync.tracking.analytics.ui.trackDetails.util.chart.formatter

import com.developers.sprintsync.tracking.analytics.dataManager.formatter.indicator.PaceFormatter
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.ValueFormatter

class PaceChartPaceFormatter : ValueFormatter() {
    override fun getAxisLabel(
        value: Float,
        axis: AxisBase?,
    ): String = PaceFormatter.formatPaceWithOneDecimal(value)
}
