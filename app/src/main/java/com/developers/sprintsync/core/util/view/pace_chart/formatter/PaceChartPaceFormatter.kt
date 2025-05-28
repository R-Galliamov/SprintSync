package com.developers.sprintsync.core.util.view.pace_chart.formatter

import com.developers.sprintsync.core.util.track_formatter.PaceUiFormatter
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.ValueFormatter

class PaceChartPaceFormatter : ValueFormatter() {
    override fun getAxisLabel(
        value: Float,
        axis: AxisBase?,
    ): String = PaceUiFormatter.format(value, PaceUiFormatter.Pattern.ONE_DECIMAL)
}
