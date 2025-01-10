package com.developers.sprintsync.core.presentation.view.pace_chart.formatter

import com.developers.sprintsync.core.components.track.presentation.indicator_formatters.PaceFormatter
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.ValueFormatter

class PaceChartPaceFormatter : ValueFormatter() {
    override fun getAxisLabel(
        value: Float,
        axis: AxisBase?,
    ): String = PaceFormatter.formatPaceWithOneDecimal(value)
}